package com.grave.objects.weapons.ranged;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;

import com.grave.AssetManager;
import com.grave.achievements.Metrics;
import com.grave.entities.Player;
import com.grave.gfx.Camera;
import com.grave.gfx.particles.Particle;
import com.grave.gfx.particles.Projectile;
import com.grave.gfx.particles.ProjectileType;
import com.grave.gfx.particles.emitters.BloodGenerator;
import com.grave.math.Dice;
import com.grave.misc.Pair;
import com.grave.objects.weapons.ArmConfig;
import com.grave.objects.weapons.DamageType;
import com.grave.objects.weapons.WType;

public class Remington extends RangedWeapon {
	private static final int PRICE = 400;
	private static final int AMMO_PRICE = 15;
	private static final long COOLDOWN = 1_000L;
	private static final int CLIP_SIZE = 6;
	private static final int START_CLIPS = 5;
	private static final int MAX_CLIPS = 10;
	private static final long RELOAD_TIME = 1_500L;
	private static final float KNOCKBACK = 10.0f;
	private static final String FIRE_SOUND = "sniper_shot";
	private static final String RELOAD_SOUND = "buy_ammo2";

	private static final Dice DAMAGE = new Dice(5, 4);
	private static final int DAMAGE_MOD = 20;

	public Remington() {
		super(Size.MEDIUM, false);

		AssetManager assets = AssetManager.getManager();

		this.useSound = assets.getSound(Remington.FIRE_SOUND);
		this.reloadSound = assets.getSound(Remington.RELOAD_SOUND);

		this.shakeEffect = new Camera.ShakeEffect(200L, 20L, 10.0f);

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
		double dmg = getDamageTotal(critical);

		Projectile projectile = new Projectile(particle, BloodGenerator.BURST, dmg, critical);
		projectile.setPenetrations(1);
		projectiles.add(projectile);

		super.use(player, position, theta, cTime);
	}

	@Override
	public Pair<Integer> getDamageRange() { return Remington.DAMAGE.getRange(Remington.DAMAGE_MOD); }

	@Override
	public double rollDamage(boolean critical) { return Remington.DAMAGE.roll(Remington.DAMAGE_MOD, critical); }

	@Override
	public DamageType getDamageType() { return DamageType.PIERCING; }

	@Override
	public float getKnockback() { return Remington.KNOCKBACK; }

	@Override
	public long getReloadTime() { return Remington.RELOAD_TIME; }

	@Override
	public ArmConfig getArmConfig() { return ArmConfig.REMINGTON; }

	@Override
	public Image getInventoryIcon() { return WType.REMINGTON.getImage(); }

	@Override
	public int getClipSize() { return Remington.CLIP_SIZE; }

	@Override
	public int getStartClips() { return Remington.START_CLIPS; }

	@Override
	public int getMaxClips() { return Remington.MAX_CLIPS; }

	@Override
	public long getCooldown() { return Remington.COOLDOWN; }

	@Override
	public ProjectileType getProjectile() { return ProjectileType.RIFLE; }

	@Override
	public String getProjectileName() { return null; }

	@Override
	public int getPrice() { return Remington.PRICE; }

	@Override
	public int getAmmoPrice() { return Remington.AMMO_PRICE; }

	@Override
	public WType getType() { return WType.REMINGTON; }

	@Override
	public int getLevelRequirement() { return 8; }

	@Override
	public Metrics getWeaponMetric() { return Metrics.REMINGTON; }

	@Override
	public String getName() {
		return WType.REMINGTON.getName();
	}

	@Override
	public String getDescription() {
		return WType.REMINGTON.getDescription();
	}
}
