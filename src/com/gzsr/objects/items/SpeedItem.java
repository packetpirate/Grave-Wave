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
		
		iconName = Status.SPEED_UP.getIconName();
		duration = SpeedItem.DURATION;
		powerupSound = AssetManager.getManager().getSound("powerup2");
	}

	@Override
	public void apply(Player player, long cTime) {
		SpeedEffect effect = new SpeedEffect(SpeedItem.EFFECT_DURATION, cTime);
		player.addStatus(effect, cTime);
		player.setAttribute("spdMult", SpeedEffect.EFFECT);
		duration = 0L;
		powerupSound.play();
	}

	@Override
	public String getName() {
		return "Speed Up";
	}
}
