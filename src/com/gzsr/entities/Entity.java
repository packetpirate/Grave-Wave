package com.gzsr.entities;

import org.newdawn.slick.Graphics;

import com.gzsr.states.GameState;

public interface Entity {
	public abstract String getName();
	public abstract void update(GameState gs, long cTime, int delta);
	public abstract void render(Graphics g, long cTime);
}
