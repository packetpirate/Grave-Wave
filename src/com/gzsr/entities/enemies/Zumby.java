package com.gzsr.entities.enemies;

import com.gzsr.Globals;
import com.gzsr.math.Calculate;
import com.gzsr.misc.Pair;

public class Zumby extends Enemy {
	private static final int FIRST_WAVE = 1;
	private static final int SPAWN_COST = 1;
	private static final float HEALTH = 100.0f;
	private static final float SPEED = 0.10f;
	private static final float DPS = 5.0f;
	
	public Zumby(Pair<Float> position_) {
		super(EnemyType.ZUMBY, position_);
		this.health = Zumby.HEALTH;
	}

	@Override
	public void move(int delta) {
		theta = Calculate.Hypotenuse(position, Globals.player.getPosition());
		
		if(!moveBlocked) {
			position.x += (float)Math.cos(theta) * Zumby.SPEED * delta;
			position.y += (float)Math.sin(theta) * Zumby.SPEED * delta;
		}
		
		moveBlocked = false;
		
		bounds.setCenterX(position.x);
		bounds.setCenterY(position.y);
	}
	
	@Override
	public double getDamage() {
		return Zumby.DPS;
	}
	
	@Override
	public float getSpeed() {
		return Zumby.SPEED;
	}
	
	public static int appearsOnWave() {
		return FIRST_WAVE;
	}
	
	public static int getSpawnCost() {
		return Zumby.SPAWN_COST;
	}

	@Override
	public String getName() {
		return "Zumby";
	}
	
	@Override
	public String getDescription() {
		return "Zumby";
	}
}
