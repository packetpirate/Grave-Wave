package com.gzsr.objects.weapons.ranged;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Sound;
import org.newdawn.slick.state.BasicGameState;

import com.gzsr.AssetManager;
import com.gzsr.Controls;
import com.gzsr.entities.Player;
import com.gzsr.gfx.Animation;
import com.gzsr.gfx.Camera;
import com.gzsr.gfx.particles.Particle;
import com.gzsr.gfx.particles.Projectile;
import com.gzsr.gfx.particles.ProjectileType;
import com.gzsr.misc.Pair;
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
	
	protected boolean release;
	protected boolean automatic;
	public boolean isAutomatic() { return automatic; }
	public void setAutomatic(boolean val) { this.automatic = val; }
	
	protected Camera.ShakeEffect shakeEffect;
	
	protected Animation muzzleFlash;
	public void addMuzzleFlash() {
		if(muzzleFlash == null) muzzleFlash = AssetManager.getManager().getAnimation("GZS_MuzzleFlash");
	}
	
	public RangedWeapon() {
		this(true);
	}
	
	public RangedWeapon(boolean automatic_) {
		this.reloadSound = null;
		
		this.projectiles = new ArrayList<Projectile>();
		
		this.ammoInClip = getClipSize();
		this.ammoInInventory = (getStartClips() - 1) * getClipSize();
		
		this.reloading = false;
		this.reloadStart = 0L;
		
		this.automatic = automatic_;
		this.release = false;
		
		this.shakeEffect = null;
		
		this.muzzleFlash = null;
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
		
		if(!release && !Controls.getInstance().getMouse().isLeftDown()) release = true;
		if((muzzleFlash != null) && (muzzleFlash.isActive(cTime))) muzzleFlash.update(cTime);
	}
	
	@Override
	public void render(Graphics g, long cTime) {
		projectiles.stream()
				   .filter(p -> p.isActive(cTime))
				   .forEach(p -> p.render(g, cTime));
		
		if(muzzleFlash != null) {
			Player player = Player.getPlayer();
			Pair<Float> mp = new Pair<Float>((player.getPosition().x + 5.0f), (player.getPosition().y - 28.0f));
			if(muzzleFlash.isActive(cTime)) muzzleFlash.render(g, mp, player.getPosition(), (player.getRotation() - (float)(Math.PI / 2)));
		}
	}
	
	@Override
	public boolean canUse(long cTime) {
		if(reloading) return false;
		boolean clipNotEmpty = ammoInClip > 0;
		boolean cool = (cTime - lastUsed) >= getCooldown();
		
		if(!clipNotEmpty) return false;
		
		return (Player.getPlayer().isAlive() && equipped && cool && (automatic || release));
	}
	
	@Override
	public void use(Player player, Pair<Float> position, float theta, long cTime) {
		// Derived classes should call this super method AFTER particle creation.
		if(!hasUnlimitedAmmo()) ammoInClip--;
		
		if(shakeEffect != null) {
			if(!Camera.getCamera().isShaking()) Camera.getCamera().shake(shakeEffect, cTime);
			else Camera.getCamera().refreshShake(cTime);
		}
		
		lastUsed = cTime;
		release = false;
		if(muzzleFlash != null) muzzleFlash.restart(cTime);
		useSound.play(1.0f, AssetManager.getManager().getSoundVolume());
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
