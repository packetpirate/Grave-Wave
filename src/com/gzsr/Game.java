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
		
		enterState(MenuState.ID);
	}
	
	public static void handleInput(GameContainer gc) {
		Input input = gc.getInput();
		
		// Handle mouse input.
		Globals.mouse.setMouseDown(input.isMouseButtonDown(0));
		Globals.mouse.setPosition(input.getMouseX(), input.getMouseY());
		
		// Handle keyboard input.
		int [] codes = new int[] { input.KEY_W, input.KEY_A, input.KEY_S, input.KEY_D, input.KEY_R, input.KEY_T, input.KEY_B,
								   input.KEY_1, input.KEY_2, input.KEY_3, input.KEY_4, input.KEY_5,
								   input.KEY_6, input.KEY_7, input.KEY_8, input.KEY_9, input.KEY_0 };
		String [] inputs = new String[] { "W", "A", "S", "D", "R", "T", "B",
										  "1", "2", "3", "4", "5", 
										  "6", "7", "8", "9", "0" };
		
		for(int i = 0; i < codes.length; i++) {
			String in = inputs[i];
			if(input.isKeyDown(codes[i])) Globals.inputs.add(in);
			else Globals.inputs.remove(in);
		}
	}
}
