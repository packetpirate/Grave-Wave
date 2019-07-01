package com.grave.objects.items;

import com.grave.AssetManager;
import com.grave.entities.Player;
import com.grave.gfx.ui.StatusMessages;
import com.grave.misc.Pair;
import com.grave.status.CritChanceEffect;
import com.grave.status.Status;

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
		
		float critBonus = player.getAttributes().getFloat("critBonus");
		player.getAttributes().set("critBonus", (critBonus + CritChanceEffect.CRIT_CHANCE));
		
		player.getStatusHandler().addStatus(effect, cTime);
		duration = 0L;
		pickup.play(1.0f, AssetManager.getManager().getSoundVolume());
		
		String message = "+25% Critical Chance!";
		StatusMessages.getInstance().addMessage(message, player, Player.ABOVE_1, cTime, 2_000L);
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
