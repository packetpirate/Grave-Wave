package com.gzsr.objects.items;

import com.gzsr.AssetManager;
import com.gzsr.entities.Player;
import com.gzsr.gfx.ui.StatusMessages;
import com.gzsr.misc.Pair;
import com.gzsr.status.CritChanceEffect;
import com.gzsr.status.Status;

public class CritChanceItem extends Item {
	private static final long DURATION = 10_000L;
	private static final long EFFECT_DURATION = 20_000L;
	
	public CritChanceItem(Pair<Float> pos, long cTime) {
		super(pos, cTime);
		
		iconName = Status.CRIT_CHANCE.getIconName();
		duration = CritChanceItem.DURATION;
		pickup = AssetManager.getManager().getSound("powerup2");
	}

	@Override
	public void apply(Player player, long cTime) {
		CritChanceEffect effect = new CritChanceEffect(CritChanceItem.EFFECT_DURATION, cTime);
		float critChance = player.getAttributes().getFloat("critChance");
		player.getAttributes().set("critChance", (critChance + CritChanceEffect.CRIT_CHANCE));
		player.getStatusHandler().addStatus(effect, cTime);
		duration = 0L;
		pickup.play(1.0f, AssetManager.getManager().getSoundVolume());
		
		String message = "+25% Critical Chance!";
		StatusMessages.getInstance().addMessage(message, player, new Pair<Float>(0.0f, -32.0f), cTime, 2_000L);
	}
	
	@Override
	public int getCost() {
		return 0;
	}
	
	@Override
	public String getName() {
		return "Critical Chance";
	}

	@Override
	public String getDescription() {
		return "Increases the player's chance of scoring a critical hit.";
	}
}
