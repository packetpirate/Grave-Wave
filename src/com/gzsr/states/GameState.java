package com.gzsr.states;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.MouseListener;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import com.gzsr.AssetManager;
import com.gzsr.Game;
import com.gzsr.Globals;
import com.gzsr.entities.Entity;
import com.gzsr.entities.Player;
import com.gzsr.entities.enemies.EnemyController;
import com.gzsr.gfx.HUD;
import com.gzsr.objects.items.Item;

public class GameState extends BasicGameState implements MouseListener {
	public static final int ID = 1;
	
	private AssetManager assets;
	private long time;
	
	private HUD hud;
	private Map<String, Entity> entities;
	
	@Override
	public void init(GameContainer gc, StateBasedGame game) throws SlickException {
		assets = AssetManager.getManager();
		
		loadImages();
		loadSounds();
		
		time = 0L;
		
		gc.setMouseCursor(assets.getImage("GZS_Crosshair"), 16, 16);
		hud = new HUD();
		
		Globals.player = new Player();
		
		entities = new HashMap<String, Entity>();
		entities.put("enemyController", new EnemyController());
	}

	@Override
	public void update(GameContainer gc, StateBasedGame game, int delta) throws SlickException {
		time += (long)delta;
		Game.handleInput(gc);
		
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
		
		// If the player has died, transition state.
		if(!player.isAlive()) {
			game.enterState(GameOverState.ID, 
							new FadeOutTransition(), 
							new FadeInTransition());
		}
		
		if(Globals.inputs.contains("T")) {
			// Open the training screen.
			game.enterState(TrainState.ID,
							new FadeOutTransition(),
							new FadeInTransition());
		} else if(Globals.inputs.contains("B")) {
			game.enterState(ShopState.ID,
							new FadeOutTransition(),
							new FadeInTransition());
		}
		
		hud.update(player, time);
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
	}
	
	private void loadImages() throws SlickException {
		String [] assetList = new String [] {
			// Primary Images
			"images/GZS_Background6.png",
			"images/GZS_Player.png",
			"images/GZS_Crosshair.png",
			// Powerup Images
			"images/GZS_Invulnerability.png",
			"images/GZS_SpeedUp.png",
			//Enemy Images
			"images/GZS_Zumby2.png",
			"images/GZS_Rotdog2.png",
			// Weapon Images
			"images/GZS_Popgun.png",
			"images/GZS_RTPS.png",
			"images/GZS_Boomstick.png"
		};
		
		for(String asset : assetList) {
			Image image = new Image(asset);
			String key = asset.substring((asset.indexOf('/') + 1), 
										  asset.lastIndexOf('.'));
			assets.addImage(key, image);
		}
	}
	
	private void loadSounds() throws SlickException {
		String [] assetList = new String [] {
			"sounds/shoot4.wav",
			"sounds/shoot3.wav",
			"sounds/shotgun1.wav",
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
