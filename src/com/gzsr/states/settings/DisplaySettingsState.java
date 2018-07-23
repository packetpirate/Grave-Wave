package com.gzsr.states.settings;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
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
import com.gzsr.states.SettingsState;

public class DisplaySettingsState extends BasicGameState implements InputListener {
	public static final int ID = 11;

	private MenuButton shadowSettings;
	private MenuButton backButton;
	
	@Override
	public void init(GameContainer gc, StateBasedGame game) throws SlickException {
		shadowSettings = new MenuButton(new Pair<Float>(50.0f, (Globals.HEIGHT - 80.0f)), "Shadow Opacity");
		backButton = new MenuButton(new Pair<Float>((Globals.WIDTH - 200.0f), (Globals.HEIGHT - 80.0f)), "Back");
	}
	
	@Override
	public void enter(GameContainer gc, StateBasedGame game) throws SlickException {
		Controls.getInstance().resetAll();
	}

	@Override
	public void update(GameContainer gc, StateBasedGame game, int delta) throws SlickException {
		MouseInfo mouse = Controls.getInstance().getMouse();
		
		if(shadowSettings.inBounds(mouse.getPosition().x, mouse.getPosition().y)) {
			shadowSettings.mouseEnter();
			if(mouse.isLeftDown()) game.enterState(ShadowSettingsState.ID, new FadeOutTransition(), new FadeInTransition());
		} else shadowSettings.mouseExit();
		
		if(backButton.inBounds(mouse.getPosition().x, mouse.getPosition().y)) {
			backButton.mouseEnter();
			if(mouse.isLeftDown()) game.enterState(SettingsState.ID, new FadeOutTransition(), new FadeInTransition());
		} else backButton.mouseExit();
		
		MusicPlayer.getInstance().update(true);
	}
	
	@Override
	public void render(GameContainer gc, StateBasedGame game, Graphics g) throws SlickException {
		Image background = AssetManager.getManager().getImage("GZS_Background02");
		
		g.resetTransform();
		g.clear();
		
		if(background != null) g.drawImage(background, 0.0f, 0.0f, Globals.WIDTH, Globals.HEIGHT, 0.0f, 0.0f, background.getWidth(), background.getHeight());
		
		shadowSettings.render(g, 0L);
		backButton.render(g, 0L);
		
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
		return DisplaySettingsState.ID;
	}

}
