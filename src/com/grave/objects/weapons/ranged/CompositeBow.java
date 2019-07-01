package com.grave.objects.weapons.ranged;

import org.newdawn.slick.Image;

import com.grave.achievements.Metrics;
import com.grave.gfx.particles.ProjectileType;
import com.grave.math.Dice;
import com.grave.misc.Pair;
import com.grave.objects.weapons.WType;

public class CompositeBow extends BowAndArrow {
	private static final int PRICE = 0;
	private static final int AMMO_PRICE = 200;
	private static final long COOLDOWN = 750L;
	private static final int CLIP_SIZE = 12;
	private static final int START_CLIPS = 1;
	private static final int MAX_CLIPS = 4;
	private static final float KNOCKBACK = 8.0f;
	private static final float CHARGE_RATE = 0.003f;
	private static final String PROJECTILE_NAME = "GZS_Arrow2";

	private static final Dice DAMAGE = new Dice(4, 6);
	private static final int DAMAGE_MOD = 20;

	public CompositeBow() {
		super();
	}

	@Override
	public Pair<Integer> getDamageRange() { return CompositeBow.DAMAGE.getRange(CompositeBow.DAMAGE_MOD); }

	@Override
	public double rollDamage(boolean critical) { return CompositeBow.DAMAGE.roll(CompositeBow.DAMAGE_MOD, critical); }

	@Override
	public float getKnockback() { return CompositeBow.KNOCKBACK; }

	@Override
	public Image getInventoryIcon() { return WType.COMPOSITE_BOW.getImage(); }

	@Override
	protected float getChargeRate() { return CompositeBow.CHARGE_RATE; }

	@Override
	public int getClipSize() { return CompositeBow.CLIP_SIZE; }

	@Override
	public int getStartClips() { return CompositeBow.START_CLIPS; }

	@Override
	public int getMaxClips() { return CompositeBow.MAX_CLIPS; }

	@Override
	public long getCooldown() { return CompositeBow.COOLDOWN; }

	@Override
	public ProjectileType getProjectile() { return ProjectileType.ARROW2; }

	@Override
	public String getProjectileName() { return CompositeBow.PROJECTILE_NAME; }

	@Override
	public int getPrice() { return CompositeBow.PRICE; }

	@Override
	public int getAmmoPrice() { return CompositeBow.AMMO_PRICE; }

	@Override
	public WType getType() { return WType.COMPOSITE_BOW; }

	@Override
	public int getLevelRequirement() { return 8; }

	@Override
	public Metrics getWeaponMetric() { return Metrics.COMPOSITE_BOW; }

	@Override
	public String getName() { return WType.COMPOSITE_BOW.getName(); }

	@Override
	public String getDescription() { return WType.COMPOSITE_BOW.getDescription(); }
}
