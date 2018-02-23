package com.gzsr.entities.enemies.bosses;

import com.gzsr.Globals;
import com.gzsr.entities.enemies.EnemyType;
import com.gzsr.math.Calculate;
import com.gzsr.misc.Pair;

public class Stitches extends Boss {
	private static final float HEALTH = 15_000.0f;
	private static final float SPEED = 0.10f;
	private static final float DPS = 20.0f;
	
	public Stitches(Pair<Float> position_) {
		super(EnemyType.STITCHES, position_);
		this.health = Stitches.HEALTH;
	}

	@Override
	public boolean isAlive(long cTime) {
		return (health > 0);
	}

	@Override
	public void move(int delta) {
		theta = Calculate.Hypotenuse(position, Globals.player.getPosition());
		
		if(!moveBlocked) {
			position.x += (float)Math.cos(theta) * Stitches.SPEED * delta;
			position.y += (float)Math.sin(theta) * Stitches.SPEED * delta;
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
		return Stitches.DPS;
	}

}
