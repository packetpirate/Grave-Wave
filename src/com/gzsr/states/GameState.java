package com.gzsr.states;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import com.gzsr.AssetManager;
import com.gzsr.Game;
import com.gzsr.entities.Player;

public class GameState extends BasicGameState {
	public static final int ID = 1;
	
	private AssetManager assets;
	private long time;
	
	private Player player;
	
	@Override
	public void init(GameContainer gc, StateBasedGame game) throws SlickException {
		assets = AssetManager.getManager();
		
		loadImages();
		loadSounds();
		
		time = 0L;
		
		player = new Player();
	}

	@Override
	public void update(GameContainer gc, StateBasedGame game, int delta) throws SlickException {
		time += (long)delta;
		Game.handleInput(gc);
		
		player.update(time);
	}
	
	@Override
	public void render(GameContainer gc, StateBasedGame game, Graphics g) throws SlickException {
		g.resetTransform();
		g.clear();
		
		player.render(g, time);
	}
	
	private void loadImages() throws SlickException {
		String [] assetList = new String [] {
			"images/GZS_Player.png"
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
			"sounds/buy_ammo2.wav"
		};
		
		for(String asset : assetList) {
			Sound sound = new Sound(asset);
			String key = asset.substring((asset.indexOf('/') + 1), 
										  asset.lastIndexOf('.'));
			assets.addSound(key, sound);
		}
	}

	@Override
	public int getID() {
		return GameState.ID;
	}
}
