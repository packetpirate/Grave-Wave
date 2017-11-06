package com.gzsr.objects.weapons;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Sound;

import com.gzsr.entities.Entity;
import com.gzsr.entities.Player;
import com.gzsr.gfx.particles.Particle;
import com.gzsr.gfx.particles.Projectile;
import com.gzsr.gfx.particles.ProjectileType;
import com.gzsr.misc.Pair;
import com.gzsr.states.GameState;

public abstract class Weapon implements Entity {
	protected Sound fireSound;
	protected Sound reloadSound;
	
	protected List<Projectile> projectiles;
	
	protected int ammoInClip;
	protected int ammoInInventory;
	protected boolean active;
	protected long lastFired;
	protected boolean reloading;
	protected long reloadStart;
	
	public Weapon() {
		this.fireSound = null;
		this.reloadSound = null;
		
		this.projectiles = new ArrayList<Projectile>();
		this.ammoInClip = getClipSize();
		this.ammoInInventory = (getStartClips() - 1) * getClipSize();
		this.active = false;
		this.lastFired = 0L;
		this.reloading = false;
		this.reloadStart = 0L;
	}
	
	@Override
	public void update(GameState gs, long cTime, int delta) {
		// Basically just checking to see if the reload time has elapsed.
		if(!isReloading(cTime)) reloading = false;
		
		// Update all projectiles.
		if(!getProjectiles().isEmpty()) {
			Iterator<Projectile> it = getProjectiles().iterator();
			while(it.hasNext()) {
				Particle p = it.next();
				if(p.isAlive(cTime)) {
					p.update(gs, cTime, delta);
				} else {
					p.onDestroy(gs, cTime);
					it.remove();
				}
			}
		}
	}
	
	@Override
	public void render(Graphics g, long cTime) {
		// Render all projectiles.
		if(!projectiles.isEmpty()) {
			Iterator<Projectile> it = projectiles.iterator();
			while(it.hasNext()) {
				Projectile p = it.next();
				if(p.isAlive(cTime)) p.render(g, cTime);
			}
		}
	}
	
	public boolean canFire(long cTime) {
		if(reloading) return false;
		boolean clipNotEmpty = ammoInClip > 0;
		boolean ammoLeft = ammoInInventory > 0;
		boolean cool = (cTime - lastFired) >= getCooldown();
		
		if(!clipNotEmpty) {
			reload(cTime);
			return false;
		}
		
		return ((clipNotEmpty || ammoLeft) && cool);
	}
	
	public abstract void fire(Player player, Pair<Float> position, float theta, long cTime);
	
	public void reload(long cTime) {
		if(ammoInInventory > 0) {
			reloading = true;
			reloadStart = cTime;
			
			int newClip = (ammoInInventory < getClipSize()) ? ammoInInventory : getClipSize();
			ammoInInventory -= (newClip - ammoInClip);
			ammoInClip = newClip;
			
			if(reloadSound != null) reloadSound.play();
		}
	}
	
	public abstract boolean isReloading(long cTime);
	public abstract double getReloadTime(long cTime);
	public abstract Image getInventoryIcon();
	public abstract int getClipSize();
	public int getClipAmmo() { return ammoInClip; }
	protected abstract int getStartClips();
	public int getInventoryAmmo() { return ammoInInventory; }
	public void addInventoryAmmo(int amnt) { ammoInInventory += amnt; }
	public boolean hasWeapon() { return active; }
	public void activate() { active = true; }
	public void deactivate() { active = false; }
	public abstract long getCooldown();
	public List<Projectile> getProjectiles() { return projectiles; }
	public abstract ProjectileType getProjectile();
}