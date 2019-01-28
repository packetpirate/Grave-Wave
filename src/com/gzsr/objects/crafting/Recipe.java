package com.gzsr.objects.crafting;

import java.util.ArrayList;
import java.util.List;

import com.gzsr.entities.Entity;
import com.gzsr.entities.Player;
import com.gzsr.objects.Inventory;
import com.gzsr.objects.weapons.Weapon;

public class Recipe {
	public static class Builder {
		private List<String> weaponCost;
		private Resources resourceCosts;

		private Entity result;

		private boolean advanced;

		public Builder(Entity result_, boolean advanced_) {
			weaponCost = new ArrayList<String>();
			resourceCosts = new Resources();

			result = result_;
			advanced = advanced_;
		}

		public Builder addWeapon(String weapon) {
			weaponCost.add(weapon);
			return this;
		}

		public Builder addResource(int resource, int amnt) {
			resourceCosts.add(resource, amnt);
			return this;
		}

		public Recipe build() {
			return new Recipe(weaponCost.toArray(new String[weaponCost.size()]), resourceCosts, result, advanced);
		}
	}

	private String [] wCost;
	private Resources rCost;

	private Entity result;

	private boolean advanced;
	public boolean isAdvanced() { return advanced; }

	private Recipe(String [] weaponCosts, Resources resourceCosts, Entity result_, boolean advanced_) {
		this.wCost = weaponCosts;
		this.rCost = resourceCosts;

		this.result = result_;

		this.advanced = advanced_;
	}

	public void craft() {
		Inventory playerInventory = Player.getPlayer().getInventory();
		Resources playerResources = Player.getPlayer().getResources();

		// Subtract resources first.
		for(int i = 0; i < 6; i++) {
			int amnt = rCost.get(i);
			playerResources.add(i, -amnt);
		}

		// Remove weapons from player inventory, if any.
		if(wCost.length > 0) {
			for(String name : wCost) {
				playerInventory.dropItem(name);
			}
		}

		// Add the new item to the player's inventory.
		playerInventory.addItem(result);
	}

	public boolean hasIngredients() {
		Player player = Player.getPlayer();
		Resources resources = player.getResources();

		// Check resources first.
		for(int i = 0; i < 6; i++) {
			int rp = resources.get(i);
			int rr = rCost.get(i);
			if(rp < rr) return false;
		}

		// Check to make sure player has the requisite weapons, if any.
		if(wCost.length > 0) {
			List<Weapon> weapons = new ArrayList<Weapon>();
			weapons.addAll(player.getMeleeWeapons());
			weapons.addAll(player.getRangedWeapons());

			for(String name : wCost) {
				boolean has = false;
				for(Weapon w : weapons) {
					if(w.getName().equals(name)) {
						has = true;
						break;
					}
				}

				if(!has) return false;
			}
		}

		return true;
	}
}
