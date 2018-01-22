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
import com.gzsr.Globals;
import com.gzsr.MusicPlayer;
import com.gzsr.gfx.ui.MenuButton;

public class MenuState extends BasicGameState implements InputListener {
	public static final int ID = 0;
	
	private AssetManager assets = null;
	
	private MenuButton gameStart;
	private MenuButton credits;
	private MenuButton exit;
	
	@Override
	public void init(GameContainer gc, StateBasedGame game) throws SlickException {
		assets = AssetManager.getManager();
		
		gameStart = new MenuButton(50.0f, (Globals.HEIGHT - 200.0f), "Start Game");
		credits = new MenuButton(50.0f, (Globals.HEIGHT - 140.0f), "Credits");
		exit = new MenuButton(50.0f, (Globals.HEIGHT - 80.0f), "Exit");
	}
	
	@Override
	public void enter(GameContainer gc, StateBasedGame game) throws SlickException {
		MusicPlayer.getInstance().reset();
		MusicPlayer.getInstance().nextSong();
	}
	
	@Override
	public void update(GameContainer gc, StateBasedGame game, int delta) throws SlickException {
		if(gameStart.contains(Globals.mouse)) {
			gameStart.mouseEnter();
			if(Globals.mouse.isMouseDown()) {
				Globals.resetEntityNum();
				Globals.resetInputs();
				game.enterState(GameState.ID, new FadeOutTransition(), new FadeInTransition()); 
			}
		} else gameStart.mouseExit();
		
		if(credits.contains(Globals.mouse)) credits.mouseEnter();
		else credits.mouseExit();
		
		if(exit.contains(Globals.mouse)) {
			exit.mouseEnter();
			if(Globals.mouse.isMouseDown()) gc.exit();
		}
		else exit.mouseExit();
		
		MusicPlayer.getInstance().update();
	}
	
	@Override
	public void render(GameContainer gc, StateBasedGame game, Graphics g) throws SlickException {
		Image background = assets.getImage("GZS_Background02");
		
		g.resetTransform();
		g.clear();
		
		if(background != null) g.drawImage(background, 0.0f, 0.0f, Globals.WIDTH, Globals.HEIGHT, 0.0f, 0.0f, background.getWidth(), background.getHeight());
		
		gameStart.render(g);
		credits.render(g);
		exit.render(g);
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
		return MenuState.ID;
	}
}
