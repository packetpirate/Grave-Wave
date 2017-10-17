package com.gzsr.states;

import java.awt.Font;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
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
import com.gzsr.gfx.ui.TooltipText;
import com.gzsr.misc.Pair;

public class TrainState extends BasicGameState {
	public static final int ID = 3;
	private static final TrueTypeFont FONT_HEADER = new TrueTypeFont(new Font("Lucida Console", Font.BOLD, 32), true);
	private static final TrueTypeFont FONT_NORMAL = new TrueTypeFont(new Font("Lucida Console", Font.PLAIN, 16), true);
	
	private AssetManager assets;
	
	private TooltipText expToLevelTooltip;
	private TooltipText healthUpTooltip;
	private TooltipText speedUpTooltip;
	private TooltipText damageUpTooltip;
	
	@Override
	public void init(GameContainer gc, StateBasedGame game) throws SlickException {
		assets = AssetManager.getManager();
		
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
	}
	
	@Override
	public void update(GameContainer gc, StateBasedGame game, int delta) throws SlickException {
		Game.handleInput(gc);
		
		Player player = Globals.player;
		
		if(Globals.released.contains(Input.KEY_T)) {
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
		
		g.setFont(TrainState.FONT_NORMAL);
		
		// Display the user's current level.
		g.setColor(Color.red);
		g.drawString(String.format("Level: %d", player.getIntAttribute("level")), 30.0f, 70.0f);
		
		{// Show progress bar indicating how much experience until the next level.
			float expWidth = (float)player.getIntAttribute("experience") / (float)player.getIntAttribute("expToLevel");
			g.setColor(Color.black);
			g.fillRect(30.0f, 150.0f, 300.0f, 50.0f);
			g.setColor(Color.lightGray);
			g.drawRect(30.0f, 150.0f, 300.0f, 50.0f);
			if(expWidth > 0) {
				g.setColor(Color.green);
				g.fillRect(35.0f, 155.0f, (expWidth * 290.0f), 40.0f);
				g.setColor(Color.lightGray);
				g.drawRect(35.0f, 155.0f, (expWidth * 290.0f), 40.0f);
			}
		} // End drawing of experience bar.
		
		{ // Begin drawing health upgrade components.
			int healthLevel = player.getIntAttribute("healthUp");
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
			int speedLevel = player.getIntAttribute("speedUp");
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
			int damageLevel = player.getIntAttribute("damageUp");
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
		
		// Show how many skill points the player has.
		g.setColor(Color.white);
		g.drawString(String.format("Skill Points: %d", player.getIntAttribute("skillPoints")), 30.0f, (Globals.HEIGHT - 66.0f));
		
		{ // Draw tooltips last, since they need to be drawn over everything else.
			expToLevelTooltip.render(g, 0L);
			healthUpTooltip.render(g, 0L);
			speedUpTooltip.render(g, 0L);
			damageUpTooltip.render(g, 0L);
		} // End tooltip drawing.
	}

	@Override
	public int getID() {
		return TrainState.ID;
	}
}
