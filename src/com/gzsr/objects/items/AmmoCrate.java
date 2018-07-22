package com.gzsr.objects.items;

import java.util.ArrayList;
import java.util.List;

import com.gzsr.AssetManager;
import com.gzsr.Globals;
import com.gzsr.entities.Player;
import com.gzsr.gfx.ui.StatusMessages;
import com.gzsr.misc.Pair;
import com.gzsr.objects.weapons.Weapon;
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
		List<Weapon> active = player.getWeapons();
		List<RangedWeapon> ranged = new ArrayList<RangedWeapon>();
		for(Weapon w : active) {
			if(w instanceof RangedWeapon) ranged.add((RangedWeapon) w);
		}
		
		int weapon = Globals.rand.nextInt(active.size());
		RangedWeapon w = ranged.get(weapon);
		
		w.addInventoryAmmo(w.getClipSize());
		
		duration = 0L;
		pickup.play(1.0f, AssetManager.getManager().getSoundVolume());
		
		String message = String.format("+%d %s Ammo!", w.getClipSize(), w.getName());
		StatusMessages.getInstance().addMessage(message, player, new Pair<Float>(0.0f, -32.0f), cTime, 2_000L);
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
