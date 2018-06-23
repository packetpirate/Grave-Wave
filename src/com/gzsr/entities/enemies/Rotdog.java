package com.gzsr.entities.enemies;

import com.gzsr.entities.Player;
import com.gzsr.math.Calculate;
import com.gzsr.misc.Pair;
import com.gzsr.states.GameState;

public class Rotdog extends Enemy {
	private static final int FIRST_WAVE = 2;
	private static final int SPAWN_COST = 2;
	private static final float HEALTH = 50.0f;
	private static final float SPEED = 0.20f;
	private static final float DPS = 8.0f;
	
	public Rotdog(Pair<Float> position_) {
		super(EnemyType.ROTDOG, position_);
		this.health = Rotdog.HEALTH;
	}

	@Override
	public void move(GameState gs, int delta) {
		theta = Calculate.Hypotenuse(position, Player.getPlayer().getPosition());
		velocity.x = (float)Math.cos(theta) * Rotdog.SPEED * delta;
		velocity.y = (float)Math.sin(theta) * Rotdog.SPEED * delta;

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
		return Rotdog.DPS;
	}
	
	@Override
	public float getSpeed() {
		return Rotdog.SPEED;
	}
	
	public static int appearsOnWave() {
		return FIRST_WAVE;
	}
	
	public static int getSpawnCost() {
		return Rotdog.SPAWN_COST;
	}

	@Override
	public String getName() {
		return "Rotdog";
	}
	
	@Override
	public String getDescription() {
		return "Rotdog";
	}
}
