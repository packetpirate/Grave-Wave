package com.gzsr.entities.enemies;

import com.gzsr.gfx.Animation;

public enum EnemyType {
	ZUMBY(new Animation("GZS_Zumby2", 48, 48, 4, 200), 25, 25),
	CHUCK(new Animation("GZS_Upchuck2", 64, 64, 4, 400), 100, 100),
	GASBAG(new Animation("GZS_Gasbag2", 48, 48, 4, 400), 100, 100),
	ROTDOG(new Animation("GZS_Rotdog2", 48, 48, 4, 150), 40, 50),
	BIG_MAMA(new Animation("GZS_BigMama2", 64, 64, 4, 400), 400, 500),
	LIL_ZUMBY(new Animation("GZS_TinyZumby", 24, 24, 4, 200), 5, 10);
	
	private Animation animation;
	public Animation getAnimation() { return animation; }
	
	private int cash;
	public int getCashValue() { return cash; }
	
	private int experience;
	public int getExperience() { return experience; }
	
	EnemyType(Animation animation_, int cash_, int experience_) {
		this.animation = animation_;
	}
}
