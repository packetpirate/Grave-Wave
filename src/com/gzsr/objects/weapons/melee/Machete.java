package com.gzsr.objects.weapons.melee;

import org.newdawn.slick.Image;

import com.gzsr.AssetManager;
import com.gzsr.achievements.Metrics;
import com.gzsr.math.Dice;
import com.gzsr.misc.Pair;
import com.gzsr.objects.weapons.DamageType;
import com.gzsr.objects.weapons.WType;

public class Machete extends MeleeWeapon {
	private static final Pair<Float> HIT_AREA_SIZE = new Pair<Float>(96.0f, 32.0f);
	private static final float HIT_AREA_OFFSET = -32.0f;
	private static final float IMAGE_DISTANCE = -8.0f;
	private static final long ATTACK_TIME = 200L;
	private static final long COOLDOWN = 250L;
	private static final int BPM = 10;
	private static final float KNOCKBACK = 0.0f;
	private static final float THETA_OFFSET = (float)(Math.PI / 3.6);
	private static final String WEAPON_IMAGE = "GZS_Machete";
	private static final Dice DAMAGE = new Dice(4, 4);
	private static final int DAMAGE_MOD = 2;

	public Machete() {
		super();

		img = AssetManager.getManager().getImage(Machete.WEAPON_IMAGE);
		useSound = AssetManager.getManager().getSound("throw2");
	}

	@Override
	public float getDistance() { return Machete.HIT_AREA_OFFSET; }

	@Override
	public float getImageDistance() { return Machete.IMAGE_DISTANCE; }

	@Override
	public Pair<Float> getHitAreaSize() { return Machete.HIT_AREA_SIZE; }

	@Override
	public float getThetaOffset() { return Machete.THETA_OFFSET; }

	@Override
	public long getAttackTime() { return Machete.ATTACK_TIME; }

	@Override
	public long getCooldown() { return Machete.COOLDOWN; }

	@Override
	public int getPrice() { return 0; }

	@Override
	public boolean canSell() { return false; }

	@Override
	public DamageType getDamageType() { return DamageType.SLICING; }

	@Override
	public Pair<Integer> getDamageRange() { return Machete.DAMAGE.getRange(Machete.DAMAGE_MOD); }

	@Override
	public double rollDamage(boolean critical) { return Machete.DAMAGE.roll(Machete.DAMAGE_MOD, critical); }

	@Override
	public int getBPMCost() { return Machete.BPM; }

	@Override
	public float getKnockback() { return Machete.KNOCKBACK; }

	@Override
	public Image getInventoryIcon() { return WType.MACHETE.getImage(); }

	@Override
	public WType getType() { return WType.MACHETE; }

	@Override
	public int getLevelRequirement() { return 1; }

	@Override
	public long getWeaponMetric() { return Metrics.MACHETE; }

	@Override
	public String getName() {
		return WType.MACHETE.getName();
	}

	@Override
	public String getDescription() {
		return WType.MACHETE.getDescription();
	}
}
