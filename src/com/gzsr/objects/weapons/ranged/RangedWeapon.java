package com.gzsr.objects.weapons.ranged;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Sound;
import org.newdawn.slick.state.BasicGameState;

import com.gzsr.entities.Player;
import com.gzsr.gfx.particles.Particle;
import com.gzsr.gfx.particles.Projectile;
import com.gzsr.gfx.particles.ProjectileType;
import com.gzsr.objects.weapons.Weapon;
import com.gzsr.states.GameState;
import com.gzsr.status.Status;

public abstract class RangedWeapon extends Weapon {
	protected Sound reloadSound;
	
	protected List<Projectile> projectiles;
	public List<Projectile> getProjectiles() { return projectiles; }
	
	protected int ammoInClip;
	public int getClipAmmo() { return ammoInClip; }
	protected int ammoInInventory;
	public int getInventoryAmmo() { return ammoInInventory; }
	
	protected boolean reloading;
	protected long reloadStart;
	
	public RangedWeapon() {
		this.reloadSound = null;
		
		this.projectiles = new ArrayList<Projectile>();
		
		this.ammoInClip = getClipSize();
		this.ammoInInventory = (getStartClips() - 1) * getClipSize();
		
		this.reloading = false;
		this.reloadStart = 0L;
	}
	
	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
		// Basically just checking to see if the reload time has elapsed.
		if(reloading && !isReloading(cTime)) {
			int takeFromInv = getClipSize() - ammoInClip;
			int taken = Math.min(takeFromInv, ammoInInventory);
			ammoInInventory -= taken;
			ammoInClip += taken;
			
			reloading = false;
		}
		
		// Update all projectiles.
		if(!getProjectiles().isEmpty()) {
			Iterator<Projectile> it = getProjectiles().iterator();
			while(it.hasNext()) {
				Particle p = it.next();
				if(p.isAlive(cTime)) {
					p.update(gs, cTime, delta);
				} else {
					p.onDestroy((GameState)gs, cTime);
					it.remove();
				}
			}
		}
	}
	
	@Override
	public void render(Graphics g, long cTime) {
		projectiles.stream()
				   .filter(p -> p.isActive(cTime))
				   .forEach(p -> p.render(g, cTime));
	}
	
	@Override
	public boolean canUse(long cTime) {
		if(reloading) return false;
		boolean clipNotEmpty = ammoInClip > 0;
		boolean cool = (cTime - lastUsed) >= getCooldown();
		
		if(!clipNotEmpty) return false;
		
		return (Player.getPlayer().isAlive() && equipped && cool);
	}
	
	public void reload(long cTime) {
		if(ammoInInventory > 0) {
			reloading = true;
			reloadStart = cTime;
			
			if(reloadSound != null) reloadSound.play();
		}
	}

	public abstract boolean isReloading(long cTime);
	public abstract double getReloadTime(long cTime);
	public abstract long getReloadTime();
	
	public abstract int getClipSize();
	public abstract int getStartClips();
	public abstract int getMaxClips();
	public abstract int getAmmoPrice();
	
	public boolean clipsMaxedOut() {
		int totalAmmo = ammoInClip + ammoInInventory;
		int maxAmmo = getMaxClips() * getClipSize();
		return (totalAmmo >= maxAmmo); 
	}
	
	public void addInventoryAmmo(int amnt) {
		if(!clipsMaxedOut()) {
			int totalAmmo = ammoInClip + ammoInInventory;
			boolean noOverflow = (totalAmmo + amnt) <= (getClipSize() * getMaxClips()); 
			ammoInInventory += (noOverflow ? amnt : ((getClipSize() * getMaxClips()) - totalAmmo));
		}
	}
	
	public void maxOutAmmo() {
		ammoInInventory = (getMaxClips() * getClipSize()) - ammoInClip; 
	}
	
	protected boolean hasUnlimitedAmmo() { return Player.getPlayer().getStatusHandler().hasStatus(Status.UNLIMITED_AMMO); }

	public int getMaxAmmoPrice() {
		int maxAmmo = getMaxClips() * getClipSize();
		int currentAmmo = ammoInClip + ammoInInventory;
		int difference = maxAmmo - currentAmmo;
		float pricePerAmmo = getAmmoPrice() / getClipSize();
		
		return Math.round(pricePerAmmo * difference);
	}
	
	public abstract ProjectileType getProjectile();
	
	@Override
	public String getName() {
		return "Ranged Weapon";
	}

	@Override
	public String getDescription() {
		return "Ranged Weapon";
	}
}
