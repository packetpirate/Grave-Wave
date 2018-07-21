package com.gzsr.objects.items;

import org.newdawn.slick.Color;

import com.gzsr.AssetManager;
import com.gzsr.Globals;
import com.gzsr.entities.Player;
import com.gzsr.gfx.ui.VanishingText;
import com.gzsr.misc.Pair;
import com.gzsr.states.GameState;
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
		
		VanishingText vt = new VanishingText("Night Vision!", "PressStart2P-Regular_small", 
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
		return "Night Vision";
	}

	@Override
	public String getDescription() {
		return "Lets you see in the dark. Those damn undead aren't sneaking up on me!";
	}
}
