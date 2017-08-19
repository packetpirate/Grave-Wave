package gzs.entities.enemies;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import gzs.entities.Entity;
import gzs.entities.Player;
import gzs.game.gfx.Animation;
import gzs.game.misc.Pair;

public abstract class Enemy implements Entity {
	protected EnemyType type;
	protected Animation animation;
	protected Pair<Double> position;
	protected double theta;
	
	protected double health;
	protected int cash;
	protected int experience;
	
	public Enemy(EnemyType type_, Pair<Double> position_) {
		this.type = type_;
		this.animation = type.getAnimation();
		this.position = position_;
		this.theta = 0.0;
		this.health = 0.0;
		this.cash = type.getCashValue();
		this.experience = type.getExperience();
	}
	
	public abstract boolean isAlive(long cTime);
	
	@Override
	public void update(long cTime) {
		// All enemies should update.
		if(isAlive(cTime)) animation.update(cTime);
	}
	
	public abstract void move(Player player);

	@Override
	public void render(SpriteBatch batch, ShapeRenderer sr, long cTime) {
		// All enemies should render their animation.
		if(isAlive(cTime)) animation.render(batch, position, theta, cTime);
	}
	
	public abstract boolean checkCollision(Pair<Double> p);
	public abstract void takeDamage(double amnt);
	public abstract double getDamage();
}