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

public class MenuState extends BasicGameState implements InputListener {
	public static final int ID = 0;
	
	private AssetManager assets = null;
	
	private MenuButton gameStart;
	private MenuButton settings;
	private MenuButton credits;
	private MenuButton exit;
	
	@Override
	public void init(GameContainer gc, StateBasedGame game) throws SlickException {
		assets = AssetManager.getManager();
		
		gameStart = new MenuButton(new Pair<Float>(50.0f, (Globals.HEIGHT - 260.0f)), "Start Game");
		settings = new MenuButton(new Pair<Float>(50.0f, (Globals.HEIGHT - 200.0f)), "Settings");
		credits = new MenuButton(new Pair<Float>(50.0f, (Globals.HEIGHT - 140.0f)), "Credits");
		exit = new MenuButton(new Pair<Float>(50.0f, (Globals.HEIGHT - 80.0f)), "Exit");
	}
	
	@Override
	public void enter(GameContainer gc, StateBasedGame game) throws SlickException {
		Controls.getInstance().resetAll();
		MusicPlayer.getInstance().reset();
		MusicPlayer.getInstance().nextSong();
	}
	
	@Override
	public void update(GameContainer gc, StateBasedGame game, int delta) throws SlickException {
		MouseInfo mouse = Controls.getInstance().getMouse();
		
		if(gameStart.inBounds(mouse.getPosition().x, mouse.getPosition().y)) {
			gameStart.mouseEnter();
			if(mouse.isMouseDown()) {
				Globals.resetEntityNum();
				Controls.getInstance().resetAll();
				game.enterState(GameState.ID, new FadeOutTransition(), new FadeInTransition()); 
			}
		} else gameStart.mouseExit();
		
		if(settings.inBounds(mouse.getPosition().x, mouse.getPosition().y)) {
			settings.mouseEnter();
			if(mouse.isMouseDown()) game.enterState(SettingsState.ID, new FadeOutTransition(), new FadeInTransition());
		} else settings.mouseExit();
		
		if(credits.inBounds(mouse.getPosition().x, mouse.getPosition().y)) credits.mouseEnter();
		else credits.mouseExit();
		
		if(exit.inBounds(mouse.getPosition().x, mouse.getPosition().y)) {
			exit.mouseEnter();
			if(mouse.isMouseDown()) gc.exit();
		} else exit.mouseExit();
		
		MusicPlayer.getInstance().update();
	}
	
	@Override
	public void render(GameContainer gc, StateBasedGame game, Graphics g) throws SlickException {
		Image background = assets.getImage("GZS_Background02");
		
		g.resetTransform();
		g.clear();
		
		if(background != null) g.drawImage(background, 0.0f, 0.0f, Globals.WIDTH, Globals.HEIGHT, 0.0f, 0.0f, background.getWidth(), background.getHeight());
		
		gameStart.render(g, 0L);
		settings.render(g, 0L);
		credits.render(g, 0L);
		exit.render(g, 0L);
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
		return MenuState.ID;
	}
}
