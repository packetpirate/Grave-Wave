package com.gzsr.objects.weapons.melee;

import org.newdawn.slick.Image;

import com.gzsr.AssetManager;
import com.gzsr.achievements.Metrics;
import com.gzsr.math.Dice;
import com.gzsr.misc.Pair;
import com.gzsr.objects.weapons.DamageType;

public class BaseballBat extends MeleeWeapon {
	private static final int PRICE = 200;
	private static final Pair<Float> HIT_AREA_SIZE = new Pair<Float>(96.0f, 32.0f);
	private static final float HIT_AREA_OFFSET = -32.0f;
	private static final float IMAGE_DISTANCE = -8.0f;
	private static final long ATTACK_TIME = 400L;
	private static final long COOLDOWN = 500L;
	//private static final double STAMINA = 40.0;
	private static final int BPM = 15;
	private static final float KNOCKBACK = 10.0f;
	private static final float THETA_OFFSET = (float)(Math.PI / 3.6);
	private static final String ICON_NAME = "GZS_Baseball_Bat_Icon";
	private static final String WEAPON_IMAGE = "GZS_Baseball_Bat";

	private static final Dice DAMAGE = new Dice(5, 4);
	private static final int DAMAGE_MOD = 4;

	public BaseballBat() {
		super();

		img = AssetManager.getManager().getImage(BaseballBat.WEAPON_IMAGE);
		useSound = AssetManager.getManager().getSound("throw2");
	}

	@Override
	public float getDistance() { return BaseballBat.HIT_AREA_OFFSET; }

	@Override
	public float getImageDistance() { return BaseballBat.IMAGE_DISTANCE; }

	@Override
	public Pair<Float> getHitAreaSize() { return BaseballBat.HIT_AREA_SIZE; }

	@Override
	public float getThetaOffset() { return BaseballBat.THETA_OFFSET; }

	@Override
	public long getAttackTime() { return BaseballBat.ATTACK_TIME; }

	@Override
	public long getCooldown() { return BaseballBat.COOLDOWN; }

	@Override
	public int getPrice() { return BaseballBat.PRICE; }

	@Override
	public DamageType getDamageType() { return DamageType.BLUNT; }

	@Override
	public Pair<Integer> getDamageRange() { return BaseballBat.DAMAGE.getRange(BaseballBat.DAMAGE_MOD); }

	@Override
	public double rollDamage(boolean critical) { return BaseballBat.DAMAGE.roll(BaseballBat.DAMAGE_MOD, critical); }

	@Override
	public int getBPMCost() { return BaseballBat.BPM; }
	//public double getStaminaCost() { return BaseballBat.STAMINA; }

	@Override
	public float getKnockback() { return BaseballBat.KNOCKBACK; }

	@Override
	public Image getInventoryIcon() { return AssetManager.getManager().getImage(ICON_NAME); }

	@Override
	public int getLevelRequirement() { return 5; }

	@Override
	public long getWeaponMetric() { return Metrics.BASEBALL_BAT; }

	@Override
	public String getName() {
		return "Baseball Bat";
	}

	@Override
	public String getDescription() {
		return "A classic. This one has seen a lot of use, but can still bash their heads in just fine.";
	}
}
