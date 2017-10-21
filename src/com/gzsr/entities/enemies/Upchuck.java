package com.gzsr.entities.enemies;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import com.gzsr.Globals;
import com.gzsr.gfx.particles.Particle;
import com.gzsr.gfx.particles.Projectile;
import com.gzsr.gfx.particles.ProjectileType;
import com.gzsr.math.Calculate;
import com.gzsr.misc.Pair;

public class Upchuck extends Enemy {
	private static final float HEALTH = 150.0f;
	private static final float SPEED = 0.08f;
	private static final float DPS = 1.2f;
	private static final float BILE_DAMAGE = 0.4f;
	private static final float BILE_DEVIATION = (float)(Math.PI / 18);
	private static final long BILE_DELAY = 25L;
	private static final int BILE_PER_TICK = 5;
	private static final float ATTACK_DIST = 200.0f;
	
	private List<Projectile> bile;
	private long lastBile;
	
	public Upchuck(Pair<Float> position_) {
		super(EnemyType.CHUCK, position_);
		this.health = Upchuck.HEALTH;
		this.bile = new ArrayList<Projectile>();
		this.lastBile = 0L;
	}
	
	@Override
	public void update(long cTime) {
		if(!dead()) {
			theta = Calculate.Hypotenuse(position, Globals.player.getPosition());
			if(!nearPlayer()) {
				animation.update(cTime);
				move();
			} else vomit(cTime);
		}
		
		// Update bile projectiles.
		Iterator<Projectile> it = bile.iterator();
		while(it.hasNext()) {
			Projectile p = it.next();
			if(p.isAlive(cTime)) {
				p.update(cTime);
				if(Globals.player.checkCollision(p)) {
					Globals.player.takeDamage(p.getDamage());
					it.remove();
				}
			} else it.remove(); // need iterator instead of stream so we can remove if they're dead :/
		}
	}
	
	@Override
	public void render(Graphics g, long cTime) {
		// Only render the Upchuck until it dies.
		if(!dead()) animation.render(g, position, theta);
		// Even if Upchuck is dead, render its particles until they all die.
		if(!bile.isEmpty()) bile.stream().filter(p -> p.isAlive(cTime)).forEach(p -> p.render(g, cTime));
	}
	
	private void vomit(long cTime) {
		if(cTime >= (lastBile + Upchuck.BILE_DELAY)) {
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
	
	private boolean dead() {
		return (health <= 0);
	}

	@Override
	public void move() {
		position.x += (float)Math.cos(theta) * Upchuck.SPEED;
		position.y += (float)Math.sin(theta) * Upchuck.SPEED;
	}
	
	private boolean nearPlayer() {
		return (Calculate.Distance(position, Globals.player.getPosition()) <= Upchuck.ATTACK_DIST);
	}

	@Override
	public boolean checkCollision(Pair<Float> p) {
		return (Calculate.Distance(p, position) <= animation.getSize());
	}

	@Override
	public void takeDamage(double amnt) {
		health -= amnt;
	}

	@Override
	public double getDamage() {
		return Upchuck.DPS;
	}
}