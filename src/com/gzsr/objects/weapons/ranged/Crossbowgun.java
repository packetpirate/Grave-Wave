package com.gzsr.objects.weapons.ranged;

import org.newdawn.slick.Image;

import com.gzsr.achievements.Metrics;
import com.gzsr.math.Dice;
import com.gzsr.misc.Pair;
import com.gzsr.objects.weapons.WType;

public class Crossbowgun extends Crossbow {
	private static final int PRICE = 0;
	private static final int AMMO_PRICE = 200;
	private static final long COOLDOWN = 500L;
	private static final int CLIP_SIZE = 8;
	private static final int START_CLIPS = 4;
	private static final int MAX_CLIPS = 8;
	private static final long RELOAD_TIME = 1_500L;

	private static final Dice DAMAGE = new Dice(2, 10);
	private static final int DAMAGE_MOD = 16;

	public Crossbowgun() {
		super();
		this.automatic = true;
	}

	@Override
	public long getReloadTime() { return Crossbowgun.RELOAD_TIME; }

	@Override
	public long getCooldown() { return Crossbowgun.COOLDOWN; }

	@Override
	public int getClipSize() { return Crossbowgun.CLIP_SIZE; }

	@Override
	public int getStartClips() { return Crossbowgun.START_CLIPS; }

	@Override
	public int getMaxClips() { return Crossbowgun.MAX_CLIPS; }

	@Override
	public int getPrice() { return Crossbowgun.PRICE; }

	@Override
	public int getAmmoPrice() { return Crossbowgun.AMMO_PRICE; }

	@Override
	public Pair<Integer> getDamageRange() { return Crossbowgun.DAMAGE.getRange(Crossbowgun.DAMAGE_MOD); }

	@Override
	public double rollDamage(boolean critical) { return Crossbowgun.DAMAGE.roll(Crossbowgun.DAMAGE_MOD, critical); }

	@Override
	public Image getInventoryIcon() { return WType.CROSSBOWGUN.getImage(); }

	@Override
	public WType getType() { return WType.CROSSBOWGUN; }

	@Override
	public Metrics getWeaponMetric() { return Metrics.CROSSBOWGUN; }

	@Override
	public String getName() { return WType.CROSSBOWGUN.getName(); }

	@Override
	public String getDescription() { return WType.CROSSBOWGUN.getDescription(); }
}
