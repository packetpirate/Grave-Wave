package com.gzsr.entities.enemies;

import com.gzsr.entities.Player;
import com.gzsr.math.Calculate;
import com.gzsr.misc.Pair;
import com.gzsr.objects.items.Powerups;
import com.gzsr.states.GameState;

public class TinyZumby extends Enemy {
	private static final int SPAWN_COST = 0;
	private static final float HEALTH = 25.0f;
	private static final float SPEED = 0.20f;
	private static final float DPS = 2.0f;
	
	public static final LootTable LOOT = new LootTable()
			.addItem(Powerups.Type.HEALTH, 0.25f)
			.addItem(Powerups.Type.EXTRA_LIFE, 0.05f)
			.addItem(Powerups.Type.NIGHT_VISION, 0.30f)
			.addItem(Powerups.Type.UNLIMITED_AMMO, 0.10f);
	
	public TinyZumby(Pair<Float> position) {
		super(EnemyType.LIL_ZUMBY, position);
		this.health = TinyZumby.HEALTH;
	}

	@Override
	public void move(GameState gs, int delta) {
		theta = Calculate.Hypotenuse(position, Player.getPlayer().getPosition());
		velocity.x = (float)Math.cos(theta) * TinyZumby.SPEED * delta;
		velocity.y = (float)Math.sin(theta) * TinyZumby.SPEED * delta;

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
		return TinyZumby.DPS;
	}
	
	@Override
	public float getSpeed() {
		return TinyZumby.SPEED;
	}
	
	public static int getSpawnCost() {
		return TinyZumby.SPAWN_COST;
	}

	@Override
	public String getName() {
		return "Tiny Zumby";
	}

	@Override
	public String getDescription() {
		return "Tiny Zumby";
	}
	
	@Override
	public LootTable getLootTable() {
		return TinyZumby.LOOT;
	}
}
