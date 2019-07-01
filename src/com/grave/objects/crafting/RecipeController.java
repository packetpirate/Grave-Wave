package com.gzsr.objects.crafting;

import java.util.ArrayList;
import java.util.List;

import com.gzsr.objects.weapons.WType;
import com.gzsr.objects.weapons.melee.SpikedBat;
import com.gzsr.objects.weapons.ranged.CompositeBow;
import com.gzsr.objects.weapons.ranged.Crossbowgun;
import com.gzsr.objects.weapons.ranged.ENCannon;
import com.gzsr.objects.weapons.ranged.FlakCannon;
import com.gzsr.objects.weapons.ranged.MolotovWeapon;
import com.gzsr.objects.weapons.ranged.PipeBombWeapon;

public class RecipeController {
	private static List<Recipe> basicRecipes;
	public static List<Recipe> getBasicRecipes() { return basicRecipes; }

	private static List<Recipe> advancedRecipes;
	public static List<Recipe> getAdvancedRecipes() { return advancedRecipes; }

	public static void resetRecipes() {
		basicRecipes.clear();
		advancedRecipes.clear();

		buildBasicRecipes();
		buildAdvancedRecipes();

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
		Recipe spikedBat = new Recipe.Builder(new SpikedBat(), false)
				.addResource(Resources.METAL, 2)
				.addWeapon(WType.BASEBALL_BAT)
				.build();
		basicRecipes.add(spikedBat);

		// Spear
		// TODO: Add spear and then add recipe for it. Requires Machete + 5 Metal?

		// Molotov
		Recipe molotov = new Recipe.Builder(new MolotovWeapon(), false, true)
				.addResource(Resources.GLASS, 2)
				.addResource(Resources.CLOTH, 1)
				.build();
		basicRecipes.add(molotov);

		// Pipe Bomb
		Recipe pipeBomb = new Recipe.Builder(new PipeBombWeapon(), false, true)
				.addResource(Resources.METAL, 4)
				.addResource(Resources.ELECTRONICS, 1)
				.build();
		basicRecipes.add(pipeBomb);

		// Composite Bow
		Recipe compositeBow = new Recipe.Builder(new CompositeBow(), false)
				.addResource(Resources.METAL, 5)
				.addWeapon(WType.BOW_AND_ARROW)
				.build();
		basicRecipes.add(compositeBow);

		// Crossbow Gun
		Recipe crossbowgun = new Recipe.Builder(new Crossbowgun(), false)
				.addResource(Resources.METAL, 10)
				.addWeapon(WType.CROSSBOW)
				.addWeapon(WType.SAW_REVOLVER)
				.build();
		basicRecipes.add(crossbowgun);

		// TODO: Come up with more basic crafts.
	}

	private static void buildAdvancedRecipes() {
		// Flak Cannon
		Recipe flakCannon = new Recipe.Builder(new FlakCannon(), true)
				.addResource(Resources.METAL, 10)
				.addResource(Resources.ELECTRONICS, 1)
				.addResource(Resources.POWER, 10)
				.addWeapon(WType.MOSSBERG)
				.build();
		advancedRecipes.add(flakCannon);

		// Electric Net Cannon
		Recipe enCannon = new Recipe.Builder(new ENCannon(), true)
				.addResource(Resources.POWER, 10)
				.addWeapon(WType.GRENADE_LAUNCHER)
				.addWeapon(WType.LASER_BARRIER)
				.addWeapon(WType.TASER)
				.build();
		advancedRecipes.add(enCannon);

		// Particle Cannon
		// TODO: Add particle cannon and recipe. Requires AWP + Taser + 5 Electronics + 10 Power?

		// TODO: Come up with more advanced crafts.
	}

	public static void removeRecipe(Recipe recipe, boolean advanced) {
		List<Recipe> recipes = (advanced ? advancedRecipes : basicRecipes);
		recipes.remove(recipe);
	}
}
