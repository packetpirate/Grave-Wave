package com.gzsr.objects.items;

import org.newdawn.slick.Color;

import com.gzsr.AssetManager;
import com.gzsr.Globals;
import com.gzsr.entities.Player;
import com.gzsr.gfx.ui.VanishingText;
import com.gzsr.misc.Pair;
import com.gzsr.states.GameState;
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
		player.addStatus(effect, cTime);
		duration = 0L;
		pickup.play(1.0f, AssetManager.getManager().getSoundVolume());
		
		VanishingText vt = new VanishingText("+25% Critical Chance!", "PressStart2P-Regular_small", 
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
		return "Critical Chance";
	}

	@Override
	public String getDescription() {
		return "Increases the player's chance of scoring a critical hit.";
	}
}
