package com.gzsr.objects.weapons.ranged;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Sound;
import org.newdawn.slick.state.BasicGameState;

import com.gzsr.AssetManager;
import com.gzsr.Globals;
import com.gzsr.entities.enemies.Enemy;
import com.gzsr.entities.enemies.EnemyController;
import com.gzsr.gfx.Camera;
import com.gzsr.gfx.particles.Particle;
import com.gzsr.gfx.particles.Projectile;
import com.gzsr.gfx.particles.ProjectileType;
import com.gzsr.gfx.particles.emitters.BloodGenerator;
import com.gzsr.misc.Pair;
import com.gzsr.states.GameState;

public class PipeBomb extends Projectile {
	public static final float SPEED = ProjectileType.PIPE_BOMB.getVelocity();
	private static final float SPIN_VELOCITY = 0.126f;
	private static final long LIFESPAN = 750L;
	public static final int SHRAPNEL_COUNT = 32;

	private List<Projectile> shrapnel;
	public List<Projectile> getShrapnel() { return shrapnel; }
	private boolean exploded;
	private boolean shrapnelCreated;
	private double damage;
	private boolean critical;

	private Sound explosion;

	public PipeBomb(Particle p_, double damage_, boolean critical_) {
		super(p_, null, 0.0, false);

		this.shrapnel = new ArrayList<Projectile>();
		this.exploded = false;
		this.shrapnelCreated = false;
		this.damage = damage_;
		this.critical = critical_;

		this.explosion = AssetManager.getManager().getSound("explosion2");
	}

	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
		long elapsed = (cTime - created);
		if((elapsed > PipeBomb.LIFESPAN) && !exploded) {
			explode((GameState)gs, cTime);
			if(!shrapnelCreated) createShrapnel(cTime);
		}

		if(!exploded) {
			// Move the pipe bomb along its path.
			position.x += (float)Math.cos(theta - (Math.PI / 2)) * Molotov.SPEED * delta;
			position.y += (float)Math.sin(theta - (Math.PI / 2)) * Molotov.SPEED * delta;
			rotation += SPIN_VELOCITY;
			bounds.setLocation((position.x - (size.x / 2)), (position.y - (size.y / 2)));

			// Check to see if the pipe bomb has collided with an enemy. If so, explode.
			EnemyController ec = EnemyController.getInstance();
			Iterator<Enemy> it = ec.getAliveEnemies().iterator();
			while(it.hasNext()) {
				Enemy e = it.next();
				boolean collision = (e.getCollider().intersects(getCollider()) || e.getCollider().contains(getCollider()));
				if(collision) explode((GameState)gs, cTime);
			}
		}

		if(exploded && !shrapnelCreated) createShrapnel(cTime);

		if(!shrapnel.isEmpty()) {
			Iterator<Projectile> it = shrapnel.iterator();
			while(it.hasNext()) {
				Projectile p = it.next();
				if(!p.isAlive(cTime)) {
					p.onDestroy((GameState)gs, cTime);
					it.remove();
				}
			}
		}
	}

	@Override
	public void render(Graphics g, long cTime) {
		if(exploded) {
			if(!shrapnel.isEmpty()) shrapnel.stream().forEach(sh -> sh.render(g, cTime));
		} else super.render(g, cTime);
	}

	private void explode(GameState gs, long cTime) {
		System.out.println("Boom!");
		explosion.play(1.0f, AssetManager.getManager().getSoundVolume());
		exploded = true;

		if(!Camera.getCamera().isShaking()) Camera.getCamera().shake(cTime, 200L, 50L, 2.0f);
		else Camera.getCamera().refreshShake(cTime);
	}

	private void createShrapnel(long cTime) {
		for(int i = 0; i < PipeBomb.SHRAPNEL_COUNT; i++) {
			Color color = ProjectileType.SHRAPNEL.getColor();
			float velocity = ProjectileType.SHRAPNEL.getVelocity();
			float width = ProjectileType.SHRAPNEL.getWidth();
			float height = ProjectileType.SHRAPNEL.getHeight();
			long lifespan = ProjectileType.SHRAPNEL.getLifespan();
			float dir = (Globals.rand.nextFloat() * (float)(Math.PI * 2));
			Particle particle = new Particle(color, position, velocity, dir,
											 0.0f, new Pair<Float>(width, height),
											 lifespan, cTime);

			Projectile projectile = new Projectile(particle, BloodGenerator.BURST, damage, critical);

			shrapnel.add(projectile);
		}

		shrapnelCreated = true;
	}

	@Override
	public void onDestroy(GameState gs, long cTime) {
		if(!isDestroyed()) {
			System.out.println("Pipe Bomb onDestroy()");
			if(!exploded) {
				System.out.println("Pipe Bomb onDestroy(): exploded");
				explode(gs, cTime);
				if(!shrapnelCreated) createShrapnel(cTime);
			}
		}

		destroyed = true;
	}

	@Override
	public boolean isAlive(long cTime) {
		return (!exploded || !shrapnel.isEmpty());
	}
}
