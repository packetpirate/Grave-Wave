package com.gzsr.entities.enemies.bosses;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.BasicGameState;

import com.gzsr.AssetManager;
import com.gzsr.achievements.Metrics;
import com.gzsr.controllers.AchievementController;
import com.gzsr.entities.Player;
import com.gzsr.entities.enemies.EnemyType;
import com.gzsr.entities.enemies.LootTable;
import com.gzsr.gfx.particles.Particle;
import com.gzsr.math.Calculate;
import com.gzsr.math.Dice;
import com.gzsr.misc.Pair;
import com.gzsr.objects.items.Powerups;
import com.gzsr.objects.weapons.DamageType;
import com.gzsr.states.GameState;
import com.gzsr.status.Status;

public class Stitches extends Boss {
	private static final int FIRST_WAVE = 25;
	private static final int SPAWN_COST = 40;
	private static final long ATTACK_DELAY = 2_000L;
	private static final float SPEED = 0.10f;
	private static final float ATTACK_DIST = 364.0f;
	private static final float RELEASE_DIST = 128.0f;
	private static final float HOOK_THROW_SPEED = 0.6f;
	private static final float HOOK_REEL_SPEED = 0.2f;
	private static final long HOOK_DAMAGE_DELAY = 1_000L;
	private static final long HOOK_COOLDOWN = 5_000L;
	
	private static final Dice HEALTH = new Dice(100, 10);
	private static final int HEALTH_MOD = 4_000;
	
	private static final Dice DAMAGE = new Dice(10, 4);
	
	private static final Dice HOOK_DAMAGE = new Dice(2, 4);
	private static final int HOOK_DAMAGE_MOD = 4;
	
	public static final LootTable LOOT = new LootTable()
			.addItem(Powerups.Type.HEALTH, 1.0f)
			.addItem(Powerups.Type.AMMO, 1.0f)
			.addItem(Powerups.Type.EXTRA_LIFE, 0.50f)
			.addItem(Powerups.Type.CRIT_CHANCE, 0.20f)
			.addItem(Powerups.Type.NIGHT_VISION, 0.30f);
	
	private Particle hook;
	private boolean hooked;
	private long lastHookDamage;
	private long lastHook;
	private float hookTheta;
	
	public Stitches(Pair<Float> position_) {
		super(EnemyType.STITCHES, position_);
		
		this.health = Stitches.HEALTH.roll(Stitches.HEALTH_MOD);
		
		this.damageImmunities.add(DamageType.BLUNT);
		this.statusHandler.addImmunity(Status.PARALYSIS);
		this.statusHandler.addImmunity(Status.POISON);
		
		hook = null;
		hooked = false;
		lastHookDamage = 0L;
		lastHook = -Stitches.HOOK_COOLDOWN;
		hookTheta = 0.0f;
	}
	
	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
		if(isAlive(cTime)) {
			Player player = Player.getPlayer();
			
			// Need to make sure to update the status effects first.
			statusHandler.update((GameState)gs, cTime, delta);
			
			updateFlash(cTime);
			theta = Calculate.Hypotenuse(position, player.getPosition());
			
			if((hook != null) && !hooked) {
				float distToPlayer = Calculate.Distance(hook.getPosition(), player.getPosition());
				if(distToPlayer > Stitches.ATTACK_DIST) {
					// If the hook missed the player, get rid of it.
					hook = null;
					hooked = false;
					lastHook = cTime;
				} else {
					// Move the hook toward the player until it collides or misses.
					hook.update(gs, cTime, delta);
					if(hook.checkCollision(player)) {
						hooked = true;
						lastHookDamage = -Stitches.HOOK_DAMAGE_DELAY;
					}
				}
			}
			
			long sinceLastHook = (cTime - lastHook);
			if(player.isAlive() && (hook == null) && (sinceLastHook >= Stitches.HOOK_COOLDOWN) && nearPlayer(Stitches.ATTACK_DIST)) {
				// Throw the hook.
				Pair<Float> hookPos = new Pair<Float>(position.x, position.y);
				hookTheta = (findPlayerIntercept(player.getPosition(), player.getVelocity(), delta) + (float)(Math.PI / 2));
				hook = new Particle("GZS_Hook", Color.gray, hookPos, Stitches.HOOK_THROW_SPEED, hookTheta, 0.0f, new Pair<Float>(16.0f, 16.0f), -1L, cTime);
				AssetManager.getManager().getSound("throw2").play(1.0f, AssetManager.getManager().getSoundVolume());
			} else if(hooked) {
				// If we're close enough to the player now, release the hook.
				if(!player.isAlive() || nearPlayer(Stitches.RELEASE_DIST)) {
					hook = null;
					hooked = false;
					lastHook = cTime;
				} else {
					// Otherwise, reel the player in.
					float reelSpeed = (float)((player.getSpeed() + (player.getSpeed() * (player.getAttributes().getInt("speedUp") * 0.10))) * player.getAttributes().getDouble("spdMult"));
					reelSpeed += Stitches.HOOK_REEL_SPEED;
					
					float xOff = (float)(Math.cos(theta) * -(reelSpeed * delta));
					float yOff = (float)(Math.sin(theta) * -(reelSpeed * delta));
					player.move(xOff, yOff);
					hook.setPosition(new Pair<Float>(player.getPosition().x, player.getPosition().y));
					
					// Make the player take bleed damage.
					long elapsed = (cTime - lastHookDamage);
					if(elapsed >= Stitches.HOOK_DAMAGE_DELAY) {
						double dmg = Stitches.HOOK_DAMAGE.roll(Stitches.HOOK_DAMAGE_MOD);
						player.takeDamage(dmg, cTime);
						lastHookDamage = cTime;
					}
				}
			} else if(!hooked && (hook == null)) {
				animation.getCurrentAnimation().update(cTime);
				if(player.isAlive()) {
					if(touchingPlayer()) {
						long elapsed = (cTime - lastAttack);
						if(elapsed >= getAttackDelay()) {
							player.takeDamage(getDamage(), cTime);
							lastAttack = cTime;
						}
					} else move((GameState)gs, delta);
				}
			}
		} else {
			hook = null;
			hooked = false;
		}
		
		postDamageTexts();
	}
	
	@Override
	public void render(Graphics g, long cTime) {
		if(hook != null) {
			// Draw the hook's tether.
			g.setColor(Color.black);
			g.setLineWidth(2.0f);
			g.drawLine(position.x, position.y, hook.getPosition().x, hook.getPosition().y);
			g.setLineWidth(1.0f);
			
			hook.render(g, cTime);
		}
		
		super.render(g, cTime);
	}
	
	private float findPlayerIntercept(Pair<Float> playerPos, Pair<Float> playerVel, int delta) {
		float hookSpeed = HOOK_THROW_SPEED * delta;
		Pair<Float> hPos = new Pair<Float>(position);
		Pair<Float> pPos = new Pair<Float>(playerPos);
		
		// While the hook hasn't intercepted the player yet.
		while(Calculate.Distance(position, hPos) < Calculate.Distance(position, pPos)) {
			// Move the player according to player velocity.
			pPos.x += playerVel.x;
			pPos.y += playerVel.y;
			
			float toPlayer = Calculate.Hypotenuse(position, pPos);
			
			// Aim the hook at the new player position and move it in that direction.
			hPos.x += ((float)Math.cos(toPlayer) * hookSpeed);
			hPos.y += ((float)Math.sin(toPlayer) * hookSpeed);
		}
		
		// Calculate the theta value between Stitches and the hook's calculated intercept point.
		return Calculate.Hypotenuse(position, hPos);
	}

	@Override
	public void move(GameState gs, int delta) {
		velocity.x = (float)Math.cos(theta) * Stitches.SPEED * delta;
		velocity.y = (float)Math.sin(theta) * Stitches.SPEED * delta;

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
	public void takeDamage(DamageType dType, double amnt, float knockback, long sourceMetric, long cTime, int delta) {
		takeDamage(dType, amnt, knockback, (float)(theta + Math.PI), sourceMetric, cTime, delta, true);
	}
	
	@Override
	public void takeDamage(DamageType dType, double amnt, float knockback, float knockbackTheta, long sourceMetric, long cTime, int delta, boolean flash) {
		takeDamage(dType, amnt, knockback, knockbackTheta, sourceMetric, cTime, delta, flash, false);
	}
	
	@Override
	public void takeDamage(DamageType dType, double amnt, float knockback, float knockbackTheta, long sourceMetric, long cTime, int delta, boolean flash, boolean isCritical) {
		if(!dead() && !damageImmunities.contains(dType)) {
			health -= amnt;
			
			createDamageText(amnt, 64.0f, knockbackTheta, cTime, isCritical);
			
			if(flash) {
				hit = true;
				hitTime = cTime;
			}
			
			AchievementController.getInstance().postMetric(Metrics.compose(type.getEnemyMetric(), sourceMetric, Metrics.ENEMY_DAMAGE));
		}
	}
	
	@Override
	public void onDeath(GameState gs, long cTime) {
		AchievementController.getInstance().postMetric(Metrics.compose(Metrics.STITCHES, Metrics.ENEMY_KILL));
	}
	
	@Override
	public long getAttackDelay() { return Stitches.ATTACK_DELAY; }
	
	@Override
	public double getDamage() { return Stitches.DAMAGE.roll(); }
	
	@Override
	public float getSpeed() { return Stitches.SPEED; }
	
	public static int appearsOnWave() { return FIRST_WAVE; }

	public static int getSpawnCost() { return Stitches.SPAWN_COST; }
	
	@Override
	public String getName() {
		return "Stitches";
	}
	
	@Override
	public String getDescription() {
		return "Stitches";
	}
	
	@Override
	public String print() {
		return String.format("%s at (%.2f, %.2f) - %.2f health",
							 getName(), position.x, position.y, health);
	}
	
	@Override
	public LootTable getLootTable() { return Stitches.LOOT; }
}
