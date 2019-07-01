package com.grave.objects.items;

import com.grave.AssetManager;
import com.grave.entities.Player;
import com.grave.gfx.ui.StatusMessages;
import com.grave.misc.Pair;
import com.grave.status.Status;
import com.grave.status.UnlimitedAmmoEffect;

public class UnlimitedAmmoItem extends Item {
	private static final long DURATION = 10_000L;
	private static final long EFFECT_DURATION = 10_000L;
	
	public UnlimitedAmmoItem(Pair<Float> pos, long cTime) {
		super(pos, cTime);
		
		iconName = Status.UNLIMITED_AMMO.getIconName();
		duration = UnlimitedAmmoItem.DURATION;
		pickup = AssetManager.getManager().getSound("powerup2");
	}

	@Override
	public void apply(Player player, long cTime) {
		UnlimitedAmmoEffect effect = new UnlimitedAmmoEffect(UnlimitedAmmoItem.EFFECT_DURATION, cTime);
		player.getStatusHandler().addStatus(effect, cTime);
		duration = 0L;
		pickup.play(1.0f, AssetManager.getManager().getSoundVolume());
		
		String message = "Unlimited Ammo!";
		StatusMessages.getInstance().addMessage(message, player, Player.ABOVE_1, cTime, 2_000L);
	}

	@Override
	public int getCost() {
		return 0;
	}
	
	@Override
	public String getName() {
		return "Unlimited Ammo";
	}
	
	@Override
	public String getDescription() {
		return "Unlimited Ammo";
	}
}
