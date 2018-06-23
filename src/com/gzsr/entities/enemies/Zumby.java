package com.gzsr.entities.enemies;

import com.gzsr.entities.Player;
import com.gzsr.math.Calculate;
import com.gzsr.misc.Pair;
import com.gzsr.states.GameState;

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
	public void move(GameState gs, int delta) {
		theta = Calculate.Hypotenuse(position, Player.getPlayer().getPosition());
		velocity.x = (float)Math.cos(theta) * Zumby.SPEED * delta;
		velocity.y = (float)Math.sin(theta) * Zumby.SPEED * delta;

		avoidObstacles(gs, delta);
		
		if(!moveBlocked) {
			position.x += velocity.x;
			position.y += velocity.y;
		}
		
		moveBlocked = false;
		
		bounds.setCenterX(position.x);
		bounds.setCenterY(position.y);
	}
	
	@Override
	public float getCohesionDistance() {
		return (Math.min(type.getFrameWidth(), type.getFrameHeight()) * 2);
	}
	
	@Override
	public float getSeparationDistance() {
		return Math.min(type.getFrameWidth(), type.getFrameHeight());
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
