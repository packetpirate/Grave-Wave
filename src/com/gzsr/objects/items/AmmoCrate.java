package com.gzsr.objects.items;

import java.util.List;
import java.util.stream.Collectors;

import com.gzsr.AssetManager;
import com.gzsr.Globals;
import com.gzsr.entities.Player;
import com.gzsr.gfx.ui.StatusMessages;
import com.gzsr.misc.Pair;
import com.gzsr.objects.weapons.ranged.RangedWeapon;

public class AmmoCrate extends Item {
	private static final String ICONNAME = "GZS_Ammo";
	private static final long DURATION = 10_000L;
	
	public AmmoCrate(Pair<Float> pos, long cTime) {
		super(pos, cTime);
		
		iconName = AmmoCrate.ICONNAME;
		duration = AmmoCrate.DURATION;
		pickup = AssetManager.getManager().getSound("powerup2");
	}

	@Override
	public void apply(Player player, long cTime) {
		// Filter the player's weapons by those that are not full.
		List<RangedWeapon> weapons = player.getRangedWeapons().stream().filter(w -> !w.clipsMaxedOut()).collect(Collectors.toList());
		
		if(!weapons.isEmpty()) {
			int weapon = Globals.rand.nextInt(weapons.size());
			RangedWeapon w = weapons.get(weapon);
			
			w.addInventoryAmmo(w.getClipSize());
			
			duration = 0L;
			pickup.play(1.0f, AssetManager.getManager().getSoundVolume());
			
			String message = String.format("+%d %s Ammo!", w.getClipSize(), w.getName());
			StatusMessages.getInstance().addMessage(message, player, Player.ABOVE_1, cTime, 2_000L);
		} else {
			StatusMessages.getInstance().addMessage("All Weapons Full!", player, Player.ABOVE_1, cTime, 2_000L);
		}
	}
	
	@Override
	public int getCost() {
		return 0;
	}

	@Override
	public String getName() {
		return "Ammo Crate";
	}
	
	@Override
	public String getDescription() {
		return "Ammo Crate";
	}
}
