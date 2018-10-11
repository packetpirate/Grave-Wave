package com.gzsr.states;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.InputListener;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import com.gzsr.AssetManager;
import com.gzsr.Controls;
import com.gzsr.Globals;
import com.gzsr.MusicPlayer;
import com.gzsr.entities.Player;
import com.gzsr.gfx.ui.TalentButton;
import com.gzsr.misc.Pair;
import com.gzsr.talents.Talents;

public class TalentsState extends BasicGameState implements InputListener {
	public static final int ID = 3;
	
	// Use (row - 1) as index to find level requirement for a particular talent.
	private static final int [] TIER_LEVEL_REQUIREMENTS = new int[] {1, 5, 10, 15, 20, 25, 30};
	
	private static final float WINDOW_WIDTH = 300.0f;
	private static final float WINDOW_HEIGHT = 468.0f;
	
	private static final float EXP_BAR_WIDTH = 250.0f;
	private static final float EXP_BAR_HEIGHT = 50.0f;
	private static final Color EXP_BAR_COLOR = new Color(0.0f, 0.6f, 0.0f);
	
	private static final Pair<Float> MUNITIONS_WINDOW = new Pair<Float>(((Globals.WIDTH - (WINDOW_WIDTH * 3) - 50.0f) / 2), 200.0f);
	private static final Pair<Float> FORTIFICATION_WINDOW = new Pair<Float>((MUNITIONS_WINDOW.x + WINDOW_WIDTH + 25.0f), 200.0f);
	private static final Pair<Float> TACTICS_WINDOW = new Pair<Float>((FORTIFICATION_WINDOW.x + WINDOW_WIDTH + 25.0f), 200.0f);
	
	private TalentButton [][] munitions;
	private TalentButton [][] fortification;
	private TalentButton [][] tactics;
	
	private boolean exit;
	
	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		munitions = new TalentButton[7][3];
		fortification = new TalentButton[7][3];
		tactics = new TalentButton[7][3];
		
		exit = false;
		
		int talentCount = 0;
		
		for(Talents.Munitions m : Talents.Munitions.values()) {
			TalentButton button = new TalentButton(m);
			button.setPosition(new Pair<Float>((MUNITIONS_WINDOW.x + (m.col() * (51.0f + 32.0f)) + 67.0f), (MUNITIONS_WINDOW.y + (m.row() * (20.0f + 32.0f)) + 62.0f)));
			
			munitions[m.row()][m.col()] = button;
			talentCount++;
		}
		
		for(Talents.Fortification f : Talents.Fortification.values()) {
			TalentButton button = new TalentButton(f);
			button.setPosition(new Pair<Float>((FORTIFICATION_WINDOW.x + (f.col() * (51.0f + 32.0f)) + 67.0f), (FORTIFICATION_WINDOW.y + (f.row() * (20.0f + 32.0f)) + 62.0f)));
			
			fortification[f.row()][f.col()] = button;
			talentCount++;
		}
		
		for(Talents.Tactics t : Talents.Tactics.values()) {
			TalentButton button = new TalentButton(t);
			button.setPosition(new Pair<Float>((TACTICS_WINDOW.x + (t.col() * (51.0f + 32.0f)) + 67.0f), (TACTICS_WINDOW.y + (t.row() * (20.0f + 32.0f)) + 62.0f)));
			
			tactics[t.row()][t.col()] = button;
			talentCount++;
		}
	}

	@Override
	public void render(GameContainer gc, StateBasedGame game, Graphics g) throws SlickException {
		Player player = Player.getPlayer();
		
		g.setColor(Color.darkGray);
		g.fillRect(0.0f, 0.0f, Globals.WIDTH, Globals.HEIGHT);
		
		// Render the title text.
		UnicodeFont large = AssetManager.getManager().getFont("PressStart2P-Regular_large"); 
		g.setFont(large);
		g.setColor(Color.white);
		g.drawString("Talents", 50.0f, 50.0f);
		
		// Draw the experience bar.
		int exp = player.getAttributes().getInt("experience");
		int toLevel = player.getAttributes().getInt("expToLevel");
		String expText = String.format("%d / %d", exp, toLevel);
		g.setColor(Color.black);
		g.fillRect((Globals.WIDTH - EXP_BAR_WIDTH - 50.0f), 50.0f, EXP_BAR_WIDTH, EXP_BAR_HEIGHT);
		g.setColor(EXP_BAR_COLOR);
		g.fillRect((Globals.WIDTH - EXP_BAR_WIDTH - 50.0f), 50.0f, (EXP_BAR_WIDTH * ((float)exp / (float)toLevel)), EXP_BAR_HEIGHT);
		g.setColor(Color.white);
		g.drawRect((Globals.WIDTH - EXP_BAR_WIDTH - 50.0f), 50.0f, EXP_BAR_WIDTH, EXP_BAR_HEIGHT);
		
		UnicodeFont normal = AssetManager.getManager().getFont("PressStart2P-Regular");
		float expTextWidth = normal.getWidth(expText);
		float expTextHeight = normal.getHeight(expText);
		g.setFont(normal);
		g.drawString(expText, (Globals.WIDTH - (EXP_BAR_WIDTH / 2) - (expTextWidth / 2) - 50.0f), (((EXP_BAR_HEIGHT / 2) + 50.0F) - (expTextHeight / 2)));
		
		
		// Draw the background for the talent trees.
		g.setColor(Color.gray);
		g.fillRect(MUNITIONS_WINDOW.x, MUNITIONS_WINDOW.y, WINDOW_WIDTH, WINDOW_HEIGHT);
		g.fillRect(FORTIFICATION_WINDOW.x, FORTIFICATION_WINDOW.y, WINDOW_WIDTH, WINDOW_HEIGHT);
		g.fillRect(TACTICS_WINDOW.x, TACTICS_WINDOW.y, WINDOW_WIDTH, WINDOW_HEIGHT);
		
		g.setColor(Color.white);
		g.drawRect(MUNITIONS_WINDOW.x, MUNITIONS_WINDOW.y, WINDOW_WIDTH, WINDOW_HEIGHT);
		g.drawRect(FORTIFICATION_WINDOW.x, FORTIFICATION_WINDOW.y, WINDOW_WIDTH, WINDOW_HEIGHT);
		g.drawRect(TACTICS_WINDOW.x, TACTICS_WINDOW.y, WINDOW_WIDTH, WINDOW_HEIGHT);
		
		// Draw the talent trees themselves.
		for(int r = 0; r < 7; r++) {
			for(int c = 0; c < 3; c++) {
				if(munitions[r][c] != null) munitions[r][c].render(g, 0L);
				if(fortification[r][c] != null) fortification[r][c].render(g, 0L);
				if(tactics[r][c] != null) tactics[r][c].render(g, 0L);
			}
		}
	}

	@Override
	public void update(GameContainer gc, StateBasedGame game, int delta) throws SlickException {
		if(exit) game.enterState(GameState.ID, new FadeOutTransition(Color.black, 250), new FadeInTransition(Color.black, 100));
		MusicPlayer.getInstance().update(false);
	}

	@Override
	public void mouseMoved(int oldx, int oldy, int newx, int newy) {
		Controls.getInstance().getMouse().setPosition(newx, newy);
	}
	
	@Override
	public void mousePressed(int button, int x, int y) {
		if(button == Input.MOUSE_LEFT_BUTTON) Controls.getInstance().getMouse().setLeftDown(true);
		
		// Check all talent buttons to see if they've been clicked.
		for(int r = 0; r < 7; r++) {
			for(int c = 0; c < 3; c++) {
				TalentButton m = munitions[r][c];
				TalentButton f = fortification[r][c];
				TalentButton t = tactics[r][c];
				
				if((m != null) && (m.inBounds(x, y))) m.click();
				else if((f != null) && (f.inBounds(x, y))) f.click();
				else if((t != null) && (t.inBounds(x, y))) t.click();
			}
		}
	}
	
	@Override
	public void mouseReleased(int button, int x, int y) {
		if(button == Input.MOUSE_LEFT_BUTTON) Controls.getInstance().getMouse().setLeftDown(false);
	}
	
	@Override
	public void keyReleased(int key, char c) {
		if((key == Controls.Layout.TALENTS_SCREEN.getKey()) || 
		   (key == Input.KEY_ESCAPE)) exit = true;
	}
	
	@Override
	public void enter(GameContainer gc, StateBasedGame game) {
		Controls.getInstance().resetAll();
		exit = false;
	}
	
	@Override
	public int getID() {
		return TalentsState.ID;
	}
}
