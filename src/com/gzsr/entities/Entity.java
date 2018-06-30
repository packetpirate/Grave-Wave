package com.gzsr.entities;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.BasicGameState;

public interface Entity {
	public abstract String getName();
	public abstract String getDescription();
	public abstract void update(BasicGameState gs, long cTime, int delta);
	public abstract void render(Graphics g, long cTime);
}
