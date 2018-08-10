package com.gzsr.controllers;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import com.gzsr.Globals;
import com.gzsr.entities.Player;
import com.gzsr.objects.Inventory;
import com.gzsr.objects.weapons.Weapon;
import com.gzsr.objects.weapons.melee.BastardSword;
import com.gzsr.objects.weapons.melee.Lollipop;
import com.gzsr.objects.weapons.ranged.AK47;
import com.gzsr.objects.weapons.ranged.AWP;
import com.gzsr.objects.weapons.ranged.BigRedButton;
import com.gzsr.objects.weapons.ranged.BowAndArrow;
import com.gzsr.objects.weapons.ranged.ClaymoreWeapon;
import com.gzsr.objects.weapons.ranged.Crossbow;
import com.gzsr.objects.weapons.ranged.Flamethrower;
import com.gzsr.objects.weapons.ranged.GrenadeLauncher;
import com.gzsr.objects.weapons.ranged.LaserBarrier;
import com.gzsr.objects.weapons.ranged.MP5;
import com.gzsr.objects.weapons.ranged.MolotovWeapon;
import com.gzsr.objects.weapons.ranged.Mossberg;
import com.gzsr.objects.weapons.ranged.Remington;
import com.gzsr.objects.weapons.ranged.SAWRevolver;
import com.gzsr.objects.weapons.ranged.SentryWeapon;
import com.gzsr.objects.weapons.ranged.Stinger;
import com.gzsr.objects.weapons.ranged.Taser;

public class ShopController {
	private static final float CHANCE = 0.4f;
	
	private static ShopController instance;
	public static ShopController getInstance() {
		if(instance == null) instance = new ShopController();
		return instance;
	}
	
	//private Map<Integer, List<Class>> unreleased;
	private List<Class> unreleased;
	
	private ShopController() {
		//unreleased = new HashMap<Integer, List<Class>>();
		unreleased = new ArrayList<Class>();
		reset();
	}
	
	/**
	 * Releases weapons randomly into the shop based on player level.
	 * @return A list of weapons to add to the shop inventory.
	 */
	public void release(Inventory shop) {
		Player player = Player.getPlayer();
		List<Weapon> weapons = new ArrayList<Weapon>();
		
		int pLevel = player.getAttributes().getInt("level");
		List<Class> possible = new ArrayList<Class>(unreleased);
		
		if(!possible.isEmpty()) {
			float cChance = CHANCE;
			for(int i = 0; i < possible.size(); i++) {
				Class cl = possible.get(i);
				Weapon w = constructWeapon(cl);
				if((w != null) && (w.getLevelRequirement() <= pLevel)) {
					boolean release = (Globals.rand.nextFloat() <= cChance);
					if(release) {
						weapons.add(w);
						unreleased.remove(cl);
					} else cChance += 0.2f;
				}
			}
		}
		
		weapons.stream().forEach(w -> shop.addItem(w));
	}
	
	private Weapon constructWeapon(Class cl) {
		try {
			Constructor constructor = cl.getConstructor();
			Weapon w = (Weapon) constructor.newInstance();
			return w;
		} catch(NoSuchMethodException nsm) {
			nsm.printStackTrace();
			System.err.println("No constructor found for weapon!");
		} catch (InstantiationException e) {
			e.printStackTrace();
			System.err.println("Problem instantiating weapon object!");
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			System.err.println("Constructor for weapon object not available!");
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			System.err.println("Invalid arguments for weapon constructor!");
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			System.err.println("Failed reflection in weapon constructor!");
		}
		
		return null;
	}
	
	public void reset() {
		Class [] classes = new Class[] {
			BastardSword.class, Lollipop.class, AK47.class, AWP.class, BigRedButton.class,
			BowAndArrow.class, ClaymoreWeapon.class, Crossbow.class, Flamethrower.class, GrenadeLauncher.class,
			LaserBarrier.class, MolotovWeapon.class, Mossberg.class, MP5.class, Remington.class,
			SAWRevolver.class, SentryWeapon.class, Stinger.class, Taser.class
		};
		
		unreleased.clear();
		
		for(int i = 0; i < classes.length; i++) {
			unreleased.add(classes[i]);
		}
	}
}
