package com.gzsr.objects.items;

import com.gzsr.AssetManager;
import com.gzsr.Globals;
import com.gzsr.entities.Player;
import com.gzsr.gfx.ui.StatusMessages;
import com.gzsr.misc.Pair;

public class HealthKit extends Item {
	private static final String ICON_NAME = "GZS_Health";
	private static final long DURATION = 10_000L;
	private static final double RESTORE_MIN = 50.0;
	private static final double RESTORE_MAX = 75.0;
	
	public HealthKit(Pair<Float> pos, long cTime) {
		super(pos, cTime);
		
		iconName = HealthKit.ICON_NAME;
		duration = HealthKit.DURATION;
		pickup = AssetManager.getManager().getSound("powerup2");
	}
	
	@Override
	public void apply(Player player, long cTime) {
		double amnt = (Globals.rand.nextDouble() * (HealthKit.RESTORE_MAX - HealthKit.RESTORE_MIN)) + HealthKit.RESTORE_MIN;
		player.addHealth(amnt);
		player.getStatusHandler().clearHarmful();
		duration = 0L;
		pickup.play(1.0f, AssetManager.getManager().getSoundVolume());
		
		String message = String.format("+%d Health!", (int)amnt);
		StatusMessages.getInstance().addMessage(message, player, new Pair<Float>(0.0f, -32.0f), cTime, 2_000L);
	}
	
	@Override
	public int getCost() {
		return 0;
	}

	@Override
	public String getName() {
		return "Health Kit";
	}
	
	@Override
	public String getDescription() {
		return "Health Kit";
	}
}
