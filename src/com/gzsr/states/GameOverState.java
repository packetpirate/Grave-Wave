package com.gzsr.states;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.InputListener;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import com.gzsr.AssetManager;
import com.gzsr.Controls;
import com.gzsr.Globals;
import com.gzsr.gfx.ui.MenuButton;
import com.gzsr.misc.MouseInfo;
import com.gzsr.misc.Pair;

public class GameOverState extends BasicGameState implements InputListener {
	public static final int ID = 4;
	
	private AssetManager assets;
	
	private MenuButton menuButton;
	private MenuButton exitButton;
	
	private long time;
	
	@Override
	public void init(GameContainer gc, StateBasedGame game) throws SlickException {
		assets = AssetManager.getManager();
		
		UnicodeFont uni = AssetManager.getManager().getFont("PressStart2P-Regular_large");
		menuButton = new MenuButton(new Pair<Float>((float)((Globals.WIDTH / 2) - (uni.getWidth("Main Menu") / 2)), (Globals.HEIGHT - 200.0f)), "Main Menu");
		exitButton = new MenuButton(new Pair<Float>((float)((Globals.WIDTH / 2) - (uni.getWidth("Give Up") / 2)), (Globals.HEIGHT - 150.0f)), "Give Up");
		
		time = 0L;
	}

	@Override
	public void update(GameContainer gc, StateBasedGame game, int delta) throws SlickException {
		time += (long)delta;
		
		MouseInfo mouse = Controls.getInstance().getMouse();
		if(menuButton.inBounds(mouse.getPosition().x, mouse.getPosition().y)) {
			menuButton.mouseEnter();
			if(mouse.isLeftDown()) {
				Controls.getInstance().resetAll();
				game.enterState(MenuState.ID);
			}
		} else menuButton.mouseExit();
		
		if(exitButton.inBounds(mouse.getPosition().x, mouse.getPosition().y)) {
			exitButton.mouseEnter();
			if(mouse.isLeftDown()) gc.exit();
		} else exitButton.mouseExit();
	}
	
	@Override
	public void render(GameContainer gc, StateBasedGame game, Graphics g) throws SlickException {
		Image deathScreen = assets.getImage("GZS_DeathScreen");
		if(deathScreen != null) {
			g.drawImage(deathScreen, 
						0.0f, 0.0f, Globals.WIDTH, Globals.HEIGHT, 
						0.0f, 0.0f, deathScreen.getWidth(), deathScreen.getHeight());
		}
		
		menuButton.render(g, 0L);
		exitButton.render(g, 0L);
	}
	
	@Override
	public void enter(GameContainer gc, StateBasedGame game) throws SlickException {
		// TODO: Stop currently playing music and player game over track.
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
		return GameOverState.ID;
	}
}
