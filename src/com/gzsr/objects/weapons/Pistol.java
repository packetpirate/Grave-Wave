package com.gzsr.objects.weapons;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import com.gzsr.AssetManager;
import com.gzsr.Globals;
import com.gzsr.entities.Player;
import com.gzsr.gfx.Animation;
import com.gzsr.gfx.particles.Particle;
import com.gzsr.gfx.particles.Projectile;
import com.gzsr.gfx.particles.ProjectileType;
import com.gzsr.misc.Pair;
import com.gzsr.states.GameState;
import com.gzsr.status.Status;

public class Pistol extends Weapon {
	private static final int AMMO_PRICE = 100;
	private static final long COOLDOWN = 300L;
	private static final int CLIP_SIZE = 12;
	private static final int START_CLIPS = 4;
	private static final long RELOAD_TIME = 1_500L;
	private static final double DAMAGE = 75.0;
	private static final String ICON_NAME = "GZS_Popgun";
	private static final String FIRE_SOUND = "shoot4";
	private static final String RELOAD_SOUND = "buy_ammo2";
	
	private Animation muzzleFlash;
	private boolean release;
	
	public Pistol() {
		super();
		
		release = false;
		
		AssetManager assets = AssetManager.getManager();
		
		this.fireSound = assets.getSound(Pistol.FIRE_SOUND);
		this.reloadSound = assets.getSound(Pistol.RELOAD_SOUND);
		this.muzzleFlash = assets.getAnimation("GZS_MuzzleFlash");
	}
	
	@Override
	public void update(GameState gs, long cTime, int delta) {
		super.update(gs, cTime, delta);
		
		// If mouse released, release fire lock.
		if(!release && !Globals.mouse.isMouseDown()) release = true;
		
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
	public boolean canFire(long cTime) {
		return (super.canFire(cTime) && release);
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
		double damage = Pistol.DAMAGE + (Pistol.DAMAGE * (player.getIntAttribute("damageUp") * 0.10));
		Projectile projectile = new Projectile(particle, damage);
		projectiles.add(projectile);
		if(!player.hasStatus(Status.UNLIMITED_AMMO)) ammoInClip--;
		lastFired = cTime;
		release = false;
		
		muzzleFlash.restart(cTime);
		fireSound.play();
	}
	
	@Override
	public double getDamage() {
		return Pistol.DAMAGE;
	}
	
	@Override
	public boolean isReloading(long cTime) {
		long elapsed = cTime - reloadStart;
		return ((elapsed < Pistol.RELOAD_TIME) && reloading);
	}
	
	@Override
	public long getReloadTime() {
		return Pistol.RELOAD_TIME;
	}
	
	@Override
	public double getReloadTime(long cTime) {
		long elapsed = cTime - reloadStart;
		return ((double)elapsed / (double)Pistol.RELOAD_TIME);
	}
	
	@Override
	public String getName() {
		return "Pistol";
	}
	
	@Override
	public String getDescription() {
		return "A standard issue pistol. Nothing more, nothing less.";
	}
	
	@Override
	public Image getInventoryIcon() { 
		return AssetManager.getManager().getImage(Pistol.ICON_NAME); 
	}
	
	@Override
	public int getClipSize() { return Pistol.CLIP_SIZE; }
	
	@Override
	protected int getStartClips() { return Pistol.START_CLIPS; }

	@Override
	public long getCooldown() {
		return Pistol.COOLDOWN;
	}
	
	@Override
	public ProjectileType getProjectile() {
		return ProjectileType.HANDGUN;
	}

	@Override
	public int getPrice() {
		return 0;
	}
	
	@Override
	public int getAmmoPrice() {
		return Pistol.AMMO_PRICE;
	}
}
