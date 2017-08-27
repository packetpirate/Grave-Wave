package com.gzsr;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import com.gzsr.states.*;

public class Game extends StateBasedGame {
	public Game() {
		super("Generic Zombie Shooter: Remastered");
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
		
		// TODO: Temporary so we can skip to game screen. Remove once menu screen implemented.
		enterState(GameState.ID);
	}
	
	public static void handleInput(GameContainer gc) {
		Input input = gc.getInput();
		
		// Handle mouse input.
		Globals.mouse.setMouseDown(input.isMouseButtonDown(0));
		Globals.mouse.setPosition(input.getMouseX(), input.getMouseY());
		
		// Handle keyboard input.
		int [] codes = new int[] { input.KEY_W, input.KEY_A, input.KEY_S, input.KEY_D, input.KEY_R };
		String [] inputs = new String[] { "W", "A", "S", "D", "R" };
		
		for(int i = 0; i < codes.length; i++) {
			String in = inputs[i];
			if(input.isKeyDown(codes[i])) Globals.inputs.add(in);
			else Globals.inputs.remove(in);
		}
	}
}
