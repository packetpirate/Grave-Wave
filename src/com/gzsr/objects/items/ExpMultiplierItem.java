package com.gzsr.objects.items;

import org.newdawn.slick.Color;

import com.gzsr.AssetManager;
import com.gzsr.Globals;
import com.gzsr.entities.Player;
import com.gzsr.gfx.ui.VanishingText;
import com.gzsr.misc.Pair;
import com.gzsr.states.GameState;
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
		
		VanishingText vt = new VanishingText("2x Experience!", "PressStart2P-Regular_small", 
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
		return "Experience Multiplier";
	}

	@Override
	public String getDescription() {
		return "Gives double experience for a short time.";
	}
}
