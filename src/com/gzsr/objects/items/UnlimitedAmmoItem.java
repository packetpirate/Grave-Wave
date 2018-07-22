package com.gzsr.objects.items;

import com.gzsr.AssetManager;
import com.gzsr.entities.Player;
import com.gzsr.gfx.ui.StatusMessages;
import com.gzsr.misc.Pair;
import com.gzsr.status.Status;
import com.gzsr.status.UnlimitedAmmoEffect;

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
		player.addStatus(effect, cTime);
		duration = 0L;
		pickup.play(1.0f, AssetManager.getManager().getSoundVolume());
		
		String message = "Unlimited Ammo!";
		StatusMessages.getInstance().addMessage(message, player, new Pair<Float>(0.0f, -32.0f), cTime, 2_000L);
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
