package com.gzsr.entities.enemies.bosses;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.BasicGameState;

import com.gzsr.Globals;
import com.gzsr.entities.Player;
import com.gzsr.entities.enemies.EnemyType;
import com.gzsr.entities.enemies.LootTable;
import com.gzsr.gfx.particles.Particle;
import com.gzsr.gfx.particles.Projectile;
import com.gzsr.gfx.particles.ProjectileType;
import com.gzsr.math.Calculate;
import com.gzsr.math.Dice;
import com.gzsr.misc.Pair;
import com.gzsr.objects.items.Powerups;
import com.gzsr.states.GameState;
import com.gzsr.status.StatusEffect;

public class Aberration extends Boss {
	private static final int FIRST_WAVE = 15;
	private static final int SPAWN_COST = 25;
	private static final int MIN_HEALTH_COUNT = 100;
	private static final int MIN_HEALTH_SIDES = 10;
	private static final int MIN_HEALTH_MOD = 2_500;
	private static final int MIN_DAMAGE_COUNT = 4;
	private static final int MIN_DAMAGE_SIDES = 4;
	private static final int MIN_DAMAGE_MOD = 4;
	private static final float SPEED = 0.15f;
	private static final float BILE_DAMAGE = 1.0f;
	private static final float BILE_DEVIATION = (float)(Math.PI / 9);
	private static final long BILE_DELAY = 25L;
	private static final int BILE_PER_TICK = 5;
	private static final float ATTACK_DIST = 200.0f;
	private static final long ATTACK_DELAY = 1_500L;
	
	public static final LootTable LOOT = new LootTable()
			.addItem(Powerups.Type.HEALTH, 1.0f)
			.addItem(Powerups.Type.AMMO, 1.0f)
			.addItem(Powerups.Type.EXTRA_LIFE, 0.40f)
			.addItem(Powerups.Type.CRIT_CHANCE, 0.10f)
			.addItem(Powerups.Type.NIGHT_VISION, 0.20f);
	
	private List<Projectile> bile;
	private long lastBile;
	
	public Aberration(Pair<Float> position_) {
		super(EnemyType.ABERRATION, position_);
		
		this.health = Dice.roll(Aberration.MIN_HEALTH_COUNT, Aberration.MIN_HEALTH_SIDES, Aberration.MIN_HEALTH_MOD);
		this.damage = new Dice(Aberration.MIN_DAMAGE_COUNT, Aberration.MIN_DAMAGE_SIDES);
		
		this.bile = new ArrayList<Projectile>();
		this.lastBile = 0L;
	}
	
	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
		if(!dead()) {
			// Need to make sure to update the status effects first.
			Iterator<StatusEffect> it = statusEffects.iterator();
			while(it.hasNext()) {
				StatusEffect status = (StatusEffect) it.next();
				if(status.isActive(cTime)) {
					status.update(this, (GameState)gs, cTime, delta);
				} else {
					status.onDestroy(this, cTime);
					it.remove();
				}
			}
			
			updateFlash(cTime);
			theta = Calculate.Hypotenuse(position, Player.getPlayer().getPosition());
			if(!nearPlayer(Aberration.ATTACK_DIST)) {
				animation.getCurrentAnimation().update(cTime);
				if(Player.getPlayer().isAlive() && !touchingPlayer()) move((GameState)gs, delta);
			} else vomit(cTime);
		}
		
		postDamageTexts();
		
		// Update bile projectiles.
		Iterator<Projectile> it = bile.iterator();
		while(it.hasNext()) {
			Projectile p = it.next();
			if(p.isAlive(cTime)) {
				p.update(gs, cTime, delta);
				if(Player.getPlayer().checkCollision(p)) {
					Player.getPlayer().takeDamage(p.getDamage(), cTime);
					it.remove();
				}
			} else it.remove(); // need iterator instead of stream so we can remove if they're dead :/
		}
	}
	
	private void vomit(long cTime) {
		if(Player.getPlayer().isAlive() && (cTime >= (lastBile + Aberration.BILE_DELAY))) {
			for(int i = 0; i < Aberration.BILE_PER_TICK; i++) {
				Color color = ProjectileType.BILE.getColor();
				float velocity = ProjectileType.BILE.getVelocity();
				float width = ProjectileType.BILE.getWidth();
				float height = ProjectileType.BILE.getHeight();
				long lifespan = ProjectileType.BILE.getLifespan() * 2;
				float angle = (theta + (float)(Math.PI / 2)) + getBileDeviation();
				float angularVel = ((Globals.rand.nextInt(3) - 1) * 0.001f) * Globals.rand.nextFloat();
				Particle particle = new Particle("GZS_AcidParticle2", color, position, velocity, angle,
												 angularVel, new Pair<Float>(width, height), 
												 lifespan, cTime);
				Projectile projectile = new Projectile(particle, Aberration.BILE_DAMAGE, false);
				bile.add(projectile);
			}
			lastBile = cTime;
		}
	}
	
	private float getBileDeviation() {
		int rl = Globals.rand.nextInt(3) - 1;
		return ((Globals.rand.nextFloat() * Aberration.BILE_DEVIATION) * rl);
	}
	
	@Override
	public void render(Graphics g, long cTime) {
		// Even if Aberration is dead, render its particles until they all die.
		if(!bile.isEmpty()) bile.stream().filter(p -> p.isAlive(cTime)).forEach(p -> p.render(g, cTime));
		// Only render the Aberration until it dies.
		if(!dead()) animation.getCurrentAnimation().render(g, position, theta, shouldDrawFlash(cTime));
		if(!statusEffects.isEmpty()) statusEffects.stream().filter(status -> status.isActive(cTime)).forEach(status -> status.render(g, cTime));
		
		if(Globals.SHOW_COLLIDERS) {
			g.setColor(Color.red);
			g.draw(bounds);
		}
	}

	@Override
	public boolean isAlive(long cTime) {
		return (!dead() || !bile.isEmpty());
	}

	@Override
	public void move(GameState gs, int delta) {
		velocity.x = (float)Math.cos(theta) * Aberration.SPEED * delta;
		velocity.y = (float)Math.sin(theta) * Aberration.SPEED * delta;

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
	public void takeDamage(double amnt, float knockback, long cTime, int delta) {
		takeDamage(amnt, knockback, (float)(theta + Math.PI), cTime, delta, true);
	}
	
	@Override
	public void takeDamage(double amnt, float knockback, float knockbackTheta, long cTime, int delta, boolean flash) {
		takeDamage(amnt, knockback, knockbackTheta, cTime, delta, flash, false);
	}
	
	@Override
	public void takeDamage(double amnt, float knockback, float knockbackTheta, long cTime, int delta, boolean flash, boolean isCritical) {
		if(!dead()) {
			health -= amnt;
			
			createDamageText(amnt, 64.0f, knockbackTheta, cTime, isCritical);
			
			if(flash) {
				hit = true;
				hitTime = cTime;
			}
		}
	}

	@Override
	public double getDamage() { return damage.roll(Aberration.MIN_DAMAGE_MOD); }
	
	@Override
	public long getAttackDelay() { return Aberration.ATTACK_DELAY; }
	
	@Override
	public float getSpeed() { return Aberration.SPEED; }

	public static int appearsOnWave() { return FIRST_WAVE; }
	
	public static int getSpawnCost() { return Aberration.SPAWN_COST; }
	
	@Override
	public String getName() {
		return "Aberration";
	}
	
	@Override
	public String getDescription() {
		return "Aberration";
	}
	
	@Override
	public LootTable getLootTable() { return Aberration.LOOT; }
}
