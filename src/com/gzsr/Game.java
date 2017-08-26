package com.gzsr;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class Game extends BasicGame {
	private Image player;
	
	public Game() {
		super("Generic Zombie Shooter: Remastered");
	}

	public static void main(String[] args) {
		try {
			AppGameContainer app = new AppGameContainer(new Game());
			app.setDisplayMode(Globals.WIDTH, Globals.HEIGHT, false);
			app.start();
		} catch(SlickException se) {
			se.printStackTrace();
		}
	}
	
	@Override
	public void init(GameContainer gc) throws SlickException {
		player = new Image("images/GZS_Player.png");
	}
	
	@Override
	public void update(GameContainer gc, int delta) throws SlickException {
		
	}

	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException {
		g.drawImage(player, 200, 200);
	}
}
