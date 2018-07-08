package com.gzsr.objects.weapons;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.state.BasicGameState;

import com.gzsr.AssetManager;
import com.gzsr.Globals;
import com.gzsr.entities.Player;
import com.gzsr.gfx.Animation;
import com.gzsr.gfx.particles.Particle;
import com.gzsr.gfx.particles.Projectile;
import com.gzsr.gfx.particles.ProjectileType;
import com.gzsr.gfx.particles.emitters.BloodGenerator;
import com.gzsr.math.Dice;
import com.gzsr.misc.Pair;
import com.gzsr.status.Status;

public class Mossberg extends Weapon {
	private static final int PRICE = 1_200;
	private static final int AMMO_PRICE = 250;
	private static final long COOLDOWN = 1_200L;
	private static final int CLIP_SIZE = 8;
	private static final int START_CLIPS = 5;
	private static final int MAX_CLIPS = 8;
	private static final int SHOT_COUNT = 5;
	private static final float MAX_SPREAD = (float)(Math.PI / 12);
	private static final long RELOAD_TIME = 2_500L;
	private static final int MIN_DAMAGE_COUNT = 1;
	private static final int MIN_DAMAGE_SIDES = 6;
	private static final int MIN_DAMAGE_MOD = 8;
	private static final float KNOCKBACK = 5.0f;
	private static final String ICON_NAME = "GZS_Boomstick";
	private static final String FIRE_SOUND = "shotgun1";
	private static final String RELOAD_SOUND = "buy_ammo2";
	
	private Animation muzzleFlash;
	
	public Mossberg() {
		super();
		
		AssetManager assets = AssetManager.getManager();
		
		this.damage = new Dice(Mossberg.MIN_DAMAGE_COUNT, Mossberg.MIN_DAMAGE_SIDES);
		
		this.muzzleFlash = assets.getAnimation("GZS_MuzzleFlash");
		this.fireSound = assets.getSound(Mossberg.FIRE_SOUND);
		this.reloadSound = assets.getSound(Mossberg.RELOAD_SOUND);
	}
	
	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
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
		for(int i = 0; i < Mossberg.SHOT_COUNT; i++) {
			Color color = getProjectile().getColor();
			float velocity = getProjectile().getVelocity();
			float width = getProjectile().getWidth();
			float height = getProjectile().getHeight();
			float devTheta = (theta + (Globals.rand.nextFloat() * Mossberg.MAX_SPREAD * (Globals.rand.nextBoolean()?1:-1)));
			long lifespan = getProjectile().getLifespan();
			Particle particle = new Particle(color, position, velocity, devTheta,
											 0.0f, new Pair<Float>(width, height), 
											 lifespan, cTime);
			
			boolean critical = isCritical();
			double dmg = damage.roll(Mossberg.MIN_DAMAGE_MOD, critical);
			dmg += (dmg * (player.getAttributes().getInt("damageUp") * 0.10));
			
			Projectile projectile = new Projectile(particle, BloodGenerator.BURST, dmg, critical);
			projectiles.add(projectile);
		}
		
		if(!player.hasStatus(Status.UNLIMITED_AMMO)) ammoInClip--;
		lastFired = cTime;
		
		muzzleFlash.restart(cTime);
		fireSound.play(1.0f, AssetManager.getManager().getSoundVolume());
	}
	
	@Override
	public Pair<Integer> getDamage() {
		Pair<Integer> range = damage.getRange(Mossberg.MIN_DAMAGE_MOD);
		range.x *= Mossberg.SHOT_COUNT;
		range.y *= Mossberg.SHOT_COUNT;
		return range;
	}
	
	@Override
	public float getKnockback() {
		return Mossberg.KNOCKBACK;
	}

	@Override
	public boolean isReloading(long cTime) {
		long elapsed = cTime - reloadStart;
		return ((elapsed < Mossberg.RELOAD_TIME) && reloading);
	}

	@Override
	public long getReloadTime() {
		return Mossberg.RELOAD_TIME;
	}
	
	@Override
	public double getReloadTime(long cTime) {
		long elapsed = cTime - reloadStart;
		return ((double)elapsed / (double)Mossberg.RELOAD_TIME);
	}

	@Override
	public String getName() {
		return "Mossberg";
	}
	
	@Override
	public String getDescription() {
		return "It was meant for hunting turkeys, but... well... it'll have to do.";
	}
	
	@Override
	public Image getInventoryIcon() {
		return AssetManager.getManager().getImage(Mossberg.ICON_NAME);
	}

	@Override
	public int getClipSize() {
		return Mossberg.CLIP_SIZE;
	}

	@Override
	protected int getStartClips() { return Mossberg.START_CLIPS; }
	
	@Override
	protected int getMaxClips() { return Mossberg.MAX_CLIPS; }

	@Override
	public long getCooldown() {
		return Mossberg.COOLDOWN;
	}
	
	@Override
	public ProjectileType getProjectile() {
		return ProjectileType.SHOTGUN;
	}

	@Override
	public int getPrice() {
		return Mossberg.PRICE;
	}
	
	@Override
	public int getAmmoPrice() {
		return Mossberg.AMMO_PRICE;
	}
}
