package com.gzsr.entities.enemies;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.BasicGameState;

import com.gzsr.Globals;
import com.gzsr.entities.Player;
import com.gzsr.gfx.particles.Particle;
import com.gzsr.gfx.particles.ProjectileType;
import com.gzsr.gfx.particles.StatusProjectile;
import com.gzsr.math.Calculate;
import com.gzsr.math.Dice;
import com.gzsr.misc.Pair;
import com.gzsr.objects.items.Powerups;
import com.gzsr.objects.weapons.DamageType;
import com.gzsr.states.GameState;
import com.gzsr.status.AcidEffect;
import com.gzsr.status.Status;

public class Upchuck extends Enemy {
	private static final int FIRST_WAVE = 5;
	private static final int SPAWN_COST = 4;
	private static final float SPEED = 0.08f;
	private static final float DPS = 1.2f;
	private static final float BILE_DEVIATION = (float)(Math.PI / 18);
	private static final long BILE_DELAY = 25L;
	private static final int BILE_PER_TICK = 5;
	private static final float ATTACK_DIST = 200.0f;
	
	private static final Dice HEALTH = new Dice(4, 6);
	private static final int HEALTH_MOD = 12;
	
	public static final LootTable LOOT = new LootTable()
			.addItem(Powerups.Type.HEALTH, 0.20f)
			.addItem(Powerups.Type.AMMO, 0.40f)
			.addItem(Powerups.Type.EXTRA_LIFE, 0.05f)
			.addItem(Powerups.Type.CRIT_CHANCE, 0.05f)
			.addItem(Powerups.Type.EXP_MULTIPLIER, 0.05f)
			.addItem(Powerups.Type.NIGHT_VISION, 0.05f)
			.addItem(Powerups.Type.UNLIMITED_AMMO, 0.025f);
	
	private List<StatusProjectile> bile;
	private long lastBile;
	
	public Upchuck(Pair<Float> position_) {
		super(EnemyType.CHUCK, position_);
		this.health = Upchuck.HEALTH.roll(Upchuck.HEALTH_MOD);
		this.bile = new ArrayList<StatusProjectile>();
		this.lastBile = 0L;
		
		this.damageImmunities.add(DamageType.CORROSIVE);
		this.statusHandler.addImmunity(Status.POISON);
		this.statusHandler.addImmunity(Status.PARALYSIS);
	}
	
	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
		Player player = Player.getPlayer();
		if(!dead()) {
			// Need to make sure to update the status effects first.
			statusHandler.update((GameState)gs, cTime, delta);
			
			updateFlash(cTime);
			theta = Calculate.Hypotenuse(position, player.getPosition());
			if(!nearPlayer(Upchuck.ATTACK_DIST)) {
				animation.getCurrentAnimation().update(cTime);
				if(player.isAlive() && !touchingPlayer()) move((GameState)gs, delta);
			} else vomit(cTime);
		}
		
		if(!damageTexts.isEmpty()) {
			damageTexts.stream().forEach(dt -> ((GameState)gs).addEntity(String.format("dt%d", Globals.generateEntityID()), dt));
			damageTexts.clear();
		}
		
		// Update bile projectiles.
		Iterator<StatusProjectile> it = bile.iterator();
		while(it.hasNext()) {
			StatusProjectile p = it.next();
			if(p.isAlive(cTime)) {
				p.update(gs, cTime, delta);
				if(player.checkCollision(p)) {
					p.applyEffect(player, cTime);
					it.remove();
				}
			} else it.remove(); // need iterator instead of stream so we can remove if they're dead :/
		}
	}
	
	@Override
	public void render(Graphics g, long cTime) {
		// Only render the Upchuck until it dies.
		float pTheta = Calculate.Hypotenuse(position, Player.getPlayer().getPosition());
		// Even if Upchuck is dead, render its particles until they all die.
		if(!bile.isEmpty()) bile.stream().filter(p -> p.isAlive(cTime)).forEach(p -> p.render(g, cTime));
		
		if(!dead()) animation.getCurrentAnimation().render(g, position, pTheta, shouldDrawFlash(cTime));
		statusHandler.render(g, cTime);
		
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
				
				AcidEffect acid = new AcidEffect(cTime);
				StatusProjectile projectile = new StatusProjectile(particle, 0.0, false, acid);
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
		if(!statusHandler.hasStatus(Status.PARALYSIS)) {
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
	}
	
	@Override
	public float getCohesionDistance() { return 0.0f; }
	
	@Override
	public float getSeparationDistance() {
		return Math.min(type.getFrameWidth(), type.getFrameHeight());
	}

	@Override
	public double getDamage() { return Upchuck.DPS; }
	
	@Override
	public long getAttackDelay() { return 0L; }
	
	@Override
	public float getSpeed() { return Upchuck.SPEED; }
	
	public static int appearsOnWave() { return Upchuck.FIRST_WAVE; }
	
	public static int getSpawnCost() { return Upchuck.SPAWN_COST; }

	@Override
	public String getName() {
		return "Upchuck";
	}
	
	@Override
	public String getDescription() {
		return "Upchuck";
	}
	
	@Override
	public String print() {
		return String.format("%s at (%.2f, %.2f) - %.2f health - Bile Left: %d",
							 getName(), position.x, position.y, health, bile.size());
	}
	
	@Override
	public LootTable getLootTable() { return Upchuck.LOOT; }
}
