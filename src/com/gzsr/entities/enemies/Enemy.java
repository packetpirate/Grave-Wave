package com.gzsr.entities.enemies;

import org.newdawn.slick.Graphics;

import com.gzsr.entities.Entity;
import com.gzsr.entities.Player;
import com.gzsr.gfx.Animation;
import com.gzsr.misc.Pair;

public abstract class Enemy implements Entity {
	protected EnemyType type;
	protected Animation animation;
	protected Pair<Float> position;
	protected float theta;
	
	protected double health;
	protected int cash;
	public int getCashValue() { return cash; }
	protected int experience;
	public int getExpValue() { return experience; }
	
	public Enemy(EnemyType type_, Pair<Float> position_) {
		this.type = type_;
		this.animation = type.getAnimation();
		this.position = position_;
		this.theta = 0.0f;
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
	public void render(Graphics g, long cTime) {
		// All enemies should render their animation.
		if(isAlive(cTime)) animation.render(g, position, theta);
	}
	
	public abstract boolean checkCollision(Pair<Float> p);
	public abstract void takeDamage(double amnt);
	public abstract double getDamage();
}
