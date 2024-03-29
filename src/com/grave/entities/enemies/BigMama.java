package com.grave.entities.enemies;

import org.newdawn.slick.Sound;
import org.newdawn.slick.state.BasicGameState;

import com.grave.AssetManager;
import com.grave.Globals;
import com.grave.entities.Player;
import com.grave.math.Calculate;
import com.grave.math.Dice;
import com.grave.misc.Pair;
import com.grave.objects.crafting.Resources;
import com.grave.objects.items.Powerups;
import com.grave.objects.weapons.Explosion;
import com.grave.states.GameState;
import com.grave.status.Status;

public class BigMama extends Enemy {
	private static final int FIRST_WAVE = 15;
	private static final int SPAWN_COST = 10;
	private static final float SPEED = 0.10f;
	private static final long LIFESPAN = 10_000L;
	private static final float EXP_DIST = 128.0f;
	private static final double EXP_DAMAGE = 75.0f;
	private static final float EXP_KNOCKBACK = 10.0f;
	private static final int ZUMBY_COUNT = 10;

	private static final Dice HEALTH = new Dice(2, 10);
	private static final int HEALTH_MOD = 30;

	public static final ResourceTable RESOURCES = new ResourceTable()
			.addResource(Resources.METAL, 0.15f)
			.addResource(Resources.CLOTH, 0.2f)
			.addResource(Resources.GLASS, 0.05f)
			.addResource(Resources.WOOD, 0.025f)
			.addResource(Resources.ELECTRONICS, 0.15f)
			.addResource(Resources.POWER, 0.2f);

	public static final LootTable LOOT = new LootTable()
			.addItem(Powerups.Type.HEALTH, 0.35f)
			.addItem(Powerups.Type.AMMO, 0.25f)
			.addItem(Powerups.Type.EXTRA_LIFE, 0.05f)
			.addItem(Powerups.Type.CRIT_CHANCE, 0.05f)
			.addItem(Powerups.Type.EXP_MULTIPLIER, 0.05f)
			.addItem(Powerups.Type.INVULNERABILITY, 0.025f)
			.addItem(Powerups.Type.NIGHT_VISION, 0.05f)
			.addItem(Powerups.Type.UNLIMITED_AMMO, 0.05f);

	private Sound explosion;

	private long created;
	private boolean exploded;

	public BigMama(Pair<Float> position) {
		super(EnemyType.BIG_MAMA, position);
		this.health = BigMama.HEALTH.roll(BigMama.HEALTH_MOD);
		this.speed = BigMama.SPEED;

		this.statusHandler.addImmunity(Status.PARALYSIS);

		explosion = AssetManager.getManager().getSound("explosion2");

		created = -1L;
		exploded = false;
	}

	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
		Player player = Player.getPlayer();

		if(created == -1L) created = cTime; // So that we don't have to pass spawn time into constructor.
		if(!exploded) {
			theta = Calculate.Hypotenuse(position, player.getPosition());

			// Has the Big Mama's lifespan elapsed, or have they been killed? Are they near the player?
			boolean timesUp = (cTime - created) >= BigMama.LIFESPAN;
			boolean dead = health <= 0;
			if(nearPlayer(BigMama.EXP_DIST) || timesUp || dead) {
				// Big Mama self-detonates.
				EnemyController ec = EnemyController.getInstance();

				// Spawn zumbies around the Big Mama's explosion area.
				for(int i = 0; i < BigMama.ZUMBY_COUNT; i++) {
					float d = Globals.rand.nextFloat() * BigMama.EXP_DIST;
					float t = Globals.rand.nextFloat() * (float)(Math.PI * 2);
					float x = position.x + (d * (float)Math.cos(t));
					float y = position.y + (d * (float)Math.sin(t));

					TinyZumby tz = new TinyZumby(new Pair<Float>(x, y));
					ec.addNextUpdate(tz);
				}

				// Spawn a blood explosion centered on the Big Mama.
				Explosion blood = new Explosion(Explosion.Type.BLOOD, "GZS_BloodExplosion",
												new Pair<Float>(position.x, position.y),
												BigMama.EXP_DAMAGE, false, BigMama.EXP_KNOCKBACK,
												BigMama.EXP_DIST, cTime);
				((GameState)gs).getLevel().addEntity(blood.getTag(), blood);

				exploded = true;
				explosion.play(1.0f, AssetManager.getManager().getSoundVolume());
			} else {
				// Need to make sure to update the status effects first.
				statusHandler.update((GameState)gs, cTime, delta);

				// If there are any damage texts recently added, add them to the entities list.
				postDamageTexts();

				updateFlash(cTime);
				animation.getCurrentAnimation().update(cTime);
				if(player.isAlive() && !touchingPlayer()) move((GameState)gs, delta);
			}
		}
	}

	@Override
	public boolean isAlive(long cTime) {
		return (!exploded && !dead());
	}

	@Override
	public void move(GameState gs, int delta) {
		if(!statusHandler.hasStatus(Status.PARALYSIS)) {
			velocity.x = (float)Math.cos(theta) * BigMama.SPEED * delta;
			velocity.y = (float)Math.sin(theta) * BigMama.SPEED * delta;

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
	public void resetSpeed() { speed = BigMama.SPEED; }

	@Override
	public long getAttackDelay() { return 0L; }

	public static int appearsOnWave() { return BigMama.FIRST_WAVE; }

	public static int getSpawnCost() { return BigMama.SPAWN_COST; }

	@Override
	public String getName() {
		return "Big Mama";
	}

	@Override
	public String getDescription() {
		return "Big Mama";
	}

	@Override
	public String print() {
		return String.format("%s at (%.2f, %.2f) - %.2f health - Exploded? %s",
							 getName(), position.x, position.y, health, (exploded ? "Yes" : "No"));
	}

	@Override
	public ResourceTable getResourceTable() { return BigMama.RESOURCES; }

	@Override
	public LootTable getLootTable() { return BigMama.LOOT; }
}
