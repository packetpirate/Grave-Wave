package com.gzsr.objects.items;

import java.util.List;

import org.newdawn.slick.Sound;

import com.gzsr.AssetManager;
import com.gzsr.Globals;
import com.gzsr.entities.Player;
import com.gzsr.misc.Pair;
import com.gzsr.objects.weapons.Weapon;

public class AmmoCrate extends Item {
	private static final String ICONNAME = "GZS_Ammo";
	private static final long DURATION = 10_000L;
	
	private Sound powerupSound;
	
	public AmmoCrate(Pair<Float> pos, long cTime) {
		super(pos, cTime);
		
		iconName = AmmoCrate.ICONNAME;
		duration = AmmoCrate.DURATION;
		powerupSound = AssetManager.getManager().getSound("powerup2");
	}

	@Override
	public void apply(Player player, long cTime) {
		List<Weapon> active = player.getActiveWeapons();
		int weapon = Globals.rand.nextInt(active.size());
		Weapon w = active.get(weapon);
		w.addInventoryAmmo(w.getClipSize());
		duration = 0L;
		powerupSound.play();
	}
}
