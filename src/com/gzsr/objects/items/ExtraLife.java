package com.gzsr.objects.items;

import org.newdawn.slick.Color;

import com.gzsr.AssetManager;
import com.gzsr.Globals;
import com.gzsr.entities.Player;
import com.gzsr.gfx.ui.VanishingText;
import com.gzsr.misc.Pair;
import com.gzsr.states.GameState;

public class ExtraLife extends Item {
	private static final String ICON_NAME = "GZS_ExtraLife";
	private static final long DURATION = 5_000L;
	private static final double RESTORE_MIN = 50.0;
	private static final double RESTORE_MAX = 75.0;
	
	public ExtraLife(Pair<Float> pos, long cTime) {
		super(pos, cTime);
		
		iconName = ExtraLife.ICON_NAME;
		duration = ExtraLife.DURATION;
		pickup = AssetManager.getManager().getSound("powerup2");
	}

	@Override
	public void apply(Player player, long cTime) {
		int max = player.getAttributes().getInt("maxLives");
		int lives = player.getAttributes().getInt("lives");
		
		if(lives < max) {
			lives++;
			player.getAttributes().set("lives", lives);
		} else {
			double amnt = (Globals.rand.nextDouble() * (ExtraLife.RESTORE_MAX - ExtraLife.RESTORE_MIN)) + ExtraLife.RESTORE_MIN;
			player.addHealth(amnt);
		}
		
		duration = 0L;
		pickup.play(1.0f, AssetManager.getManager().getSoundVolume());
		
		VanishingText vt = new VanishingText("+1 Life!", "PressStart2P-Regular_small", 
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
		return "Extra Life";
	}

	@Override
	public String getDescription() {
		return "Provides a means to cling to life for just a little longer...";
	}
}
