package com.gzsr.entities.enemies;

import com.gzsr.Globals;
import com.gzsr.math.Calculate;
import com.gzsr.misc.Pair;

public class Rotdog extends Enemy {
	private static final float HEALTH = 50.0f;
	private static final float SPEED = 0.30f;
	private static final float DPS = 8.0f;
	
	public Rotdog(Pair<Float> position_) {
		super(EnemyType.ROTDOG, position_);
		this.health = Rotdog.HEALTH;
	}

	@Override
	public boolean isAlive(long cTime) {
		return (health > 0);
	}

	@Override
	public void move(int delta) {
		theta = Calculate.Hypotenuse(position, Globals.player.getPosition());
		
		position.x += (float)Math.cos(theta) * Rotdog.SPEED * delta;
		position.y += (float)Math.sin(theta) * Rotdog.SPEED * delta;
		
		bounds.setCenterX(position.x);
		bounds.setCenterY(position.y);
	}

	@Override
	public void takeDamage(double amnt) {
		health -= amnt;
	}
	
	@Override
	public double getDamage() {
		return Rotdog.DPS;
	}
}
