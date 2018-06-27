package com.gzsr.states;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.InputListener;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import com.gzsr.AssetManager;
import com.gzsr.Controls;
import com.gzsr.Globals;
import com.gzsr.MusicPlayer;
import com.gzsr.gfx.ui.MenuButton;
import com.gzsr.misc.MouseInfo;
import com.gzsr.misc.Pair;
import com.gzsr.states.settings.AudioSettingsState;
import com.gzsr.states.settings.ControlSettingsState;
import com.gzsr.states.settings.DisplaySettingsState;
import com.gzsr.states.settings.GameSettingsState;

public class SettingsState extends BasicGameState implements InputListener {
	public static final int ID = 8;

	private AssetManager assets = null;
	
	private MenuButton gameSettings;
	private MenuButton audioSettings;
	private MenuButton displaySettings;
	private MenuButton controlSettings;
	private MenuButton backButton;
	
	@Override
	public void init(GameContainer gc, StateBasedGame game) throws SlickException {
		assets = AssetManager.getManager();
		
		gameSettings = new MenuButton(new Pair<Float>(50.0f, (Globals.HEIGHT - 260.0f)), "Game");
		audioSettings = new MenuButton(new Pair<Float>(50.0f, (Globals.HEIGHT - 200.0f)), "Audio");
		displaySettings = new MenuButton(new Pair<Float>(50.0f, (Globals.HEIGHT - 140.0f)), "Display");
		controlSettings = new MenuButton(new Pair<Float>(50.0f, (Globals.HEIGHT - 80.0f)), "Controls");
		
		backButton = new MenuButton(new Pair<Float>((Globals.WIDTH - 200.0f), (Globals.HEIGHT - 80.0f)), "Back");
	}
	
	@Override
	public void enter(GameContainer gc, StateBasedGame game) throws SlickException {
		Controls.getInstance().resetAll();
	}

	@Override
	public void update(GameContainer gc, StateBasedGame game, int delta) throws SlickException {
		MouseInfo mouse = Controls.getInstance().getMouse();
		
		if(gameSettings.inBounds(mouse.getPosition().x, mouse.getPosition().y)) {
			gameSettings.mouseEnter();
			if(mouse.isMouseDown()) game.enterState(GameSettingsState.ID, new FadeOutTransition(), new FadeInTransition());
		} else gameSettings.mouseExit();
		
		if(audioSettings.inBounds(mouse.getPosition().x, mouse.getPosition().y)) {
			audioSettings.mouseEnter();
			if(mouse.isMouseDown()) game.enterState(AudioSettingsState.ID, new FadeOutTransition(), new FadeInTransition());
		} else audioSettings.mouseExit();
		
		if(displaySettings.inBounds(mouse.getPosition().x, mouse.getPosition().y)) {
			displaySettings.mouseEnter();
			if(mouse.isMouseDown()) game.enterState(DisplaySettingsState.ID, new FadeOutTransition(), new FadeInTransition());
		} else displaySettings.mouseExit();
		
		if(controlSettings.inBounds(mouse.getPosition().x, mouse.getPosition().y)) {
			controlSettings.mouseEnter();
			if(mouse.isMouseDown()) game.enterState(ControlSettingsState.ID, new FadeOutTransition(), new FadeInTransition());
		} else controlSettings.mouseExit();
		
		if(backButton.inBounds(mouse.getPosition().x, mouse.getPosition().y)) {
			backButton.mouseEnter();
			if(mouse.isMouseDown()) game.enterState(MenuState.ID, new FadeOutTransition(), new FadeInTransition());
		} else backButton.mouseExit();
		
		MusicPlayer.getInstance().update();
	}
	
	@Override
	public void render(GameContainer gc, StateBasedGame game, Graphics g) throws SlickException {
		Image background = assets.getImage("GZS_Background02");
		
		g.resetTransform();
		g.clear();
		
		if(background != null) g.drawImage(background, 0.0f, 0.0f, Globals.WIDTH, Globals.HEIGHT, 0.0f, 0.0f, background.getWidth(), background.getHeight());
		
		gameSettings.render(g, 0L);
		audioSettings.render(g, 0L);
		displaySettings.render(g, 0L);
		controlSettings.render(g, 0L);
		backButton.render(g, 0L);
	}
	
	@Override
	public void mouseMoved(int oldx, int oldy, int newx, int newy) {
		Controls.getInstance().getMouse().setPosition(newx, newy);
	}
	
	@Override
	public void mousePressed(int button, int x, int y) {
		if(button == 0) Controls.getInstance().getMouse().setMouseDown(true);
	}
	
	@Override
	public void mouseReleased(int button, int x, int y) {
		if(button == 0) Controls.getInstance().getMouse().setMouseDown(false);
	}

	@Override
	public int getID() {
		return SettingsState.ID;
	}
}
