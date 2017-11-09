package com.gzsr.entities.enemies;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;

import com.gzsr.Globals;
import com.gzsr.entities.Entity;
import com.gzsr.gfx.Animation;
import com.gzsr.misc.Pair;
import com.gzsr.states.GameState;

public abstract class Enemy implements Entity {
	// TODO: Add support for enemies having status effects.
	protected EnemyType type;
	protected Animation animation;
	protected Shape bounds;
	protected Pair<Float> position;
	public Pair<Float> getPosition() { return position; }
	protected float theta;
	protected boolean moveBlocked;
	public void blockMovement() { moveBlocked = true; }
	
	protected double health;
	protected int cash;
	public int getCashValue() { return cash; }
	protected int experience;
	public int getExpValue() { return experience; }
	
	public Enemy(EnemyType type_, Pair<Float> position_) {
		this.type = type_;
		this.animation = type.getAnimation();
		this.position = position_;
		
		float w = animation.getSrcSize().x;
		float h = animation.getSrcSize().y;
		this.bounds = new Rectangle((position.x - (w / 2)), (position.y - (h / 2)), w, h);
		
		this.moveBlocked = false;
		this.theta = 0.0f;
		this.health = 0.0;
		this.cash = type.getCashValue();
		this.experience = type.getExperience();
	}
	
	public abstract boolean isAlive(long cTime);
	
	@Override
	public void update(GameState gs, long cTime, int delta) {
		// All enemies should update.
		if(isAlive(cTime)) {
			animation.update(cTime);
			move(delta);
		}
	}
	
	public abstract void move(int delta);

	@Override
	public void render(Graphics g, long cTime) {
		// All enemies should render their animation.
		if(isAlive(cTime)) animation.render(g, position, theta);
		
		if(Globals.SHOW_COLLIDERS) {
			g.setColor(Color.red);
			g.draw(bounds);
		}
	}
	
	public Shape getCollider() { return bounds; }
	
	public abstract void takeDamage(double amnt);
	public void onDeath(GameState gs, long cTime) {}
	public abstract double getDamage();
}
