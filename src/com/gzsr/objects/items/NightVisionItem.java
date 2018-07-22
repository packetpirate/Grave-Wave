package com.gzsr.objects.items;

import com.gzsr.AssetManager;
import com.gzsr.entities.Player;
import com.gzsr.gfx.ui.StatusMessages;
import com.gzsr.misc.Pair;
import com.gzsr.status.NightVisionEffect;

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
		player.addStatus(effect, cTime);
		duration = 0L;
		pickup.play(1.0f, AssetManager.getManager().getSoundVolume());
		
		String message = "Night Vision!";
		StatusMessages.getInstance().addMessage(message, player, new Pair<Float>(0.0f, -32.0f), cTime, 2_000L);
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
