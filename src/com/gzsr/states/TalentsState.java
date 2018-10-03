package com.gzsr.states;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.InputListener;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import com.gzsr.talents.Talent;

public class TalentsState extends BasicGameState implements InputListener {
	public static final int ID = 3;
	
	// Use (row - 1) as index to find level requirement for a particular talent.
	private static final int [] TIER_LEVEL_REQUIREMENTS = new int[] {1, 5, 10, 15, 20, 25, 30};
	
	private Talent[][] munitions;
	private Talent[][] fortification;
	private Talent[][] tactics;
	
	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		munitions = new Talent[7][3];
		fortification = new Talent[7][3];
		tactics = new Talent[7][3];
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
