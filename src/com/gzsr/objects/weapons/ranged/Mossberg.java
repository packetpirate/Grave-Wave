package com.gzsr.objects.weapons.ranged;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;

import com.gzsr.AssetManager;
import com.gzsr.Globals;
import com.gzsr.entities.Player;
import com.gzsr.gfx.Camera;
import com.gzsr.gfx.particles.Particle;
import com.gzsr.gfx.particles.Projectile;
import com.gzsr.gfx.particles.ProjectileType;
import com.gzsr.gfx.particles.emitters.BloodGenerator;
import com.gzsr.math.Dice;
import com.gzsr.misc.Pair;

public class Mossberg extends RangedWeapon {
	private static final int PRICE = 4_000;
	private static final int AMMO_PRICE = 500;
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
	private static final String FIRE_SOUND = "mossberg_shot_01";
	private static final String RELOAD_SOUND = "buy_ammo2";
	
	public Mossberg() {
		super();
		
		AssetManager assets = AssetManager.getManager();
		
		this.damage = new Dice(Mossberg.MIN_DAMAGE_COUNT, Mossberg.MIN_DAMAGE_SIDES);
		
		this.useSound = assets.getSound(Mossberg.FIRE_SOUND);
		this.reloadSound = assets.getSound(Mossberg.RELOAD_SOUND);
		
		this.shakeEffect = new Camera.ShakeEffect(100L, 20L, 15.0f);
		
		addMuzzleFlash();
	}

	@Override
	public void use(Player player, Pair<Float> position, float theta, long cTime) {
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
			
			Projectile projectile = new Projectile(particle, BloodGenerator.BURST, dmg, critical);
			projectiles.add(projectile);
		}
		
		super.use(player, position, theta, cTime);
	}
	
	@Override
	public Pair<Integer> getDamage() {
		Pair<Integer> range = damage.getRange(Mossberg.MIN_DAMAGE_MOD);
		range.x *= Mossberg.SHOT_COUNT;
		range.y *= Mossberg.SHOT_COUNT;
		return range;
	}
	
	@Override
	public float getKnockback() { return Mossberg.KNOCKBACK; }

	@Override
	public boolean isReloading(long cTime) {
		long elapsed = cTime - reloadStart;
		return ((elapsed < Mossberg.RELOAD_TIME) && reloading);
	}

	@Override
	public long getReloadTime() { return Mossberg.RELOAD_TIME; }
	
	@Override
	public double getReloadTime(long cTime) {
		long elapsed = cTime - reloadStart;
		return ((double)elapsed / (double)Mossberg.RELOAD_TIME);
	}

	@Override
	public Image getInventoryIcon() { return AssetManager.getManager().getImage(Mossberg.ICON_NAME); }
	
	@Override
	public int getClipSize() { return Mossberg.CLIP_SIZE; }

	@Override
	public int getStartClips() { return Mossberg.START_CLIPS; }
	
	@Override
	public int getMaxClips() { return Mossberg.MAX_CLIPS; }

	@Override
	public long getCooldown() { return Mossberg.COOLDOWN; }
	
	@Override
	public ProjectileType getProjectile() { return ProjectileType.SHOTGUN; }

	@Override
	public int getPrice() { return Mossberg.PRICE; }
	
	@Override
	public int getAmmoPrice() { return Mossberg.AMMO_PRICE; }

	@Override
	public int getLevelRequirement() { return 5; }
	
	@Override
	public String getName() {
		return "Mossberg 500";
	}
	
	@Override
	public String getDescription() {
		return "It was meant for hunting turkeys, but... well... it'll have to do.";
	}
}
