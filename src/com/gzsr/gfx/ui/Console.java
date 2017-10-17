package com.gzsr.gfx.ui;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.TrueTypeFont;

import com.gzsr.entities.Entity;
import com.gzsr.states.GameState;

public class Console implements Entity {
	private static final Color CONSOLE_BACKGROUND = Color.darkGray;
	private static final Color CONSOLE_BORDER = Color.black;
	private static final Color CONSOLE_TEXT = Color.white;
	private static final Color CONSOLE_TEXTBOX = Color.black;
	private static final Color CONSOLE_TEXTBORDER = Color.lightGray;
	private static final TrueTypeFont CONSOLE_FONT = new TrueTypeFont(new Font("Lucida Console", Font.PLAIN, 12), true);
	
	private GameState gs;
	private List<String> pastCommands;
	
	public Console(GameState gs_) {
		this.gs = gs_;
		this.pastCommands = new ArrayList<String>();
	}

	@Override
	public void update(long cTime) {
		
	}

	@Override
	public void render(Graphics g, long cTime) {
		g.setColor(CONSOLE_BACKGROUND);
		
	}
}
