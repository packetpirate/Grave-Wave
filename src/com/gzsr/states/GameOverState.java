package com.gzsr.states;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import com.gzsr.AssetManager;
import com.gzsr.Globals;

public class GameOverState extends BasicGameState {
	public static final int ID = 4;
	
	private AssetManager assets;
	
	private long time;
	
	@Override
	public void init(GameContainer gc, StateBasedGame game) throws SlickException {
		assets = AssetManager.getManager();
		assets.addImage("GZS_DeathScreen", new Image("images/GZS_DeathScreen.png"));
		
		time = 0L;
	}

	@Override
	public void update(GameContainer gc, StateBasedGame game, int delta) throws SlickException {
		time += (long)delta;
	}
	
	@Override
	public void render(GameContainer gc, StateBasedGame game, Graphics g) throws SlickException {
		Image deathScreen = assets.getImage("GZS_DeathScreen");
		if(deathScreen != null) {
			g.drawImage(deathScreen, 
						0.0f, 0.0f, Globals.WIDTH, Globals.HEIGHT, 
						0.0f, 0.0f, deathScreen.getWidth(), deathScreen.getHeight());
		}
	}

	@Override
	public int getID() {
		return GameOverState.ID;
	}
}
