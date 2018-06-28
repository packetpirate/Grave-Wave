package com.gzsr.states;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.InputListener;
import org.newdawn.slick.SlickException;
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
import com.gzsr.gfx.ui.Console;
import com.gzsr.gfx.ui.HUD;
import com.gzsr.gfx.ui.VanishingText;
import com.gzsr.objects.items.Item;

public class GameState extends BasicGameState implements InputListener {
	public static final int ID = 1;
	
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
	
	private boolean gameStarted, paused, consoleOpen;
	public boolean isConsoleOpen() { return consoleOpen; }
	
	@Override
	public void init(GameContainer gc, StateBasedGame game) throws SlickException {
		assets = AssetManager.getManager();
		
		gc.setMouseCursor(assets.getImage("GZS_Crosshair"), 16, 16);
		
		entities = new ConcurrentHashMap<String, Entity>();
		
		reset(gc);
	}

	@Override
	public void update(GameContainer gc, StateBasedGame game, int delta) throws SlickException {
		accu = Math.min((accu + delta), (Globals.STEP_TIME * Globals.MAX_STEPS));
		
		while(accu >= Globals.STEP_TIME) {
			if(!paused && !consoleOpen) {
				time += (long)Globals.STEP_TIME; // Don't want to update time while paused; otherwise, game objects and events could despawn / occur while paused.
				
				Player player = Player.getPlayer();
				player.update(this, time, Globals.STEP_TIME);
				
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
					}
				}
				
				Controls controls = Controls.getInstance();
				
				if(!player.isAlive() && (player.getIntAttribute("lives") <= 0)) {
					// If the player has died, transition state.
					controls.resetAll();
					Globals.gameOver = true;
					game.enterState(GameOverState.ID, 
									new FadeOutTransition(Color.black, 250), 
									new FadeInTransition(Color.black, 250));
				}
				
				if(player.isAlive()) {
					if(controls.isPressed(Controls.Layout.TRAIN_SCREEN)) {
						// Open the training screen.
						controls.resetAll();
						game.enterState(TrainState.ID,
										new FadeOutTransition(Color.black, 250),
										new FadeInTransition(Color.black, 250));
					} else if(controls.isPressed(Controls.Layout.SHOP_SCREEN)) {
						// Open the weapon shopping screen.
						controls.resetAll();
						game.enterState(ShopState.ID,
										new FadeOutTransition(Color.black, 250),
										new FadeInTransition(Color.black, 250));
					}
				}
				
				Iterator<Entry<String, VanishingText>> vit = messages.entrySet().iterator();
				while(vit.hasNext()) {
					// Draw all vanishing texts to the screen.
					VanishingText vt = ((Map.Entry<String, VanishingText>) vit.next()).getValue();
					vt.update(this, time, delta);
					if(!vt.isActive()) vit.remove(); 
				}
				
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
	
	@Override
	public void render(GameContainer gc, StateBasedGame game, Graphics g) throws SlickException {
		Player player = Player.getPlayer();
		
		g.resetTransform();
		g.clear();
		
		Image background = assets.getImage("GZS_Background6");
		g.drawImage(background, 0.0f, 0.0f, Globals.WIDTH, Globals.HEIGHT, 0.0f, 0.0f, background.getWidth(), background.getHeight());
		
		player.render(g, time);

		Iterator<Entry<String, Entity>> it = entities.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry<String, Entity> pair = (Map.Entry<String, Entity>) it.next();
			pair.getValue().render(g, time);
		}
		
		player.getFlashlight().render(g, time);
		
		Iterator<Entry<String, VanishingText>> vit = messages.entrySet().iterator();
		while(vit.hasNext()) {
			VanishingText vt = ((Map.Entry<String, VanishingText>) vit.next()).getValue();
			if(vt.isActive()) vt.render(g, time);
		}
		
		hud.render(g, this, time);
		
		if(paused) {
			g.setFont(AssetManager.getManager().getFont("PressStart2P-Regular"));
			g.setColor(Color.white);
			int w = g.getFont().getWidth("Paused");
			int h = g.getFont().getLineHeight();
			FontUtils.drawCenter(g.getFont(), "Paused", 
								 ((Globals.WIDTH / 2) - (w / 2)), ((Globals.HEIGHT / 2) - (h / 2)), w);
		}
		
		if(consoleOpen) console.render(g, time);
	}
	
	public void reset(GameContainer gc) throws SlickException{
		time = 0L;
		accu = 0L;
		consoleTimer = 0L;
		
		Controls.getInstance().resetAll();
		
		Player.getPlayer().reset();
		entities.clear();
		entities.put("enemyController", new EnemyController());
		
		messages.clear();
		
		gameStarted = false;
		paused = false;
		consoleOpen = false;
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
		Controls.getInstance().getMouse().setMouseDown(true);
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
	
	@Override
	public void mouseWheelMoved(int change) {
		Player player = Player.getPlayer();
		if(player.getWeapons().size() > 1) {
			player.weaponRotate((change > 0)?1:-1);
			hud.queueWeaponCycle();
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
