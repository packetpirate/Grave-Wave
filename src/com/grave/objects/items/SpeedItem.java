package com.grave.objects.items;

import com.grave.AssetManager;
import com.grave.entities.Player;
import com.grave.gfx.ui.StatusMessages;
import com.grave.misc.Pair;
import com.grave.status.SpeedEffect;
import com.grave.status.Status;

public class SpeedItem extends Item {
	private static final long DURATION = 10_000L;
	private static final long EFFECT_DURATION = 10_000L;
	
	public SpeedItem(Pair<Float> pos, long cTime) {
		super(pos, cTime);
		
		iconName = Status.SPEED_UP.getIconName();
		duration = SpeedItem.DURATION;
		pickup = AssetManager.getManager().getSound("powerup2");
	}

	@Override
	public void apply(Player player, long cTime) {
		SpeedEffect effect = new SpeedEffect(SpeedItem.EFFECT_DURATION, cTime);
		player.getStatusHandler().addStatus(effect, cTime);
		player.getAttributes().set("spdMult", SpeedEffect.EFFECT);
		duration = 0L;
		pickup.play(1.0f, AssetManager.getManager().getSoundVolume());
		
		String message = "Speed Up!";
		StatusMessages.getInstance().addMessage(message, player, Player.ABOVE_1, cTime, 2_000L);
	}
	
	@Override
	public int getCost() {
		return 0;
	}

	@Override
	public String getName() {
		return "Speed Up";
	}
	
	@Override
	public String getDescription() {
		return "Speed Up";
	}
}
