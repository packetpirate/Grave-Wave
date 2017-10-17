package com.gzsr;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
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
	
	public static void handleInput(GameContainer gc) {
		Input input = gc.getInput();
		
		// Handle mouse input.
		Globals.mouse.setMouseDown(input.isMouseButtonDown(0));
		Globals.mouse.setPosition(input.getMouseX(), input.getMouseY());
		
		// Clear the previously released inputs.
		Globals.released.clear();
		
		// Handle keyboard input.
		for(int i = 0; i < Globals.keyCodes.length; i++) {
			if(input.isKeyDown(Globals.keyCodes[i])) Globals.inputs.add(Globals.keyCodes[i]);
			else {
				boolean down = Globals.inputs.remove(Globals.keyCodes[i]);
				if(down) Globals.released.add(Globals.keyCodes[i]);
			}
		}
	}
}
