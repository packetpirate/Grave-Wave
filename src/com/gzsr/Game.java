package com.gzsr;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import com.gzsr.states.BlankState;
import com.gzsr.states.CreditsState;
import com.gzsr.states.GameOverState;
import com.gzsr.states.GameState;
import com.gzsr.states.MenuState;
import com.gzsr.states.ShopState;
import com.gzsr.states.TrainState;

public class Game extends StateBasedGame {
	public Game() {
		super("Generic Zombie Shooter: Redux");
	}

	public static void main(String[] args) {
		try {
			AppGameContainer app = new AppGameContainer(new Game());
			app.setDisplayMode(Globals.WIDTH, Globals.HEIGHT, false);
			app.setShowFPS(false);
			app.setTargetFrameRate(60);
			app.start();
		} catch(SlickException se) {
			se.printStackTrace();
		}
	}

	@Override
	public void initStatesList(GameContainer gc) throws SlickException {
		addState(new MenuState());
		addState(new GameState());
		addState(new ShopState());
		addState(new TrainState());
		addState(new GameOverState());
		addState(new CreditsState());
		addState(new BlankState());
		
		enterState(MenuState.ID);
	}
}
