package com.gzsr.objects.items;

import com.gzsr.AssetManager;
import com.gzsr.entities.Player;
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
		player.addStatus(effect, cTime);
		duration = 0L;
		pickup.play(1.0f, AssetManager.getManager().getSoundVolume());
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
