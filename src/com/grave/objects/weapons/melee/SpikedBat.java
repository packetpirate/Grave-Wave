package com.grave.objects.weapons.melee;

import org.newdawn.slick.Image;

import com.grave.AssetManager;
import com.grave.achievements.Metrics;
import com.grave.entities.enemies.Enemy;
import com.grave.math.Dice;
import com.grave.misc.Pair;
import com.grave.objects.weapons.DamageType;
import com.grave.objects.weapons.WType;
import com.grave.states.GameState;
import com.grave.status.DamageEffect;

public class SpikedBat extends BaseballBat {
	private static final int PRICE = 0;
	private static final long ATTACK_TIME = 500L;
	private static final long COOLDOWN = 750L;
	private static final int BPM = 20;
	private static final float KNOCKBACK = 8.0f;
	private static final String WEAPON_IMAGE = "GZS_Spiked_Bat";

	private static final Dice DAMAGE = new Dice(4, 4);
	private static final int DAMAGE_MOD = 8;

	private static final Dice BLEED_DAMAGE = new Dice(0, 0);
	private static final int BLEED_MOD = 4;
	private static final long BLEED_INTERVAL = 1_000L;
	private static final long BLEED_DURATION = 3_000L;

	public SpikedBat() {
		super();

		img = AssetManager.getManager().getImage(SpikedBat.WEAPON_IMAGE);
	}

	@Override
	public void onHit(GameState gs, Enemy enemy, long cTime) {
		DamageEffect effect = new DamageEffect(DamageType.NONE, BLEED_DAMAGE, BLEED_MOD, BLEED_INTERVAL, BLEED_DURATION, cTime);
		enemy.getStatusHandler().addStatus(effect, cTime);
	}

	@Override
	public long getAttackTime() { return SpikedBat.ATTACK_TIME; }

	@Override
	public long getCooldown() { return SpikedBat.COOLDOWN; }

	@Override
	public int getPrice() { return SpikedBat.PRICE; }

	@Override
	public DamageType getDamageType() { return DamageType.BLUNT; }

	@Override
	public Pair<Integer> getDamageRange() { return SpikedBat.DAMAGE.getRange(SpikedBat.DAMAGE_MOD); }

	@Override
	public double rollDamage(boolean critical) { return SpikedBat.DAMAGE.roll(SpikedBat.DAMAGE_MOD, critical); }

	@Override
	public int getBPMCost() { return SpikedBat.BPM; }

	@Override
	public float getKnockback() { return SpikedBat.KNOCKBACK; }

	@Override
	public Image getInventoryIcon() { return WType.SPIKED_BAT.getImage(); }

	@Override
	public WType getType() { return WType.SPIKED_BAT; }

	@Override
	public int getLevelRequirement() { return 5; }

	@Override
	public Metrics getWeaponMetric() { return Metrics.SPIKED_BAT; }

	@Override
	public String getName() { return WType.SPIKED_BAT.getName(); }

	@Override
	public String getDescription() { return WType.SPIKED_BAT.getDescription(); }
}
