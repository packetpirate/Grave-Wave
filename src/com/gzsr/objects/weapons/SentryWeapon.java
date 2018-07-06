package com.gzsr.objects.weapons;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;

import com.gzsr.AssetManager;
import com.gzsr.entities.Player;
import com.gzsr.gfx.particles.Particle;
import com.gzsr.gfx.particles.Projectile;
import com.gzsr.gfx.particles.ProjectileType;
import com.gzsr.misc.Pair;

public class SentryWeapon extends Weapon {
	private static final int PRICE = 4_000;
	private static final int AMMO_PRICE = 2_000;
	private static final long COOLDOWN = 0L;
	private static final int CLIP_SIZE = 1;
	private static final int START_CLIPS = 1;
	private static final int MAX_CLIPS = 2;
	private static final long RELOAD_TIME = 10_000L;
	private static final float KNOCKBACK = 1.0f;
	private static final String ICON_NAME = "GZS_Turret";
	private static final String FIRE_SOUND = "landmine_armed";
	private static final String RELOAD_SOUND = "buy_ammo2";
	
	public SentryWeapon() {
		super();
		
		AssetManager assets = AssetManager.getManager();
		this.fireSound = assets.getSound(SentryWeapon.FIRE_SOUND);
		this.reloadSound = assets.getSound(SentryWeapon.RELOAD_SOUND);
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
		Turret turret = new Turret(particle);
		projectiles.add(turret);
		
		ammoInClip--;
		lastFired = cTime;
		
		fireSound.play(1.0f, AssetManager.getManager().getSoundVolume());
	}

	@Override
	public Pair<Integer> getDamage() {
		return Turret.getTotalDamage(); 
	}
	
	@Override
	public float getKnockback() {
		return SentryWeapon.KNOCKBACK;
	}
	
	@Override
	public boolean isReloading(long cTime) {
		long elapsed = cTime - reloadStart;
		return ((elapsed < SentryWeapon.RELOAD_TIME) && reloading);
	}

	@Override
	public long getReloadTime() {
		return SentryWeapon.RELOAD_TIME;
	}
	
	@Override
	public double getReloadTime(long cTime) {
		long elapsed = cTime - reloadStart;
		return ((double)elapsed / (double)SentryWeapon.RELOAD_TIME);
	}
	
	@Override
	public String getName() {
		return "Sentry Gun";
	}
	
	@Override
	public String getDescription() {
		return "Feeling overwhelmed and need some automated assistance? Unleash a second, robotic apocalypse on the undead horde.";
	}

	@Override
	public Image getInventoryIcon() {
		return AssetManager.getManager().getImage(SentryWeapon.ICON_NAME);
	}

	@Override
	public int getClipSize() {
		return SentryWeapon.CLIP_SIZE;
	}

	@Override
	protected int getStartClips() {
		return SentryWeapon.START_CLIPS;
	}
	
	@Override
	protected int getMaxClips() { return SentryWeapon.MAX_CLIPS; }

	@Override
	public long getCooldown() {
		return SentryWeapon.COOLDOWN;
	}
	
	@Override
	public List<Projectile> getProjectiles() {
		List<Projectile> allProjectiles = new ArrayList<Projectile>();
		
		allProjectiles.addAll(projectiles);
		for(Projectile p : projectiles) {
			Turret t = (Turret) p;
			allProjectiles.addAll(t.getProjectiles());
		}
		
		return allProjectiles;
	}

	@Override
	public ProjectileType getProjectile() {
		return ProjectileType.TURRET;
	}

	@Override
	public int getPrice() {
		return SentryWeapon.PRICE;
	}

	@Override
	public int getAmmoPrice() {
		return SentryWeapon.AMMO_PRICE;
	}
}
