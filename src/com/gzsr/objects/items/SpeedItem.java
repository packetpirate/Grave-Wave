package com.gzsr.objects.items;

import org.newdawn.slick.Sound;

import com.gzsr.AssetManager;
import com.gzsr.entities.Player;
import com.gzsr.misc.Pair;
import com.gzsr.status.SpeedEffect;
import com.gzsr.status.Status;

public class SpeedItem extends Item {
	private static final long DURATION = 10_000L;
	private static final long EFFECT_DURATION = 10_000L;
	
	private Sound powerupSound;
	
	public SpeedItem(Pair<Float> pos, long cTime) {
		super(pos, cTime);
		
		iconName = "GZS_SpeedUp";
		duration = SpeedItem.DURATION;
		powerupSound = AssetManager.getManager().getSound("powerup2");
	}

	@Override
	public void apply(Player player, long cTime) {
		SpeedEffect effect = new SpeedEffect(Status.SPEED_UP, SpeedItem.EFFECT_DURATION, cTime);
		player.addStatus(effect, cTime);
		player.setDoubleAttribute("spdMult", SpeedEffect.EFFECT);
		duration = 0L;
		powerupSound.play();
	}
}