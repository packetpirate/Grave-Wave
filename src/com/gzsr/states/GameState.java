package com.gzsr.states;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import com.gzsr.AssetManager;
import com.gzsr.Globals;

public class GameState extends BasicGameState {
	public static final int ID = 1;
	
	private AssetManager assets;
	private long time;
	
	@Override
	public void init(GameContainer gc, StateBasedGame game) throws SlickException {
		assets = AssetManager.getManager();
		
		{ // Load Images
			String [] assetList = new String [] {
				"images/GZS_Player.png"
			};
			
			for(String asset : assetList) {
				Image image = new Image(asset);
				String key = asset.substring((asset.indexOf('/') + 1), 
											  asset.lastIndexOf('.'));
				assets.addImage(key, image);
			}
		} // End image load.
		
		time = 0L;
	}

	@Override
	public void update(GameContainer gc, StateBasedGame game, int delta) throws SlickException {
		time += (long)delta;
		
		Input input = gc.getInput();
		Globals.mouse.setMouseDown(input.isMouseButtonDown(0));
		Globals.mouse.setPosition(input.getMouseX(), input.getMouseY());
	}
	
	@Override
	public void render(GameContainer gc, StateBasedGame game, Graphics g) throws SlickException {
		g.drawImage(assets.getImage("GZS_Player"), 50, 50);
	}

	@Override
	public int getID() {
		return GameState.ID;
	}
}
