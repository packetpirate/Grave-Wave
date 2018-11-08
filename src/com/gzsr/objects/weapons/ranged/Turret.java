package com.gzsr.objects.weapons.ranged;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.state.BasicGameState;

import com.gzsr.AssetManager;
import com.gzsr.Globals;
import com.gzsr.entities.Player;
import com.gzsr.entities.enemies.Enemy;
import com.gzsr.entities.enemies.EnemyController;
import com.gzsr.gfx.particles.Particle;
import com.gzsr.gfx.particles.Projectile;
import com.gzsr.gfx.particles.ProjectileType;
import com.gzsr.gfx.particles.emitters.BloodGenerator;
import com.gzsr.math.Calculate;
import com.gzsr.math.Dice;
import com.gzsr.misc.Pair;
import com.gzsr.misc.RotationLerp;
import com.gzsr.talents.Talents;

public class Turret extends Projectile {
	private static final double HEALTH_MAX = 100.0;
	private static final long DAMAGE_COOLDOWN = 500L;
	private static final long TURRET_LIFESPAN = 60_000L;
	private static final long PROJECTILE_COOLDOWN = 200L;
	private static final float PROJECTILE_SPREAD = (float)(Math.PI / 12); // 15 degree spread total
	private static final float FIRING_RANGE = 250.0f;
	private static final Color TURRET_LASER = new Color(1.0f, 0.0f, 0.0f, 0.3f);
	private static final String TURRET_IMAGE = "GZS_TurretPieces";
	private static final String FIRE_SOUND = "revolver_shot_01";
	
	private static final Dice DAMAGE = new Dice(1, 8);
	private static final int DAMAGE_MOD = 4;
	
	private Sound fireSound;
	private Shape collider;
	private RotationLerp lerp;
	private Enemy target;
	
	private double health;
	private long lastDamage;
	private void takeDamage(double amnt) { health -= amnt; }
	private double getHealthMax() {
		double max = Turret.HEALTH_MAX;
		if(Talents.Fortification.MANUFACTURING.active()) {
			int ranks = Talents.Fortification.MANUFACTURING.ranks();
			max += (max * (ranks * 0.20));
		}
		return max;
	}
	
	private List<Projectile> projectiles;
	public List<Projectile> getProjectiles() { return projectiles; }
	private long created, lastProjectile;

	public Turret(Particle p) {
		super(p, 0.0, false);
		
		this.fireSound = AssetManager.getManager().getSound(Turret.FIRE_SOUND);
		this.collider = new Circle(position.x, position.y, 36.0f);
		
		this.lerp = null;
		this.target = null;
		
		this.health = getHealthMax();
		this.lastDamage = 0L;
		this.projectiles = new ArrayList<Projectile>();
		
		this.created = p.getCreated();
		this.lifespan = Turret.TURRET_LIFESPAN;
		if(Talents.Fortification.DURABILITY.active()) this.lifespan *= 2;
		
		this.lastProjectile = 0L;
	}

	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
		if(isAlive(cTime)) {
			if((target != null) && !target.isAlive(cTime)) {
				lerp = null;
				target = null; // Don't want to target dead enemies...
			}
			
			EnemyController ec = EnemyController.getInstance();
			for(Enemy e : ec.getAliveEnemies()) {
				if(collider.intersects(e.getCollider())) {
					// Enemy is touching the sentry, so it should take damage.
					long elapsed = (cTime - lastDamage);
					if(elapsed >= Turret.DAMAGE_COOLDOWN) {
						takeDamage(e.getDamage());
						lastDamage = cTime;
					}
				}
			}
			
			// Acquire a target only if we're not currently firing on one..
			if(target == null) {
				float targetDist = Float.MAX_VALUE;
				Iterator<Enemy> it = ec.getAliveEnemies().iterator();
				while(it.hasNext()) {
					Enemy e = it.next();
					
					// If there's a closer target, aim for it.
					float dist = Calculate.Distance(position, e.getPosition());
					if(dist < targetDist) {
						target = e;
						targetDist = dist;
					}
				}
			}
			
			// If the turret has a target and can fire, shoot!
			if((target != null) && target.isAlive(cTime)) {
				// Re-orient the sentry to face the target.
				if(lerp != null) {
					if(!lerp.isComplete()) {
						lerp.update(position, target.getPosition(), delta);
						theta = lerp.getCurrent();
					} else lerp = null;
				} else {
					// Don't want to fire while re-orienting.
					if(canFire(target, cTime)) fire(cTime);
					
					float end = (Calculate.Hypotenuse(position, target.getPosition()) + (float)(Math.PI * 2)) % (float)(Math.PI * 2);
					lerp = new RotationLerp(position, target.getPosition(), theta, end, 0.005f);
				}
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
		
		// Render the sentry's laser sight.
		float facing = theta;
		float dist = ((target != null) && target.isAlive(cTime)) ? Math.min(getRange(), Calculate.Distance(position, target.getPosition())) : getRange();
		g.setColor(Turret.TURRET_LASER);
		g.setLineWidth(2.0f);
		g.drawLine(position.x, position.y, 
				   (position.x + ((float)Math.cos(facing) * dist)), 
				   (position.y + ((float)Math.sin(facing) * dist)));
		g.setLineWidth(1.0f);
		
		// Render the rotated turret head.
		Image head = AssetManager.getManager().getImage(Turret.TURRET_IMAGE).getSubImage(48, 0, 48, 48);
		if(head != null) {
			g.rotate(position.x, position.y, (float)Math.toDegrees(theta + (float)(Math.PI / 2)));
			g.drawImage(head, (position.x - 24.0f), (position.y - 24.0f));
			g.resetTransform();
		}
		
		if(Globals.SHOW_COLLIDERS) {
			g.setColor(Color.red);
			g.draw(collider);
		}
		
		// Show the turret's health.
		float percentage = (float)(health / getHealthMax());
		g.setColor(Color.black);
		g.fillRect((position.x - 24.0f), (position.y - 34.0f), 48.0f, 5.0f);
		g.setColor(Color.green);
		g.fillRect((position.x - 24.0f), (position.y - 34.0f), (percentage * 48.0f), 5.0f);
		g.setColor(Color.white);
		g.drawRect((position.x - 24.0f), (position.y - 34.0f), 48.0f, 5.0f);
	}
	
	private boolean canFire(Enemy target, long cTime) {
		long elapsed = cTime - lastProjectile;
		return (Player.getPlayer().isAlive() && (elapsed >= Turret.PROJECTILE_COOLDOWN) && inRange(target));
	}
	
	private void fire(long cTime) {
		Color color = ProjectileType.ASSAULT.getColor();
		float velocity = ProjectileType.ASSAULT.getVelocity();
		float width = ProjectileType.ASSAULT.getWidth();
		float height = ProjectileType.ASSAULT.getHeight();
		long lifespan = ProjectileType.ASSAULT.getLifespan();
		float devTheta = (theta + (float)(Math.PI / 2) + (Globals.rand.nextFloat() * (Turret.PROJECTILE_SPREAD / 2) * (Globals.rand.nextBoolean()?1:-1)));
		Particle particle = new Particle(color, position, velocity, devTheta,
										 0.0f, new Pair<Float>(width, height), 
										 lifespan, cTime);
		
		boolean critical = (Globals.rand.nextFloat() <= Player.getPlayer().getAttributes().getFloat("rangeCritChance"));
		double dmg = getDamageTotal(critical);
		
		Projectile projectile = new Projectile(particle, BloodGenerator.BURST, dmg, critical);
		
		projectiles.add(projectile);
		lastProjectile = cTime;
		fireSound.play(1.0f, AssetManager.getManager().getSoundVolume());
	}
	
	public static Pair<Integer> getTotalDamage() {
		return Turret.DAMAGE.getRange(Turret.DAMAGE_MOD);
	}
	
	public double rollDamage(boolean critical) { return Turret.DAMAGE.roll(Turret.DAMAGE_MOD, critical); }
	private double getDamageTotal(boolean critical) {
		double dmg = rollDamage(critical);
		if(critical) dmg *= Player.getPlayer().getAttributes().getDouble("critMult");
		
		double bonus = (Talents.Munitions.COMMANDO.ranks() * 0.20);
		bonus += (Talents.Fortification.FIREPOWER.ranks() * 0.10);
		if(bonus > 0.0) dmg += (bonus * dmg);
		
		return dmg;
	}
	
	@Override
	public boolean isAlive(long cTime) {
		long elapsed = cTime - created;
		return ((elapsed <= Turret.TURRET_LIFESPAN) && (health > 0.0));
	}
	
	private float getRange() {
		float range = Turret.FIRING_RANGE;
		if(Talents.Fortification.TARGETING.active()) range *= 2.0f;
		return range;
	}
	
	private boolean inRange(Enemy e) {
		float dist = Calculate.Distance(position, e.getPosition());
		return (dist <= getRange());
	}
}
