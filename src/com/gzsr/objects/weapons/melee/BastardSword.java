package com.gzsr.objects.weapons.melee;

import org.newdawn.slick.Image;

import com.gzsr.AssetManager;
import com.gzsr.math.Dice;
import com.gzsr.misc.Pair;

public class BastardSword extends MeleeWeapon {
	private static final int PRICE = 5_000;
	private static final Pair<Float> HIT_AREA_SIZE = new Pair<Float>(96.0f, 96.0f);
	private static final float HIT_AREA_OFFSET = -32.0f;
	private static final float IMAGE_DISTANCE = -8.0f;
	private static final long ATTACK_TIME = 400L;
	private static final long COOLDOWN = 800L;
	private static final float KNOCKBACK = 10.0f;
	private static final float THETA_OFFSET = (float)(Math.PI / 3.6);
	private static final int MIN_DAMAGE_COUNT = 6;
	private static final int MIN_DAMAGE_SIDES = 4;
	private static final int MIN_DAMAGE_MOD = 12;
	private static final String ICON_NAME = "GZS_Bastard_Sword_Icon";
	private static final String WEAPON_IMAGE = "GZS_Bastard_Sword";
	
	public BastardSword() {
		super();
		
		img = AssetManager.getManager().getImage(BastardSword.WEAPON_IMAGE);
		useSound = AssetManager.getManager().getSound("throw2");
		
		multihit = true;
		damage = new Dice(MIN_DAMAGE_COUNT, MIN_DAMAGE_SIDES);
	}

	@Override
	public int rollDamage() { return damage.roll(MIN_DAMAGE_MOD, isCurrentCritical()); }
	
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
	public Pair<Integer> getDamage() { return damage.getRange(MIN_DAMAGE_MOD); }

	@Override
	public float getKnockback() { return BastardSword.KNOCKBACK; }

	@Override
	public Image getInventoryIcon() { return AssetManager.getManager().getImage(ICON_NAME); }
	
	@Override
	public int getLevelRequirement() { return 10; }
	
	@Override
	public String getName() {
		return "Bastard Sword";
	}

	@Override
	public String getDescription() {
		return "Now this thing can do some damage! Go medieval on those undead freaks.";
	}
}
