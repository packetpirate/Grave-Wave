package com.gzsr.states;

import java.awt.Font;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import com.gzsr.AssetManager;
import com.gzsr.Game;
import com.gzsr.Globals;
import com.gzsr.entities.Player;

public class TrainState extends BasicGameState {
	public static final int ID = 3;
	private static final TrueTypeFont FONT_HEADER = new TrueTypeFont(new Font("Lucida Console", Font.BOLD, 32), true);
	private static final TrueTypeFont FONT_NORMAL = new TrueTypeFont(new Font("Lucida Console", Font.PLAIN, 12), true);
	
	private AssetManager assets;
	
	@Override
	public void init(GameContainer gc, StateBasedGame game) throws SlickException {
		assets = AssetManager.getManager();
	}
	
	@Override
	public void update(GameContainer gc, StateBasedGame game, int delta) throws SlickException {
		Game.handleInput(gc);
		
		Player player = Globals.player;
		
		if(Globals.inputs.contains("T")) {
			game.enterState(GameState.ID, 
							new FadeOutTransition(),
							new FadeInTransition());
		}
	}

	@Override
	public void render(GameContainer gc, StateBasedGame game, Graphics g) throws SlickException {
		g.resetTransform();
		g.clear();
		
		g.setColor(Color.darkGray);
		g.fillRect(0.0f, 0.0f, Globals.WIDTH, Globals.HEIGHT);
		
		Player player = Globals.player;
		
		// Draw the header and footer.
		g.setColor(Color.white);
		g.drawLine(10.0f, 36.0f, (Globals.WIDTH - 10.0f), 36.0f);
		g.drawLine(10.0f, (Globals.HEIGHT - 36.0f), (Globals.WIDTH - 10.0f), (Globals.HEIGHT - 36.0f));
		g.setFont(TrainState.FONT_HEADER);
		g.drawString("Training", 30.0f, 20.0f);
		
		
	}

	@Override
	public int getID() {
		return TrainState.ID;
	}
}
