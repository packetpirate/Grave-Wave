package com.gzsr.objects.crafting;

import java.util.ArrayList;
import java.util.List;

import com.gzsr.objects.weapons.WType;
import com.gzsr.objects.weapons.melee.SpikedBat;

public class RecipeController {
	private static List<Recipe> basicRecipes;
	public static List<Recipe> getBasicRecipes() { return basicRecipes; }

	private static List<Recipe> advancedRecipes;
	public static List<Recipe> getAdvancedRecipes() { return advancedRecipes; }

	public static void resetRecipes() {
		basicRecipes.clear();
		advancedRecipes.clear();

		Recipe.resetID();
	}

	static {
		basicRecipes = new ArrayList<Recipe>();
		advancedRecipes = new ArrayList<Recipe>();

		buildBasicRecipes();
		buildAdvancedRecipes();
	}

	private static void buildBasicRecipes() {
		// Spiked Bat
		// TODO: Add spiked bat and recipe. Requires Baseball Bat + 5 Metal?
		Recipe spikedBat = new Recipe.Builder(new SpikedBat(), false)
				.addResource(Resources.METAL, 5)
				.addWeapon(WType.BASEBALL_BAT)
				.build();
		basicRecipes.add(spikedBat);

		// Spear
		// TODO: Add spear and then add recipe for it. Requires Machete + 5 Wood?

		// Composite Bow
		// TODO: Add composite bow and recipe. Requires Bow + 5 Metal?

		// Crossbow Gun
		// TODO: Add crossbow gun and recipe. Requires Crossbow + 10 Metal?

		// TODO: Come up with more basic crafts.
	}

	private static void buildAdvancedRecipes() {
		// Electric Net Cannon
		// TODO: Add electric net cannon and recipe. Requires Grenade Launcher + Laser Barrier + Taser + 10 Power?

		// Particle Cannon
		// TODO: Add particle cannon and recipe. Requires AWP + Taser + 5 Electronics + 10 Power?

		// TODO: Come up with more advanced crafts.
	}

	public static void removeRecipe(Recipe recipe, boolean advanced) {
		List<Recipe> recipes = (advanced ? advancedRecipes : basicRecipes);
		recipes.remove(recipe);
	}
}
