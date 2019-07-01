package com.gzsr.objects.weapons.ranged;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;

import com.gzsr.AssetManager;
import com.gzsr.achievements.Metrics;
import com.gzsr.entities.Player;
import com.gzsr.gfx.particles.Particle;
import com.gzsr.gfx.particles.Projectile;
import com.gzsr.gfx.particles.ProjectileType;
import com.gzsr.gfx.particles.emitters.BloodGenerator;
import com.gzsr.math.Dice;
import com.gzsr.misc.Pair;
import com.gzsr.objects.weapons.ArmConfig;
import com.gzsr.objects.weapons.WType;

public class Crossbow extends RangedWeapon {
	private static final int PRICE = 600;
	private static final int AMMO_PRICE = 75;
	private static final long COOLDOWN = 1_500L;
	private static final int CLIP_SIZE = 12;
	private static final int START_CLIPS = 5;
	private static final int MAX_CLIPS = 10;
	private static final long RELOAD_TIME = 2_000L;
	private static final float KNOCKBACK = 10.0f;
	private static final String PROJECTILE_NAME = "GZS_Arrow";
	private static final String FIRE_SOUND = "bow_fire";
	private static final String RELOAD_SOUND = "buy_ammo2";

	private static final Dice DAMAGE = new Dice(2, 8);
	private static final int DAMAGE_MOD = 12;

	public Crossbow() {
		super(Size.SMALL, false);

		AssetManager assets = AssetManager.getManager();

		this.useSound = assets.getSound(Crossbow.FIRE_SOUND);
		this.reloadSound = assets.getSound(Crossbow.RELOAD_SOUND);
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

		Projectile projectile = new Projectile(particle, BloodGenerator.BURST, dmg, critical);
		projectiles.add(projectile);

		super.use(player, position, theta, cTime);
	}

	@Override
	public long getReloadTime() { return Crossbow.RELOAD_TIME; }

	@Override
	public long getCooldown() { return Crossbow.COOLDOWN; }

	@Override
	public int getClipSize() { return Crossbow.CLIP_SIZE; }

	@Override
	public int getStartClips() { return Crossbow.START_CLIPS; }

	@Override
	public int getMaxClips() { return Crossbow.MAX_CLIPS; }

	@Override
	public int getPrice() { return Crossbow.PRICE; }

	@Override
	public int getAmmoPrice() { return Crossbow.AMMO_PRICE; }

	@Override
	public ProjectileType getProjectile() { return ProjectileType.BOLT; }

	@Override
	public String getProjectileName() { return Crossbow.PROJECTILE_NAME; }

	@Override
	public Pair<Integer> getDamageRange() { return Crossbow.DAMAGE.getRange(Crossbow.DAMAGE_MOD); }

	@Override
	public double rollDamage(boolean critical) { return Crossbow.DAMAGE.roll(Crossbow.DAMAGE_MOD, critical); }

	@Override
	public float getKnockback() { return Crossbow.KNOCKBACK; }

	@Override
	public ArmConfig getArmConfig() { return ArmConfig.CROSSBOW; }

	@Override
	public Image getInventoryIcon() { return WType.CROSSBOW.getImage(); }

	@Override
	public WType getType() { return WType.CROSSBOW; }

	@Override
	public int getLevelRequirement() { return 10; }

	@Override
	public Metrics getWeaponMetric() { return Metrics.CROSSBOW; }

	@Override
	public String getName() { return WType.CROSSBOW.getName(); }

	@Override
	public String getDescription() { return WType.CROSSBOW.getDescription(); }
}
