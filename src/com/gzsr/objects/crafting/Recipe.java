package com.gzsr.objects.crafting;

import java.util.ArrayList;
import java.util.List;

import com.gzsr.entities.Entity;
import com.gzsr.entities.Player;
import com.gzsr.objects.Inventory;
import com.gzsr.objects.weapons.WType;
import com.gzsr.objects.weapons.Weapon;

public class Recipe {
	private static int rNum = 0;
	private static int generateRecipeID() { return rNum++; }
	public static void resetID() { rNum = 0; }

	public static class Builder {
		private List<WType> weaponCost;
		private Resources resourceCosts;

		private Entity result;

		private boolean advanced;

		public Builder(Entity result_, boolean advanced_) {
			weaponCost = new ArrayList<WType>();
			resourceCosts = new Resources();

			result = result_;
			advanced = advanced_;
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
			return new Recipe(weaponCost.toArray(new WType[weaponCost.size()]), resourceCosts, result, advanced);
		}
	}

	private int id;
	public int getID() { return id; }

	private WType [] wCost;
	public WType [] getWeapons() { return wCost; }
	private Resources rCost;
	public Resources getResources() { return rCost; }

	private Entity result;
	public Entity getResult() { return result; }

	private boolean advanced;
	public boolean isAdvanced() { return advanced; }
	private boolean crafted;
	public boolean isCrafted() { return crafted; }

	private Recipe(WType [] weaponCosts, Resources resourceCosts, Entity result_, boolean advanced_) {
		this.id = Recipe.generateRecipeID();

		this.wCost = weaponCosts;
		this.rCost = resourceCosts;

		this.result = result_;

		this.advanced = advanced_;
		this.crafted = false;
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
			for(WType type : wCost) {
				playerInventory.dropItem(type);
			}
		}

		// Add the new item to the player's inventory.
		playerInventory.addItem(result);
		crafted = true;

		RecipeController.removeRecipe(this, advanced);
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
