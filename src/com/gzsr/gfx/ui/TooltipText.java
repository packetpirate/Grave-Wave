package com.gzsr.gfx.ui;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.state.BasicGameState;

import com.gzsr.Controls;
import com.gzsr.entities.Entity;
import com.gzsr.gfx.Layers;
import com.gzsr.misc.MouseInfo;
import com.gzsr.misc.Pair;

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
	public void update(BasicGameState gs, long cTime, int delta) {
		// No need for update.
	}

	@Override
	public void render(Graphics g, long cTime) {
		// Draw the text as normal.
		g.setColor(color);
		g.setFont(font);
		g.drawString(text, pos.x, pos.y);
		
		// If the mouse cursor is hovering over the text, display the tooltip.
		MouseInfo mouse = Controls.getInstance().getMouse();
		if(isMouseInside()) {
			float mx = mouse.getPosition().x;
			float my = mouse.getPosition().y;
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
		MouseInfo mouse = Controls.getInstance().getMouse();
		
		float x = mouse.getPosition().x;
		float y = mouse.getPosition().y;
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
	
	@Override
	public int getLayer() {
		return Layers.NONE.val();
	}
}
