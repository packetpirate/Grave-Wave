package com.gzsr.objects.items;

import org.newdawn.slick.Color;

import com.gzsr.AssetManager;
import com.gzsr.Globals;
import com.gzsr.entities.Player;
import com.gzsr.gfx.ui.VanishingText;
import com.gzsr.misc.Pair;
import com.gzsr.states.GameState;
import com.gzsr.status.SpeedEffect;
import com.gzsr.status.Status;

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
		player.addStatus(effect, cTime);
		player.getAttributes().set("spdMult", SpeedEffect.EFFECT);
		duration = 0L;
		pickup.play(1.0f, AssetManager.getManager().getSoundVolume());
		
		VanishingText vt = new VanishingText("Speed Up!", "PressStart2P-Regular_small", 
											 new Pair<Float>(0.0f, -32.0f), Color.white, 
											 cTime, 2_000L, true);
		GameState.addVanishingText(String.format("vanishText%d", Globals.generateEntityID()), vt);
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
