package com.grave.objects.items;

import com.grave.AssetManager;
import com.grave.entities.Player;
import com.grave.gfx.ui.StatusMessages;
import com.grave.misc.Pair;
import com.grave.status.NightVisionEffect;

public class NightVisionItem extends Item {
	private static final String ICON_NAME = "GZS_NightVision";
	private static final long DURATION = 30_000L;
	
	public NightVisionItem(Pair<Float> pos, long cTime) {
		super(pos, cTime);
		
		iconName = NightVisionItem.ICON_NAME;
		duration = NightVisionItem.DURATION;
		pickup = AssetManager.getManager().getSound("powerup2");
	}
	
	@Override
	public void apply(Player player, long cTime) {
		NightVisionEffect effect = new NightVisionEffect(NightVisionItem.DURATION, cTime);
		player.getStatusHandler().addStatus(effect, cTime);
		duration = 0L;
		pickup.play(1.0f, AssetManager.getManager().getSoundVolume());
		
		String message = "Night Vision!";
		StatusMessages.getInstance().addMessage(message, player, Player.ABOVE_1, cTime, 2_000L);
	}
	
	@Override
	public int getCost() {
		return 0;
	}
	
	@Override
	public String getName() {
		return "Night Vision";
	}

	@Override
	public String getDescription() {
		return "Lets you see in the dark. Those damn undead aren't sneaking up on me!";
	}
}
