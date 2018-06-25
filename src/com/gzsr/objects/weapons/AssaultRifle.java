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

public class AssaultRifle extends Weapon {
	private static final int PRICE = 1_000;
	private static final int AMMO_PRICE = 200;
	private static final long COOLDOWN = 100L;
	private static final int CLIP_SIZE = 30;
	private static final int START_CLIPS = 4;
	private static final int MAX_CLIPS = 10;
	private static final long RELOAD_TIME = 2_000L;
	private static final double DAMAGE = 50.0;
	private static final float KNOCKBACK = 1.0f;
	private static final float MAX_DEVIATION = (float)(Math.PI / 18.0);
	private static final String ICON_NAME = "GZS_RTPS";
	private static final String FIRE_SOUND = "shoot3";
	private static final String RELOAD_SOUND = "buy_ammo2";
	
	private Animation muzzleFlash;
	
	public AssaultRifle() {
		super();
		
		AssetManager assets = AssetManager.getManager();
		
		this.muzzleFlash = assets.getAnimation("GZS_MuzzleFlash");
		this.fireSound = assets.getSound(AssaultRifle.FIRE_SOUND);
		this.reloadSound = assets.getSound(AssaultRifle.RELOAD_SOUND);
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
		Pair<Float> mp = new Pair<Float>((Player.getPlayer().getPosition().x + 5.0f), (Player.getPlayer().getPosition().y - 28.0f));
		if(muzzleFlash.isActive(cTime)) muzzleFlash.render(g, mp, Player.getPlayer().getPosition(), (Player.getPlayer().getRotation() - (float)(Math.PI / 2)));
	}

	@Override
	public void fire(Player player, Pair<Float> position, float theta, long cTime) {
		Color color = getProjectile().getColor();
		float velocity = getProjectile().getVelocity();
		float width = getProjectile().getWidth();
		float height = getProjectile().getHeight();
		long lifespan = getProjectile().getLifespan();
		double damage = AssaultRifle.DAMAGE + (AssaultRifle.DAMAGE * (player.getIntAttribute("damageUp") * 0.10));
		float deviation = Globals.rand.nextFloat() * (MAX_DEVIATION / 2) * (Globals.rand.nextBoolean() ? 1 : -1);
		
		Particle particle = new Particle(color, position, velocity, (theta + deviation),
										 0.0f, new Pair<Float>(width, height), 
										 lifespan, cTime);
		Projectile projectile = new Projectile(particle, damage);
		
		projectiles.add(projectile);
		if(!player.hasStatus(Status.UNLIMITED_AMMO)) ammoInClip--;
		lastFired = cTime;

		muzzleFlash.restart(cTime);
		fireSound.play();
	}
	
	@Override
	public double getDamage() {
		return AssaultRifle.DAMAGE;
	}
	
	@Override
	public float getKnockback() {
		return AssaultRifle.KNOCKBACK;
	}

	@Override
	public boolean isReloading(long cTime) {
		long elapsed = cTime - reloadStart;
		return ((elapsed < AssaultRifle.RELOAD_TIME) && reloading);
	}

	@Override
	public long getReloadTime() {
		return AssaultRifle.RELOAD_TIME;
	}
	
	@Override
	public double getReloadTime(long cTime) {
		long elapsed = cTime - reloadStart;
		return ((double)elapsed / (double)AssaultRifle.RELOAD_TIME);
	}
	
	@Override
	public String getName() {
		return "Assault Rifle";
	}
	
	@Override
	public String getDescription() {
		return "A rapid-fire automatic weapon with the stopping power to mow down an entire wave of zombies in seconds.";
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
	protected int getStartClips() { return AssaultRifle.START_CLIPS; }
	
	@Override
	protected int getMaxClips() { return AssaultRifle.MAX_CLIPS; }

	@Override
	public long getCooldown() {
		return AssaultRifle.COOLDOWN;
	}
	
	@Override
	public ProjectileType getProjectile() {
		return ProjectileType.ASSAULT;
	}

	@Override
	public int getPrice() {
		return AssaultRifle.PRICE;
	}
	
	@Override
	public int getAmmoPrice() {
		return AssaultRifle.AMMO_PRICE;
	}
}
