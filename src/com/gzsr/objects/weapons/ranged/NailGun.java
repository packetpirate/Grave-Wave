package com.gzsr.objects.weapons.ranged;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;

import com.gzsr.AssetManager;
import com.gzsr.entities.Player;
import com.gzsr.gfx.Camera;
import com.gzsr.gfx.particles.Particle;
import com.gzsr.gfx.particles.Projectile;
import com.gzsr.gfx.particles.ProjectileType;
import com.gzsr.gfx.particles.emitters.BloodGenerator;
import com.gzsr.math.Dice;
import com.gzsr.misc.Pair;

public class NailGun extends RangedWeapon {
	private static final int PRICE = 400;
	private static final int AMMO_PRICE = 2;
	private static final long COOLDOWN = 200L;
	private static final int CLIP_SIZE = 110;
	private static final int START_CLIPS = 2;
	private static final int MAX_CLIPS = 10;
	private static final long RELOAD_TIME = 1_500L;
	private static final int MIN_DAMAGE_COUNT = 1;
	private static final int MIN_DAMAGE_SIDES = 4;
	private static final int MIN_DAMAGE_MOD = 2;
	private static final float KNOCKBACK = 1.0f;
	private static final String ICON_NAME = "GZS_NailGun";
	private static final String PROJECTILE_IMAGE = "GZS_Nail";
	private static final String FIRE_SOUND = "nailgun";
	private static final String RELOAD_SOUND = "buy_ammo2";
	
	public NailGun() {
		super(false);
		
		AssetManager assets = AssetManager.getManager();
		
		this.damage = new Dice(NailGun.MIN_DAMAGE_COUNT, NailGun.MIN_DAMAGE_SIDES);
		
		this.useSound = assets.getSound(NailGun.FIRE_SOUND);
		this.reloadSound = assets.getSound(NailGun.RELOAD_SOUND);
		
		this.shakeEffect = new Camera.ShakeEffect(100L, 20L, 5.0f);
	}

	@Override
	public void use(Player player, Pair<Float> position, float theta, long cTime) {
		Color color = getProjectile().getColor();
		float velocity = getProjectile().getVelocity();
		float width = getProjectile().getWidth();
		float height = getProjectile().getHeight();
		long lifespan = getProjectile().getLifespan();
		Particle particle = new Particle(NailGun.PROJECTILE_IMAGE, color, position, velocity, theta,
										 0.0f, new Pair<Float>(width, height), 
										 lifespan, cTime);
		
		boolean critical = isCritical();
		double dmg = damage.roll(NailGun.MIN_DAMAGE_MOD, critical);
		
		Projectile projectile = new Projectile(particle, BloodGenerator.BURST, dmg, critical);
		projectiles.add(projectile);
		
		super.use(player, position, theta, cTime);
	}
	
	@Override
	public Pair<Integer> getDamage() { return damage.getRange(NailGun.MIN_DAMAGE_MOD); }
	
	@Override
	public float getKnockback() { return NailGun.KNOCKBACK; }
	
	@Override
	public boolean isReloading(long cTime) {
		long elapsed = cTime - reloadStart;
		return ((elapsed < NailGun.RELOAD_TIME) && reloading);
	}
	
	@Override
	public long getReloadTime() { return NailGun.RELOAD_TIME; }
	
	@Override
	public double getReloadTime(long cTime) {
		long elapsed = cTime - reloadStart;
		return ((double)elapsed / (double)NailGun.RELOAD_TIME);
	}
	
	@Override
	public Image getInventoryIcon() { return AssetManager.getManager().getImage(NailGun.ICON_NAME); }
	
	@Override
	public int getClipSize() { return NailGun.CLIP_SIZE; }
	
	@Override
	public int getStartClips() { return NailGun.START_CLIPS; }
	
	@Override
	public int getMaxClips() { return NailGun.MAX_CLIPS; }

	@Override
	public long getCooldown() { return NailGun.COOLDOWN; }
	
	@Override
	public ProjectileType getProjectile() { return ProjectileType.NAIL; }

	@Override
	public int getPrice() { return NailGun.PRICE; }
	
	@Override
	public int getAmmoPrice() { return NailGun.AMMO_PRICE; }

	@Override
	public int getLevelRequirement() { return 1; }
	
	@Override
	public String getName() {
		return "Nail Gun";
	}
	
	@Override
	public String getDescription() {
		return "Well it's not a gun, but it'll do... I guess.";
	}
}
