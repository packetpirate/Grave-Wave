package com.gzsr.gfx.ui;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.UnicodeFont;

import com.gzsr.Globals;
import com.gzsr.entities.Entity;
import com.gzsr.misc.Pair;
import com.gzsr.states.GameState;

public class TooltipText implements Entity {
	private UnicodeFont font;
	private String text;
	private String tooltip;
	private Color color;
	private Pair<Float> pos;
	
	public TooltipText(UnicodeFont font_, String text_, String tooltip_, Color color_, Pair<Float> pos_) {
		this.font = font_;
		this.text = text_;
		this.tooltip = tooltip_;
		this.color = color_;
		this.pos = pos_;
	}

	@Override
	public void update(GameState gs, long cTime, int delta) {
		// No need for update.
	}

	@Override
	public void render(Graphics g, long cTime) {
		// Draw the text as normal.
		g.setColor(color);
		g.setFont(font);
		g.drawString(text, pos.x, pos.y);
		
		// If the mouse cursor is hovering over the text, display the tooltip.
		if(isMouseInside()) {
			float mx = Globals.mouse.getPosition().x;
			float my = Globals.mouse.getPosition().y;
			float txtWidth = font.getWidth(tooltip);
			
			g.setColor(Color.gray);
			g.fillRect(mx, my, (txtWidth + 10.0f), (font.getLineHeight() + 10.0f));
			g.setColor(Color.white);
			g.drawRect(mx, my, (txtWidth + 10.0f), (font.getLineHeight() + 10.0f));
			g.setColor(Color.black);
			g.drawString(tooltip, (mx + 5.0f), (my + 5.0f));
		}
	}
	
	private boolean isMouseInside() {
		float x = Globals.mouse.getPosition().x;
		float y = Globals.mouse.getPosition().y;
		float txtWidth = font.getWidth(text);
		return ((x >= pos.x) && (y >= pos.y) && 
				(x <= (pos.x + txtWidth)) && (y <= (pos.y + font.getLineHeight())));
	}

	@Override
	public String getName() {
		return "Tooltip";
	}
	
	@Override
	public String getDescription() {
		return "Tooltip Text";
	}
}
