package com.gzsr.gfx.ui;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.util.FontUtils;

import com.gzsr.AssetManager;
import com.gzsr.entities.Entity;
import com.gzsr.misc.Pair;

public class VanishingText implements Entity {
	protected String text;
	protected String font;
	protected Pair<Float> position;
	protected Color color;
	
	protected long creationTime;
	protected long duration;
	
	protected boolean active;
	public boolean isActive() { return active; }
	
	/**
	 * Draws a floating piece of text on the screen.
	 * @param text The text to draw on the screen.
	 * @param position The center of the text to draw.
	 * @param color The color of the text to draw.
	 */
	public VanishingText(String text_, String font_, Pair<Float> position_, Color color_, long creationTime_, long duration_) {
		this.text = text_;
		this.font = font_;
		
		this.position = position_;
		
		this.color = new Color(color_);
		
		this.creationTime = creationTime_;
		this.duration = duration_;
		this.active = true;
	}

	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
		long elapsed = (cTime - creationTime);
		if(elapsed > duration) active = false; 
	}

	@Override
	public void render(Graphics g, long cTime) {
		if(isActive()) {
			g.setFont(AssetManager.getManager().getFont(font));
			g.setColor(color);
			
			float w = g.getFont().getWidth(text);
			float h = g.getFont().getLineHeight();
			float x = (position.x - (w / 2));
			float y = (position.y - (h / 2));
			
			long elapsed = (cTime - creationTime);
			float percentageTimeLeft = ((float)elapsed / (float)duration);
			color.a = (1.0f - percentageTimeLeft);
			
			FontUtils.drawCenter(g.getFont(), text, (int)x, (int)y, (int)w, color);
		}
	}
	
	@Override
	public String getName() {
		return "Floating Text";
	}

	@Override
	public String getDescription() {
		return "Text displayed on the screen.";
	}
}
