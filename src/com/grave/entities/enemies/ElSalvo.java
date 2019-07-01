package com.grave.entities.enemies;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
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
import com.grave.objects.weapons.DamageType;
import com.grave.objects.weapons.Explosion;
import com.grave.states.GameState;
import com.grave.status.Status;

public class ElSalvo extends Enemy {
	public static final int FIRST_WAVE = 35;
	private static final int SPAWN_COST = 12;
	private static final float SPEED = 0.2f;
	private static final float ATTACK_DIST = 100.0f;
	private static final double EXPLODE_DAMAGE = 250.0f;
	private static final float EXPLODE_RADIUS = 256.0f;
	private static final long EXPLOSION_DELAY = 500L;
	private static final long FLASH_DURATION = 100L;
	private static final long FLASH_LENGTH = 50L;

	private static final Dice HEALTH = new Dice(5, 10);
	private static final int HEALTH_MOD = 50;

	public static final ResourceTable RESOURCES = new ResourceTable()
			.addResource(Resources.METAL, 0.1f)
			.addResource(Resources.CLOTH, 0.1f)
			.addResource(Resources.POWER, 0.25f);

	public static final LootTable LOOT = new LootTable()
			.addItem(Powerups.Type.HEALTH, 0.25f)
			.addItem(Powerups.Type.AMMO, 0.25f)
			.addItem(Powerups.Type.EXTRA_LIFE, 0.05f)
			.addItem(Powerups.Type.CRIT_CHANCE, 0.10f)
			.addItem(Powerups.Type.EXP_MULTIPLIER, 0.10f)
			.addItem(Powerups.Type.NIGHT_VISION, 0.025f)
			.addItem(Powerups.Type.UNLIMITED_AMMO, 0.025f);

	private Sound explode;

	private boolean exploding, flashing, exploded;
	private long flashStart;
	private long lastFlash;

	public ElSalvo(Pair<Float> position_) {
		super(EnemyType.ELSALVO, position_);
		this.health = ElSalvo.HEALTH.roll(ElSalvo.HEALTH_MOD);
		this.speed = ElSalvo.SPEED;
		this.explode = AssetManager.getManager().getSound("explosion2");

		this.damageImmunities.add(DamageType.FIRE);
		this.damageImmunities.add(DamageType.CONCUSSIVE);
		this.statusHandler.addImmunity(Status.BURNING);

		exploding = false;
		flashing = false;
		exploded = false;
		flashStart = 0L;
		lastFlash = 0L;
	}

	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
		if(exploding) {
			long elapsed = (cTime - lastFlash);
			if((cTime - flashStart) >= EXPLOSION_DELAY) {
				explode((GameState)gs, cTime);
			} else if(elapsed >= FLASH_DURATION) {
				flashing = true;
				lastFlash = cTime;
			} else if(elapsed >= FLASH_LENGTH) {
				flashing = false;
			}
		} else if(isAlive(cTime)) {
			Player player = Player.getPlayer();
			// Need to make sure to update the status effects first.
			statusHandler.update((GameState)gs, cTime, delta);

			updateFlash(cTime);
			theta = Calculate.Hypotenuse(position, player.getPosition());
			if(!nearPlayer(ElSalvo.ATTACK_DIST)) {
				animation.getCurrentAnimation().update(cTime);
				if(player.isAlive() && !touchingPlayer()) move((GameState)gs, delta);
			} else {
				exploding = true;
				flashing = true;
				flashStart = cTime;
				lastFlash = cTime;
			}
		}

		postDamageTexts();
	}

	@Override
	public void render(Graphics g, long cTime) {
		float pTheta = Calculate.Hypotenuse(position, Player.getPlayer().getPosition());
		if(!dead()) animation.getCurrentAnimation().render(g, position, pTheta, (flashing || shouldDrawFlash(cTime)));
		statusHandler.render(g, cTime);

		if(Globals.SHOW_COLLIDERS) {
			g.setColor(Color.red);
			g.draw(bounds);
		}
	}

	private void explode(GameState gs, long cTime) {
		Explosion exp = new Explosion(Explosion.Type.NORMAL, "GZS_Explosion",
									  new Pair<Float>(position.x, position.y),
									  ElSalvo.EXPLODE_DAMAGE, false, 0.0f,
									  ElSalvo.EXPLODE_RADIUS, cTime);
		gs.getLevel().addEntity("explosion", exp);

		explode.play(1.0f, AssetManager.getManager().getSoundVolume());
		health = 0.0;
		exploded = true;
	}

	@Override
	public boolean isAlive(long cTime) {
		return (!exploded && !dead());
	}

	@Override
	public void move(GameState gs, int delta) {
		if(!statusHandler.hasStatus(Status.PARALYSIS)) {
			velocity.x = (float)Math.cos(theta) * ElSalvo.SPEED * delta;
			velocity.y = (float)Math.sin(theta) * ElSalvo.SPEED * delta;

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
	public void resetSpeed() { speed = ElSalvo.SPEED; }

	@Override
	public int getExpValue() { return (exploded ? 0 : experience); }

	@Override
	public long getAttackDelay() { return 0L; }

	public static int appearsOnWave() { return ElSalvo.FIRST_WAVE; }

	public static int getSpawnCost() { return ElSalvo.SPAWN_COST; }

	@Override
	public String getName() {
		return "El Salvo";
	}

	@Override
	public String getDescription() {
		return "El Salvo";
	}

	@Override
	public String print() {
		return String.format("%s at (%.2f, %.2f) - %.2f health - Exploded? %s",
							 getName(), position.x, position.y, health, (exploded ? "Yes" : "No"));
	}

	@Override
	public ResourceTable getResourceTable() { return ElSalvo.RESOURCES; }

	@Override
	public LootTable getLootTable() { return ElSalvo.LOOT; }
}
