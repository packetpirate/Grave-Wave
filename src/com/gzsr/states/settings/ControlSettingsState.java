package com.gzsr.states.settings;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.InputListener;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public class ControlSettingsState extends BasicGameState implements InputListener {
	public static final int ID = 12;

	@Override
	public void init(GameContainer gc, StateBasedGame game) throws SlickException {
	}

	@Override
	public void update(GameContainer gc, StateBasedGame game, int delta) throws SlickException {
	}
	
	@Override
	public void render(GameContainer gc, StateBasedGame game, Graphics g) throws SlickException {
	}

	@Override
	public int getID() {
		return ControlSettingsState.ID;
	}

}
