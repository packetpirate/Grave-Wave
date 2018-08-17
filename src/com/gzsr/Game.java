package com.gzsr;

import java.io.File;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import com.gzsr.states.LoadingState;

public class Game extends StateBasedGame {
	public Game() {
		super("Generic Zombie Shooter - Redux");
	}

	public static void main(String[] args) {
		System.setProperty("java.library.path", "libs");
		System.setProperty("org.lwjgl.librarypath", new File("libs/natives").getAbsolutePath());
		
		try {
			Globals.app = new AppGameContainer(new Game());
			
			Globals.app.setDisplayMode(Globals.WIDTH, Globals.HEIGHT, false);
			Globals.app.setShowFPS(false);
			Globals.app.start();
		} catch(SlickException se) {
			se.printStackTrace();
		}
	}

	@Override
	public void initStatesList(GameContainer gc) throws SlickException {
		addState(new LoadingState());
	}
}
