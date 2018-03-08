package com.gzsr.objects.weapons;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import com.gzsr.AssetManager;
import com.gzsr.Globals;
import com.gzsr.entities.Player;
import com.gzsr.gfx.Animation;
import com.gzsr.gfx.particles.Particle;
import com.gzsr.gfx.particles.ProjectileType;
import com.gzsr.misc.Pair;
import com.gzsr.states.GameState;
import com.gzsr.status.Status;

public class GrenadeLauncher extends Weapon {
	private static final int PRICE = 6_000;
	private static final int AMMO_PRICE = 1_500;
	private static final long COOLDOWN = 1000L;
	private static final int CLIP_SIZE = 8;
	private static final int START_CLIPS = 4;
	private static final long RELOAD_TIME = 3000L;
	private static final double DAMAGE = 250.0;
	private static final float EXP_RADIUS = 150.0f;
	private static final String ICON_NAME = "GZS_HandEgg";
	private static final String PROJECTILE_NAME = "GZS_HandEggParticle";
	private static final String EXP_NAME = "GZS_Explosion";
	private static final String FIRE_SOUND = "throw2";
	private static final String RELOAD_SOUND = "buy_ammo2";
	
	private Animation muzzleFlash;
	
	public GrenadeLauncher() {
		super();
		
		AssetManager assets = AssetManager.getManager();
		
		this.muzzleFlash = assets.getAnimation("GZS_MuzzleFlash");
		this.fireSound = assets.getSound(GrenadeLauncher.FIRE_SOUND);
		this.reloadSound = assets.getSound(GrenadeLauncher.RELOAD_SOUND);
	}
	
	@Override
	public void update(GameState gs, long cTime, int delta) {
		super.update(gs, cTime, delta);
		
		// Update muzzle flash animation.
		if(muzzleFlash.isActive(cTime)) muzzleFlash.update(cTime);
	}
	
	@Override
	public void render(Graphics g, long cTime) {
		super.render(g, cTime);
		
		// Render muzzle flash.
		Pair<Float> mp = new Pair<Float>((Globals.player.getPosition().x + 5.0f), (Globals.player.getPosition().y - 28.0f));
		if(muzzleFlash.isActive(cTime)) muzzleFlash.render(g, mp, Globals.player.getPosition(), (Globals.player.getRotation() - (float)(Math.PI / 2)));
	}

	@Override
	public void fire(Player player, Pair<Float> position, float theta, long cTime) {
		Color color = getProjectile().getColor();
		float velocity = getProjectile().getVelocity();
		float width = getProjectile().getWidth();
		float height = getProjectile().getHeight();
		long lifespan = getProjectile().getLifespan();
		Particle particle = new Particle(GrenadeLauncher.PROJECTILE_NAME, color, position, velocity, theta,
										 0.0f, new Pair<Float>(width, height), 
										 lifespan, cTime);
		double damage = GrenadeLauncher.DAMAGE + (GrenadeLauncher.DAMAGE * (player.getIntAttribute("damageUp") * 0.10));
		Explosion exp = new Explosion(ExplosionType.NORMAL, GrenadeLauncher.EXP_NAME, new Pair<Float>(0.0f, 0.0f), damage, GrenadeLauncher.EXP_RADIUS);
		Grenade gr = new Grenade(particle, exp);
		projectiles.add(gr);
		if(!player.hasStatus(Status.UNLIMITED_AMMO)) ammoInClip--;
		lastFired = cTime;
		
		muzzleFlash.restart(cTime);
		fireSound.play();
	}
	
	@Override
	public double getDamage() {
		return GrenadeLauncher.DAMAGE;
	}

	@Override
	public boolean isReloading(long cTime) {
		long elapsed = cTime - reloadStart;
		return ((elapsed < GrenadeLauncher.RELOAD_TIME) && reloading);
	}

	@Override
	public long getReloadTime() {
		return GrenadeLauncher.RELOAD_TIME;
	}
	
	@Override
	public double getReloadTime(long cTime) {
		long elapsed = cTime - reloadStart;
		return ((double)elapsed / (double)GrenadeLauncher.RELOAD_TIME);
	}
	
	@Override
	public String getName() {
		return "Grenade Launcher";
	}
	
	@Override
	public String getDescription() {
		return "A tube-barreled weapon with a revolving chamber full of grenades so you can rain concussive blasts of fire upon the undead horde.";
	}

	@Override
	public Image getInventoryIcon() {
		return AssetManager.getManager().getImage(GrenadeLauncher.ICON_NAME);
	}

	@Override
	public int getClipSize() {
		return GrenadeLauncher.CLIP_SIZE;
	}

	@Override
	protected int getStartClips() {
		return GrenadeLauncher.START_CLIPS;
	}

	@Override
	public long getCooldown() {
		return GrenadeLauncher.COOLDOWN;
	}

	@Override
	public ProjectileType getProjectile() {
		return ProjectileType.GRENADE;
	}

	@Override
	public int getPrice() {
		return GrenadeLauncher.PRICE;
	}
	
	@Override
	public int getAmmoPrice() {
		return GrenadeLauncher.AMMO_PRICE;
	}
}
