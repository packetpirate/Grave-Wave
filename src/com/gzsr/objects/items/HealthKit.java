package com.gzsr.objects.items;

import org.newdawn.slick.Sound;

import com.gzsr.AssetManager;
import com.gzsr.Globals;
import com.gzsr.entities.Player;
import com.gzsr.misc.Pair;

public class HealthKit extends Item {
	private static final String ICONNAME = "GZS_Health";
	private static final long DURATION = 10_000L;
	private static final double RESTORE_MIN = 50.0;
	private static final double RESTORE_MAX = 75.0;
	
	private Sound powerupSound;
	
	public HealthKit(Pair<Float> pos, long cTime) {
		super(pos, cTime);
		
		iconName = HealthKit.ICONNAME;
		duration = HealthKit.DURATION;
		powerupSound = AssetManager.getManager().getSound("powerup2");
	}
	
	@Override
	public void apply(Player player, long cTime) {
		double amnt = (Globals.rand.nextDouble() * (HealthKit.RESTORE_MAX - HealthKit.RESTORE_MIN)) + HealthKit.RESTORE_MIN;
		player.addHealth(amnt);
		duration = 0L;
		powerupSound.play();
	}
}
