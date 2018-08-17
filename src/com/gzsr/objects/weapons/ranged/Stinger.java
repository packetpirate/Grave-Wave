package com.gzsr.objects.weapons.ranged;

import org.newdawn.slick.Image;

import com.gzsr.AssetManager;
import com.gzsr.achievements.Metrics;
import com.gzsr.entities.Player;
import com.gzsr.gfx.Animation;
import com.gzsr.gfx.particles.Particle;
import com.gzsr.gfx.particles.ProjectileType;
import com.gzsr.math.Dice;
import com.gzsr.misc.Pair;
import com.gzsr.objects.weapons.Explosion;

public class Stinger extends RangedWeapon {
	private static final int PRICE = 38_000;
	private static final int AMMO_PRICE = 10_000;
	private static final long COOLDOWN = 0L;
	private static final int CLIP_SIZE = 1;
	private static final int START_CLIPS = 2;
	private static final int MAX_CLIPS = 4;
	private static final long RELOAD_TIME = 3_000L;
	private static final float KNOCKBACK = 20.0f;
	private static final float EXP_RADIUS = 128.0f;
	private static final String ICON_NAME = "GZS_Stinger";
	private static final String ANIMATION_NAME = "GZS_Stinger_Missile";
	private static final String EXP_NAME = "GZS_Explosion";
	private static final String FIRE_SOUND = "missile";
	private static final String RELOAD_SOUND = "buy_ammo2";
	
	private static final Dice DAMAGE = new Dice(25, 10);
	private static final int DAMAGE_MOD = 250;
	
	public Stinger() {
		super();
		
		AssetManager assets = AssetManager.getManager();
		
		this.useSound = assets.getSound(Stinger.FIRE_SOUND);
		this.reloadSound = assets.getSound(Stinger.RELOAD_SOUND);
		
		addMuzzleFlash();
	}

	@Override
	public void use(Player player, Pair<Float> position, float theta, long cTime) {
		Animation animation = AssetManager.getManager().getAnimation(Stinger.ANIMATION_NAME);
		
		float velocity = getProjectile().getVelocity();
		float width = getProjectile().getWidth();
		float height = getProjectile().getHeight();
		long lifespan = getProjectile().getLifespan();
		Particle particle = new Particle(animation, position, velocity, theta,
										 0.0f, new Pair<Float>(width, height), 
										 lifespan, cTime);
		
		boolean critical = isCritical();
		double dmg = rollDamage(critical);
		dmg += (dmg * (player.getAttributes().getInt("damageUp") * 0.10));
		if(isCritical()) dmg *= player.getAttributes().getDouble("critMult");

		Explosion exp = new Explosion(Explosion.Type.NORMAL, Stinger.EXP_NAME, 
									  new Pair<Float>(0.0f, 0.0f), 
									  dmg, Stinger.KNOCKBACK, Stinger.EXP_RADIUS, 
									  cTime);
		Missile missile = new Missile(particle, exp);
		projectiles.add(missile);
		
		super.use(player, position, theta, cTime);
	}
	
	@Override
	public Pair<Integer> getDamage() { return Stinger.DAMAGE.getRange(Stinger.DAMAGE_MOD); }
	
	@Override
	public double rollDamage(boolean critical) { return Stinger.DAMAGE.roll(Stinger.DAMAGE_MOD, critical); }
	
	@Override
	public float getKnockback() { return Stinger.KNOCKBACK; }

	@Override
	public boolean isReloading(long cTime) {
		long elapsed = cTime - reloadStart;
		return ((elapsed < Stinger.RELOAD_TIME) && reloading);
	}

	@Override
	public long getReloadTime() { return Stinger.RELOAD_TIME; }
	
	@Override
	public double getReloadTime(long cTime) {
		long elapsed = cTime - reloadStart;
		return ((double)elapsed / (double)Stinger.RELOAD_TIME);
	}

	@Override
	public Image getInventoryIcon() { return AssetManager.getManager().getImage(Stinger.ICON_NAME); }
	
	@Override
	public int getClipSize() { return Stinger.CLIP_SIZE; }

	@Override
	public int getStartClips() { return Stinger.START_CLIPS; }
	
	@Override
	public int getMaxClips() { return Stinger.MAX_CLIPS; }

	@Override
	public long getCooldown() { return Stinger.COOLDOWN; }

	@Override
	public ProjectileType getProjectile() { return ProjectileType.MISSILE; }

	@Override
	public int getPrice() { return Stinger.PRICE; }
	
	@Override
	public int getAmmoPrice() { return Stinger.AMMO_PRICE; }

	@Override
	public int getLevelRequirement() { return 15; }
	
	@Override
	public long getWeaponMetric() { return Metrics.STINGER; }
	
	@Override
	public String getName() {
		return "Stinger FIM-92";
	}
	
	@Override
	public String getDescription() {
		return "Who just leaves something like this lying around? This just got a lot easier...";
	}
}
