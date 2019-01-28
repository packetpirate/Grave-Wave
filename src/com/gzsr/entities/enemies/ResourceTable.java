package com.gzsr.entities.enemies;

import java.util.ArrayList;
import java.util.List;

import com.gzsr.Globals;
import com.gzsr.misc.Pair;
import com.gzsr.objects.crafting.Resources;
import com.gzsr.objects.items.ResourceDrop;
import com.gzsr.talents.Talents;

public class ResourceTable {
	private float [] dropChance;

	public ResourceTable() {
		dropChance = new float[6];
	}

	public ResourceDrop getDrop(Pair<Float> pos, long cTime) {
		List<Integer> drops = new ArrayList<Integer>();

		float roll = Globals.rand.nextFloat();
		for(int i = 0; i < 6; i++) {
			if(roll <= dropChance[i]) {
				drops.add(i);
			}
		}

		if(!drops.isEmpty()) {
			int index = Globals.rand.nextInt(drops.size());
			int drop = drops.get(index);
			int amount = 1 + (Talents.Munitions.SCAVENGER.active() ? 1 : 0);

			String icon = Resources.getIconName(drop);
			String name = Resources.getName(drop);

			return new ResourceDrop(icon, name, drop, amount, pos, cTime);
		} else return null;
	}

	public ResourceTable addResource(int resource, float chance) {
		if(Talents.Munitions.SCAVENGER.active()) {
			chance += 0.1f;
			if(chance > 1.0f) chance = 1.0f;
		}

		dropChance[resource] = chance;

		return this;
	}
}
