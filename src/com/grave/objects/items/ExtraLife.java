package com.grave.objects.items;

import com.grave.AssetManager;
import com.grave.Globals;
import com.grave.entities.Player;
import com.grave.gfx.ui.StatusMessages;
import com.grave.misc.Pair;

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
		int max = player.getAttributes().getInt("maxLives");
		int lives = player.getAttributes().getInt("lives");
		
		if(lives < max) {
			lives++;
			player.getAttributes().set("lives", lives);
		} else {
			double amnt = (Globals.rand.nextDouble() * (ExtraLife.RESTORE_MAX - ExtraLife.RESTORE_MIN)) + ExtraLife.RESTORE_MIN;
			player.addHealth(amnt);
		}
		
		duration = 0L;
		pickup.play(1.0f, AssetManager.getManager().getSoundVolume());
		
		String message = "Extra Life!";
		StatusMessages.getInstance().addMessage(message, player, Player.ABOVE_1, cTime, 2_000L);
	}
	
	@Override
	public int getCost() {
		return 0;
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
