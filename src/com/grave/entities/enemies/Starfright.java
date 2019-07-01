package com.gzsr.entities.enemies;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.BasicGameState;

import com.gzsr.AssetManager;
import com.gzsr.Globals;
import com.gzsr.entities.Player;
import com.gzsr.math.Calculate;
import com.gzsr.math.Dice;
import com.gzsr.misc.Pair;
import com.gzsr.objects.crafting.Resources;
import com.gzsr.objects.items.Powerups;
import com.gzsr.objects.weapons.DamageType;
import com.gzsr.states.GameState;
import com.gzsr.status.DeafenedEffect;
import com.gzsr.status.FlashbangEffect;
import com.gzsr.status.Status;

public class Starfright extends Enemy {
	private static final int FIRST_WAVE = 25;
	private static final int SPAWN_COST = 10;
	private static final float SPEED = 0.30f;
	private static final float ATTACK_DIST = 100.0f;
	private static final float EFFECTIVE_FLASH_DIST = 200.0f;
	private static final long FLASHBANG_DURATION = 5_000L;
	private static final long EXPLOSION_DELAY = 500L;
	private static final long FLASH_DURATION = 100L;
	private static final long FLASH_LENGTH = 50L;

	private static final Dice HEALTH = new Dice(10, 6);
	private static final int HEALTH_MOD = 40;

	private static final Dice DAMAGE = new Dice(10, 4);
	private static final int DAMAGE_MOD = 40;

	public static final ResourceTable RESOURCES = new ResourceTable()
			.addResource(Resources.METAL, 0.025f)
			.addResource(Resources.CLOTH, 0.1f)
			.addResource(Resources.GLASS, 0.1f)
			.addResource(Resources.WOOD, 0.005f)
			.addResource(Resources.ELECTRONICS, 0.1f)
			.addResource(Resources.POWER, 0.1f);

	public static final LootTable LOOT = new LootTable()
			.addItem(Powerups.Type.HEALTH, 0.25f)
			.addItem(Powerups.Type.AMMO, 0.25f)
			.addItem(Powerups.Type.EXTRA_LIFE, 0.05f)
			.addItem(Powerups.Type.CRIT_CHANCE, 0.15f)
			.addItem(Powerups.Type.EXP_MULTIPLIER, 0.15f)
			.addItem(Powerups.Type.NIGHT_VISION, 0.25f);

	private boolean exploding, flashing, exploded;
	private long flashStart;
	private long lastFlash;

	public Starfright(Pair<Float> position_) {
		super(EnemyType.STARFRIGHT, position_);
		this.health = Starfright.HEALTH.roll(Starfright.HEALTH_MOD);
		this.speed = Starfright.SPEED;

		this.damageImmunities.add(DamageType.CONCUSSIVE);
		this.statusHandler.addImmunity(Status.PARALYSIS);

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
				explode(cTime);
			} else if(elapsed >= FLASH_DURATION) {
				flashing = true;
				lastFlash = cTime;
			} else if(elapsed >= FLASH_LENGTH) {
				flashing = false;
			}
		} else if(!dead()) {
			statusHandler.update((GameState)gs, cTime, delta);

			Player player = Player.getPlayer();
			theta = Calculate.Hypotenuse(position, player.getPosition());
			if(!nearPlayer(Starfright.ATTACK_DIST)) {
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

	private void explode(long cTime) {
		Player player = Player.getPlayer();
		AssetManager assets = AssetManager.getManager();

		assets.getSound("explosion2").play(1.0f, assets.getSoundVolume());
		if(player.getFlashlight().inView(getCollider())) {
			// If the player can see Starfright, apply the flashbang effect.
			FlashbangEffect flashbang = new FlashbangEffect(Starfright.FLASHBANG_DURATION, cTime);
			player.getStatusHandler().addStatus(flashbang, cTime);
		} else {
			// Otherwise, still deafen the player.
			DeafenedEffect deafened = new DeafenedEffect(Starfright.FLASHBANG_DURATION, cTime);
			player.getStatusHandler().addStatus(deafened, cTime);
		}

		double dmg = Starfright.DAMAGE.roll(Starfright.DAMAGE_MOD);
		float dist = Calculate.Distance(position, player.getPosition());
		double total = (1.0f - (dist / Starfright.EFFECTIVE_FLASH_DIST)) * dmg;
		if(total < 0.0) total = 0.0;
		player.takeDamage(total, cTime);

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
			velocity.x = (float)Math.cos(theta) * getSpeed() * delta;
			velocity.y = (float)Math.sin(theta) * getSpeed() * delta;

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
	public float getCohesionDistance() { return 0.0f; }

	@Override
	public float getSeparationDistance() {
		return Math.min(type.getFrameWidth(), type.getFrameHeight());
	}

	@Override
	public void resetSpeed() { speed = Starfright.SPEED; }

	@Override
	public long getAttackDelay() { return Starfright.EXPLOSION_DELAY; }

	@Override
	public float getSpeed() {
		Player player = Player.getPlayer();

		boolean blinded = (player.getFlashlight().isEnabled() && player.getFlashlight().inView(getCollider()));
		if(blinded) return (speed * 0.25f);

		return speed;
	}

	public static int appearsOnWave() { return Starfright.FIRST_WAVE; }

	public static int getSpawnCost() { return Starfright.SPAWN_COST; }

	@Override
	public String getName() {
		return "Starfright";
	}

	@Override
	public String getDescription() {
		return "Starfright";
	}

	@Override
	public String print() {
		return String.format("%s at (%.2f, %.2f) - %.2f health - Exploded? %s",
							 getName(), position.x, position.y, health, (exploded ? "Yes" : "No"));
	}

	@Override
	public ResourceTable getResourceTable() { return Starfright.RESOURCES; }

	@Override
	public LootTable getLootTable() { return Starfright.LOOT; }
}
