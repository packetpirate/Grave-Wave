package com.gzsr.objects.items;

import org.newdawn.slick.Sound;

import com.gzsr.AssetManager;
import com.gzsr.entities.Player;
import com.gzsr.misc.Pair;
import com.gzsr.status.InvulnerableEffect;
import com.gzsr.status.Status;

public class InvulnerableItem extends Item {
	private static final long DURATION = 10_000L;
	private static final long EFFECT_DURATION = 10_000L;
	
	private Sound powerupSound;
	
	public InvulnerableItem(Pair<Float> pos, long cTime) {
		super(pos, cTime);
		
		iconName = Status.INVULNERABLE.getIconName();
		duration = InvulnerableItem.DURATION;
		powerupSound = AssetManager.getManager().getSound("powerup2");
	}

	@Override
	public void apply(Player player, long cTime) {
		InvulnerableEffect effect = new InvulnerableEffect(InvulnerableItem.EFFECT_DURATION, cTime);
		player.addStatus(effect, cTime);
		duration = 0L;
		powerupSound.play();
	}

	@Override
	public String getName() {
		return "Invulnerability";
	}
}
