package com.gzsr.entities.enemies;

import com.gzsr.Globals;
import com.gzsr.math.Calculate;
import com.gzsr.misc.Pair;

public class Zumby extends Enemy {
	private static final float COLLISION_DIST = 24.0f;
	private static final float HEALTH = 100.0f;
	private static final float SPEED = 0.10f;
	private static final float DPS = 2.0f;
	
	public Zumby(Pair<Float> position_) {
		super(EnemyType.ZUMBY, position_);
		this.health = Zumby.HEALTH;
	}

	@Override
	public boolean isAlive(long cTime) {
		return (health > 0);
	}

	@Override
	public void move(int delta) {
		theta = Calculate.Hypotenuse(position, Globals.player.getPosition());
		position.x += (float)Math.cos(theta) * Zumby.SPEED * delta;
		position.y += (float)Math.sin(theta) * Zumby.SPEED * delta;
	}

	@Override
	public void takeDamage(double amnt) {
		health -= amnt;
	}
	
	@Override
	public double getDamage() {
		return Zumby.DPS;
	}

	@Override
	public boolean checkCollision(Pair<Float> p) {
		return (Calculate.Distance(p, position) <= Zumby.COLLISION_DIST);
	}
}
