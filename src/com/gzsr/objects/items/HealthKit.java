package com.gzsr.objects.items;

import org.newdawn.slick.Sound;

import com.gzsr.AssetManager;
import com.gzsr.Globals;
import com.gzsr.entities.Player;
import com.gzsr.misc.Pair;

public class HealthKit extends Item {
	private static final String ICONNAME = "GZS_Health";
	private static final long DURATION = 10_000L;
	private static final double MAX_RESTORE = 75.0;
	
	private Sound powerupSound;
	
	public HealthKit(Pair<Float> pos, long cTime) {
		super(pos, cTime);
		
		iconName = HealthKit.ICONNAME;
		duration = HealthKit.DURATION;
		powerupSound = AssetManager.getManager().getSound("powerup2");
	}
	
	@Override
	public void apply(Player player, long cTime) {
		double amnt = Globals.rand.nextDouble() * HealthKit.MAX_RESTORE;
		player.addHealth(amnt);
		duration = 0L;
		powerupSound.play();
	}
}
