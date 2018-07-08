package com.gzsr.objects.weapons;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.state.BasicGameState;

import com.gzsr.AssetManager;
import com.gzsr.Controls;
import com.gzsr.entities.Player;
import com.gzsr.gfx.Animation;
import com.gzsr.gfx.particles.Particle;
import com.gzsr.gfx.particles.Projectile;
import com.gzsr.gfx.particles.ProjectileType;
import com.gzsr.math.Dice;
import com.gzsr.misc.Pair;
import com.gzsr.status.Status;

public class Beretta extends Weapon {
	private static final int AMMO_PRICE = 100;
	private static final long COOLDOWN = 500L;
	private static final int CLIP_SIZE = 12;
	private static final int START_CLIPS = 4;
	private static final int MAX_CLIPS = 10;
	private static final long RELOAD_TIME = 1_500L;
	private static final int MIN_DAMAGE_COUNT = 1;
	private static final int MIN_DAMAGE_SIDES = 12;
	private static final int MIN_DAMAGE_MOD = 8;
	private static final float KNOCKBACK = 1.0f;
	private static final String ICON_NAME = "GZS_Popgun";
	private static final String FIRE_SOUND = "shoot4";
	private static final String RELOAD_SOUND = "buy_ammo2";
	
	private Animation muzzleFlash;
	private boolean release;
	
	public Beretta() {
		super();
		
		release = false;
		
		AssetManager assets = AssetManager.getManager();
		
		this.damage = new Dice(Beretta.MIN_DAMAGE_COUNT, Beretta.MIN_DAMAGE_SIDES);
		
		this.fireSound = assets.getSound(Beretta.FIRE_SOUND);
		this.reloadSound = assets.getSound(Beretta.RELOAD_SOUND);
		this.muzzleFlash = assets.getAnimation("GZS_MuzzleFlash");
	}
	
	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
		super.update(gs, cTime, delta);
		
		// If mouse released, release fire lock.
		if(!release && !Controls.getInstance().getMouse().isMouseDown()) release = true;
		
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
		
		boolean critical = isCritical();
		double dmg = damage.roll(Beretta.MIN_DAMAGE_MOD, critical);
		dmg += (dmg * (player.getAttributes().getInt("damageUp") * 0.10));
		
		Projectile projectile = new Projectile(particle, dmg, critical);
		projectiles.add(projectile);
		if(!player.hasStatus(Status.UNLIMITED_AMMO)) ammoInClip--;
		lastFired = cTime;
		release = false;
		
		muzzleFlash.restart(cTime);
		fireSound.play(1.0f, AssetManager.getManager().getSoundVolume());
	}
	
	@Override
	public Pair<Integer> getDamage() {
		return damage.getRange(Beretta.MIN_DAMAGE_MOD);
	}
	
	@Override
	public float getKnockback() {
		return Beretta.KNOCKBACK;
	}
	
	@Override
	public boolean isReloading(long cTime) {
		long elapsed = cTime - reloadStart;
		return ((elapsed < Beretta.RELOAD_TIME) && reloading);
	}
	
	@Override
	public long getReloadTime() {
		return Beretta.RELOAD_TIME;
	}
	
	@Override
	public double getReloadTime(long cTime) {
		long elapsed = cTime - reloadStart;
		return ((double)elapsed / (double)Beretta.RELOAD_TIME);
	}
	
	@Override
	public String getName() {
		return "Beretta";
	}
	
	@Override
	public String getDescription() {
		return "A fairly popular pistol. Enough to put a bullet in their heads, at least...";
	}
	
	@Override
	public Image getInventoryIcon() { 
		return AssetManager.getManager().getImage(Beretta.ICON_NAME); 
	}
	
	@Override
	public int getClipSize() { return Beretta.CLIP_SIZE; }
	
	@Override
	protected int getStartClips() { return Beretta.START_CLIPS; }
	
	@Override
	protected int getMaxClips() { return Beretta.MAX_CLIPS; }

	@Override
	public long getCooldown() {
		return Beretta.COOLDOWN;
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
		return Beretta.AMMO_PRICE;
	}
}
