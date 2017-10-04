package com.gzsr.states;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import com.gzsr.AssetManager;
import com.gzsr.Game;
import com.gzsr.Globals;
import com.gzsr.gfx.ui.MenuButton;

public class MenuState extends BasicGameState {
	public static final int ID = 0;
	
	private AssetManager assets = null;
	
	private MenuButton gameStart;
	private MenuButton credits;
	private MenuButton exit;
	
	@Override
	public void init(GameContainer gc, StateBasedGame game) throws SlickException {
		assets = AssetManager.getManager();
		
		String [] assetList = new String [] {
			"images/GZS_Background02.png"
		};
			
		for(String asset : assetList) {
			Image image = new Image(asset);
			String key = asset.substring((asset.indexOf('/') + 1), 
										  asset.lastIndexOf('.'));
			assets.addImage(key, image);
		}
		
		gameStart = new MenuButton(50, (Globals.HEIGHT - 200), 100, 50, "Start Game");
		credits = new MenuButton(50, (Globals.HEIGHT - 140), 100, 50, "Credits");
		exit = new MenuButton(50, (Globals.HEIGHT - 80), 100, 50, "Exit");
	}
	
	@Override
	public void update(GameContainer gc, StateBasedGame game, int delta) throws SlickException {
		Game.handleInput(gc);
		
		if(gameStart.contains(Globals.mouse)) {
			gameStart.mouseEnter();
			if(Globals.mouse.isMouseDown()) game.enterState(GameState.ID, new FadeOutTransition(), new FadeInTransition()); 
		} else gameStart.mouseExit();
		
		if(credits.contains(Globals.mouse)) credits.mouseEnter();
		else credits.mouseExit();
		
		if(exit.contains(Globals.mouse)) exit.mouseEnter();
		else exit.mouseExit();
	}
	
	@Override
	public void render(GameContainer gc, StateBasedGame game, Graphics g) throws SlickException {
		Image background = assets.getImage("GZS_Background02");
		
		g.resetTransform();
		g.clear();
		
		if(background != null) g.drawImage(background, 0.0f, 0.0f);
		
		gameStart.render(g);
		credits.render(g);
		exit.render(g);
	}
	
	@Override
	public int getID() {
		return MenuState.ID;
	}
}
