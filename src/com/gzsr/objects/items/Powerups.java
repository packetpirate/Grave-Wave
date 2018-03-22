package com.gzsr.objects.items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.gzsr.Globals;
import com.gzsr.misc.Pair;
import com.gzsr.states.GameState;

public class Powerups {
	private enum Type {
		HEALTH("health", 0.8f),
		AMMO("ammo", 0.6f),
		EXTRA_LIFE("life", 0.1f),
		INVULNERABILITY("invuln", 0.1f),
		SPEED("speed", 0.4f),
		UNLIMITED_AMMO("unlimitedammo", 0.4f);
		
		private String name;
		public String getName() { return name; }
		
		private float dropChance;
		public float getDropChance() { return dropChance; }
		
		Type(String name_, float dropChance_) {
			this.name = name_;
			this.dropChance = dropChance_;
		}
		
		public static Type getRandom(Pair<Float> position, long cTime, float roll) {
			List<Type> types = new ArrayList<Type>(Arrays.asList(Type.values()));
			types = types.stream().filter(t -> (roll <= t.getDropChance())).collect(Collectors.toList());
			
			if(types.size() > 0) {
				int r = Globals.rand.nextInt(types.size());
				return types.get(r);
			} else return null;
		}
	}
	
	public static void spawnRandomPowerup(GameState gs, Pair<Float> position, long cTime) {
		float roll = Globals.rand.nextFloat();
		Type type = Type.getRandom(position, cTime, roll);
		if(type != null) {
			Item item = null;
			switch(type) {
				case HEALTH:
					item = new HealthKit(position, cTime);
					break;
				case AMMO:
					item = new AmmoCrate(position, cTime);
					break;
				case EXTRA_LIFE:
					item = new ExtraLife(position, cTime);
					break;
				case INVULNERABILITY:
					item = new InvulnerableItem(position, cTime);
					break;
				case SPEED:
					item = new SpeedItem(position, cTime);
					break;
				case UNLIMITED_AMMO:
					item = new UnlimitedAmmoItem(position, cTime);
					break;
				default:
					break;
			}
			
			if(item != null) {
				String id = String.format("%s%d", type.getName(), Globals.generateEntityID());
				gs.addEntity(id, item);
			}
		}
	}
}
