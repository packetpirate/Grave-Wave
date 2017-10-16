package com.gzsr.entities.enemies;

import com.gzsr.gfx.Animation;

public enum EnemyType {
	ZUMBY("GZS_Zumby2", 48, 48, 4, 200, 25, 25),
	CHUCK("GZS_Upchuck2", 64, 64, 4, 400, 100, 100),
	GASBAG("GZS_Gasbag2", 48, 48, 4, 400, 100, 100),
	ROTDOG("GZS_Rotdog2", 48, 48, 4, 150, 40, 50),
	BIG_MAMA("GZS_BigMama2", 64, 64, 4, 400, 400, 500),
	LIL_ZUMBY("GZS_TinyZumby", 24, 24, 4, 200, 5, 10);
	
	private String animationName;
	private int frameWidth;
	private int frameHeight;
	private int frameCount;
	private long frameDelay;
	public Animation getAnimation() { return new Animation(animationName, frameWidth, frameHeight, frameCount, frameDelay); }
	
	private int cash;
	public int getCashValue() { return cash; }
	
	private int experience;
	public int getExperience() { return experience; }
	
	EnemyType(String animationName_, int frameWidth_, int frameHeight_, int frameCount_, long frameDelay_, int cash_, int experience_) {
		this.animationName = animationName_;
		this.frameWidth = frameWidth_;
		this.frameHeight = frameHeight_;
		this.frameCount = frameCount_;
		this.frameDelay = frameDelay_;
		this.cash = cash_;
		this.experience = experience_;
	}
}
