package com.grave.objects.weapons.ranged;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.state.BasicGameState;

import com.grave.AssetManager;
import com.grave.Globals;
import com.grave.entities.enemies.Enemy;
import com.grave.entities.enemies.EnemyController;
import com.grave.gfx.particles.Particle;
import com.grave.gfx.particles.Projectile;
import com.grave.gfx.particles.ProjectileType;
import com.grave.gfx.particles.emitters.BloodGenerator;
import com.grave.math.Calculate;
import com.grave.misc.Pair;
import com.grave.states.GameState;

public class Claymore extends Projectile {
	private static final Color DETECTOR = new Color(1.0f, 0.0f, 0.0f, 0.1f);
	public static final int SHRAPNEL_COUNT = 50;
	private static final float SHRAPNEL_SPREAD = (float)(Math.PI / 3.6); // 50 degree spread total
	private static final float EXP_RANGE = 200.0f;
	private static final String EXP_SOUND = "explosion2";
	
	private Sound explosion;
	private Shape collider;
	private List<Projectile> shrapnel;
	public List<Projectile> getShrapnel() { return shrapnel; }
	private boolean exploded;
	private boolean shrapnelCreated;
	private double damage;
	private boolean critical;
	
	public Claymore(Particle p_, double damage_, boolean critical_) {
		super(p_, 0.0, false);
		
		this.explosion = AssetManager.getManager().getSound(Claymore.EXP_SOUND);
		
		float mTheta = theta - (float)(Math.PI / 2);
		float magX1 = (float)(Math.cos(mTheta - (SHRAPNEL_SPREAD / 2)) * EXP_RANGE);
		float magY1 = (float)(Math.sin(mTheta - (SHRAPNEL_SPREAD / 2)) * EXP_RANGE);
		float magX2 = (float)(Math.cos(mTheta + (SHRAPNEL_SPREAD / 2)) * EXP_RANGE);
		float magY2 = (float)(Math.sin(mTheta + (SHRAPNEL_SPREAD / 2)) * EXP_RANGE);
		this.collider = new Polygon(new float[] { position.x, position.y, 
												  (position.x + magX1), (position.y + magY1), 
												  (position.x + magX2), (position.y + magY2) 
												});
		
		this.shrapnel = new ArrayList<Projectile>();
		this.exploded = false;
		this.shrapnelCreated = false;
		this.damage = damage_;
		this.critical = critical_;
	}

	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
		if(!exploded) {
			EnemyController ec = EnemyController.getInstance();
			
			Iterator<Enemy> it = ec.getAliveEnemies().iterator();
			while(it.hasNext()) {
				Enemy e = it.next();
				if(checkRange(e)) {
					collide((GameState)gs, e, cTime);
					exploded = true;
					explosion.play(1.0f, AssetManager.getManager().getSoundVolume());
				}
			}
		}
		
		if(exploded && !shrapnelCreated) {
			// Create the Claymore projectiles.
			for(int i = 0; i < Claymore.SHRAPNEL_COUNT; i++) {
				Color color = ProjectileType.SHRAPNEL.getColor();
				float velocity = ProjectileType.SHRAPNEL.getVelocity();
				float width = ProjectileType.SHRAPNEL.getWidth();
				float height = ProjectileType.SHRAPNEL.getHeight();
				long lifespan = ProjectileType.SHRAPNEL.getLifespan();
				float devTheta = (theta + (Globals.rand.nextFloat() * (Claymore.SHRAPNEL_SPREAD / 2) * (Globals.rand.nextBoolean()?1:-1)));
				Particle particle = new Particle(color, position, velocity, devTheta,
												 0.0f, new Pair<Float>(width, height), 
												 lifespan, cTime);
				
				Projectile projectile = new Projectile(particle, BloodGenerator.BURST, damage, critical);
				
				shrapnel.add(projectile);	
			}
			
			shrapnelCreated = true;
		}
		
		// Update the shrapnel particles.
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
		if(!exploded) {
			super.render(g, cTime);
			
			// Draw the collider.
			g.setColor(Claymore.DETECTOR);
			g.draw(collider);
			g.fill(collider);
		} else {
			// Draw the shrapnel particles.
			if(!shrapnel.isEmpty()) shrapnel.stream().forEach(sh -> sh.render(g, cTime));
		}
	}
	
	public static int getShrapnelCount() {
		return Claymore.SHRAPNEL_COUNT;
	}
	
	@Override
	public boolean isAlive(long cTime) {
		return (!exploded || !shrapnel.isEmpty());
	}
	
	private boolean checkRange(Enemy e) {
		float dist = Calculate.Distance(position, e.getPosition());
		return (collider.intersects(e.getCollider()) && (dist <= Claymore.EXP_RANGE));
	}
}
