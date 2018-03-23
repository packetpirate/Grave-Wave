package com.gzsr.entities.enemies;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;

import com.gzsr.Globals;
import com.gzsr.entities.Entity;
import com.gzsr.gfx.Animation;
import com.gzsr.math.Calculate;
import com.gzsr.misc.Pair;
import com.gzsr.states.GameState;

public abstract class Enemy implements Entity {
	private static long FLASH_DURATION = 100L;
	
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
	
	protected boolean hit;
	protected long hitTime;
	protected void updateFlash(long cTime) {
		if(hit) {
			long elapsed = (cTime - hitTime);
			if(elapsed > Enemy.FLASH_DURATION) {
				hit = false;
				hitTime = 0L;
			}
		}
	}
	protected boolean shouldDrawFlash(long cTime) {
		long elapsed = (cTime - hitTime);
		return (hit && (elapsed <= Enemy.FLASH_DURATION));
	}
	
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
		
		this.hit = false;
		this.hitTime = 0L;
	}
	
	public boolean dead() {
		return (health <= 0);
	}
	
	public boolean isAlive(long cTime) {
		return !dead();
	}
	
	@Override
	public void update(GameState gs, long cTime, int delta) {
		// All enemies should update.
		if(isAlive(cTime)) {
			updateFlash(cTime);
			animation.update(cTime);
			if(Globals.player.isAlive() && !touchingPlayer()) move(delta);
		}
	}
	
	protected boolean nearPlayer(float attackDist) {
		return (Calculate.Distance(position, Globals.player.getPosition()) <= attackDist);
	}
	
	public abstract void move(int delta);

	@Override
	public void render(Graphics g, long cTime) {
		// All enemies should render their animation.
		if(isAlive(cTime)) animation.render(g, position, theta, shouldDrawFlash(cTime));
		
		if(Globals.SHOW_COLLIDERS) {
			g.setColor(Color.red);
			g.draw(bounds);
		}
	}
	
	public Shape getCollider() { return bounds; }
	public boolean touchingPlayer() {
		return bounds.intersects(Globals.player.getCollider());
	}
	
	public void takeDamage(double amnt, float knockback, long cTime, int delta) {
		health -= amnt;
		hit = true;
		hitTime = cTime;
		
		if(knockback > 0.0f) {
			float dx = (float)(Math.cos(theta) * knockback * delta);
			float dy = (float)(Math.sin(theta) * knockback * delta);
			position.x += -dx;
			position.y += -dy;
		}
	}
	public void onDeath(GameState gs, long cTime) {}
	public abstract double getDamage();
	public abstract float getSpeed();
}
