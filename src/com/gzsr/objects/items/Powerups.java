package com.gzsr.objects.items;

import com.gzsr.Globals;
import com.gzsr.misc.Pair;
import com.gzsr.states.GameState;

public class Powerups {
	private enum Type {
		AMMO("ammo"),
		EXTRA_LIFE("life"),
		INVULNERABILITY("invuln"),
		SPEED("speed"),
		UNLIMITED_AMMO("unlimitedammo");
		
		private String name;
		public String getName() { return name; }
		
		Type(String name_) {
			this.name = name_;
		}
		
		public static Type getRandom(Pair<Float> position, long cTime) {
			Type [] types = Type.values();
			int r = Globals.rand.nextInt(types.length);
			return types[r];
		}
	}
	
	public static void spawnRandomPowerup(GameState gs, Pair<Float> position, long cTime) {
		Type type = Type.getRandom(position, cTime);
		Item item = null;
		switch(type) {
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
