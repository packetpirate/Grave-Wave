package com.grave.entities.enemies;

import com.grave.entities.Player;
import com.grave.math.Calculate;
import com.grave.math.Dice;
import com.grave.misc.Pair;
import com.grave.objects.crafting.Resources;
import com.grave.objects.items.Powerups;
import com.grave.states.GameState;
import com.grave.status.Status;

public class Rotdog extends Enemy {
	private static final int FIRST_WAVE = 2;
	private static final int SPAWN_COST = 2;
	private static final long ATTACK_DELAY = 1_000L;
	private static final float SPEED = 0.20f;

	private static final Dice HEALTH = new Dice(3, 4);
	private static final int HEALTH_MOD = 8;

	private static final Dice DAMAGE = new Dice(1, 4);
	private static final int DAMAGE_MOD = 2;

	public static final ResourceTable RESOURCES = new ResourceTable()
			.addResource(Resources.METAL, 0.005f)
			.addResource(Resources.CLOTH, 0.025f)
			.addResource(Resources.WOOD, 0.01f);

	public static final LootTable LOOT = new LootTable()
			.addItem(Powerups.Type.HEALTH, 0.10f)
			.addItem(Powerups.Type.SPEED, 0.05f)
			.addItem(Powerups.Type.CRIT_CHANCE, 0.05f);

	public Rotdog(Pair<Float> position_) {
		super(EnemyType.ROTDOG, position_);

		this.health = Rotdog.HEALTH.roll(Rotdog.HEALTH_MOD);
		this.speed = Rotdog.SPEED;
		this.animation.addState("attack", type.createLayerAnimation(1, 4, 200L, -1L, -1L));
	}

	@Override
	public void move(GameState gs, int delta) {
		if(!statusHandler.hasStatus(Status.PARALYSIS)) {
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
	public double getDamage() { return Rotdog.DAMAGE.roll(Rotdog.DAMAGE_MOD); }

	@Override
	public void resetSpeed() { speed = Rotdog.SPEED; }

	@Override
	public long getAttackDelay() { return Rotdog.ATTACK_DELAY; }

	public static int appearsOnWave() { return Rotdog.FIRST_WAVE; }

	public static int getSpawnCost() { return Rotdog.SPAWN_COST; }

	@Override
	public String getName() {
		return "Rotdog";
	}

	@Override
	public String getDescription() {
		return "Rotdog";
	}

	@Override
	public String print() {
		return String.format("%s at (%.2f, %.2f) - %.2f health",
							 getName(), position.x, position.y, health);
	}

	@Override
	public ResourceTable getResourceTable() { return Rotdog.RESOURCES; }

	@Override
	public LootTable getLootTable() { return Rotdog.LOOT; }
}
