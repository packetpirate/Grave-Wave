package com.gzsr.entities.enemies;

import com.gzsr.entities.Player;
import com.gzsr.math.Calculate;
import com.gzsr.math.Dice;
import com.gzsr.misc.Pair;
import com.gzsr.objects.items.Powerups;
import com.gzsr.states.GameState;
import com.gzsr.status.Status;

public class Zumby extends Enemy {
	private static final int FIRST_WAVE = 1;
	private static final int SPAWN_COST = 1;
	private static final int MIN_HEALTH_COUNT = 2;
	private static final int MIN_HEALTH_SIDES = 8;
	private static final int MIN_HEALTH_MOD = 12;
	private static final int MIN_DAMAGE_COUNT = 1;
	private static final int MIN_DAMAGE_SIDES = 4;
	private static final float SPEED = 0.10f;
	private static final long ATTACK_DELAY = 1_000L;
	
	public static final LootTable LOOT = new LootTable()
			.addItem(Powerups.Type.HEALTH, 0.05f)
			.addItem(Powerups.Type.AMMO, 0.025f);
	
	public Zumby(Pair<Float> position_) {
		super(EnemyType.ZUMBY, position_);
		
		this.health = Dice.roll(Zumby.MIN_HEALTH_COUNT, Zumby.MIN_HEALTH_SIDES, Zumby.MIN_HEALTH_MOD);
		this.damage = new Dice(Zumby.MIN_DAMAGE_COUNT, Zumby.MIN_DAMAGE_SIDES);
		this.animation.addState("attack", type.createLayerAnimation(1, 4, 200L, -1L, -1L));
	}

	@Override
	public void move(GameState gs, int delta) {
		if(!statusHandler.hasStatus(Status.PARALYSIS)) {
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
	public long getAttackDelay() { return Zumby.ATTACK_DELAY; }
	
	@Override
	public float getSpeed() { return Zumby.SPEED; }
	
	public static int appearsOnWave() { return Zumby.FIRST_WAVE; }
	
	public static int getSpawnCost() { return Zumby.SPAWN_COST; }

	@Override
	public String getName() {
		return "Zumby";
	}
	
	@Override
	public String getDescription() {
		return "Zumby";
	}
	
	@Override
	public LootTable getLootTable() { return Zumby.LOOT; }
}
