package com.gzsr.objects.items;

import java.util.List;

import com.gzsr.Globals;
import com.gzsr.entities.enemies.Enemy;
import com.gzsr.misc.Pair;
import com.gzsr.states.GameState;

public class Powerups {
	public enum Type {
		HEALTH("health", 0.8f),
		AMMO("ammo", 0.6f),
		EXTRA_LIFE("life", 0.1f),
		INVULNERABILITY("invuln", 0.1f),
		NIGHT_VISION("nightvision", 0.4f),
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
	}
	
	public static void spawnRandomPowerup(GameState gs, Enemy enemy, Pair<Float> position, long cTime) {
		float roll = Globals.rand.nextFloat();
		List<Type> possibleItems = enemy.getLootTable().possibleItems(roll);
		if(!possibleItems.isEmpty()) {
			Type type = possibleItems.get(Globals.rand.nextInt(possibleItems.size()));
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
					case NIGHT_VISION:
						item = new NightVisionItem(position, cTime);
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
}
