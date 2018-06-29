package com.gzsr.entities.enemies;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import com.gzsr.Globals;
import com.gzsr.entities.Player;
import com.gzsr.gfx.particles.Particle;
import com.gzsr.gfx.particles.Projectile;
import com.gzsr.gfx.particles.ProjectileType;
import com.gzsr.math.Calculate;
import com.gzsr.misc.Pair;
import com.gzsr.objects.items.Powerups;
import com.gzsr.states.GameState;
import com.gzsr.status.StatusEffect;

public class Upchuck extends Enemy {
	private static final int FIRST_WAVE = 5;
	private static final int SPAWN_COST = 4;
	private static final float HEALTH = 150.0f;
	private static final float SPEED = 0.08f;
	private static final float DPS = 1.2f;
	private static final float BILE_DAMAGE = 0.4f;
	private static final float BILE_DEVIATION = (float)(Math.PI / 18);
	private static final long BILE_DELAY = 25L;
	private static final int BILE_PER_TICK = 5;
	private static final float ATTACK_DIST = 200.0f;
	
	public static final LootTable LOOT = new LootTable()
			.addItem(Powerups.Type.HEALTH, 0.20f)
			.addItem(Powerups.Type.AMMO, 0.20f)
			.addItem(Powerups.Type.EXTRA_LIFE, 0.05f)
			.addItem(Powerups.Type.NIGHT_VISION, 0.15f)
			.addItem(Powerups.Type.UNLIMITED_AMMO, 0.025f);
	
	private List<Projectile> bile;
	private long lastBile;
	
	public Upchuck(Pair<Float> position_) {
		super(EnemyType.CHUCK, position_);
		this.health = Upchuck.HEALTH;
		this.bile = new ArrayList<Projectile>();
		this.lastBile = 0L;
	}
	
	@Override
	public void update(GameState gs, long cTime, int delta) {
		if(!dead()) {
			// Need to make sure to update the status effects first.
			Iterator<StatusEffect> it = statusEffects.iterator();
			while(it.hasNext()) {
				StatusEffect status = (StatusEffect) it.next();
				if(status.isActive(cTime)) {
					status.update(this, gs, cTime, delta);
				} else {
					status.onDestroy(this, cTime);
					it.remove();
				}
			}
			
			updateFlash(cTime);
			theta = Calculate.Hypotenuse(position, Player.getPlayer().getPosition());
			if(!nearPlayer(Upchuck.ATTACK_DIST)) {
				animation.update(cTime);
				if(Player.getPlayer().isAlive() && !touchingPlayer()) move(gs, delta);
			} else vomit(cTime);
		}
		
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
	
	@Override
	public void render(Graphics g, long cTime) {
		// Only render the Upchuck until it dies.
		float pTheta = Calculate.Hypotenuse(position, Player.getPlayer().getPosition());
		if(!dead()) animation.render(g, position, pTheta, shouldDrawFlash(cTime));
		// Even if Upchuck is dead, render its particles until they all die.
		if(!bile.isEmpty()) bile.stream().filter(p -> p.isAlive(cTime)).forEach(p -> p.render(g, cTime));
		if(!statusEffects.isEmpty()) statusEffects.stream().filter(status -> status.isActive(cTime)).forEach(status -> status.render(g, cTime));
		
		if(Globals.SHOW_COLLIDERS) {
			g.setColor(Color.red);
			g.draw(bounds);
		}
	}
	
	private void vomit(long cTime) {
		if(Player.getPlayer().isAlive() && (cTime >= (lastBile + Upchuck.BILE_DELAY))) {
			for(int i = 0; i < Upchuck.BILE_PER_TICK; i++) {
				Color color = ProjectileType.BILE.getColor();
				float velocity = ProjectileType.BILE.getVelocity();
				float width = ProjectileType.BILE.getWidth();
				float height = ProjectileType.BILE.getHeight();
				long lifespan = ProjectileType.BILE.getLifespan();
				float angle = (theta + (float)(Math.PI / 2)) + getBileDeviation();
				float angularVel = ((Globals.rand.nextInt(3) - 1) * 0.001f) * Globals.rand.nextFloat();
				Particle particle = new Particle("GZS_AcidParticle2", color, position, velocity, angle,
												 angularVel, new Pair<Float>(width, height), 
												 lifespan, cTime);
				Projectile projectile = new Projectile(particle, Upchuck.BILE_DAMAGE);
				bile.add(projectile);
			}
			lastBile = cTime;
		}
	}
	
	private float getBileDeviation() {
		int rl = Globals.rand.nextInt(3) - 1;
		return ((Globals.rand.nextFloat() * Upchuck.BILE_DEVIATION) * rl);
	}

	@Override
	public boolean isAlive(long cTime) {
		return !dead() || !bile.isEmpty();
	}

	@Override
	public void move(GameState gs, int delta) {
		velocity.x = (float)Math.cos(theta) * Upchuck.SPEED * delta;
		velocity.y = (float)Math.sin(theta) * Upchuck.SPEED * delta;

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
		return 0.0f;
	}
	
	@Override
	public float getSeparationDistance() {
		return Math.min(type.getFrameWidth(), type.getFrameHeight());
	}

	@Override
	public double getDamage() {
		return Upchuck.DPS;
	}
	
	@Override
	public float getSpeed() {
		return Upchuck.SPEED;
	}
	
	public static int appearsOnWave() {
		return FIRST_WAVE;
	}
	
	public static int getSpawnCost() {
		return Upchuck.SPAWN_COST;
	}

	@Override
	public String getName() {
		return "Upchuck";
	}
	
	@Override
	public String getDescription() {
		return "Upchuck";
	}
	
	@Override
	public LootTable getLootTable() {
		return Upchuck.LOOT;
	}
}
