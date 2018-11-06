package com.gzsr.objects.weapons.ranged;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Sound;
import org.newdawn.slick.state.BasicGameState;

import com.gzsr.AssetManager;
import com.gzsr.Controls;
import com.gzsr.Globals;
import com.gzsr.entities.Player;
import com.gzsr.gfx.Animation;
import com.gzsr.gfx.Camera;
import com.gzsr.gfx.particles.Particle;
import com.gzsr.gfx.particles.Projectile;
import com.gzsr.gfx.particles.ProjectileType;
import com.gzsr.gfx.ui.StatusMessages;
import com.gzsr.misc.Pair;
import com.gzsr.objects.weapons.Weapon;
import com.gzsr.states.GameState;
import com.gzsr.status.Status;
import com.gzsr.talents.Talents;

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
	
	private Size size;
	protected enum Size {
		NONE, SMALL, MEDIUM, LARGE
	}
	
	public RangedWeapon(Size size_) {
		this(size_, true);
	}
	
	public RangedWeapon(Size size_, boolean automatic_) {
		this.reloadSound = null;
		
		this.projectiles = new ArrayList<Projectile>();
		
		this.ammoInClip = getClipSize();
		this.ammoInInventory = (getStartClips() - 1) * getClipSize();
		
		this.reloading = false;
		this.reloadStart = 0L;
		
		this.automatic = automatic_;
		this.release = false;
		
		this.size = size_;
		
		this.shakeEffect = null;
		
		this.muzzleFlash = null;
	}
	
	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
		// Basically just checking to see if the reload time has elapsed.
		if(reloading && !isReloading(cTime)) reload();
		
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
		
		if(!automatic && !release && !Controls.getInstance().getMouse().isLeftDown()) release = true;
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
	
	// Determines if a player can move while this weapon is being used.
	public boolean blockingMovement() { return false; }
	
	@Override
	public boolean canUse(long cTime) {
		if(reloading) return false;
		boolean clipNotEmpty = ammoInClip > 0;
		boolean cool = (cTime - lastUsed) >= getTotalCooldown();
		
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
	
	private void reload() {
		int takeFromInv = getClipCapacity() - ammoInClip;
		int taken = Math.min(takeFromInv, ammoInInventory);
		ammoInInventory -= taken;
		ammoInClip += taken;
		
		reloading = false;
	}
	
	public void reload(long cTime) {
		if(ammoInInventory > 0) {
			reloading = true;
			reloadStart = cTime;
			if(Talents.Munitions.HASTE.active()) {
				float roll = Globals.rand.nextFloat();
				if(roll <= 0.1f) {
					reload();
					StatusMessages.getInstance().addMessage("Haste!", Player.getPlayer(), Player.ABOVE_1, cTime, 1_000L);
				}
			}
			
			if(reloadSound != null) reloadSound.play();
		}
	}

	public boolean isReloading(long cTime) {
		long elapsed = cTime - reloadStart;
		return ((elapsed < getReloadTimeTotal()) && reloading);
	}
	
	public double getReloadTime(long cTime) {
		long elapsed = cTime - reloadStart;
		return ((double)elapsed / (double)getReloadTimeTotal());
	}
	
	public abstract long getReloadTime();
	public long getReloadTimeTotal() {
		long time = getReloadTime();
		if(Talents.Munitions.QUICK_FINGERS.active()) time = (long)(time * (1.0 - (Talents.Munitions.QUICK_FINGERS.ranks() * 0.1)));
		return time;
	}
	
	@Override
	public double getDamageTotal(boolean critical) {
		double dmg = rollDamage(critical);
		if(critical) dmg *= Player.getPlayer().getAttributes().getDouble("critMult");
		
		double bonus = 0.0;
		switch(size) {
			case SMALL:
				bonus = (Talents.Munitions.SCOUT.ranks() * 0.20);
				break;
			case MEDIUM:
				bonus = (Talents.Munitions.SOLDIER.ranks() * 0.20);
				break;
			case LARGE:
				bonus = (Talents.Munitions.COMMANDO.ranks() * 0.20);
				break;
			default:
				break;
		}
		
		if(Talents.Munitions.DESPOT.active()) bonus += 0.5;
		if(bonus > 0.0) dmg += (bonus * dmg);
		
		return dmg;
	}
	
	public long getTotalCooldown() {
		long cooldown = getCooldown();
		if(Talents.Munitions.RAPID_FIRE.active()) {
			int ranks = Talents.Munitions.RAPID_FIRE.ranks();
			double modifier = (1.0 - (ranks * 0.25));
			cooldown = (long)(cooldown * modifier);
		}
		
		return cooldown;
	}
	
	public abstract int getClipSize();
	public abstract int getStartClips();
	public abstract int getMaxClips();
	public abstract int getAmmoPrice();
	
	public int getClipCapacity() {
		int capacity = getClipSize();
		if(Talents.Munitions.MODDER.active()) capacity += (capacity / 2);
		return capacity;
	}
	
	public int getAmmoCapacity() {
		int capacity = getMaxClips() * getClipSize();
		if(Talents.Tactics.STOCKPILE.active()) capacity += (int)(capacity * (Talents.Tactics.STOCKPILE.ranks() * 0.5));
		return capacity;
	}
	
	public boolean clipsMaxedOut() {
		int totalAmmo = ammoInClip + ammoInInventory;
		int maxAmmo = getAmmoCapacity();
		return (totalAmmo >= maxAmmo); 
	}
	
	public void addInventoryAmmo(int amnt) {
		if(!clipsMaxedOut()) {
			int maxAmmo = getAmmoCapacity();
			int totalAmmo = ammoInClip + ammoInInventory;
			boolean noOverflow = ((totalAmmo + amnt) <= maxAmmo); 
			ammoInInventory += (noOverflow ? amnt : (maxAmmo - totalAmmo));
		}
	}
	
	public void maxOutAmmo() {
		ammoInInventory = (getAmmoCapacity() - ammoInClip); 
	}
	
	protected boolean hasUnlimitedAmmo() { return Player.getPlayer().getStatusHandler().hasStatus(Status.UNLIMITED_AMMO); }

	public int maxAmmoAffordable(int money) {
		int currentAmmo = (ammoInClip + ammoInInventory);
		int maxAmmo = getAmmoCapacity();
		int difference = (maxAmmo - currentAmmo);
		
		double pricePerAmmo = ((double)getAmmoPrice() / (double)getClipSize());
		int maxAffordable = (int)Math.floor(money / pricePerAmmo);
		
		if(maxAffordable < difference) return maxAffordable;
		return difference;
	}
	
	public int getCostForAmmo(int amnt) {
		double pricePerAmmo = ((double)getAmmoPrice() / (double)getClipSize());
		return (int)Math.round(amnt * pricePerAmmo);
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
