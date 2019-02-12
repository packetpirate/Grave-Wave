package com.gzsr.objects;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.gzsr.entities.Entity;
import com.gzsr.objects.weapons.WType;
import com.gzsr.objects.weapons.Weapon;
import com.gzsr.objects.weapons.melee.MeleeWeapon;
import com.gzsr.objects.weapons.ranged.RangedWeapon;

public class Inventory {
	private List<Entity> items;
	public void addItem(Entity item) { items.add(item); }
	public void dropItem(String name) {
		Iterator<Entity> it = items.iterator();
		while(it.hasNext()) {
			Entity e = it.next();
			if(e.getName().equals(name)) {
				it.remove();
				break;
			}
		}
	}
	public void dropItem(WType type) {
		Iterator<Entity> it = items.iterator();
		while(it.hasNext()) {
			Entity e = it.next();
			if(e instanceof Weapon) {
				Weapon weapon = (Weapon) e;
				if(weapon.getType().equals(type)) {
					it.remove();
					break;
				}
			}
		}
	}
	public boolean hasItem(String name) {
		return (items.stream()
					 .filter(e -> e.getName().equals(name))
					 .count() > 0);
	}
	public boolean hasItem(Entity item) {
		return (items.stream()
					 .filter(e -> e.getName().equals(item.getName()))
					 .count() > 0);
	}

	public List<RangedWeapon> getRangedWeapons() {
		List<RangedWeapon> weapons = new ArrayList<RangedWeapon>();

		for(Entity e : items) {
			if(e instanceof RangedWeapon) {
				weapons.add((RangedWeapon)e);
			}
		}

		return weapons;
	}

	public List<MeleeWeapon> getMeleeWeapons() {
		List<MeleeWeapon> weapons = new ArrayList<MeleeWeapon>();

		for(Entity e : items) {
			if(e instanceof MeleeWeapon) {
				weapons.add((MeleeWeapon)e);
			}
		}

		return weapons;
	}

	public Entity getItem(int i) {
		if((i >= 0) && (i < items.size())) {
			return items.get(i);
		}

		return null;
	}

	private int capacity;
	public boolean isFull() { return (items.size() >= capacity); }
	public int getCapacity() { return capacity; }

	public Inventory(int capacity_) {
		items = new ArrayList<Entity>();
		capacity = capacity_;
	}
}
