package com.gzsr.objects.weapons.ranged;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;

import com.gzsr.AssetManager;
import com.gzsr.achievements.Metrics;
import com.gzsr.entities.Player;
import com.gzsr.gfx.particles.Particle;
import com.gzsr.gfx.particles.ProjectileType;
import com.gzsr.gfx.particles.StatusProjectile;
import com.gzsr.math.Dice;
import com.gzsr.misc.Pair;
import com.gzsr.objects.weapons.WType;
import com.gzsr.status.ParalysisEffect;

public class Taser extends RangedWeapon {
	private static final int PRICE = 300;
	private static final int AMMO_PRICE = 75;
	private static final long COOLDOWN = 0L;
	private static final int CLIP_SIZE = 1;
	private static final int START_CLIPS = 8;
	private static final int MAX_CLIPS = 20;
	private static final long RELOAD_TIME = 1_000L;
	private static final long EFFECT_DURATION = 2_500L;
	private static final float KNOCKBACK = 0.0f;
	private static final String PROJECTILE_IMAGE = "GZS_Taser_Dart";
	private static final String FIRE_SOUND = "nailgun";
	private static final String RELOAD_SOUND = "buy_ammo2";

	private static final Dice DAMAGE = new Dice(1, 4);
	private static final int DAMAGE_MOD = 1;

	public Taser() {
		super(Size.SMALL, false);

		AssetManager assets = AssetManager.getManager();

		this.useSound = assets.getSound(Taser.FIRE_SOUND);
		this.reloadSound = assets.getSound(Taser.RELOAD_SOUND);
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

		ParalysisEffect paralysis = new ParalysisEffect(Taser.EFFECT_DURATION, cTime);
		StatusProjectile projectile = new StatusProjectile(particle, dmg, critical, paralysis);
		projectiles.add(projectile);

		super.use(player, position, theta, cTime);
	}

	@Override
	public Pair<Integer> getDamageRange() { return Taser.DAMAGE.getRange(Taser.DAMAGE_MOD); }

	@Override
	public double rollDamage(boolean critical) { return Taser.DAMAGE.roll(Taser.DAMAGE_MOD, critical); }

	@Override
	public float getKnockback() { return Taser.KNOCKBACK; }

	@Override
	public long getReloadTime() { return Taser.RELOAD_TIME; }

	@Override
	public Image getInventoryIcon() { return WType.TASER.getImage(); }

	@Override
	public int getClipSize() { return Taser.CLIP_SIZE; }

	@Override
	public int getClipCapacity() { return getClipSize(); }

	@Override
	public int getStartClips() { return Taser.START_CLIPS; }

	@Override
	public int getMaxClips() { return Taser.MAX_CLIPS; }

	@Override
	public long getCooldown() { return Taser.COOLDOWN; }

	@Override
	public ProjectileType getProjectile() { return ProjectileType.TASER; }

	@Override
	public String getProjectileName() { return Taser.PROJECTILE_IMAGE; }

	@Override
	public int getPrice() { return Taser.PRICE; }

	@Override
	public int getAmmoPrice() { return Taser.AMMO_PRICE; }

	@Override
	public WType getType() { return WType.TASER; }

	@Override
	public int getLevelRequirement() { return 8; }

	@Override
	public Metrics getWeaponMetric() { return Metrics.TASER; }

	@Override
	public String getName() {
		return WType.TASER.getName();
	}

	@Override
	public String getDescription() {
		return WType.TASER.getDescription();
	}
}
