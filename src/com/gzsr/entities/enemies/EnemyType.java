package com.gzsr.entities.enemies;

import com.gzsr.Globals;
import com.gzsr.gfx.Animation;

public enum EnemyType {
	// Enemies
	ZUMBY("GZS_Zumby2", 48, 48, 4, 200, 30, 50, 25),
	CHUCK("GZS_Upchuck2", 64, 64, 4, 400, 100, 140, 100),
	GASBAG("GZS_Gasbag2", 48, 48, 4, 400, 100, 140, 100),
	ROTDOG("GZS_Rotdog2", 48, 48, 4, 150, 40, 60, 50),
	BIG_MAMA("GZS_BigMama2", 64, 64, 4, 400, 300, 500, 500),
	LIL_ZUMBY("GZS_TinyZumby", 24, 24, 4, 200, 5, 15, 10),
	
	// Bosses
	ABERRATION("GZS_Aberration2", 128, 128, 4, 150, 1_000, 1_500, 5_000), 
	ZOMBAT_SWARM("GZS_Zombat", 64, 64, 4, 50, 300, 600, 1_000),
	STITCHES("GZS_Stitches", 128, 128, 4, 150, 2_000, 4_000, 10_000);
	
	private String animationName;
	private int frameWidth;
	private int frameHeight;
	private int frameCount;
	private long frameDelay;
	public Animation getAnimation() { return new Animation(animationName, frameWidth, frameHeight, frameCount, frameDelay); }
	
	private int cashMin, cashMax;
	public int getCashValue() { return (Globals.rand.nextInt(cashMax - cashMin) + cashMin); }
	
	private int experience;
	public int getExperience() { return experience; }
	
	EnemyType(String animationName_, int frameWidth_, int frameHeight_, int frameCount_, long frameDelay_, int cashMin_, int cashMax_, int experience_) {
		this.animationName = animationName_;
		this.frameWidth = frameWidth_;
		this.frameHeight = frameHeight_;
		this.frameCount = frameCount_;
		this.frameDelay = frameDelay_;
		this.cashMin = cashMin_;
		this.cashMax = cashMax_;
		this.experience = experience_;
	}
}
