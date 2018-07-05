package com.gzsr.states;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.InputListener;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;
import org.newdawn.slick.util.FontUtils;

import com.gzsr.AssetManager;
import com.gzsr.Controls;
import com.gzsr.Globals;
import com.gzsr.MusicPlayer;
import com.gzsr.entities.Player;
import com.gzsr.gfx.ui.SkillButton;
import com.gzsr.gfx.ui.TooltipText;
import com.gzsr.misc.Pair;

public class TrainState extends BasicGameState implements InputListener {
	public static final int ID = 3;
	private static UnicodeFont FONT_HEADER = null;
	private static UnicodeFont FONT_NORMAL = null;
	private static final Color EXP_BAR = new Color(0.0f, 0.6f, 0.0f);
	
	private AssetManager assets;
	
	private TooltipText expToLevelTooltip;
	private TooltipText healthUpTooltip;
	private TooltipText speedUpTooltip;
	private TooltipText damageUpTooltip;
	
	private List<SkillButton> skillButtons;
	
	private boolean exit;
	
	@SuppressWarnings("serial")
	@Override
	public void init(GameContainer gc, StateBasedGame game) throws SlickException {
		assets = AssetManager.getManager();
		FONT_HEADER = assets.getFont("PressStart2P-Regular_large");
		FONT_NORMAL = assets.getFont("PressStart2P-Regular");
		
		expToLevelTooltip = new TooltipText(TrainState.FONT_NORMAL, 
											"Exp To Next Level:", 
											"Your progress towards leveling up and gaining a skill point.", 
											Color.white, new Pair<Float>(30.0f, 120.0f));
		healthUpTooltip = new TooltipText(TrainState.FONT_NORMAL,
										  "Max Health:",
										  "Each point in this increases your health by 20.",
										  Color.white, new Pair<Float>(30.0f, 210.0f));
		speedUpTooltip = new TooltipText(TrainState.FONT_NORMAL,
										 "Speed:",
										 "Each point in this increases your movement speed by 10%.",
										 Color.white, new Pair<Float>(30.0f, 300.0f));
		damageUpTooltip = new TooltipText(TrainState.FONT_NORMAL,
										  "Damage:",
										  "Each point in this increases damage done by 10%.",
										  Color.white, new Pair<Float>(30.0f, 390.0f));
		
		skillButtons = new ArrayList<SkillButton>() {{
			add(new SkillButton("healthUp", new Pair<Float>(340.0f, 240.0f)));
			add(new SkillButton("speedUp", new Pair<Float>(340.0f, 330.0f)));
			add(new SkillButton("damageUp", new Pair<Float>(340.0f, 420.0f)));
		}};
		
		exit = false;
	}
	
	@Override
	public void update(GameContainer gc, StateBasedGame game, int delta) throws SlickException {
		if(exit) game.enterState(GameState.ID, new FadeOutTransition(Color.black, 250), new FadeInTransition(Color.black, 250));
		MusicPlayer.getInstance().update(false);
	}

	@Override
	public void render(GameContainer gc, StateBasedGame game, Graphics g) throws SlickException {
		g.resetTransform();
		g.clear();
		
		g.setColor(Color.darkGray);
		g.fillRect(0.0f, 0.0f, Globals.WIDTH, Globals.HEIGHT);
		
		Player player = Player.getPlayer();
		
		// Draw the header and footer.
		g.setColor(Color.white);
		g.drawLine(10.0f, 36.0f, (Globals.WIDTH - 10.0f), 36.0f);
		g.drawLine(10.0f, (Globals.HEIGHT - 36.0f), (Globals.WIDTH - 10.0f), (Globals.HEIGHT - 36.0f));
		g.setFont(TrainState.FONT_HEADER);
		g.drawString("Training", 30.0f, 20.0f);
		
		g.setFont(TrainState.FONT_NORMAL);
		
		// Display the user's current level.
		g.setColor(Color.white);
		g.drawString(String.format("Level: %d", player.getAttributes().getInt("level")), 30.0f, 70.0f);
		
		{// Show progress bar indicating how much experience until the next level.
			float expWidth = (float)player.getAttributes().getInt("experience") / (float)player.getAttributes().getInt("expToLevel");
			g.setColor(Color.black);
			g.fillRect(30.0f, 150.0f, 300.0f, 50.0f);
			g.setColor(Color.lightGray);
			g.drawRect(30.0f, 150.0f, 300.0f, 50.0f);
			if(expWidth > 0) {
				g.setColor(TrainState.EXP_BAR);
				g.fillRect(35.0f, 155.0f, (expWidth * 290.0f), 40.0f);
				g.setColor(Color.lightGray);
				g.drawRect(35.0f, 155.0f, (expWidth * 290.0f), 40.0f);
			}
			
			String expText = String.format("%d / %d", player.getAttributes().getInt("experience"), player.getAttributes().getInt("expToLevel"));
			g.setColor(Color.white);
			FontUtils.drawCenter(TrainState.FONT_NORMAL, expText, 30, (175 - (TrainState.FONT_NORMAL.getLineHeight() / 2)), 300);
		} // End drawing of experience bar.
		
		{ // Begin drawing health upgrade components.
			int healthLevel = player.getAttributes().getInt("healthUp");
			g.setColor(Color.black);
			g.fillRect(30.0f, 240.0f, 300.0f, 50.0f);
			g.setColor(Color.lightGray);
			g.drawRect(30.0f, 240.0f, 300.0f, 50.0f);
			
			for(int i = 0; i < healthLevel; i++) {
				float x = 35.0f + (i * 29.5f);
				g.setColor(Color.red);
				g.fillRect(x, 245.0f, 24.5f, 40.0f);
				g.setColor(Color.lightGray);
				g.drawRect(x, 245.0f, 24.5f, 40.0f);
			}
		} // End drawing health upgrade components.
		
		{ // Begin drawing speed upgrade components.
			int speedLevel = player.getAttributes().getInt("speedUp");
			g.setColor(Color.black);
			g.fillRect(30.0f, 330.0f, 300.0f, 50.0f);
			g.setColor(Color.lightGray);
			g.drawRect(30.0f, 330.0f, 300.0f, 50.0f);
			
			for(int i = 0; i < speedLevel; i++) {
				float x = 35.0f + (i * 29.5f);
				g.setColor(Color.red);
				g.fillRect(x, 335.0f, 24.5f, 40.0f);
				g.setColor(Color.lightGray);
				g.drawRect(x, 335.0f, 24.5f, 40.0f);
			}
		} // End drawing speed upgrade components.
		
		{ // Begin drawing damage upgrade components.
			int damageLevel = player.getAttributes().getInt("damageUp");
			g.setColor(Color.black);
			g.fillRect(30.0f, 420.0f, 300.0f, 50.0f);
			g.setColor(Color.lightGray);
			g.drawRect(30.0f, 420.0f, 300.0f, 50.0f);
			
			for(int i = 0; i < damageLevel; i++) {
				float x = 35.0f + (i * 29.5f);
				g.setColor(Color.red);
				g.fillRect(x, 425.0f, 24.5f, 40.0f);
				g.setColor(Color.lightGray);
				g.drawRect(x, 425.0f, 24.5f, 40.0f);
			}
		} // End drawing damage upgrade components.
		
		{ // Begin rendering skill buttons.
			Iterator<SkillButton> it = skillButtons.iterator();
			while(it.hasNext()) {
				SkillButton sk = it.next();
				sk.render(g, 0L);
			}
		} // End rendering skill buttons.
		
		// Show how many skill points the player has.
		g.setColor(Color.white);
		g.drawString(String.format("Skill Points: %d", player.getAttributes().getInt("skillPoints")), 30.0f, (Globals.HEIGHT - 66.0f));
		
		{ // Draw tooltips last, since they need to be drawn over everything else.
			expToLevelTooltip.render(g, 0L);
			healthUpTooltip.render(g, 0L);
			speedUpTooltip.render(g, 0L);
			damageUpTooltip.render(g, 0L);
		} // End tooltip drawing.
	}
	
	@Override
	public void mouseMoved(int oldx, int oldy, int newx, int newy) {
		Controls.getInstance().getMouse().setPosition(newx, newy);
	}
	
	@Override
	public void mousePressed(int button, int x, int y) {
		if(button == 0) Controls.getInstance().getMouse().setMouseDown(true);
		
		// Check all skill buttons to see if they've been clicked.
		Iterator<SkillButton> it = skillButtons.iterator();
		while(it.hasNext()) {
			SkillButton sk = it.next();
			if(sk.inBounds(x, y)) {
				sk.click();
				break;
			}
		}
	}
	
	@Override
	public void mouseReleased(int button, int x, int y) {
		if(button == 0) Controls.getInstance().getMouse().setMouseDown(false);
	}
	
	@Override
	public void keyReleased(int key, char c) {
		if(key == Controls.Layout.TRAIN_SCREEN.getKey()) exit = true;
	}
	
	@Override
	public void enter(GameContainer gc, StateBasedGame game) {
		Controls.getInstance().resetAll();
		exit = false;
	}

	@Override
	public int getID() {
		return TrainState.ID;
	}
}
