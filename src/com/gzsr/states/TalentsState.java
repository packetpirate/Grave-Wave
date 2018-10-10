package com.gzsr.states;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.InputListener;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import com.gzsr.Globals;
import com.gzsr.gfx.ui.TalentButton;
import com.gzsr.misc.Pair;
import com.gzsr.talents.Talents;

public class TalentsState extends BasicGameState implements InputListener {
	public static final int ID = 3;
	
	// Use (row - 1) as index to find level requirement for a particular talent.
	private static final int [] TIER_LEVEL_REQUIREMENTS = new int[] {1, 5, 10, 15, 20, 25, 30};
	
	private static final float WINDOW_WIDTH = 300.0f;
	private static final float WINDOW_HEIGHT = 468.0f;
	
	private static final Pair<Float> MUNITIONS_WINDOW = new Pair<Float>(((Globals.WIDTH - (WINDOW_WIDTH * 3) - 50.0f) / 2), 200.0f);
	private static final Pair<Float> FORTIFICATION_WINDOW = new Pair<Float>((MUNITIONS_WINDOW.x + WINDOW_WIDTH + 25.0f), 200.0f);
	private static final Pair<Float> TACTICS_WINDOW = new Pair<Float>((FORTIFICATION_WINDOW.x + WINDOW_WIDTH + 25.0f), 200.0f);
	
	private TalentButton [][] munitions;
	private TalentButton [][] fortification;
	private TalentButton [][] tactics;
	
	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		munitions = new TalentButton[7][3];
		fortification = new TalentButton[7][3];
		tactics = new TalentButton[7][3];
		
		for(Talents.Munitions m : Talents.Munitions.values()) {
			TalentButton button = new TalentButton(m);
			button.setPosition(new Pair<Float>((MUNITIONS_WINDOW.x + (m.col() * 51.0f) + 51.0f), (MUNITIONS_WINDOW.y + (m.row() * 20.0f) + 44.0f)));
			
			munitions[m.row()][m.col()] = button;
		}
		
		for(Talents.Fortification f : Talents.Fortification.values()) {
			TalentButton button = new TalentButton(f);
			button.setPosition(new Pair<Float>((FORTIFICATION_WINDOW.x + (f.col() * 51.0f) + 51.0f), (FORTIFICATION_WINDOW.y + (f.row() * 20.0f) + 44.0f)));
			
			munitions[f.row()][f.col()] = button;
		}
		
		for(Talents.Tactics t : Talents.Tactics.values()) {
			TalentButton button = new TalentButton(t);
			button.setPosition(new Pair<Float>((TACTICS_WINDOW.x + (t.col() * 51.0f) + 51.0f), (TACTICS_WINDOW.y + (t.row() * 20.0f) + 44.0f)));
			
			munitions[t.row()][t.col()] = button;
		}
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
	}

	@Override
	public int getID() {
		return TalentsState.ID;
	}
}
