package com.gzsr.entities.enemies;

import com.gzsr.entities.Player;
import com.gzsr.math.Calculate;
import com.gzsr.math.Dice;
import com.gzsr.misc.Pair;
import com.gzsr.objects.items.Powerups;
import com.gzsr.states.GameState;
import com.gzsr.status.Status;

public class TinyZumby extends Enemy {
	private static final int SPAWN_COST = 0;
	private static final int MIN_HEALTH_COUNT = 2;
	private static final int MIN_HEALTH_SIDES = 4;
	private static final int MIN_HEALTH_MOD = 2;
	private static final int DAMAGE = 1;
	private static final float SPEED = 0.20f;
	private static final long ATTACK_DELAY = 500L;
	
	public static final LootTable LOOT = new LootTable()
			.addItem(Powerups.Type.HEALTH, 0.025f)
			.addItem(Powerups.Type.EXTRA_LIFE, 0.0125f)
			.addItem(Powerups.Type.NIGHT_VISION, 0.025f)
			.addItem(Powerups.Type.UNLIMITED_AMMO, 0.0125f);
	
	public TinyZumby(Pair<Float> position) {
		super(EnemyType.LIL_ZUMBY, position);
		
		this.health = Dice.roll(TinyZumby.MIN_HEALTH_COUNT, TinyZumby.MIN_HEALTH_SIDES, TinyZumby.MIN_HEALTH_MOD);
	}

	@Override
	public void move(GameState gs, int delta) {
		if(!statusHandler.hasStatus(Status.PARALYSIS)) {
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
	public double getDamage() { return TinyZumby.DAMAGE; }
	
	@Override
	public long getAttackDelay() { return TinyZumby.ATTACK_DELAY; }
	
	@Override
	public float getSpeed() { return TinyZumby.SPEED; }
	
	public static int getSpawnCost() { return TinyZumby.SPAWN_COST; }

	@Override
	public String getName() {
		return "Tiny Zumby";
	}

	@Override
	public String getDescription() {
		return "Tiny Zumby";
	}
	
	@Override
	public String print() {
		return String.format("%s at (%.2f, %.2f) - %.2f health",
							 getName(), position.x, position.y, health);
	}
	
	@Override
	public LootTable getLootTable() { return TinyZumby.LOOT; }
}
