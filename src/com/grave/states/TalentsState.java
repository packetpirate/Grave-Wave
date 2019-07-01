package com.grave.states;

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
import org.newdawn.slick.util.FontUtils;

import com.grave.AssetManager;
import com.grave.Controls;
import com.grave.Globals;
import com.grave.MusicPlayer;
import com.grave.entities.Player;
import com.grave.gfx.ui.MenuButton;
import com.grave.gfx.ui.TalentButton;
import com.grave.gfx.ui.TooltipText;
import com.grave.misc.MouseInfo;
import com.grave.misc.Pair;
import com.grave.talents.Talents;

public class TalentsState extends BasicGameState implements InputListener {
	public static final int ID = 3;
	
	private static final float WINDOW_WIDTH = 300.0f;
	private static final float WINDOW_HEIGHT = 468.0f;
	
	private static final float EXP_BAR_WIDTH = 250.0f;
	private static final float EXP_BAR_HEIGHT = 50.0f;
	private static final Color EXP_BAR_COLOR = new Color(0.0f, 0.6f, 0.0f);
	
	private static final Pair<Float> MUNITIONS_WINDOW = new Pair<Float>(((Globals.WIDTH - (WINDOW_WIDTH * 3) - 50.0f) / 2), 200.0f);
	private static final Pair<Float> FORTIFICATION_WINDOW = new Pair<Float>((MUNITIONS_WINDOW.x + WINDOW_WIDTH + 25.0f), 200.0f);
	private static final Pair<Float> TACTICS_WINDOW = new Pair<Float>((FORTIFICATION_WINDOW.x + WINDOW_WIDTH + 25.0f), 200.0f);
	
	private TalentButton [][] munitions;
	private TooltipText [][] munitionsTooltips;
	
	private TalentButton [][] fortification;
	private TooltipText [][] fortificationTooltips;
	
	private TalentButton [][] tactics;
	private TooltipText [][] tacticsTooltips;
	
	private MenuButton back;
	private MenuButton accept;
	
	private int changesMade;
	private boolean exit;
	
	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		munitions = new TalentButton[7][3];
		munitionsTooltips = new TooltipText[7][3];
		
		fortification = new TalentButton[7][3];
		fortificationTooltips = new TooltipText[7][3];
		
		tactics = new TalentButton[7][3];
		tacticsTooltips = new TooltipText[7][3];
		
		UnicodeFont large = AssetManager.getManager().getFont("PressStart2P-Regular_large");
		float fh = large.getLineHeight();
		back = new MenuButton(new Pair<Float>((Globals.WIDTH - large.getWidth("Exit") - 50.0f), (Globals.HEIGHT - fh - 30.0f)), "Exit");
		accept = new MenuButton(new Pair<Float>(50.0f, (Globals.HEIGHT - fh - 30.0f)), "Accept");
		
		changesMade = 0;
		exit = false;
		
		for(Talents.Munitions m : Talents.Munitions.values()) {
			Pair<Float> pos = new Pair<Float>((MUNITIONS_WINDOW.x + (m.col() * (51.0f + 32.0f)) + 67.0f), 
											  (MUNITIONS_WINDOW.y + (m.row() * (30.0f + 32.0f)) + 32.0f));
			TalentButton button = new TalentButton(m, pos);
			
			munitions[m.row()][m.col()] = button;
			munitionsTooltips[m.row()][m.col()] = new TooltipText(AssetManager.getManager().getFont("PressStart2P-Regular_small"), 
					   											  m.getName(), m.getDescription(), pos, TalentButton.SIZE);
		}
		
		for(Talents.Fortification f : Talents.Fortification.values()) {
			Pair<Float> pos = new Pair<Float>((FORTIFICATION_WINDOW.x + (f.col() * (51.0f + 32.0f)) + 67.0f), 
											  (FORTIFICATION_WINDOW.y + (f.row() * (30.0f + 32.0f)) + 32.0f));
			TalentButton button = new TalentButton(f, pos);
			
			fortification[f.row()][f.col()] = button;
			fortificationTooltips[f.row()][f.col()] = new TooltipText(AssetManager.getManager().getFont("PressStart2P-Regular_small"), 
					   												  f.getName(), f.getDescription(), pos, TalentButton.SIZE);
		}
		
		for(Talents.Tactics t : Talents.Tactics.values()) {
			Pair<Float> pos = new Pair<Float>((TACTICS_WINDOW.x + (t.col() * (51.0f + 32.0f)) + 67.0f), 
											  (TACTICS_WINDOW.y + (t.row() * (30.0f + 32.0f)) + 32.0f));
			TalentButton button = new TalentButton(t, pos);
			
			tactics[t.row()][t.col()] = button;
			tacticsTooltips[t.row()][t.col()] = new TooltipText(AssetManager.getManager().getFont("PressStart2P-Regular_small"), 
					   											t.getName(), t.getDescription(), pos, TalentButton.SIZE);
		}
	}

	@Override
	public void render(GameContainer gc, StateBasedGame game, Graphics g) throws SlickException {
		Player player = Player.getPlayer();
		
		UnicodeFont large = AssetManager.getManager().getFont("PressStart2P-Regular_large");
		UnicodeFont normal = AssetManager.getManager().getFont("PressStart2P-Regular");
		UnicodeFont small = AssetManager.getManager().getFont("PressStart2P-Regular_small");
		
		g.setColor(Color.darkGray);
		g.fillRect(0.0f, 0.0f, Globals.WIDTH, Globals.HEIGHT);
		
		// Render the title text. 
		g.setFont(large);
		g.setColor(Color.white);
		g.drawString("Talents", 50.0f, 60.0f);
		
		// Show the player's current skill points under the experience bar.
		String skpText = String.format("Skill Points: %d", player.getAttributes().getInt("skillPoints"));
		g.setFont(normal);
		g.drawString(skpText, ((Globals.WIDTH / 2) - (normal.getWidth(skpText) / 2)), 120.0f);
		
		// Draw the experience bar.
		{
			int level = player.getAttributes().getInt("level");
			String currentText = String.format("%d", level);
			String nextText = String.format("%d", (level + 1));
			
			float currentWidth = normal.getWidth(currentText);
			float nextWidth = normal.getWidth(nextText);
			float cx = (Globals.WIDTH - EXP_BAR_WIDTH - nextWidth - currentWidth - 90.0f);
			float cy = (((EXP_BAR_HEIGHT / 2) + 50.0f) - (normal.getLineHeight() / 2));
			float nx = (Globals.WIDTH - nextWidth - 50.0f);
			
			g.setColor(Color.white);
			g.drawString(currentText, cx, cy);
			g.drawString(nextText, nx, cy);
			
			int exp = player.getAttributes().getInt("experience");
			int toLevel = player.getAttributes().getInt("expToLevel");
			String expText = String.format("%d / %d", exp, toLevel);
			g.setColor(Color.black);
			g.fillRect((Globals.WIDTH - EXP_BAR_WIDTH - nextWidth - 70.0f), 50.0f, EXP_BAR_WIDTH, EXP_BAR_HEIGHT);
			g.setColor(EXP_BAR_COLOR);
			g.fillRect((Globals.WIDTH - EXP_BAR_WIDTH - nextWidth - 65.0f), 55.0f, Math.max(((EXP_BAR_WIDTH * ((float)exp / (float)toLevel)) - 10.0f), 0.0f), (EXP_BAR_HEIGHT - 10.0f));
			g.setColor(Color.white);
			g.drawRect((Globals.WIDTH - EXP_BAR_WIDTH - nextWidth - 70.0f), 50.0f, EXP_BAR_WIDTH, EXP_BAR_HEIGHT);
			
			float expTextWidth = normal.getWidth(expText);
			float expTextHeight = normal.getHeight(expText);
			g.drawString(expText, (Globals.WIDTH - (EXP_BAR_WIDTH / 2) - (expTextWidth / 2) - nextWidth - 70.0f), (((EXP_BAR_HEIGHT / 2) + 50.0F) - (expTextHeight / 2)));
		}	
		
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
		
		for(int r = 0; r < 7; r++) {
			for(int c = 0; c < 3; c++) {
				if(munitionsTooltips[r][c] != null) munitionsTooltips[r][c].render(g, 0L);
				if(fortificationTooltips[r][c] != null) fortificationTooltips[r][c].render(g, 0L);
				if(tacticsTooltips[r][c] != null) tacticsTooltips[r][c].render(g, 0L);
			}
		}
		
		// Draw the talent tree names above the containers.
		FontUtils.drawCenter(normal, "Munitions", (int)MUNITIONS_WINDOW.x.floatValue(), (int)(MUNITIONS_WINDOW.y - normal.getLineHeight() - 5.0f), (int)WINDOW_WIDTH);
		FontUtils.drawCenter(normal, "Fortifications", (int)FORTIFICATION_WINDOW.x.floatValue(), (int)(FORTIFICATION_WINDOW.y - normal.getLineHeight() - 5.0f), (int)WINDOW_WIDTH);
		FontUtils.drawCenter(normal, "Tactics", (int)TACTICS_WINDOW.x.floatValue(), (int)(TACTICS_WINDOW.y - normal.getLineHeight() - 5.0f), (int)WINDOW_WIDTH);
		
		// Draw instructions.
		FontUtils.drawCenter(small, "Left Click: Spend Skill Point", 0, (int)(Globals.HEIGHT - (small.getLineHeight() * 2.0f) - 45.0f), Globals.WIDTH);
		FontUtils.drawCenter(small, "Right Click: Remove Skill Point", 0, (int)(Globals.HEIGHT - small.getLineHeight() - 35.0f), Globals.WIDTH);
		
		back.render(g, 0L);
		if(changesMade > 0) accept.render(g, 0L);
	}

	@Override
	public void update(GameContainer gc, StateBasedGame game, int delta) throws SlickException {
		if(exit) {
			discardChanges();
			game.enterState(GameState.ID, new FadeOutTransition(Color.black, 250), new FadeInTransition(Color.black, 100));
		}
		
		MouseInfo mouse = Controls.getInstance().getMouse();
		if(back.inBounds(mouse.getPosition().x, mouse.getPosition().y)) {
			back.mouseEnter();
			if(mouse.isLeftDown()) {
				if(changesMade > 0) discardChanges();
				game.enterState(GameState.ID, new FadeOutTransition(Color.black, 250), new FadeInTransition(Color.black, 100));
			}
		} else back.mouseExit();
		
		if(accept.inBounds(mouse.getPosition().x, mouse.getPosition().y)) {
			accept.mouseEnter();
			if(mouse.isLeftDown()) {
				for(int r = 0; r < 7; r++) {
					for(int c = 0; c < 3; c++) {
						TalentButton m = munitions[r][c];
						TalentButton f = fortification[r][c];
						TalentButton t = tactics[r][c];
						
						if((m != null) && (m.getPointsToAdd() > 0)) m.confirm();
						if((f != null) && (f.getPointsToAdd() > 0)) f.confirm();
						if((t != null) && (t.getPointsToAdd() > 0)) t.confirm();
					}
				}
				
				changesMade = 0;
			}
		} else accept.mouseExit();
		
		MusicPlayer.getInstance().update(false);
	}
	
	private void discardChanges() {
		// Discard any talent changes.
		for(int r = 0; r < 7; r++) {
			for(int c = 0; c < 3; c++) {
				TalentButton m = munitions[r][c];
				TalentButton f = fortification[r][c];
				TalentButton t = tactics[r][c];
				
				if((m != null) && (m.getPointsToAdd() > 0)) m.revert();
				if((f != null) && (f.getPointsToAdd() > 0)) f.revert();
				if((t != null) && (t.getPointsToAdd() > 0)) t.revert();
			}
		}
		
		changesMade = 0;
	}

	@Override
	public void mouseMoved(int oldx, int oldy, int newx, int newy) {
		Controls.getInstance().getMouse().setPosition(newx, newy);
	}
	
	@Override
	public void mousePressed(int button, int x, int y) {
		MouseInfo mouse = Controls.getInstance().getMouse();
		if(button == Input.MOUSE_LEFT_BUTTON) mouse.setLeftDown(true);
		if(button == Input.MOUSE_RIGHT_BUTTON) mouse.setRightDown(true);
		
		// Check all talent buttons to see if they've been clicked.
		if(mouse.isLeftDown() || mouse.isRightDown()) {
			boolean left = mouse.isLeftDown();
			for(int r = 0; r < 7; r++) {
				for(int c = 0; c < 3; c++) {
					TalentButton m = munitions[r][c];
					TalentButton f = fortification[r][c];
					TalentButton t = tactics[r][c];
					
					if((m != null) && (m.inBounds(x, y))) {
						int before = m.getPointsToAdd();
						m.click(left);
						if(m.getPointsToAdd() != before) changesMade += left ? 1 : -1;
					} else if((f != null) && (f.inBounds(x, y))) {
						int before = f.getPointsToAdd();
						f.click(left);
						if(f.getPointsToAdd() != before) changesMade += left ? 1 : -1;
					} else if((t != null) && (t.inBounds(x, y))) {
						int before = t.getPointsToAdd();
						t.click(left);
						if(t.getPointsToAdd() != before) changesMade += left ? 1 : -1;
					}
				}
			}
		}
	}
	
	@Override
	public void mouseReleased(int button, int x, int y) {
		if(button == Input.MOUSE_LEFT_BUTTON) Controls.getInstance().getMouse().setLeftDown(false);
		if(button == Input.MOUSE_RIGHT_BUTTON) Controls.getInstance().getMouse().setRightDown(false);
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
