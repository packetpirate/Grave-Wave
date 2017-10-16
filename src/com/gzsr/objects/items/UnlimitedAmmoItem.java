package com.gzsr.objects.items;

import org.newdawn.slick.Sound;

import com.gzsr.AssetManager;
import com.gzsr.entities.Player;
import com.gzsr.misc.Pair;
import com.gzsr.status.Status;
import com.gzsr.status.UnlimitedAmmoEffect;

public class UnlimitedAmmoItem extends Item {
	private static final long DURATION = 10_000L;
	private static final long EFFECT_DURATION = 10_000L;
	
	private Sound powerupSound;
	
	public UnlimitedAmmoItem(Pair<Float> pos, long cTime) {
		super(pos, cTime);
		
		iconName = Status.UNLIMITED_AMMO.getIconName();
		duration = UnlimitedAmmoItem.DURATION;
		powerupSound = AssetManager.getManager().getSound("powerup2");
	}

	@Override
	public void apply(Player player, long cTime) {
		UnlimitedAmmoEffect effect = new UnlimitedAmmoEffect(Status.UNLIMITED_AMMO, UnlimitedAmmoItem.EFFECT_DURATION, cTime);
		player.addStatus(effect, cTime);
		duration = 0L;
		powerupSound.play();
	}
}
