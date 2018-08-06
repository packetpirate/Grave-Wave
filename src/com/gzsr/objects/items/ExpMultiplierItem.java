package com.gzsr.objects.items;

import com.gzsr.AssetManager;
import com.gzsr.entities.Player;
import com.gzsr.gfx.ui.StatusMessages;
import com.gzsr.misc.Pair;
import com.gzsr.status.ExpMultiplierEffect;
import com.gzsr.status.Status;

public class ExpMultiplierItem extends Item {
	private static final long DURATION = 10_000L;
	private static final long EFFECT_DURATION = 10_000L;
	
	public ExpMultiplierItem(Pair<Float> pos, long cTime) {
		super(pos, cTime);
		
		iconName = Status.EXP_MULTIPLIER.getIconName();
		duration = ExpMultiplierItem.DURATION;
		pickup = AssetManager.getManager().getSound("powerup2");
	}
	
	@Override
	public void apply(Player player, long cTime) {
		ExpMultiplierEffect effect = new ExpMultiplierEffect(ExpMultiplierItem.EFFECT_DURATION, cTime);
		player.getAttributes().set("expMult", 2.0);
		player.getStatusHandler().addStatus(effect, cTime);
		duration = 0L;
		pickup.play(1.0f, AssetManager.getManager().getSoundVolume());
		
		String message = "2x Experience!";
		StatusMessages.getInstance().addMessage(message, player, new Pair<Float>(0.0f, -32.0f), cTime, 2_000L);
	}
	
	@Override
	public int getCost() {
		return 0;
	}
	
	@Override
	public String getName() {
		return "Experience Multiplier";
	}

	@Override
	public String getDescription() {
		return "Gives double experience for a short time.";
	}
}
