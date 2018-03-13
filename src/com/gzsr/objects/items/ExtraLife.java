package com.gzsr.objects.items;

import com.gzsr.AssetManager;
import com.gzsr.Globals;
import com.gzsr.entities.Player;
import com.gzsr.misc.Pair;

public class ExtraLife extends Item {
	private static final String ICON_NAME = "GZS_ExtraLife";
	private static final long DURATION = 5_000L;
	private static final double RESTORE_MIN = 50.0;
	private static final double RESTORE_MAX = 75.0;
	
	public ExtraLife(Pair<Float> pos, long cTime) {
		super(pos, cTime);
		
		iconName = ExtraLife.ICON_NAME;
		duration = ExtraLife.DURATION;
		pickup = AssetManager.getManager().getSound("powerup2");
	}

	@Override
	public void apply(Player player, long cTime) {
		int max = player.getIntAttribute("maxLives");
		int lives = player.getIntAttribute("lives");
		
		if(lives < max) {
			lives++;
			player.setAttribute("lives", lives);
		} else {
			double amnt = (Globals.rand.nextDouble() * (ExtraLife.RESTORE_MAX - ExtraLife.RESTORE_MIN)) + ExtraLife.RESTORE_MIN;
			player.addHealth(amnt);
		}
		
		duration = 0L;
		pickup.play();
	}
	
	@Override
	public String getName() {
		return "Extra Life";
	}

	@Override
	public String getDescription() {
		return "Provides a means to cling to life for just a little longer...";
	}
}
