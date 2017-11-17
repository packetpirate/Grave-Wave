package com.gzsr.objects.weapons;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Shape;

import com.gzsr.AssetManager;
import com.gzsr.Globals;
import com.gzsr.entities.enemies.Enemy;
import com.gzsr.entities.enemies.EnemyController;
import com.gzsr.gfx.particles.Particle;
import com.gzsr.gfx.particles.Projectile;
import com.gzsr.gfx.particles.ProjectileType;
import com.gzsr.math.Calculate;
import com.gzsr.misc.Pair;
import com.gzsr.states.GameState;

public class Turret extends Projectile {
	private static final double HEALTH_MAX = 2_00.0;
	private static final long TURRET_LIFESPAN = 15_000L;
	private static final long PROJECTILE_COOLDOWN = 200L;
	private static final float PROJECTILE_SPREAD = (float)(Math.PI / 12); // 15 degree spread total
	private static final double PROJECTILE_DAMAGE = 50.0;
	private static final float FIRING_RANGE = 300.0f;
	private static final String TURRET_IMAGE = "GZS_TurretPieces";
	private static final String FIRE_SOUND = "shoot3";
	
	private Sound fireSound;
	private Shape collider;
	private double health;
	private void takeDamage(double amnt) { health -= amnt; }
	private List<Projectile> projectiles;
	public List<Projectile> getProjectiles() { return projectiles; }
	private long created, lastProjectile;

	public Turret(Particle p) {
		super(p, 0.0);
		
		this.fireSound = AssetManager.getManager().getSound(Turret.FIRE_SOUND);
		this.collider = new Circle(position.x, position.y, 36.0f);
		
		this.health = Turret.HEALTH_MAX;
		
		this.projectiles = new ArrayList<Projectile>();
		
		this.created = p.getCreated();
		this.lastProjectile = 0L;
	}

	@Override
	public void update(GameState gs, long cTime, int delta) {
		if(isAlive(cTime)) {
			// Acquire a target.
			Enemy target = null;
			float targetDist = Float.MAX_VALUE;
			EnemyController ec = (EnemyController) gs.getEntity("enemyController");
			Iterator<Enemy> it = ec.getAliveEnemies().iterator();
			while(it.hasNext()) {
				Enemy e = it.next();
				float dist = Calculate.Distance(position, e.getPosition());
				if(collider.intersects(e.getCollider())) {
					// Enemy is touching the sentry, so it should take damage.
					takeDamage(e.getDamage());
				}
				
				// If there's a closer target, aim for it.
				if(dist < targetDist) {
					target = e;
					targetDist = dist;
				}
			}
			
			// If the turret has a target and can fire, shoot!
			if(target != null) {
				// Re-orient the sentry to face the target.
				theta = Calculate.Hypotenuse(position, target.getPosition()) + (float)(Math.PI / 2);
				if(canFire(target, cTime)) fire(cTime);
			}
		}
	}
	
	@Override
	public void render(Graphics g, long cTime) {
		// Render all projectiles.
		projectiles.stream().filter(p -> p.isAlive(cTime)).forEach(p -> p.render(g, cTime));
		
		// Render the turret base.
		Image base = AssetManager.getManager().getImage(Turret.TURRET_IMAGE).getSubImage(0, 0, 48, 48);
		if(base != null) g.drawImage(base, (position.x - 24.0f), (position.y - 24.0f));
		
		// Render the rotated turret head.
		Image head = AssetManager.getManager().getImage(Turret.TURRET_IMAGE).getSubImage(48, 0, 48, 48);
		if(head != null) {
			g.rotate(position.x, position.y, (float)Math.toDegrees(theta));
			g.drawImage(head, (position.x - 24.0f), (position.y - 24.0f));
			g.resetTransform();
		}
		
		// Render the range radius visualizer.
		g.setColor(Color.red);
		g.drawOval((position.x - Turret.FIRING_RANGE), (position.y - Turret.FIRING_RANGE), (Turret.FIRING_RANGE * 2), (Turret.FIRING_RANGE * 2));
	}
	
	private boolean canFire(Enemy target, long cTime) {
		long elapsed = cTime - lastProjectile;
		return ((elapsed >= Turret.PROJECTILE_COOLDOWN) && inRange(target));
	}
	
	private void fire(long cTime) {
		Color color = ProjectileType.ASSAULT.getColor();
		float velocity = ProjectileType.ASSAULT.getVelocity();
		float width = ProjectileType.ASSAULT.getWidth();
		float height = ProjectileType.ASSAULT.getHeight();
		long lifespan = ProjectileType.ASSAULT.getLifespan();
		float devTheta = (theta + (Globals.rand.nextFloat() * (Turret.PROJECTILE_SPREAD / 2) * (Globals.rand.nextBoolean()?1:-1)));
		Particle particle = new Particle(color, position, velocity, devTheta,
										 0.0f, new Pair<Float>(width, height), 
										 lifespan, cTime);
		
		double damage = Turret.PROJECTILE_DAMAGE + (Turret.PROJECTILE_DAMAGE * (Globals.player.getIntAttribute("damageUp") * 0.10));
		Projectile projectile = new Projectile(particle, damage);
		
		projectiles.add(projectile);
		lastProjectile = cTime;
		fireSound.play();
	}
	
	@Override
	public boolean isAlive(long cTime) {
		long elapsed = cTime - created;
		return ((elapsed <= Turret.TURRET_LIFESPAN) && (health > 0.0));
	}
	
	private boolean inRange(Enemy e) {
		float dist = Calculate.Distance(position, e.getPosition());
		return (dist <= Turret.FIRING_RANGE);
	}
}
