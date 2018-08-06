package com.gzsr.objects.weapons.ranged;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;

import com.gzsr.AssetManager;
import com.gzsr.entities.Player;
import com.gzsr.gfx.particles.Particle;
import com.gzsr.gfx.particles.Projectile;
import com.gzsr.gfx.particles.ProjectileType;
import com.gzsr.gfx.particles.emitters.BloodGenerator;
import com.gzsr.math.Dice;
import com.gzsr.misc.Pair;

public class Crossbow extends RangedWeapon {
	private static final int PRICE = 5_000;
	private static final int AMMO_PRICE = 800;
	private static final long COOLDOWN = 1_500L;
	private static final int CLIP_SIZE = 12;
	private static final int START_CLIPS = 5;
	private static final int MAX_CLIPS = 10;
	private static final long RELOAD_TIME = 2_000L;
	private static final int MIN_DAMAGE_COUNT = 2;
	private static final int MIN_DAMAGE_SIDES = 8;
	private static final int MIN_DAMAGE_MOD = 12;
	private static final float KNOCKBACK = 10.0f;
	private static final String ICON_NAME = "GZS_Crossbow";
	private static final String PROJECTILE_NAME = "GZS_Arrow";
	private static final String FIRE_SOUND = "bow_fire";
	private static final String RELOAD_SOUND = "buy_ammo2";
	
	public Crossbow() {
		super();
		
		AssetManager assets = AssetManager.getManager();
		
		this.damage = new Dice(Crossbow.MIN_DAMAGE_COUNT, Crossbow.MIN_DAMAGE_SIDES);
		
		this.useSound = assets.getSound(Crossbow.FIRE_SOUND);
		this.reloadSound = assets.getSound(Crossbow.RELOAD_SOUND);
	}
	
	@Override
	public void use(Player player, Pair<Float> position, float theta, long cTime) {
		Color color = getProjectile().getColor();
		float velocity = getProjectile().getVelocity();
		float width = getProjectile().getWidth();
		float height = getProjectile().getHeight();
		long lifespan = getProjectile().getLifespan();
		Particle particle = new Particle(Crossbow.PROJECTILE_NAME, color, position, velocity, theta,
										 0.0f, new Pair<Float>(width, height), 
										 lifespan, cTime);
		
		boolean critical = isCritical();
		double dmg = damage.roll(Crossbow.MIN_DAMAGE_MOD, critical);
		
		Projectile projectile = new Projectile(particle, BloodGenerator.BURST, dmg, critical);
		
		projectiles.add(projectile);
		if(!hasUnlimitedAmmo()) ammoInClip--;
		
		lastUsed = cTime;
		useSound.play(1.0f, AssetManager.getManager().getSoundVolume());
	}
	
	@Override
	public boolean isReloading(long cTime) {
		long elapsed = cTime - reloadStart;
		return ((elapsed < Crossbow.RELOAD_TIME) && reloading);
	}

	@Override
	public double getReloadTime(long cTime) { 
		long elapsed = cTime - reloadStart;
		return ((double)elapsed / (double)Crossbow.RELOAD_TIME); 
	}

	@Override
	public long getReloadTime() { return Crossbow.RELOAD_TIME; }

	@Override
	public long getCooldown() { return Crossbow.COOLDOWN; }
	
	@Override
	public int getClipSize() { return Crossbow.CLIP_SIZE; }

	@Override
	public int getStartClips() { return Crossbow.START_CLIPS; }

	@Override
	public int getMaxClips() { return Crossbow.MAX_CLIPS; }

	@Override
	public int getPrice() { return Crossbow.PRICE; }
	
	@Override
	public int getAmmoPrice() { return Crossbow.AMMO_PRICE; }

	@Override
	public ProjectileType getProjectile() { return ProjectileType.BOLT; }

	@Override
	public Pair<Integer> getDamage() { return damage.getRange(Crossbow.MIN_DAMAGE_MOD); }

	@Override
	public float getKnockback() { return Crossbow.KNOCKBACK; }

	@Override
	public Image getInventoryIcon() { return AssetManager.getManager().getImage(Crossbow.ICON_NAME); }
	
	@Override
	public int getLevelRequirement() { return 10; }
	
	@Override
	public String getName() {
		return "Crossbow";
	}
	
	@Override
	public String getDescription() {
		return "It may not be the most efficient weapon when you're being swarmed, but it can do some serious damage.";
	}
}
