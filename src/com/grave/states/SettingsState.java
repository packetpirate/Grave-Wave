package com.grave.states;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.InputListener;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import com.grave.AssetManager;
import com.grave.Controls;
import com.grave.Globals;
import com.grave.MusicPlayer;
import com.grave.gfx.ui.MenuButton;
import com.grave.misc.MouseInfo;
import com.grave.misc.Pair;
import com.grave.states.settings.AudioSettingsState;
import com.grave.states.settings.ControlSettingsState;
import com.grave.states.settings.DisplaySettingsState;
import com.grave.states.settings.GameSettingsState;

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
			if(mouse.isLeftDown()) game.enterState(GameSettingsState.ID, new FadeOutTransition(), new FadeInTransition());
		} else gameSettings.mouseExit();

		if(audioSettings.inBounds(mouse.getPosition().x, mouse.getPosition().y)) {
			audioSettings.mouseEnter();
			if(mouse.isLeftDown()) game.enterState(AudioSettingsState.ID, new FadeOutTransition(), new FadeInTransition());
		} else audioSettings.mouseExit();

		if(displaySettings.inBounds(mouse.getPosition().x, mouse.getPosition().y)) {
			displaySettings.mouseEnter();
			if(mouse.isLeftDown()) game.enterState(DisplaySettingsState.ID, new FadeOutTransition(), new FadeInTransition());
		} else displaySettings.mouseExit();

		if(controlSettings.inBounds(mouse.getPosition().x, mouse.getPosition().y)) {
			controlSettings.mouseEnter();
			if(mouse.isLeftDown()) game.enterState(ControlSettingsState.ID, new FadeOutTransition(), new FadeInTransition());
		} else controlSettings.mouseExit();

		if(backButton.inBounds(mouse.getPosition().x, mouse.getPosition().y)) {
			backButton.mouseEnter();
			if(mouse.isLeftDown()) game.enterState(MenuState.ID, new FadeOutTransition(), new FadeInTransition());
		} else backButton.mouseExit();

		MusicPlayer.getInstance().update(true);
	}

	@Override
	public void render(GameContainer gc, StateBasedGame game, Graphics g) throws SlickException {
		g.resetTransform();
		g.clear();

		gameSettings.render(null, g, 0L);
		audioSettings.render(null, g, 0L);
		displaySettings.render(null, g, 0L);
		controlSettings.render(null, g, 0L);
		backButton.render(null, g, 0L);

		g.setColor(Color.white);
		g.setFont(AssetManager.getManager().getFont("PressStart2P-Regular_small"));
		float vWidth = g.getFont().getWidth(Globals.VERSION);
		g.drawString(Globals.VERSION, (Globals.WIDTH - vWidth - 20.0f), 20.0f);
	}

	@Override
	public void mouseMoved(int oldx, int oldy, int newx, int newy) {
		Controls.getInstance().getMouse().setPosition(newx, newy);
	}

	@Override
	public void mousePressed(int button, int x, int y) {
		if(button == Input.MOUSE_LEFT_BUTTON) Controls.getInstance().getMouse().setLeftDown(true);
	}

	@Override
	public void mouseReleased(int button, int x, int y) {
		if(button == Input.MOUSE_LEFT_BUTTON) Controls.getInstance().getMouse().setLeftDown(false);
	}

	@Override
	public int getID() {
		return SettingsState.ID;
	}
}
