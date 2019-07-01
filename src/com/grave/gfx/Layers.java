package com.grave.gfx;

public enum Layers {
	NONE(-1),
	BACKGROUND(0),
	BLOOD(1),
	ITEMS(2),
	PARTICLES(3),
	ENEMIES(4),
	PLAYER(5),
	FLASHLIGHT(6),
	TEXT(7),
	HUD(8);
	
	private int layer;
	public int val() { return layer; }
	
	Layers(int layer_) {
		this.layer = layer_;
	}
}
