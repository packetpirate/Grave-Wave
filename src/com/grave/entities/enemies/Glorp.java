package com.grave.entities.enemies;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.BasicGameState;

import com.grave.Globals;
import com.grave.achievements.Metrics;
import com.grave.controllers.AchievementController;
import com.grave.controllers.Scorekeeper;
import com.grave.entities.Player;
import com.grave.math.Calculate;
import com.grave.math.Dice;
import com.grave.misc.Pair;
import com.grave.objects.crafting.Resources;
import com.grave.objects.items.Powerups;
import com.grave.objects.items.ResourceDrop;
import com.grave.objects.weapons.DamageType;
import com.grave.states.GameState;
import com.grave.status.AcidEffect;
import com.grave.status.Status;

public class Glorp extends Enemy {
	private static final int FIRST_WAVE = 40;
	private static final int SPAWN_COST = 15;
	private static final float SPEED = 0.15f;
	private static final long ATTACK_DELAY = 1_000L;
	private static final long REGEN_DELAY = 1_000L;
	private static final double REGEN_AMOUNT = 20;
	private static final Color REGEN_TEXT_COLOR = new Color(0xADCCFF);

	private static final Dice HEALTH = new Dice(20, 10);
	private static final int HEALTH_MOD = 120;

	private static final Dice DAMAGE = new Dice(2, 4);
	private static final int DAMAGE_MOD = 8;

	public static final ResourceTable RESOURCES = new ResourceTable()
			.addResource(Resources.METAL, 0.1f)
			.addResource(Resources.GLASS, 0.2f)
			.addResource(Resources.ELECTRONICS, 0.15f)
			.addResource(Resources.POWER, 0.2f);

	public static final LootTable LOOT = new LootTable()
			.addItem(Powerups.Type.HEALTH, 0.4f)
			.addItem(Powerups.Type.AMMO, 0.4f)
			.addItem(Powerups.Type.EXTRA_LIFE, 0.1f)
			.addItem(Powerups.Type.CRIT_CHANCE, 0.2f)
			.addItem(Powerups.Type.EXP_MULTIPLIER, 0.1f)
			.addItem(Powerups.Type.UNLIMITED_AMMO, 0.05f)
			.addItem(Powerups.Type.INVULNERABILITY, 0.025f);

	private double originalHealth;
	private int split;
	private long lastRegen;

	public Glorp(Pair<Float> position_) {
		super(EnemyType.GLORP, position_);

		this.health = Glorp.HEALTH.roll(Glorp.HEALTH_MOD);
		this.originalHealth = this.health;
		this.speed = Glorp.SPEED;

		this.animation.addState("attack", type.createLayerAnimation(1, 4, 200L, -1L, -1L));

		this.damageImmunities.add(DamageType.ACID);
		this.statusHandler.addImmunity(Status.ACID);

		this.split = 0;
		this.lastRegen = 0L;
	}

	public Glorp(Pair<Float> position_, double prevHealth_, int split_) {
		super(EnemyType.GLORP, position_);

		this.health = (prevHealth_ / (split_ * 2));
		this.originalHealth = this.health;
		this.speed = Glorp.SPEED;

		this.animation.addState("attack", type.createLayerAnimation(1, 4, 200L, -1L, -1L));

		this.damageImmunities.add(DamageType.ACID);
		this.statusHandler.addImmunity(Status.ACID);

		// TODO: Post a message that says "Glorp!"

		this.split = split_;
		this.lastRegen = 0L;
	}

	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
		if(isAlive(cTime)) {
			Player player = Player.getPlayer();

			// Need to make sure to update the status effects first.
			statusHandler.update((GameState)gs, cTime, delta);

			updateFlash(cTime);
			animation.getCurrentAnimation().update(cTime);

			// Regenerate if not on fire.
			if(!statusHandler.hasStatus(Status.BURNING)) {
				long elapsed = (cTime - lastRegen);
				if(elapsed > Glorp.REGEN_DELAY) {
					health += Glorp.REGEN_AMOUNT;

					// Post a "damage" text.
					createDamageText(Glorp.REGEN_AMOUNT, 24.0f, (float)(Math.PI / 2), cTime, Glorp.REGEN_TEXT_COLOR);

					lastRegen = cTime;
				}
			}

			if(player.isAlive()) {
				if(touchingPlayer()) {
					if(!attacking) {
						long elapsed = (cTime - lastAttack);
						if(elapsed >= getAttackDelay()) {
							player.takeDamage(getDamage(), cTime);

							// Apply acid.
							AcidEffect effect = new AcidEffect(cTime);
							player.getStatusHandler().addStatus(effect, cTime);

							lastAttack = cTime;
							attacking = true;
							animation.setCurrent("attack"); // has no effect if this enemy has no attack animation
						}
					}
				} else {
					if(!animation.getCurrent().equals("move")) animation.setCurrent("move");
					move((GameState)gs, delta);
				}
			}

			if(attacking) attacking = false;
		}

		postDamageTexts();
	}

	@Override
	public void render(GameState gs, Graphics g, long cTime) {
		// All enemies should render their animation.
		if(isAlive(cTime)) animation.getCurrentAnimation().render(g, position, theta, shouldDrawFlash(cTime));
		statusHandler.render(g, cTime);

		if(Globals.SHOW_COLLIDERS) {
			g.setColor(Color.red);
			g.draw(bounds);
		}
	}

	@Override
	public void onDeath(GameState gs, long cTime) {
		if(!deathHandled) {
			// Split into two new Glorps (in the future, they should be smaller).
			if(split < 2) {
				EnemyController ec = EnemyController.getInstance();
				for(int i = 0; i < 2; i++) {
					float d = (Globals.rand.nextFloat() * 50.0f);
					float t = (Globals.rand.nextFloat() * (float)(Math.PI * 2));
					float x = (position.x + (d * (float)Math.cos(t)));
					float y = (position.y + (d * (float)Math.sin(t)));

					Glorp glorp = new Glorp(new Pair<Float>(x, y), originalHealth, (split + 1));
					ec.addNextUpdate(glorp);
				}
			}

			AchievementController.getInstance().postMetric(Metrics.compose(Metrics.ENEMY, Metrics.KILL));

			Pair<Float> pos = new Pair<Float>(position);
			Powerups.spawnRandomPowerup(gs, this, pos, cTime);
			ResourceDrop drop = getResourceTable().getDrop(pos, cTime);
			if(drop != null) gs.getLevel().addEntity("resource", drop);

			Scorekeeper.getInstance().addKill();
			postDamageTexts();
		}
		deathHandled = true;
	}

	@Override
	public void move(GameState gs, int delta) {
		if(!statusHandler.hasStatus(Status.PARALYSIS)) {
			theta = Calculate.Hypotenuse(position, Player.getPlayer().getPosition());
			velocity.x = (float)Math.cos(theta) * Glorp.SPEED * delta;
			velocity.y = (float)Math.sin(theta) * Glorp.SPEED * delta;

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
	public long getAttackDelay() { return Glorp.ATTACK_DELAY; }

	@Override
	public double getDamage() { return Glorp.DAMAGE.roll(Glorp.DAMAGE_MOD); }

	@Override
	public void resetSpeed() { speed = Glorp.SPEED; }

	public static int appearsOnWave() { return Glorp.FIRST_WAVE; }

	public static int getSpawnCost() { return Glorp.SPAWN_COST; }

	@Override
	public String getName() { return "Glorp"; }

	@Override
	public String getDescription() { return "Glorp"; }

	@Override
	public String print() {
		return String.format("%s at (%.2f, %.2f) - %.2f health",
				 			  getName(), position.x, position.y, health);
	}

	@Override
	public ResourceTable getResourceTable() { return Glorp.RESOURCES; }

	@Override
	public LootTable getLootTable() { return Glorp.LOOT; }
}
