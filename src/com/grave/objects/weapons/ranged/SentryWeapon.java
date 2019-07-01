package com.gzsr.objects.weapons.ranged;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;

import com.gzsr.AssetManager;
import com.gzsr.achievements.Metrics;
import com.gzsr.entities.Player;
import com.gzsr.gfx.particles.Particle;
import com.gzsr.gfx.particles.Projectile;
import com.gzsr.gfx.particles.ProjectileType;
import com.gzsr.misc.Pair;
import com.gzsr.objects.weapons.ArmConfig;
import com.gzsr.objects.weapons.WType;

public class SentryWeapon extends RangedWeapon {
	private static final int PRICE = 10_000;
	private static final int AMMO_PRICE = 5_000;
	private static final long COOLDOWN = 0L;
	private static final int CLIP_SIZE = 1;
	private static final int START_CLIPS = 1;
	private static final int MAX_CLIPS = 2;
	private static final long RELOAD_TIME = 10_000L;
	private static final float KNOCKBACK = 1.0f;
	private static final String FIRE_SOUND = "landmine_armed";
	private static final String RELOAD_SOUND = "buy_ammo2";

	public SentryWeapon() {
		super(Size.LARGE);

		AssetManager assets = AssetManager.getManager();
		this.useSound = assets.getSound(SentryWeapon.FIRE_SOUND);
		this.reloadSound = assets.getSound(SentryWeapon.RELOAD_SOUND);
	}

	@Override
	public boolean canUse(long cTime) {
		int limit = 1; // TODO: If a talent is added to increase sentry capacity, have the talent modify this.
		return (super.canUse(cTime) && (projectiles.size() < limit));
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

		Turret turret = new Turret(particle);
		projectiles.add(turret);

		ammoInClip--;
		lastUsed = cTime;

		useSound.play(1.0f, AssetManager.getManager().getSoundVolume());
	}

	@Override
	public Pair<Integer> getDamageRange() { return Turret.getTotalDamage(); }

	@Override
	public double rollDamage(boolean critical) { return 0.0; }

	@Override
	public float getKnockback() { return SentryWeapon.KNOCKBACK; }

	@Override
	public long getReloadTime() { return SentryWeapon.RELOAD_TIME; }

	@Override
	public ArmConfig getArmConfig() { return ArmConfig.SENTRY_GUN; }

	@Override
	public Image getInventoryIcon() { return WType.SENTRY_GUN.getImage(); }

	@Override
	public int getClipSize() { return SentryWeapon.CLIP_SIZE; }

	@Override
	public int getClipCapacity() { return getClipSize(); }

	@Override
	public int getStartClips() { return SentryWeapon.START_CLIPS; }

	@Override
	public int getMaxClips() { return SentryWeapon.MAX_CLIPS; }

	@Override
	public long getCooldown() { return SentryWeapon.COOLDOWN; }

	@Override
	public List<Projectile> getProjectiles() {
		List<Projectile> allProjectiles = new ArrayList<Projectile>();

		allProjectiles.addAll(projectiles);
		for(Projectile p : projectiles) {
			Turret t = (Turret) p;
			allProjectiles.addAll(t.getProjectiles());
		}

		return allProjectiles;
	}

	@Override
	public ProjectileType getProjectile() { return ProjectileType.TURRET; }

	@Override
	public String getProjectileName() { return null; }

	@Override
	public int getPrice() { return SentryWeapon.PRICE; }

	@Override
	public int getAmmoPrice() { return SentryWeapon.AMMO_PRICE; }

	@Override
	public WType getType() { return WType.SENTRY_GUN; }

	@Override
	public int getLevelRequirement() { return 15; }

	@Override
	public Metrics getWeaponMetric() { return Metrics.SENTRY; }

	@Override
	public String getName() {
		return WType.SENTRY_GUN.getName();
	}

	@Override
	public String getDescription() {
		return WType.SENTRY_GUN.getDescription();
	}
}
