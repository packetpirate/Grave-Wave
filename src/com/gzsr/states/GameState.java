package com.gzsr.states;

import java.awt.Font;
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
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;
import org.newdawn.slick.util.FontUtils;

import com.gzsr.AssetManager;
import com.gzsr.Globals;
import com.gzsr.MusicPlayer;
import com.gzsr.entities.Entity;
import com.gzsr.entities.Player;
import com.gzsr.entities.enemies.EnemyController;
import com.gzsr.gfx.Animation;
import com.gzsr.gfx.ui.Console;
import com.gzsr.gfx.ui.HUD;
import com.gzsr.objects.items.Item;

public class GameState extends BasicGameState implements InputListener {
	public static final int ID = 1;
	private static final TrueTypeFont FONT_PAUSE = new TrueTypeFont(new Font("Lucida Console", Font.BOLD, 32), true);
	
	private AssetManager assets;
	private long time, accu, consoleTimer;
	public long getTime() { return time; }
	
	private Console console;
	private HUD hud;
	private ConcurrentHashMap<String, Entity> entities;
	public Entity getEntity(String key) { return entities.get(key); }
	public void addEntity(String key, Entity e) { entities.put(key, e); }
	
	private boolean gameStarted, paused, consoleOpen;
	public boolean isConsoleOpen() { return consoleOpen; }
	
	@Override
	public void init(GameContainer gc, StateBasedGame game) throws SlickException {
		assets = AssetManager.getManager();
		
		loadFonts();
		loadImages();
		loadAnimations(); // has to come after loadImages
		loadSounds();
		loadMusic();
		
		gc.setMouseCursor(assets.getImage("GZS_Crosshair"), 16, 16);
		
		Globals.player = new Player();
		entities = new ConcurrentHashMap<String, Entity>();
		
		reset(gc);
	}

	@Override
	public void update(GameContainer gc, StateBasedGame game, int delta) throws SlickException {
		accu = Math.min((accu + delta), (Globals.STEP_TIME * Globals.MAX_STEPS));
		
		while(accu >= Globals.STEP_TIME) {
			if(!paused && !consoleOpen) {
				time += (long)Globals.STEP_TIME; // Don't want to update time while paused; otherwise, game objects and events could despawn / occur while paused.
				
				Player player = Globals.player;
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
				
				if(!player.isAlive()) {
					// If the player has died, transition state.
					Globals.resetInputs();
					Globals.gameOver = true;
					game.enterState(GameOverState.ID, 
									new FadeOutTransition(), 
									new FadeInTransition());
				}
				
				if(Globals.released.contains(Input.KEY_T)) {
					// Open the training screen.
					Globals.resetInputs();
					game.enterState(TrainState.ID,
									new FadeOutTransition(),
									new FadeInTransition());
				} else if(Globals.released.contains(Input.KEY_B)) {
					// Open the weapon shopping screen.
					Globals.resetInputs();
					game.enterState(ShopState.ID,
									new FadeOutTransition(),
									new FadeInTransition());
				}
				
				MusicPlayer.getInstance().update();
				hud.update(player, time);
			} else if(consoleOpen) {
				consoleTimer += (long)delta;
				console.update(this, consoleTimer, Globals.STEP_TIME);
			}
			
			Globals.released.clear();
			accu -= Globals.STEP_TIME;
		}
	}
	
	@Override
	public void render(GameContainer gc, StateBasedGame game, Graphics g) throws SlickException {
		Player player = Globals.player;
		
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
		
		/*
		// Draw the "shadow layer".
		g.clearAlphaMap();
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		g.setDrawMode(Graphics.MODE_ALPHA_MAP);
		
		g.setColor(new Color(1.0f, 1.0f, 1.0f, 0.85f));
		g.fillRect(0.0f, 0.0f, Globals.WIDTH, Globals.HEIGHT);

		AssetManager.getManager().getImage("GZS_LightAlphaMap").drawCentered(Globals.player.getPosition().x, Globals.player.getPosition().y);
		
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		g.setDrawMode(Graphics.MODE_ALPHA_BLEND);
		
		g.setColor(Color.black);
		g.fillRect(0.0f, 0.0f, Globals.WIDTH, Globals.HEIGHT);
		
		g.setDrawMode(Graphics.MODE_NORMAL);*/
		
		hud.render(g, player, time);
		
		if(paused) {
			g.setColor(Color.white);
			int w = GameState.FONT_PAUSE.getWidth("Paused");
			int h = GameState.FONT_PAUSE.getHeight();
			FontUtils.drawCenter(GameState.FONT_PAUSE, "Paused", 
								 ((Globals.WIDTH / 2) - (w / 2)), ((Globals.HEIGHT / 2) - (h / 2)), w);
		}
		
		if(consoleOpen) console.render(g, time);
	}
	
	public void reset(GameContainer gc) throws SlickException{
		time = 0L;
		accu = 0L;
		consoleTimer = 0L;
		
		Globals.player.reset();
		entities.clear();
		entities.put("enemyController", new EnemyController());
		
		gameStarted = false;
		paused = false;
		consoleOpen = false;
		console = new Console(this, gc);
		
		hud = new HUD();
	}
	
	private void loadFonts() throws SlickException {
		String [] assetList = new String [] {
				"fonts/PressStart2P-Regular.ttf"
			};
			
			for(String asset : assetList) {
				String key = asset.substring((asset.indexOf('/') + 1), 
											  asset.lastIndexOf('.'));
				assets.addFont((key + "_small"), asset, 10, false, false);
				assets.addFont(key, asset, 16, false, false);
				assets.addFont((key + "_large"), asset, 32, false, false);
			}
	}
	
	private void loadImages() throws SlickException {
		String [] assetList = new String [] {
			// Primary Images
			"images/GZS_Background6.png",
			"images/GZS_Player.png",
			"images/GZS_Crosshair.png",
			// Item Images
			"images/GZS_Health.png",
			"images/GZS_Ammo.png",
			// Status Images
			"images/GZS_Invulnerability.png",
			"images/GZS_UnlimitedAmmo.png",
			"images/GZS_SpeedUp.png",
			"images/GZS_PoisonIcon.png",
			//Enemy Images
			"images/GZS_Zumby2.png",
			"images/GZS_Rotdog2.png",
			"images/GZS_Upchuck2.png",
			"images/GZS_Gasbag2.png",
			"images/GZS_BigMama2.png",
			"images/GZS_TinyZumby.png",
			// Projectile Images
			"images/GZS_Arrow.png",
			"images/GZS_FireParticle.png",
			"images/GZS_AcidParticle2.png",
			"images/GZS_HandEggParticle.png",
			"images/GZS_Claymore.png",
			"images/GZS_LaserTerminal.png",
			"images/GZS_TurretPieces.png",
			// Weapon Images
			"images/GZS_MuzzleFlash.png",
			"images/GZS_Popgun.png",
			"images/GZS_RTPS.png",
			"images/GZS_Boomstick.png",
			"images/GZS_Bow.png",
			"images/GZS_Flammenwerfer.png",
			"images/GZS_HandEgg.png",
			"images/GZS_ClaymoreWeapon.png",
			"images/GZS_LaserWire.png",
			"images/GZS_Turret.png",
			"images/GZS_BigRedButton.png",
			// Effect Images
			"images/GZS_LightAlphaMap.png",
			"images/GZS_Explosion.png",
			"images/GZS_PoisonExplosion.png",
			"images/GZS_BloodExplosion.png"
		};
		
		for(String asset : assetList) {
			Image image = new Image(asset);
			String key = asset.substring((asset.indexOf('/') + 1), 
										  asset.lastIndexOf('.'));
			assets.addImage(key, image);
		}
	}
	
	private void loadAnimations() throws SlickException {
		Animation mf = new Animation("GZS_MuzzleFlash", 4, 8, 4, 25L, 100L, 100L);
		assets.addAnimation("GZS_MuzzleFlash", mf);
		
		Animation exp = new Animation("GZS_Explosion", 128, 128, 8, 125L, 1000L, 1000L);
		assets.addAnimation("GZS_Explosion", exp);
		
		Animation pc = new Animation("GZS_PoisonExplosion", 128, 128, 8, 125L, 1000L, 1000L);
		assets.addAnimation("GZS_PoisonExplosion", pc);
		
		Animation be = new Animation("GZS_BloodExplosion", 128, 128, 8, 125L, 1000L, 1000L);
		assets.addAnimation("GZS_BloodExplosion", be);
	}
	
	private void loadSounds() throws SlickException {
		String [] assetList = new String [] {
			"sounds/shoot4.wav",
			"sounds/shoot3.wav",
			"sounds/throw2.wav",
			"sounds/landmine_armed.wav",
			"sounds/shotgun1.wav",
			"sounds/flamethrower2.wav",
			"sounds/explosion2.wav",
			"sounds/poison_cloud.wav",
			"sounds/buy_ammo2.wav",
			"sounds/powerup2.wav"
		};
		
		for(String asset : assetList) {
			Sound sound = new Sound(asset);
			String key = asset.substring((asset.indexOf('/') + 1), 
										  asset.lastIndexOf('.'));
			assets.addSound(key, sound);
		}
	}
	
	private void loadMusic() throws SlickException {
		// FIXME: Do this in a separate thread after loading first song so it doesn't take forever to load?
		// Load the soundtrack.
		for(int i = 1; i <= 15; i++) {
			String songName = String.format("soundtrack_%02d", i);
			Music m = new Music("music/" + songName + ".ogg");
			MusicPlayer.getInstance().addSong(songName, m);
		}
	}
	
	@Override
	public void mouseMoved(int oldx, int oldy, int newx, int newy) {
		Globals.mouse.setPosition(newx, newy);
	}
	
	@Override
	public void mousePressed(int button, int x, int y) {
		if(consoleOpen) console.mousePressed(this, button, x, y);
		Globals.mouse.setMouseDown(true);
	}
	
	@Override
	public void mouseReleased(int button, int x, int y) {
		if(button == 0) Globals.mouse.setMouseDown(false);
	}
	
	@Override
	public void mouseDragged(int oldx, int oldy, int newx, int newy) {
		Globals.mouse.setPosition(newx, newy);
	}
	
	@Override
	public void keyPressed(int key, char c) {
		if(consoleOpen) {
			console.keyPressed(key, c);
		} else {
			Globals.inputs.add(key);
		}
	}
	
	@Override
	public void keyReleased(int key, char c) {
		if(key == Input.KEY_GRAVE) {
			console.setPauseTime(time);
			consoleOpen = !consoleOpen;
		}
		else if((key == Input.KEY_P) && !consoleOpen) {
			if(!paused) MusicPlayer.getInstance().pause();
			else MusicPlayer.getInstance().resume();
			paused = !paused;
		}
		else {
			if(consoleOpen) {
				console.keyReleased(key, c);
			} else {
				Globals.inputs.remove(key);
				Globals.released.add(key);
			}
		}
	}
	
	@Override
	public void mouseWheelMoved(int change) {
		Player player = Globals.player;
		if(player.activeWeapons() > 1) {
			player.weaponRotate((change > 0)?1:-1);
			hud.queueWeaponCycle();
		}
	}
	
	@Override
	public void enter(GameContainer gc, StateBasedGame game) throws SlickException {
		if(!gameStarted) {
			MusicPlayer.getInstance().playSong("soundtrack_01");
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
