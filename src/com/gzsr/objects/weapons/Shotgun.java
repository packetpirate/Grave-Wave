package com.gzsr.objects.weapons;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Sound;

import com.gzsr.AssetManager;
import com.gzsr.Globals;
import com.gzsr.entities.Player;
import com.gzsr.gfx.particles.Particle;
import com.gzsr.gfx.particles.Projectile;
import com.gzsr.gfx.particles.ProjectileType;
import com.gzsr.misc.Pair;
import com.gzsr.status.Status;

public class Shotgun implements Weapon {
	private static final long COOLDOWN = 1200;
	private static final int CLIP_SIZE = 8;
	private static final int START_CLIPS = 5;
	private static final int SHOT_COUNT = 5;
	private static final float MAX_SPREAD = (float)(Math.PI / 12);
	private static final long RELOAD_TIME = 2500;
	private static final double DAMAGE = 20.0;
	private static final String ICON_NAME = "GZS_Boomstick";
	private static final String FIRE_SOUND = "shotgun1";
	private static final String RELOAD_SOUND = "buy_ammo2";
	
	private Sound fireSound;
	private Sound reloadSound;
	
	private List<Projectile> projectiles;
	private int ammoInClip;
	private int ammoInInventory;
	private boolean active;
	private long lastFired;
	private boolean reloading;
	private long reloadStart;
	
	public Shotgun() {
		AssetManager assets = AssetManager.getManager();
		
		this.fireSound = assets.getSound(Shotgun.FIRE_SOUND);
		this.reloadSound = assets.getSound(Shotgun.RELOAD_SOUND);
		
		this.projectiles = new ArrayList<Projectile>();
		this.ammoInClip = CLIP_SIZE;
		this.ammoInInventory = (START_CLIPS - 1) * CLIP_SIZE;
		this.active = false;
		this.lastFired = 0L;
		this.reloading = false;
		this.reloadStart = 0L;
	}
	
	@Override
	public void update(long cTime) {
		// Basically just checking to see if the reload time has elapsed.
		if(!isReloading(cTime)) reloading = false;
		
		// Update all projectiles.
		if(!projectiles.isEmpty()) {
			Iterator<Projectile> it = projectiles.iterator();
			while(it.hasNext()) {
				Particle p = it.next();
				if(p.isAlive(cTime)) {
					p.update(cTime);
				} else it.remove();
			}
		}
	}

	@Override
	public void render(Graphics g, long cTime) {
		// Render all projectiles.
		if(!projectiles.isEmpty()) {
			Iterator<Projectile> it = projectiles.iterator();
			while(it.hasNext()) {
				Particle p = it.next();
				if(p.isAlive(cTime)) p.render(g, cTime);
			}
		}
	}

	@Override
	public Image getInventoryIcon() {
		return AssetManager.getManager().getImage(Shotgun.ICON_NAME);
	}

	@Override
	public int getClipSize() {
		return Shotgun.CLIP_SIZE;
	}

	@Override
	public int getClipAmmo() {
		return ammoInClip;
	}

	@Override
	public int getInventoryAmmo() {
		return ammoInInventory;
	}
	
	@Override
	public void addInventoryAmmo(int amnt) {
		ammoInInventory += amnt;
	}

	@Override
	public boolean canFire(long cTime) {
		if(reloading) return false;
		boolean clipNotEmpty = ammoInClip > 0;
		boolean ammoLeft = ammoInInventory > 0;
		boolean cool = (cTime - lastFired) >= Shotgun.COOLDOWN;
		
		if(!clipNotEmpty) {
			reload(cTime);
			return false;
		}
		
		return ((clipNotEmpty || ammoLeft) && cool);
	}

	@Override
	public void fire(Player player, Pair<Float> position, float theta, long cTime) {
		for(int i = 0; i < Shotgun.SHOT_COUNT; i++) {
			Color color = getProjectile().getColor();
			float velocity = getProjectile().getVelocity();
			float width = getProjectile().getWidth();
			float height = getProjectile().getHeight();
			float devTheta = (theta + (Globals.rand.nextFloat() * Shotgun.MAX_SPREAD * (Globals.rand.nextBoolean()?1:-1)));
			long lifespan = getProjectile().getLifespan();
			Particle particle = new Particle(color, position, velocity, devTheta,
											 0.0f, new Pair<Float>(width, height), 
											 lifespan, cTime);
			Projectile projectile = new Projectile(particle, Shotgun.DAMAGE);
			projectiles.add(projectile);
		}
		
		if(!player.hasStatus(Status.UNLIMITED_AMMO)) ammoInClip--;
		lastFired = cTime;
		
		fireSound.play();
	}

	@Override
	public void reload(long cTime) {
		if(ammoInInventory > 0) {
			reloading = true;
			reloadStart = cTime;
			
			int newClip = (ammoInInventory < Shotgun.CLIP_SIZE) ? ammoInInventory : Shotgun.CLIP_SIZE;
			ammoInInventory -= (newClip - ammoInClip);
			ammoInClip = newClip;
			
			reloadSound.play();
		}
	}

	@Override
	public boolean isReloading(long cTime) {
		long elapsed = cTime - reloadStart;
		return ((elapsed < Shotgun.RELOAD_TIME) && reloading);
	}

	@Override
	public double getReloadTime(long cTime) {
		long elapsed = cTime - reloadStart;
		return ((double)elapsed / (double)Shotgun.RELOAD_TIME);
	}

	@Override
	public List<Projectile> getProjectiles() {
		return projectiles;
	}

	@Override
	public ProjectileType getProjectile() {
		return ProjectileType.SHOTGUN;
	}

	@Override
	public boolean hasWeapon() {
		return active;
	}

	@Override
	public void activate() {
		active = true;
	}
	
	@Override
	public void deactivate() {
		active = false;
	}
}
