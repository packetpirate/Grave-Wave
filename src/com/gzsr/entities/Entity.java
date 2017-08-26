package com.gzsr.entities;

import org.newdawn.slick.Graphics;

public interface Entity {
	public abstract void update(long cTime);
	public abstract void render(Graphics g, long cTime);
}
