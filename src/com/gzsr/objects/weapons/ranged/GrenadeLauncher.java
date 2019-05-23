package com.gzsr.objects.weapons.ranged;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;

import com.gzsr.AssetManager;
import com.gzsr.achievements.Metrics;
import com.gzsr.entities.Player;
import com.gzsr.gfx.particles.Particle;
import com.gzsr.gfx.particles.ProjectileType;
import com.gzsr.math.Dice;
import com.gzsr.misc.Pair;
import com.gzsr.objects.weapons.ArmConfig;
import com.gzsr.objects.weapons.Explosion;
import com.gzsr.objects.weapons.WType;
import com.gzsr.talents.Talents;

public class GrenadeLauncher extends RangedWeapon {
	private static final int PRICE = 6_000;
	private static final int AMMO_PRICE = 720;
	private static final long COOLDOWN = 750L;
	private static final int CLIP_SIZE = 6;
	private static final int START_CLIPS = 2;
	private static final int MAX_CLIPS = 4;
	private static final long RELOAD_TIME = 3_000L;
	private static final float KNOCKBACK = 10.0f;
	private static final float EXP_RADIUS = 64.0f;
	private static final String PROJECTILE_NAME = "GZS_HandEggParticle";
	private static final String EXP_NAME = "GZS_Explosion";
	private static final String FIRE_SOUND = "grenade_launcher";
	private static final String RELOAD_SOUND = "buy_ammo2";

	private static final Dice DAMAGE = new Dice(5, 10);
	private static final int DAMAGE_MOD = 50;

	public GrenadeLauncher() {
		super(Size.LARGE);

		AssetManager assets = AssetManager.getManager();

		this.useSound = assets.getSound(GrenadeLauncher.FIRE_SOUND);
		this.reloadSound = assets.getSound(GrenadeLauncher.RELOAD_SOUND);

		addMuzzleFlash();
	}

	@Override
	public void use(Player player, Pair<Float> position, float theta, long cTime) {
		Color color = getProjectile().getColor();
		float velocity = getProjectile().getVelocity();
		float width = getProjectile().getWidth();
		float height = getProjectile().getHeight();
		long lifespan = getProjectile().getLifespan();
		Particle particle = new Particle(getProjectileName(), color, position, velocity, theta,
										 0.0f, new Pair<Float>(width, height),
										 lifespan, cTime);

		boolean critical = isCritical();
		double dmg = getDamageTotal(critical);
		if(Talents.Munitions.DEMOLITIONS.active()) dmg += (dmg * 0.5);

		Explosion exp = new Explosion(Explosion.Type.NORMAL, GrenadeLauncher.EXP_NAME,
									  new Pair<Float>(0.0f, 0.0f),
									  dmg, critical, GrenadeLauncher.KNOCKBACK,
									  GrenadeLauncher.EXP_RADIUS, cTime);
		Grenade gr = new Grenade(particle, exp);
		projectiles.add(gr);

		super.use(player, position, theta, cTime);
	}

	@Override
	public Pair<Integer> getDamageRange() { return GrenadeLauncher.DAMAGE.getRange(GrenadeLauncher.DAMAGE_MOD); }

	@Override
	public double rollDamage(boolean critical) { return GrenadeLauncher.DAMAGE.roll(GrenadeLauncher.DAMAGE_MOD, critical); }

	@Override
	public float getKnockback() { return GrenadeLauncher.KNOCKBACK; }

	@Override
	public long getReloadTime() { return GrenadeLauncher.RELOAD_TIME; }

	@Override
	public ArmConfig getArmConfig() { return ArmConfig.GRENADE_LAUNCHER; }

	@Override
	public Image getInventoryIcon() { return WType.GRENADE_LAUNCHER.getImage(); }

	@Override
	public int getClipSize() { return GrenadeLauncher.CLIP_SIZE; }

	@Override
	public int getStartClips() { return GrenadeLauncher.START_CLIPS; }

	@Override
	public int getMaxClips() { return GrenadeLauncher.MAX_CLIPS; }

	@Override
	public long getCooldown() { return GrenadeLauncher.COOLDOWN; }

	@Override
	public ProjectileType getProjectile() { return ProjectileType.GRENADE; }

	@Override
	public String getProjectileName() { return GrenadeLauncher.PROJECTILE_NAME; }

	@Override
	public int getPrice() { return GrenadeLauncher.PRICE; }

	@Override
	public int getAmmoPrice() { return GrenadeLauncher.AMMO_PRICE; }

	@Override
	public WType getType() { return WType.GRENADE_LAUNCHER; }

	@Override
	public int getLevelRequirement() { return 12; }

	@Override
	public Metrics getWeaponMetric() { return Metrics.GRENADE; }

	@Override
	public String getName() {
		return WType.GRENADE_LAUNCHER.getName();
	}

	@Override
	public String getDescription() {
		return WType.GRENADE_LAUNCHER.getDescription();
	}
}
