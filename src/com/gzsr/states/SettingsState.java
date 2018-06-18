package com.gzsr.states;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.InputListener;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import com.gzsr.AssetManager;

public class SettingsState extends BasicGameState implements InputListener {
	public static final int ID = 0;

	private AssetManager assets = null;
	
	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		assets = AssetManager.getManager();
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		
	}

	@Override
	public int getID() {
		return SettingsState.ID;
	}
}
