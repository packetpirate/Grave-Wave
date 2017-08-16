package gzs.entities.enemies;

import gzs.game.gfx.Animation;
import gzs.game.utils.FileUtilities;

public enum EnemyType {
	ZUMBY(new Animation(FileUtilities.LoadTexture("GZS_Zumby2.png"), 48, 48, 4, 200), 25, 25),
	CHUCK(new Animation(FileUtilities.LoadTexture("GZS_Upchuck2.png"), 64, 64, 4, 400), 100, 100),
	GASBAG(new Animation(FileUtilities.LoadTexture("GZS_Gasbag2.png"), 48, 48, 4, 400), 100, 100),
	ROTDOG(new Animation(FileUtilities.LoadTexture("GZS_Rotdog2.png"), 48, 48, 4, 150), 40, 50),
	BIG_MAMA(new Animation(FileUtilities.LoadTexture("GZS_BigMama2.png"), 64, 64, 4, 400), 400, 500),
	LIL_ZUMBY(new Animation(FileUtilities.LoadTexture("GZS_TinyZumby.png"), 24, 24, 4, 200), 5, 10);
	
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