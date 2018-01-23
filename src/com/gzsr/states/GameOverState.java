package com.gzsr.states;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.InputListener;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import com.gzsr.AssetManager;
import com.gzsr.Globals;
import com.gzsr.gfx.ui.MenuButton;
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
		
		UnicodeFont uni = assets.getFont("manaspc");
		menuButton = new MenuButton(new Pair<Float>((float)((Globals.WIDTH / 2) - (uni.getWidth("Main Menu") / 2)), (Globals.HEIGHT - 200.0f)), "Main Menu");
		exitButton = new MenuButton(new Pair<Float>((float)((Globals.WIDTH / 2) - (uni.getWidth("Give Up") / 2)), (Globals.HEIGHT - 150.0f)), "Give Up");
		
		time = 0L;
	}

	@Override
	public void update(GameContainer gc, StateBasedGame game, int delta) throws SlickException {
		time += (long)delta;
		
		if(menuButton.inBounds(Globals.mouse.getPosition().x, Globals.mouse.getPosition().y)) {
			menuButton.mouseEnter();
			if(Globals.mouse.isMouseDown()) {
				Globals.resetInputs();
				game.enterState(MenuState.ID);
			}
		} else menuButton.mouseExit();
		
		if(exitButton.inBounds(Globals.mouse.getPosition().x, Globals.mouse.getPosition().y)) {
			exitButton.mouseEnter();
			if(Globals.mouse.isMouseDown()) gc.exit();
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
		
		menuButton.render(g);
		exitButton.render(g);
	}
	
	@Override
	public void mouseMoved(int oldx, int oldy, int newx, int newy) {
		Globals.mouse.setPosition(newx, newy);
	}
	
	@Override
	public void mousePressed(int button, int x, int y) {
		if(button == 0) Globals.mouse.setMouseDown(true);
	}
	
	@Override
	public void mouseReleased(int button, int x, int y) {
		if(button == 0) Globals.mouse.setMouseDown(false);
	}

	@Override
	public int getID() {
		return GameOverState.ID;
	}
}
