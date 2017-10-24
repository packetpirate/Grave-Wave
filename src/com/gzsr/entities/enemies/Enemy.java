package com.gzsr.entities.enemies;

import org.newdawn.slick.Graphics;

import com.gzsr.entities.Entity;
import com.gzsr.gfx.Animation;
import com.gzsr.misc.Pair;
import com.gzsr.states.GameState;

public abstract class Enemy implements Entity {
	// TODO: Add support for enemies having status effects.
	protected EnemyType type;
	protected Animation animation;
	protected Pair<Float> position;
	public Pair<Float> getPosition() { return position; }
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
	public void update(GameState gs, long cTime) {
		// All enemies should update.
		if(isAlive(cTime)) {
			animation.update(cTime);
			move();
		}
	}
	
	public abstract void move();

	@Override
	public void render(Graphics g, long cTime) {
		// All enemies should render their animation.
		if(isAlive(cTime)) animation.render(g, position, theta);
	}
	
	public abstract boolean checkCollision(Pair<Float> p);
	public abstract void takeDamage(double amnt);
	public abstract double getDamage();
}
