package com.grave.objects.items;

import com.grave.AssetManager;
import com.grave.entities.Player;
import com.grave.gfx.ui.StatusMessages;
import com.grave.misc.Pair;
import com.grave.status.InvulnerableEffect;
import com.grave.status.Status;

public class InvulnerableItem extends Item {
	private static final long DURATION = 10_000L;
	private static final long EFFECT_DURATION = 10_000L;
	
	public InvulnerableItem(Pair<Float> pos, long cTime) {
		super(pos, cTime);
		
		iconName = Status.INVULNERABLE.getIconName();
		duration = InvulnerableItem.DURATION;
		pickup = AssetManager.getManager().getSound("powerup2");
	}

	@Override
	public void apply(Player player, long cTime) {
		InvulnerableEffect effect = new InvulnerableEffect(InvulnerableItem.EFFECT_DURATION, cTime);
		player.getStatusHandler().addStatus(effect, cTime);
		duration = 0L;
		pickup.play(1.0f, AssetManager.getManager().getSoundVolume());
		
		String message = "Invulnerability!";
		StatusMessages.getInstance().addMessage(message, player, Player.ABOVE_1, cTime, 2_000L);
	}
	
	@Override
	public int getCost() {
		return 0;
	}

	@Override
	public String getName() {
		return "Invulnerability";
	}
	
	@Override
	public String getDescription() {
		return "Invulnerability";
	}
}
