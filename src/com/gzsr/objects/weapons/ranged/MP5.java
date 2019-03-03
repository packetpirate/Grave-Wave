package com.gzsr.objects.weapons.ranged;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;

import com.gzsr.AssetManager;
import com.gzsr.Globals;
import com.gzsr.achievements.Metrics;
import com.gzsr.entities.Player;
import com.gzsr.gfx.Camera;
import com.gzsr.gfx.particles.Particle;
import com.gzsr.gfx.particles.Projectile;
import com.gzsr.gfx.particles.ProjectileType;
import com.gzsr.gfx.particles.emitters.BloodGenerator;
import com.gzsr.math.Dice;
import com.gzsr.misc.Pair;
import com.gzsr.objects.weapons.WType;

public class MP5 extends RangedWeapon {
	private static final int PRICE = 1_450;
	private static final int AMMO_PRICE = 20;
	private static final long COOLDOWN = 75L;
	private static final int CLIP_SIZE = 40;
	private static final int START_CLIPS = 3;
	private static final int MAX_CLIPS = 10;
	private static final long RELOAD_TIME = 1_500L;
	private static final float KNOCKBACK = 1.0f;
	private static final float MAX_DEVIATION = (float)(Math.PI / 18.0);
	private static final String FIRE_SOUND = "m4a1_shot_01";
	private static final String RELOAD_SOUND = "buy_ammo2";

	private static final Dice DAMAGE = new Dice(1, 8);
	private static final int DAMAGE_MOD = 2;

	public MP5() {
		super(Size.SMALL);

		AssetManager assets = AssetManager.getManager();

		this.useSound = assets.getSound(MP5.FIRE_SOUND);
		this.reloadSound = assets.getSound(MP5.RELOAD_SOUND);

		this.shakeEffect = new Camera.ShakeEffect(150L, 50L, 5.0f);

		addMuzzleFlash();
	}

	@Override
	public void use(Player player, Pair<Float> position, float theta, long cTime) {
		Color color = getProjectile().getColor();
		float velocity = getProjectile().getVelocity();
		float width = getProjectile().getWidth();
		float height = getProjectile().getHeight();
		long lifespan = getProjectile().getLifespan();

		boolean critical = isCritical();
		double dmg = getDamageTotal(critical);

		float deviation = Globals.rand.nextFloat() * (MAX_DEVIATION / 2) * (Globals.rand.nextBoolean() ? 1 : -1);

		Particle particle = new Particle(color, position, velocity, (theta + deviation),
										 0.0f, new Pair<Float>(width, height),
										 lifespan, cTime);

		Projectile projectile = new Projectile(particle, BloodGenerator.BURST, dmg, critical);
		projectiles.add(projectile);

		super.use(player, position, theta, cTime);
	}

	@Override
	public Pair<Integer> getDamageRange() { return MP5.DAMAGE.getRange(MP5.DAMAGE_MOD); }

	@Override
	public double rollDamage(boolean critical) { return MP5.DAMAGE.roll(MP5.DAMAGE_MOD, critical); }

	@Override
	public float getKnockback() { return MP5.KNOCKBACK; }

	@Override
	public long getReloadTime() { return MP5.RELOAD_TIME; }

	@Override
	public Image getInventoryIcon() { return WType.MP5.getImage(); }

	@Override
	public int getClipSize() { return MP5.CLIP_SIZE; }

	@Override
	public int getStartClips() { return MP5.START_CLIPS; }

	@Override
	public int getMaxClips() { return MP5.MAX_CLIPS; }

	@Override
	public long getCooldown() { return MP5.COOLDOWN; }

	@Override
	public ProjectileType getProjectile() { return ProjectileType.SMG; }

	@Override
	public String getProjectileName() { return null; }

	@Override
	public int getPrice() { return MP5.PRICE; }

	@Override
	public int getAmmoPrice() { return MP5.AMMO_PRICE; }

	@Override
	public WType getType() { return WType.MP5; }

	@Override
	public int getLevelRequirement() { return 3; }

	@Override
	public Metrics getWeaponMetric() { return Metrics.MP5; }

	@Override
	public String getName() {
		return WType.MP5.getName();
	}

	@Override
	public String getDescription() {
		return WType.MP5.getDescription();
	}
}
