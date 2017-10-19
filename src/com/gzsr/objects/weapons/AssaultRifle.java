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
import com.gzsr.gfx.Animation;
import com.gzsr.gfx.particles.Particle;
import com.gzsr.gfx.particles.Projectile;
import com.gzsr.gfx.particles.ProjectileType;
import com.gzsr.misc.Pair;
import com.gzsr.status.Status;

public class AssaultRifle implements Weapon {
	private static final long COOLDOWN = 100;
	private static final int CLIP_SIZE = 30;
	private static final int START_CLIPS = 4;
	private static final long RELOAD_TIME = 2000;
	private static final double DAMAGE = 50.0;
	private static final String ICON_NAME = "GZS_RTPS";
	private static final String FIRE_SOUND = "shoot3";
	private static final String RELOAD_SOUND = "buy_ammo2";
	
	private Animation muzzleFlash;
	private Sound fireSound;
	private Sound reloadSound;
	
	private List<Projectile> projectiles;
	private int ammoInClip;
	private int ammoInInventory;
	private boolean active;
	private long lastFired;
	private boolean reloading;
	private long reloadStart;
	
	public AssaultRifle() {
		AssetManager assets = AssetManager.getManager();
		
		this.muzzleFlash = assets.getAnimation("GZS_MuzzleFlash");
		this.fireSound = assets.getSound(AssaultRifle.FIRE_SOUND);
		this.reloadSound = assets.getSound(AssaultRifle.RELOAD_SOUND);
		
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
		
		// Update muzzle flash animation.
		if(muzzleFlash.isActive(cTime)) muzzleFlash.update(cTime);
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
		
		// Render muzzle flash.
		Pair<Float> mp = new Pair<Float>((Globals.player.getPosition().x + 5.0f), (Globals.player.getPosition().y - 28.0f));
		if(muzzleFlash.isActive(cTime)) muzzleFlash.render(g, mp, Globals.player.getPosition(), (Globals.player.getRotation() - (float)(Math.PI / 2)));
	}


	@Override
	public boolean canFire(long cTime) {
		if(reloading) return false;
		boolean clipNotEmpty = ammoInClip > 0;
		boolean ammoLeft = ammoInInventory > 0;
		boolean cool = (cTime - lastFired) >= AssaultRifle.COOLDOWN;
		
		if(!clipNotEmpty) {
			reload(cTime);
			return false;
		}
		
		return ((clipNotEmpty || ammoLeft) && cool);
	}

	@Override
	public void fire(Player player, Pair<Float> position, float theta, long cTime) {
		Color color = getProjectile().getColor();
		float velocity = getProjectile().getVelocity();
		float width = getProjectile().getWidth();
		float height = getProjectile().getHeight();
		long lifespan = getProjectile().getLifespan();
		Particle particle = new Particle(color, position, velocity, theta,
										 0.0f, new Pair<Float>(width, height), 
										 lifespan, cTime);
		double damage = AssaultRifle.DAMAGE + (player.getIntAttribute("damageUp") * 0.10);
		Projectile projectile = new Projectile(particle, damage);
		projectiles.add(projectile);
		if(!player.hasStatus(Status.UNLIMITED_AMMO)) ammoInClip--;
		lastFired = cTime;

		muzzleFlash.restart(cTime);
		fireSound.play();
	}

	@Override
	public void reload(long cTime) {
		if(ammoInInventory > 0) {
			reloading = true;
			reloadStart = cTime;
			
			int newClip = (ammoInInventory < AssaultRifle.CLIP_SIZE) ? ammoInInventory : AssaultRifle.CLIP_SIZE;
			ammoInInventory -= (newClip - ammoInClip);
			ammoInClip = newClip;
			
			reloadSound.play();
		}
	}

	@Override
	public boolean isReloading(long cTime) {
		long elapsed = cTime - reloadStart;
		return ((elapsed < AssaultRifle.RELOAD_TIME) && reloading);
	}

	@Override
	public double getReloadTime(long cTime) {
		long elapsed = cTime - reloadStart;
		return ((double)elapsed / (double)AssaultRifle.RELOAD_TIME);
	}
	
	@Override
	public List<Projectile> getProjectiles() {
		return projectiles;
	}
	
	@Override
	public Image getInventoryIcon() {
		return AssetManager.getManager().getImage(AssaultRifle.ICON_NAME);
	}

	@Override
	public int getClipSize() {
		return AssaultRifle.CLIP_SIZE;
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
	public ProjectileType getProjectile() {
		return ProjectileType.ASSAULT;
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
