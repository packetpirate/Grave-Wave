package com.grave.objects.crafting;

import java.util.ArrayList;
import java.util.List;

import com.grave.entities.Player;
import com.grave.objects.Inventory;
import com.grave.objects.weapons.WType;
import com.grave.objects.weapons.Weapon;
import com.grave.objects.weapons.ranged.RangedWeapon;

public class Recipe {
	private static int rNum = 0;
	private static int generateRecipeID() { return rNum++; }
	public static void resetID() { rNum = 0; }

	public static class Builder {
		private List<WType> weaponCost;
		private Resources resourceCosts;

		private Weapon result;

		private boolean advanced;
		private boolean repeatable;

		public Builder(Weapon result_, boolean advanced_) {
			this(result_, advanced_, false);
		}

		public Builder(Weapon result_, boolean advanced_, boolean repeatable_) {
			weaponCost = new ArrayList<WType>();
			resourceCosts = new Resources();

			result = result_;
			advanced = advanced_;
			repeatable = repeatable_;
		}

		public Builder addWeapon(WType weapon) {
			weaponCost.add(weapon);
			return this;
		}

		public Builder addResource(int resource, int amnt) {
			resourceCosts.add(resource, amnt);
			return this;
		}

		public Recipe build() {
			return new Recipe(weaponCost.toArray(new WType[weaponCost.size()]), resourceCosts, result, advanced, repeatable);
		}
	}

	private int id;
	public int getID() { return id; }

	private WType [] wCost;
	public WType [] getWeapons() { return wCost; }
	private Resources rCost;
	public Resources getResources() { return rCost; }

	private Weapon result;
	public Weapon getResult() { return result; }

	private boolean advanced;
	public boolean isAdvanced() { return advanced; }
	private boolean repeatable;
	public boolean isRepeatable() { return repeatable; }
	private boolean crafted;
	public boolean isCrafted() { return crafted; }

	private Recipe(WType [] weaponCosts, Resources resourceCosts, Weapon result_, boolean advanced_, boolean repeatable_) {
		this.id = Recipe.generateRecipeID();

		this.wCost = weaponCosts;
		this.rCost = resourceCosts;

		this.result = result_;

		this.advanced = advanced_;
		this.repeatable = repeatable_;
		this.crafted = false;
	}

	public void craft() {
		Player player = Player.getPlayer();
		Inventory playerInventory = player.getInventory();
		Resources playerResources = player.getResources();

		// Subtract resources first.
		for(int i = 0; i < 6; i++) {
			int amnt = rCost.get(i);
			playerResources.add(i, -amnt);
		}

		// Remove weapons from player inventory, if any.
		if(wCost.length > 0) {
			for(WType type : wCost) {
				playerInventory.dropItem(type);
			}
		}

		Weapon pw = player.getWeaponByType(result.getType());
		if(pw != null) {
			// Add a clip of ammo to the existing weapon.
			if(pw instanceof RangedWeapon) {
				RangedWeapon rw = (RangedWeapon) pw;
				rw.addInventoryAmmo(rw.getClipSize());
			}
		} else {
			// Add the new item to the player's inventory.
			playerInventory.addItem(result);
			player.equip(result);
		}

		crafted = true;

		if(!repeatable) RecipeController.removeRecipe(this, advanced);
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

			for(WType type : wCost) {
				boolean has = false;
				for(Weapon w : weapons) {
					if(w.getType().equals(type)) {
						has = true;
						break;
					}
				}

				if(!has) return false;
			}
		}

		return true;
	}

	@Override
	public boolean equals(Object other) {
		if(other.getClass() != getClass()) return false;
		Recipe oRecipe = (Recipe) other;
		return (oRecipe.getID() == getID());
	}
}
