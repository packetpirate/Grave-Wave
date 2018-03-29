package com.gzsr.gfx.ui;

import org.newdawn.slick.Graphics;

import com.gzsr.entities.Entity;
import com.gzsr.misc.Pair;
import com.gzsr.states.GameState;

public abstract class Button implements Entity {
	protected String image;
	protected Pair<Float> position;
	protected Pair<Float> size;
	
	protected String text;
	
	private boolean mouseIn;
	public boolean mouseOver() { return mouseIn; }
	public void mouseEnter() { mouseIn = true; }
	public void mouseExit() { mouseIn = false; }
	
	public Button() {
		image = null;
		text = null;
		
		mouseIn = false;
	}
	
	@Override
	public String getName() {
		return "Button";
	}
	
	@Override
	public String getDescription() {
		return "Button";
	}

	@Override
	public void update(GameState gs, long cTime, int delta) {
		// To be overridden.
	}

	@Override
	public void render(Graphics g, long cTime) {
		// To be overridden.
	}

	public Pair<Float> getPosition() { return position; }
	public void setPosition(Pair<Float> position_) { this.position = position_; }
	public Pair<Float> getSize() { return size; }
	public abstract void click();
	public abstract boolean inBounds(float x, float y);
}
