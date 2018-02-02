package com.gzsr.entities.enemies;

import com.gzsr.Globals;
import com.gzsr.math.Calculate;
import com.gzsr.misc.Pair;

public class TinyZumby extends Enemy {
	private static final float HEALTH = 25.0f;
	private static final float SPEED = 0.20f;
	private static final float DPS = 2.0f;
	
	public TinyZumby(Pair<Float> position) {
		super(EnemyType.LIL_ZUMBY, position);
		this.health = TinyZumby.HEALTH;
	}
	
	@Override
	public boolean isAlive(long cTime) {
		return (health > 0);
	}

	@Override
	public void move(int delta) {
		theta = Calculate.Hypotenuse(position, Globals.player.getPosition());
		
		if(!moveBlocked) {
			position.x += (float)Math.cos(theta) * TinyZumby.SPEED * delta;
			position.y += (float)Math.sin(theta) * TinyZumby.SPEED * delta;
		}
		
		moveBlocked = false;
		
		bounds.setCenterX(position.x);
		bounds.setCenterY(position.y);
	}

	@Override
	public void takeDamage(double amnt) {
		health -= amnt;
	}

	@Override
	public double getDamage() {
		return TinyZumby.DPS;
	}

	@Override
	public String getName() {
		return "Tiny Zumby";
	}

	@Override
	public String getDescription() {
		return "Tiny Zumby";
	}
}
