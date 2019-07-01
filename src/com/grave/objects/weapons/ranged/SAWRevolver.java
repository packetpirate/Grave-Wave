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

public class SAWRevolver extends RangedWeapon {
	private static final int PRICE = 1_300;
	private static final int AMMO_PRICE = 10;
	private static final long COOLDOWN = 750L;
	private static final int CLIP_SIZE = 5;
	private static final int START_CLIPS = 5;
	private static final int MAX_CLIPS = 10;
	private static final long RELOAD_TIME = 2_000L;
	private static final float KNOCKBACK = 7.5f;
	private static final String FIRE_SOUND = "revolver_shot_01";
	private static final String RELOAD_SOUND = "buy_ammo2";

	private static final Dice DAMAGE = new Dice(3, 8);
	private static final int DAMAGE_MOD = 12;

	public SAWRevolver() {
		super(Size.SMALL, false);

		AssetManager assets = AssetManager.getManager();

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
		double dmg = getDamageTotal(critical);

		Projectile projectile = new Projectile(particle, BloodGenerator.BURST, dmg, critical);
		projectile.setPenetrations(1);
		projectiles.add(projectile);

		super.use(player, position, theta, cTime);
	}

	@Override
	public Pair<Integer> getDamageRange() { return SAWRevolver.DAMAGE.getRange(SAWRevolver.DAMAGE_MOD); }

	@Override
	public double rollDamage(boolean critical) { return SAWRevolver.DAMAGE.roll(SAWRevolver.DAMAGE_MOD, critical); }

	@Override
	public DamageType getDamageType() { return DamageType.PIERCING; }

	@Override
	public float getKnockback() { return SAWRevolver.KNOCKBACK; }

	@Override
	public long getReloadTime() { return SAWRevolver.RELOAD_TIME; }

	@Override
	public ArmConfig getArmConfig() { return ArmConfig.SAW_REVOLVER; }

	@Override
	public Image getInventoryIcon() { return WType.SAW_REVOLVER.getImage(); }

	@Override
	public int getClipSize() { return SAWRevolver.CLIP_SIZE; }

	@Override
	public int getStartClips() { return SAWRevolver.START_CLIPS; }

	@Override
	public int getMaxClips() { return SAWRevolver.MAX_CLIPS; }

	@Override
	public long getCooldown() { return SAWRevolver.COOLDOWN; }

	@Override
	public ProjectileType getProjectile() { return ProjectileType.MAGNUM; }

	@Override
	public String getProjectileName() { return null; }

	@Override
	public int getPrice() { return SAWRevolver.PRICE; }

	@Override
	public int getAmmoPrice() { return SAWRevolver.AMMO_PRICE; }

	@Override
	public WType getType() { return WType.SAW_REVOLVER; }

	@Override
	public int getLevelRequirement() { return 3; }

	@Override
	public Metrics getWeaponMetric() { return Metrics.SAW_REVOLVER; }

	@Override
	public String getName() {
		return WType.SAW_REVOLVER.getName();
	}

	@Override
	public String getDescription() {
		return WType.SAW_REVOLVER.getDescription();
	}
}
