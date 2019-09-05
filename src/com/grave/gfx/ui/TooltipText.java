package com.grave.gfx.ui;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.state.BasicGameState;

import com.grave.Controls;
import com.grave.Globals;
import com.grave.entities.Entity;
import com.grave.gfx.Layers;
import com.grave.math.Calculate;
import com.grave.misc.MouseInfo;
import com.grave.misc.Pair;
import com.grave.states.GameState;

public class TooltipText implements Entity {
	private static final float MAX_WIDTH = 250.0f;

	private UnicodeFont font;

	private String title;
	private String tooltip;

	private float textHeight;

	private Pair<Float> pos;
	private float size;

	public TooltipText(UnicodeFont font_, String title_, String tooltip_, Pair<Float> pos_, float size_) {
		this.font = font_;

		this.title = title_;
		this.tooltip = tooltip_;

		this.textHeight = (Calculate.TextHeight(tooltip_, font_, MAX_WIDTH) + font_.getHeight(title_) + 5.0f);

		this.pos = pos_;
		this.size = size_;
	}

	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
		// No need for update.
	}

	@Override
	public void render(GameState gs, Graphics g, long cTime) {
		// If the mouse cursor is hovering over the text, display the tooltip.
		MouseInfo mouse = Controls.getInstance().getMouse();
		if(isMouseInside()) {
			float mx = mouse.getPosition().x;
			float my = mouse.getPosition().y;
			float txtWidth = font.getWidth(tooltip);

			if(textOutOfBounds(mx, txtWidth)) mx -= (Math.min(txtWidth, MAX_WIDTH) + 10.0f);

			g.setColor(Color.gray);
			g.fillRect(mx, my, (Math.min(txtWidth, MAX_WIDTH) + 20.0f), (textHeight + 20.0f));
			g.setColor(Color.white);
			g.drawRect(mx, my, (Math.min(txtWidth, MAX_WIDTH) + 20.0f), (textHeight + 20.0f));

			g.setFont(font);
			g.setColor(Color.white);
			g.drawString(title, (mx + 10.0f), (my + 10.0f));
			Calculate.TextWrap(g, tooltip, font, (mx + 10.0f), (my + font.getHeight(title) + 15.0f),
							   MAX_WIDTH, false, Color.darkGray);
		}
	}

	private boolean textOutOfBounds(float x, float width) {
		return ((x + width) >= Globals.WIDTH);
	}

	private boolean isMouseInside() {
		MouseInfo mouse = Controls.getInstance().getMouse();

		float x = mouse.getPosition().x;
		float y = mouse.getPosition().y;

		return ((x >= (pos.x - (size / 2))) && (y >= (pos.y - (size / 2))) &&
				(x <= (pos.x + (size / 2))) && (y <= (pos.y + (size / 2))));
	}

	@Override
	public String getName() { return "Tooltip"; }

	@Override
	public String getTag() { return "tooltip"; }

	@Override
	public String getDescription() { return "Tooltip Text"; }

	@Override
	public int getLayer() { return Layers.NONE.val(); }
}
