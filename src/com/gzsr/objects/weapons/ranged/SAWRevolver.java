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

public class SAWRevolver extends RangedWeapon {
	private static final int PRICE = 2_500;
	private static final int AMMO_PRICE = 400;
	private static final long COOLDOWN = 750L;
	private static final int CLIP_SIZE = 6;
	private static final int START_CLIPS = 5;
	private static final int MAX_CLIPS = 10;
	private static final long RELOAD_TIME = 2_000L;
	private static final int MIN_DAMAGE_COUNT = 3;
	private static final int MIN_DAMAGE_SIDES = 8;
	private static final int MIN_DAMAGE_MOD = 12;
	private static final float KNOCKBACK = 7.5f;
	private static final String ICON_NAME = "GZS_SmithAndWesson";
	private static final String FIRE_SOUND = "revolver_shot_01";
	private static final String RELOAD_SOUND = "buy_ammo2";
	
	public SAWRevolver() {
		super(false);
		
		AssetManager assets = AssetManager.getManager();
		
		this.damage = new Dice(SAWRevolver.MIN_DAMAGE_COUNT, SAWRevolver.MIN_DAMAGE_SIDES);
		
		this.useSound = assets.getSound(SAWRevolver.FIRE_SOUND);
		this.reloadSound = assets.getSound(SAWRevolver.RELOAD_SOUND);
		
		this.shakeEffect = new Camera.ShakeEffect(100L, 20L, 5.0f);
		
		addMuzzleFlash();
	}

	@Override
	public void use(Player player, Pair<Float> position, float theta, long cTime) {
		Color color = getProjectile().getColor();
		float velocity = getProjectile().getVelocity();
		float width = getProjectile().getWidth();
		float height = getProjectile().getHeight();
		long lifespan = getProjectile().getLifespan();
		Particle particle = new Particle(color, position, velocity, theta,
										 0.0f, new Pair<Float>(width, height), 
										 lifespan, cTime);
		
		boolean critical = isCritical();
		double dmg = damage.roll(SAWRevolver.MIN_DAMAGE_MOD, critical);
		
		Projectile projectile = new Projectile(particle, BloodGenerator.BURST, dmg, critical);
		projectile.setPenetrations(1);
		projectiles.add(projectile);
		
		super.use(player, position, theta, cTime);
	}
	
	@Override
	public Pair<Integer> getDamage() { return damage.getRange(SAWRevolver.MIN_DAMAGE_MOD); }
	
	@Override
	public float getKnockback() { return SAWRevolver.KNOCKBACK; }
	
	@Override
	public boolean isReloading(long cTime) {
		long elapsed = cTime - reloadStart;
		return ((elapsed < SAWRevolver.RELOAD_TIME) && reloading);
	}
	
	@Override
	public long getReloadTime() { return SAWRevolver.RELOAD_TIME; }
	
	@Override
	public double getReloadTime(long cTime) {
		long elapsed = cTime - reloadStart;
		return ((double)elapsed / (double)SAWRevolver.RELOAD_TIME);
	}
	
	@Override
	public Image getInventoryIcon() { return AssetManager.getManager().getImage(SAWRevolver.ICON_NAME); }
	
	@Override
	public int getClipSize() { return SAWRevolver.CLIP_SIZE; }
	
	@Override
	public int getStartClips() { return SAWRevolver.START_CLIPS; }
	
	@Override
	public int getMaxClips() { return SAWRevolver.MAX_CLIPS; }

	@Override
	public long getCooldown() { return SAWRevolver.COOLDOWN; }
	
	@Override
	public ProjectileType getProjectile() { return ProjectileType.HANDGUN; }

	@Override
	public int getPrice() { return SAWRevolver.PRICE; }
	
	@Override
	public int getAmmoPrice() { return SAWRevolver.AMMO_PRICE; }

	@Override
	public int getLevelRequirement() { return 3; }
	
	@Override
	public String getName() {
		return "Smith & Wesson Model 500";
	}
	
	@Override
	public String getDescription() {
		return "One of the most popular revolvers in the world... and one of the most powerful.";
	}
}
