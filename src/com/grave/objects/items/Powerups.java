package com.grave.objects.items;

import java.util.List;

import com.grave.Globals;
import com.grave.entities.enemies.Enemy;
import com.grave.misc.Pair;
import com.grave.states.GameState;

public class Powerups {
	public enum Type {
		HEALTH("health"),
		AMMO("ammo"),
		EXTRA_LIFE("life"),
		CRIT_CHANCE("crit"),
		EXP_MULTIPLIER("exp"),
		INVULNERABILITY("invuln"),
		NIGHT_VISION("nightvision"),
		SPEED("speed"),
		UNLIMITED_AMMO("unlimitedammo");

		private String name;
		public String getName() { return name; }

		Type(String name_) {
			this.name = name_;
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
					case CRIT_CHANCE:
						item = new CritChanceItem(position, cTime);
						break;
					case EXP_MULTIPLIER:
						item = new ExpMultiplierItem(position, cTime);
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
					//String id = String.format("%s%d", type.getName(), Globals.generateEntityID());
					gs.getLevel().addEntity(type.getName(), item);
				}
			}
		}
	}
}
