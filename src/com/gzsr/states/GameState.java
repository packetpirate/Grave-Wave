package com.gzsr.states;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.InputListener;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import com.gzsr.AssetManager;
import com.gzsr.Globals;
import com.gzsr.entities.Entity;
import com.gzsr.entities.Player;
import com.gzsr.entities.enemies.EnemyController;
import com.gzsr.gfx.Animation;
import com.gzsr.gfx.HUD;
import com.gzsr.gfx.ui.Console;
import com.gzsr.objects.items.Item;

public class GameState extends BasicGameState implements InputListener {
	public static final int ID = 1;
	
	private AssetManager assets;
	private long time, consoleTimer;
	public long getTime() { return time; }
	
	private Console console;
	private HUD hud;
	private Map<String, Entity> entities;
	public Entity getEntity(String key) { return entities.get(key); }
	public void addEntity(String key, Entity e) { entities.put(key, e); }
	
	private boolean paused, consoleOpen;
	public boolean isConsoleOpen() { return consoleOpen; }
	
	@Override
	public void init(GameContainer gc, StateBasedGame game) throws SlickException {
		assets = AssetManager.getManager();
		
		loadImages();
		loadAnimations(); // has to come after loadImages
		loadSounds();
		
		time = 0L;
		consoleTimer = 0L;
		
		gc.setMouseCursor(assets.getImage("GZS_Crosshair"), 16, 16);
		hud = new HUD();
		
		Globals.player = new Player();
		
		entities = new HashMap<String, Entity>();
		entities.put("enemyController", new EnemyController());
		
		paused = false;
		consoleOpen = false;
		
		console = new Console(this, gc);
	}

	@Override
	public void update(GameContainer gc, StateBasedGame game, int delta) throws SlickException {
		if(!paused && !consoleOpen) {
			time += (long)delta; // Don't want to update time while paused; otherwise, game objects and events could despawn/occur while paused.
			
			Player player = Globals.player;
			player.update(time);
			
			Iterator<Entry<String, Entity>> it = entities.entrySet().iterator();
			while(it.hasNext()) {
				Map.Entry<String, Entity> pair = (Map.Entry<String, Entity>) it.next();
				pair.getValue().update(time);
				if(pair.getValue() instanceof EnemyController) {
					EnemyController ec = (EnemyController)pair.getValue();
					ec.updateEnemies(player, time);
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
			
			hud.update(player, time);
		} else if(consoleOpen) {
			consoleTimer += (long)delta;
			console.update(consoleTimer);
		}
		
		Globals.released.clear();
	}
	
	@Override
	public void render(GameContainer gc, StateBasedGame game, Graphics g) throws SlickException {
		Player player = Globals.player;
		
		g.resetTransform();
		g.clear();
		
		Image background = assets.getImage("GZS_Background6");
		g.drawImage(background, 0.0f, 0.0f, Globals.WIDTH, Globals.HEIGHT, 0.0f, 0.0f, background.getWidth(), background.getHeight());
		
		// Render the player.
		player.render(g, time);

		// Render all entities.
		Iterator<Entry<String, Entity>> it = entities.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry<String, Entity> pair = (Map.Entry<String, Entity>) it.next();
			pair.getValue().render(g, time);
		}
		
		hud.render(g, player, time);
		
		if(consoleOpen) console.render(g, time);
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
			// Powerup Images
			"images/GZS_Invulnerability.png",
			"images/GZS_UnlimitedAmmo.png",
			"images/GZS_SpeedUp.png",
			//Enemy Images
			"images/GZS_Zumby2.png",
			"images/GZS_Rotdog2.png",
			"images/GZS_Upchuck2.png",
			// Projectile Images
			"images/GZS_FireParticle2.png",
			"images/GZS_AcidParticle2.png",
			// Weapon Images
			"images/GZS_MuzzleFlash.png",
			"images/GZS_Popgun.png",
			"images/GZS_RTPS.png",
			"images/GZS_Boomstick.png",
			"images/GZS_Flammenwerfer.png"
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
	}
	
	private void loadSounds() throws SlickException {
		String [] assetList = new String [] {
			"sounds/shoot4.wav",
			"sounds/shoot3.wav",
			"sounds/shotgun1.wav",
			"sounds/flamethrower2.wav",
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
	
	@Override
	public void mouseMoved(int oldx, int oldy, int newx, int newy) {
		Globals.mouse.setPosition(newx, newy);
	}
	
	@Override
	public void mousePressed(int button, int x, int y) {
		if(button == 0) {
			if(consoleOpen) console.mousePressed(button, x, y);
			Globals.mouse.setMouseDown(true);
		}
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
		if(key == Input.KEY_GRAVE) consoleOpen = !consoleOpen; 
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
	public int getID() {
		return GameState.ID;
	}
}
