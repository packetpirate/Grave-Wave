package com.gzsr.states;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.InputListener;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;
import org.newdawn.slick.util.FontUtils;

import com.gzsr.AssetManager;
import com.gzsr.Controls;
import com.gzsr.Controls.Layout;
import com.gzsr.Globals;
import com.gzsr.MusicPlayer;
import com.gzsr.entities.Entity;
import com.gzsr.entities.Player;
import com.gzsr.entities.enemies.EnemyController;
import com.gzsr.gfx.Camera;
import com.gzsr.gfx.particles.Particle;
import com.gzsr.gfx.ui.MenuButton;
import com.gzsr.gfx.ui.VanishingText;
import com.gzsr.gfx.ui.hud.Console;
import com.gzsr.gfx.ui.hud.HUD;
import com.gzsr.misc.MouseInfo;
import com.gzsr.misc.Pair;
import com.gzsr.objects.items.Item;

public class GameState extends BasicGameState implements InputListener {
	public static final int ID = 1;
	
	private static final float PROMPT_WIDTH = 350.0f;
	private static final float PROMPT_HEIGHT = 100.0f;
	
	private static final Color PAUSE_OVERLAY = new Color(0x331F006F);
	
	private AssetManager assets;
	private long time, accu, consoleTimer;
	public long getTime() { return time; }
	
	private Console console;
	private HUD hud;
	
	private ConcurrentHashMap<String, Entity> entities;
	public Entity getEntity(String key) { return entities.get(key); }
	public void addEntity(String key, Entity e) { entities.put(key, e); }
	
	private static ConcurrentHashMap<String, VanishingText> messages = new ConcurrentHashMap<String, VanishingText>();
	public static void addVanishingText(String key, VanishingText vt) { messages.put(key, vt); }
	
	private boolean gameStarted, paused, consoleOpen, exitPrompt;
	public boolean isConsoleOpen() { return consoleOpen; }
	
	private MenuButton exitYes, exitNo;
	
	@Override
	public void init(GameContainer gc, StateBasedGame game) throws SlickException {
		assets = AssetManager.getManager();
		
		gc.setMouseCursor(assets.getImage("GZS_Crosshair"), 16, 16);
		
		entities = new ConcurrentHashMap<String, Entity>();
		
		reset(gc);
		
		UnicodeFont f = assets.getFont("PressStart2P-Regular_large");
		exitYes = new MenuButton(new Pair<Float>((((Globals.WIDTH / 2) - (PROMPT_WIDTH / 2)) + 20.0f), (float)(Globals.HEIGHT / 2)), "Yes");
		exitNo = new MenuButton(new Pair<Float>((((Globals.WIDTH / 2) + (PROMPT_WIDTH / 2)) - f.getWidth("No") - 20.0f), (float)(Globals.HEIGHT / 2)), "No");
	}

	@Override
	public void update(GameContainer gc, StateBasedGame game, int delta) throws SlickException {
		if(gc.hasFocus()) {
			accu = Math.min((accu + delta), (Globals.STEP_TIME * Globals.MAX_STEPS));
			
			while(accu >= Globals.STEP_TIME) {
				if(exitPrompt) {
					MouseInfo mouse = Controls.getInstance().getMouse();
					
					if(exitYes.inBounds(mouse.getPosition().x, mouse.getPosition().y)) {
						exitYes.mouseEnter();
						if(mouse.isMouseDown()) {
							reset(gc);
							game.enterState(MenuState.ID, new FadeOutTransition(), new FadeInTransition());
						}
					} else exitYes.mouseExit();
					
					if(exitNo.inBounds(mouse.getPosition().x, mouse.getPosition().y)) {
						exitNo.mouseEnter();
						if(mouse.isMouseDown()) {
							mouse.setMouseDown(false);
							exitPrompt = false;
						}
					} else exitNo.mouseExit();
				} else if(!paused && !consoleOpen) {
					time += (long)Globals.STEP_TIME; // Don't want to update time while paused; otherwise, game objects and events could despawn / occur while paused.
					
					Player player = Player.getPlayer();
					
					Iterator<Entry<String, Entity>> it = entities.entrySet().iterator();
					while(it.hasNext()) {
						Map.Entry<String, Entity> pair = (Map.Entry<String, Entity>) it.next();
						pair.getValue().update(this, time, Globals.STEP_TIME);
						if(pair.getValue() instanceof EnemyController) {
							EnemyController ec = (EnemyController)pair.getValue();
							ec.updateEnemies(this, player, time, Globals.STEP_TIME);
						} else if(pair.getValue() instanceof Item) {
							Item item = (Item) pair.getValue();
							if(item.isActive(time)) {
								player.checkItem(item, time);
							} else it.remove();
						} else if(pair.getValue() instanceof Particle) {
							Particle p = (Particle) pair.getValue();
							if(!p.isActive(time)) {
								p.onDestroy(this, time);
								it.remove();
							}
						}
					}
					
					Controls controls = Controls.getInstance();
					
					if(!player.isAlive() && (player.getAttributes().getInt("lives") <= 0)) {
						// If the player has died, transition state.
						controls.resetAll();
						Globals.gameOver = true;
						game.enterState(GameOverState.ID, 
										new FadeOutTransition(Color.black, 250), 
										new FadeInTransition(Color.black, 100));
					}
					
					if(player.isAlive()) {
						if(controls.isPressed(Controls.Layout.TRAIN_SCREEN)) {
							// Open the training screen.
							controls.resetAll();
							game.enterState(TrainState.ID,
											new FadeOutTransition(Color.black, 250),
											new FadeInTransition(Color.black, 100));
						} else if(controls.isPressed(Controls.Layout.SHOP_SCREEN)) {
							// Open the weapon shopping screen.
							controls.resetAll();
							game.enterState(ShopState.ID,
											new FadeOutTransition(Color.black, 250),
											new FadeInTransition(Color.black, 100));
						}
					}
					
					Iterator<Entry<String, VanishingText>> vit = messages.entrySet().iterator();
					while(vit.hasNext()) {
						// Draw all vanishing texts to the screen.
						VanishingText vt = ((Map.Entry<String, VanishingText>) vit.next()).getValue();
						vt.update(this, time, delta);
						if(!vt.isActive()) vit.remove(); 
					}
					
					Camera.getCamera().update(time);
					MusicPlayer.getInstance().update(false);
					hud.update(player, time);
				} else if(consoleOpen) {
					consoleTimer += (long)delta;
					console.update(this, consoleTimer, Globals.STEP_TIME);
				}
				
				Controls.Layout.clearReleased();
				accu -= Globals.STEP_TIME;
			}
		}
	}
	
	@Override
	public void render(GameContainer gc, StateBasedGame game, Graphics g) throws SlickException {
		g.clear();
		
		Image background = assets.getImage("GZS_Background6");
		g.drawImage(background, 0.0f, 0.0f, Globals.WIDTH, Globals.HEIGHT, 0.0f, 0.0f, background.getWidth(), background.getHeight());
		
		Camera.getCamera().translate(g);
		
		entities.values().stream().sorted(Entity.COMPARE).forEach(entity -> entity.render(g, time));
		
		Iterator<Entry<String, VanishingText>> vit = messages.entrySet().iterator();
		while(vit.hasNext()) {
			VanishingText vt = ((Map.Entry<String, VanishingText>) vit.next()).getValue();
			if(vt.isActive()) vt.render(g, time);
		}
		
		hud.render(g, this, time);
		
		if(Camera.getCamera().displayVignette()) {
			g.setColor(Camera.VIGNETTE_COLOR);
			g.fillRect(0.0f, 0.0f, Globals.WIDTH, Globals.HEIGHT);
		}
		
		if(paused) {
			g.setColor(PAUSE_OVERLAY);
			g.fillRect(0.0f, 0.0f, Globals.WIDTH, Globals.HEIGHT);
			g.setFont(AssetManager.getManager().getFont("PressStart2P-Regular"));
			g.setColor(Color.white);
			int w = g.getFont().getWidth("Paused");
			int h = g.getFont().getLineHeight();
			FontUtils.drawCenter(g.getFont(), "Paused", 
								 ((Globals.WIDTH / 2) - (w / 2)), ((Globals.HEIGHT / 2) - (h / 2)), w);
		}
		
		if(exitPrompt) {
			// Draw the exit prompt in the center of the screen.
			float px = ((Globals.WIDTH / 2) - (PROMPT_WIDTH / 2));
			float py = ((Globals.HEIGHT / 2) - (PROMPT_HEIGHT / 2));
			
			g.setColor(Color.gray);
			g.fillRect(px, py, PROMPT_WIDTH, PROMPT_HEIGHT);
			g.setColor(Color.white);
			g.drawRect(px, py, PROMPT_WIDTH, PROMPT_HEIGHT);
			
			g.setFont(AssetManager.getManager().getFont("PressStart2P-Regular"));
			FontUtils.drawCenter(g.getFont(), "DO YOU WANT TO QUIT?", (int)(px + 5.0f), (int)(py + 10.0f), (int)(PROMPT_WIDTH - 10.0f), Color.white);
			
			exitYes.render(g, 0L);
			exitNo.render(g, 0L);
		} else if(consoleOpen) console.render(g, time);
		
		g.resetTransform();
	}
	
	public void reset(GameContainer gc) throws SlickException{
		time = 0L;
		accu = 0L;
		consoleTimer = 0L;
		
		Controls.getInstance().resetAll();
		
		Player.getPlayer().reset();
		entities.clear();
		entities.put("enemyController", new EnemyController());
		entities.put("player", Player.getPlayer());
		
		messages.clear();
		
		gameStarted = false;
		paused = false;
		consoleOpen = false;
		exitPrompt = false;
		
		Camera.getCamera().reset();
		console = new Console(this, gc);
		
		hud = new HUD();
		
		ShopState.resetShop();
	}
	
	@Override
	public void mouseMoved(int oldx, int oldy, int newx, int newy) {
		Controls.getInstance().getMouse().setPosition(newx, newy);
	}
	
	@Override
	public void mousePressed(int button, int x, int y) {
		if(consoleOpen) console.mousePressed(this, button, x, y);
		if(button == 0) Controls.getInstance().getMouse().setMouseDown(true);
	}
	
	@Override
	public void mouseReleased(int button, int x, int y) {
		if(button == 0) Controls.getInstance().getMouse().setMouseDown(false);
	}
	
	@Override
	public void mouseDragged(int oldx, int oldy, int newx, int newy) {
		Controls.getInstance().getMouse().setPosition(newx, newy);
	}
	
	@Override
	public void keyPressed(int key, char c) {
		if(consoleOpen) {
			console.keyPressed(key, c);
		} else {
			Controls.getInstance().press(key);
		}
	}
	
	@Override
	public void keyReleased(int key, char c) {
		EnemyController ec = (EnemyController) getEntity("enemyController");
		
		if(key == Input.KEY_ESCAPE) {
			if(!exitPrompt) exitPrompt = true;
			else exitPrompt = false;
		} else {
			Layout keyID = Controls.Layout.identify(key);
			if((keyID == Controls.Layout.OPEN_CONSOLE) && Globals.ENABLE_CONSOLE) {
				console.setPauseTime(time);
				consoleOpen = !consoleOpen;
			} else {
				if(consoleOpen) console.keyReleased(key, c);
				else {
					if(keyID == Controls.Layout.PAUSE_GAME) {
						if(!paused) MusicPlayer.getInstance().pause();
						else MusicPlayer.getInstance().resume();
						paused = !paused;
					} else if((keyID == Controls.Layout.NEXT_WAVE) && !paused && ec.isRestarting()) {
						ec.skipToNextWave();
					} else {
						Controls.getInstance().release(key);
					}
				}
			}
		}
	}
	
	@Override
	public void mouseWheelMoved(int change) {
		Player player = Player.getPlayer();
		if(player.getWeapons().size() > 1) {
			player.weaponRotate((change > 0) ? 1 : -1);
			hud.getWeaponDisplay().queueWeaponCycle();
		}
	}
	
	@Override
	public void enter(GameContainer gc, StateBasedGame game) throws SlickException {
		Controls.getInstance().resetAll();
		
		if(!gameStarted) {
			gameStarted = true;
		}
		
		if(Globals.gameOver) {
			reset(gc);
			Globals.gameOver = false;
		}
	}

	@Override
	public int getID() {
		return GameState.ID;
	}
}
