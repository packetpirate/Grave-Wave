package com.gzsr.objects.weapons.ranged;

import java.util.Iterator;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import com.gzsr.AssetManager;
import com.gzsr.entities.Player;
import com.gzsr.gfx.particles.Particle;
import com.gzsr.gfx.particles.Projectile;
import com.gzsr.gfx.particles.ProjectileType;
import com.gzsr.math.Calculate;
import com.gzsr.misc.Pair;

public class LaserBarrier extends RangedWeapon {
	private static final int PRICE = 2_000;
	private static final int AMMO_PRICE = 400;
	private static final long COOLDOWN = 1_000L;
	private static final int CLIP_SIZE = 2;
	private static final int START_CLIPS = 4;
	private static final int MAX_CLIPS = 8;
	private static final long RELOAD_TIME = 3_000L;
	private static final Color BARRIER_RANGE_INDICATOR = new Color(0xA0FAFF);
	private static final float BARRIER_RANGE = 750.0f;
	private static final String ICON_NAME = "GZS_LaserWire";
	private static final String PARTICLE_NAME = "GZS_LaserTerminal";
	private static final String FIRE_SOUND = "throw2";
	private static final String RELOAD_SOUND = "buy_ammo2";
	
	private LaserNode lastNode;
	
	public LaserBarrier() {
		super(false);
		
		AssetManager assets = AssetManager.getManager();
		this.useSound = assets.getSound(LaserBarrier.FIRE_SOUND);
		this.reloadSound = assets.getSound(LaserBarrier.RELOAD_SOUND);
		
		this.lastNode = null;
	}
	
	@Override
	public void render(Graphics g, long cTime) {
		if(lastNode != null) {
			lastNode.render(g, cTime);
			
			// Render the range of the last node.
			g.setColor(LaserBarrier.BARRIER_RANGE_INDICATOR);
			g.drawOval((lastNode.getPosition().x - (LaserBarrier.BARRIER_RANGE / 2)), 
					   (lastNode.getPosition().y - (LaserBarrier.BARRIER_RANGE / 2)), 
					   LaserBarrier.BARRIER_RANGE, LaserBarrier.BARRIER_RANGE);
			
			float dist = Calculate.Distance(Player.getPlayer().getPosition(), lastNode.getPosition());
			if(dist <= (BARRIER_RANGE / 2)) {
				// Draw the barrier itself between the first node and the player.
				g.setColor(LaserNode.BARRIER_COLOR);
				g.setLineWidth(LaserNode.BARRIER_WIDTH);
				g.drawLine(lastNode.getPosition().x, lastNode.getPosition().y, 
						   Player.getPlayer().getPosition().x, Player.getPlayer().getPosition().y);
				g.setLineWidth(1.0f);
			}
		}
		
		// Render all the nodes and the barrier between them.
		Iterator<Projectile> it = projectiles.iterator();
		while(it.hasNext()) {
			Projectile node = it.next();
			node.render(g, cTime);
		}
	}

	@Override
	public void use(Player player, Pair<Float> position, float theta, long cTime) {
		Color color = getProjectile().getColor();
		float velocity = getProjectile().getVelocity();
		float width = getProjectile().getWidth();
		float height = getProjectile().getHeight();
		long lifespan = getProjectile().getLifespan();
		Particle particle = new Particle(LaserBarrier.PARTICLE_NAME, color, position, velocity, theta,
										 0.0f, new Pair<Float>(width, height), 
										 lifespan, cTime);
		
		LaserNode node = new LaserNode(particle);
		if(lastNode != null) {
			// A new pair has been registered.
			lastNode.pair(node);
			node.pair(lastNode);
			
			// Add the pair to the list.
			projectiles.add(lastNode);
			projectiles.add(node);
			lastNode = null;
		} else {
			node.setAsHost();
			lastNode = node; // This node is unpaired.
		}
		
		super.use(player, position, theta, cTime);
	}
	
	@Override
	public boolean canUse(long cTime) {
		boolean notUnpaired = (lastNode == null);
		boolean inRange = notUnpaired ? true : ((Calculate.Distance(Player.getPlayer().getPosition(), lastNode.getPosition())) <= (LaserBarrier.BARRIER_RANGE / 2));
		return super.canUse(cTime) && (notUnpaired || inRange);
	}
	
	@Override
	public Pair<Integer> getDamage() { return new Pair<Integer>(0, 0); }
	
	@Override
	public double rollDamage(boolean critical) { return 0.0; }
	
	@Override
	public float getKnockback() { return 0.0f; }

	@Override
	public boolean isReloading(long cTime) {
		long elapsed = cTime - reloadStart;
		return ((elapsed < LaserBarrier.RELOAD_TIME) && reloading);
	}

	@Override
	public long getReloadTime() { return LaserBarrier.RELOAD_TIME; }
	
	@Override
	public double getReloadTime(long cTime) {
		long elapsed = cTime - reloadStart;
		return ((double)elapsed / (double)LaserBarrier.RELOAD_TIME);
	}

	@Override
	public Image getInventoryIcon() { return AssetManager.getManager().getImage(LaserBarrier.ICON_NAME); }
	
	@Override
	public int getClipSize() { return LaserBarrier.CLIP_SIZE; }

	@Override
	public int getStartClips() { return LaserBarrier.START_CLIPS; }
	
	@Override
	public int getMaxClips() { return LaserBarrier.MAX_CLIPS; }

	@Override
	public long getCooldown() { return LaserBarrier.COOLDOWN; }

	@Override
	public ProjectileType getProjectile() { return ProjectileType.LASERNODE; }

	@Override
	public int getPrice() { return LaserBarrier.PRICE; }
	
	@Override
	public int getAmmoPrice() { return LaserBarrier.AMMO_PRICE; }

	@Override
	public int getLevelRequirement() { return 10; }
	
	@Override
	public String getName() {
		return "Laser Barrier";
	}
	
	@Override
	public String getDescription() {
		return "For those who need a break from fighting zombies to recuperate. Stops the enemies in their tracks.";
	}
}
