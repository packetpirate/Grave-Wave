package com.gzsr.entities.enemies;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gzsr.objects.items.Powerups;

public class LootTable {
	private Map<Powerups.Type, Float> probabilities;
	public LootTable addItem(Powerups.Type type, float chance) { 
		probabilities.put(type, chance);
		return this;
	}
	
	public LootTable() {
		probabilities = new HashMap<Powerups.Type, Float>();
	}
	
	public boolean dropsItem(Powerups.Type type) {
		return (probabilities.containsKey(type) && (probabilities.get(type) > 0.0f));
	}
	
	public List<Powerups.Type> possibleItems(float roll) {
		List<Powerups.Type> types = new ArrayList<Powerups.Type>();
		probabilities.entrySet().stream()
					 .filter(entry -> roll <= entry.getValue())
					 .forEach(entry -> types.add(entry.getKey()));
		return types;
	}
}
