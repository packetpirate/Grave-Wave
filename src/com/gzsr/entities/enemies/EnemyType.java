package com.gzsr.entities.enemies;

import com.gzsr.Globals;
import com.gzsr.achievements.Metrics;
import com.gzsr.entities.enemies.bosses.Aberration;
import com.gzsr.entities.enemies.bosses.Stitches;
import com.gzsr.entities.enemies.bosses.Zombat;
import com.gzsr.gfx.Animation;
import com.gzsr.misc.Pair;

public enum EnemyType {
	// Enemies
	ZUMBY("GZS_Zumby3", 48, 48, 4, 200L, Metrics.ZUMBY, 10, 20, 25),
	CHUCK("GZS_Upchuck3", 64, 64, 4, 400L, Metrics.UPCHUCK, 40, 60, 100),
	GASBAG("GZS_Gasbag2", 48, 48, 4, 400L, Metrics.GASBAG, 50, 75, 100),
	ROTDOG("GZS_Rotdog3", 48, 48, 4, 150L, Metrics.ROTDOG, 20, 30, 50),
	BIG_MAMA("GZS_BigMama2", 64, 64, 4, 400L, Metrics.BIG_MAMA, 80, 120, 500),
	LIL_ZUMBY("GZS_TinyZumby", 24, 24, 4, 200L, Metrics.TINY_ZUMBY, 5, 10, 10),
	STARFRIGHT("GZS_Starfright", 48, 48, 4, 200L, Metrics.STARFRIGHT, 200, 250, 750),
	ELSALVO("GZS_ElSalvo", 64, 64, 4, 200L, Metrics.EL_SALVO, 300, 500, 1_500),
	PROWLER("GZS_Prowler", 48, 48, 4, 250L, Metrics.PROWLER, 400, 600, 2_500),
	
	// Bosses
	ABERRATION("GZS_Aberration2", 128, 128, 4, 150L, Metrics.ABERRATION, 2_000, 3_000, 5_000), 
	ZOMBAT_SWARM("GZS_Zombat", 64, 64, 4, 50L, Metrics.ZOMBAT, 2_000, 2_500, 1_000),
	STITCHES("GZS_Stitches", 128, 128, 4, 150L, Metrics.STITCHES, 5_000, 7_500, 10_000);
	
	private String animationName;
	private int frameWidth;
	public int getFrameWidth() { return frameWidth; }
	private int frameHeight;
	public int getFrameHeight() { return frameHeight; }
	private int frameCount;
	private long frameDelay;
	public Animation getAnimation() { return new Animation(animationName, frameWidth, frameHeight, frameCount, frameDelay); }
	
	private long enemyMetric;
	public long getEnemyMetric() { return enemyMetric; }
	
	private int cashMin, cashMax;
	public int getCashValue() { return (Globals.rand.nextInt(cashMax - cashMin) + cashMin); }
	
	private int experience;
	public int getExperience() { return experience; }
	
	public Animation createLayerAnimation(int layer, int count, long delay, long lifespan, long created) {
		return new Animation(animationName, layer, frameWidth, frameHeight, frameCount, frameDelay, lifespan, created);
	}
	
	EnemyType(String animationName_, int frameWidth_, int frameHeight_, int frameCount_, long frameDelay_, long enemyMetric_, int cashMin_, int cashMax_, int experience_) {
		this.animationName = animationName_;
		this.frameWidth = frameWidth_;
		this.frameHeight = frameHeight_;
		this.frameCount = frameCount_;
		this.frameDelay = frameDelay_;
		
		this.enemyMetric = enemyMetric_;
		this.cashMin = cashMin_;
		this.cashMax = cashMax_;
		this.experience = experience_;
	}
	
	public static int appearsOnWave(EnemyType type_) {
		switch(type_) {
			case ZUMBY: return Zumby.appearsOnWave();
			case ROTDOG: return Rotdog.appearsOnWave();
			case CHUCK: return Upchuck.appearsOnWave();
			case GASBAG: return Gasbag.appearsOnWave();
			case BIG_MAMA: return BigMama.appearsOnWave();
			case STARFRIGHT: return Starfright.appearsOnWave();
			case ELSALVO: return ElSalvo.appearsOnWave();
			case PROWLER: return Prowler.appearsOnWave();
			case ABERRATION: return Aberration.appearsOnWave();
			case ZOMBAT_SWARM: return Zombat.appearsOnWave();
			case STITCHES: return Stitches.appearsOnWave();
			default: return -1;
		}
	}
	
	public static int spawnCost(EnemyType type_) {
		switch(type_) {
			case ZUMBY: return Zumby.getSpawnCost();
			case ROTDOG: return Rotdog.getSpawnCost();
			case CHUCK: return Upchuck.getSpawnCost();
			case GASBAG: return Gasbag.getSpawnCost();
			case BIG_MAMA: return BigMama.getSpawnCost();
			case STARFRIGHT: return Starfright.getSpawnCost();
			case ELSALVO: return ElSalvo.getSpawnCost();
			case PROWLER: return Prowler.getSpawnCost();
			case ABERRATION: return Aberration.getSpawnCost();
			case ZOMBAT_SWARM: return Zombat.getSpawnCost();
			case STITCHES: return Stitches.getSpawnCost();
			default: return 1;
		}
	}
	
	public static Enemy createInstance(EnemyType type_, Pair<Float> position_) {
		switch(type_) {
			case ZUMBY: return new Zumby(position_);
			case ROTDOG: return new Rotdog(position_);
			case CHUCK: return new Upchuck(position_);
			case GASBAG: return new Gasbag(position_);
			case BIG_MAMA: return new BigMama(position_);
			case STARFRIGHT: return new Starfright(position_);
			case ELSALVO: return new ElSalvo(position_);
			case PROWLER: return new Prowler(position_);
			case ABERRATION: return new Aberration(position_);
			case ZOMBAT_SWARM: return new Zombat(position_);
			case STITCHES: return new Stitches(position_);
			default: return null;
		}
	}
}
