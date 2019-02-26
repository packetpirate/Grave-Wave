package com.gzsr.objects.weapons.melee;

import org.newdawn.slick.Image;

import com.gzsr.AssetManager;
import com.gzsr.achievements.Metrics;
import com.gzsr.math.Dice;
import com.gzsr.misc.Pair;
import com.gzsr.objects.weapons.DamageType;
import com.gzsr.objects.weapons.WType;

public class BastardSword extends MeleeWeapon {
	private static final int PRICE = 400;
	private static final Pair<Float> HIT_AREA_SIZE = new Pair<Float>(96.0f, 48.0f);
	private static final float HIT_AREA_OFFSET = -32.0f;
	private static final float IMAGE_DISTANCE = -8.0f;
	private static final long ATTACK_TIME = 400L;
	private static final long COOLDOWN = 800L;
	private static final int BPM = 30;
	private static final float KNOCKBACK = 5.0f;
	private static final float THETA_OFFSET = (float)(Math.PI / 3.6);
	private static final String WEAPON_IMAGE = "GZS_Bastard_Sword";

	private static final Dice DAMAGE = new Dice(6, 4);
	private static final int DAMAGE_MOD = 12;

	public BastardSword() {
		super();

		img = AssetManager.getManager().getImage(BastardSword.WEAPON_IMAGE);
		useSound = AssetManager.getManager().getSound("throw2");

		multihit = true;
	}

	@Override
	public float getDistance() { return BastardSword.HIT_AREA_OFFSET; }

	@Override
	public float getImageDistance() { return BastardSword.IMAGE_DISTANCE; }

	@Override
	public Pair<Float> getHitAreaSize() { return BastardSword.HIT_AREA_SIZE; }

	@Override
	public float getThetaOffset() { return BastardSword.THETA_OFFSET; }

	@Override
	public long getAttackTime() { return BastardSword.ATTACK_TIME; }

	@Override
	public long getCooldown() { return BastardSword.COOLDOWN; }

	@Override
	public int getPrice() { return BastardSword.PRICE; }

	@Override
	public DamageType getDamageType() { return DamageType.SLICING; }

	@Override
	public Pair<Integer> getDamageRange() { return BastardSword.DAMAGE.getRange(BastardSword.DAMAGE_MOD); }

	@Override
	public double rollDamage(boolean critical) { return BastardSword.DAMAGE.roll(BastardSword.DAMAGE_MOD, critical); }

	@Override
	public int getBPMCost() { return BastardSword.BPM; }

	@Override
	public float getKnockback() { return BastardSword.KNOCKBACK; }

	@Override
	public Image getInventoryIcon() { return WType.BASTARD_SWORD.getImage(); }

	@Override
	public WType getType() { return WType.BASTARD_SWORD; }

	@Override
	public int getLevelRequirement() { return 10; }

	@Override
	public Metrics getWeaponMetric() { return Metrics.BASTARD_SWORD; }

	@Override
	public String getName() {
		return WType.BASTARD_SWORD.getName();
	}

	@Override
	public String getDescription() {
		return WType.BASTARD_SWORD.getDescription();
	}
}
