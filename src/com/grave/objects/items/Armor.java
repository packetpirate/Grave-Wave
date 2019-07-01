package com.grave.objects.items;

import com.grave.AssetManager;
import com.grave.Globals;
import com.grave.entities.Player;
import com.grave.misc.Pair;

public class Armor extends Item {
	private static final String ICONNAME = "GZS_Armor";
	private static final long DURATION = 10_000L;
	private static final int COST = 1_000;
	public static final float SHOP_SPAWN_CHANCE = 0.2f;
	
	private Type armorType;
	
	public enum Type {
		NORMAL(50.0), 
		REINFORCED(100.0);
		
		private double amnt;
		public double getArmorValue() { return amnt; }
		
		Type(double amnt_) {
			this.amnt = amnt_;
		}
		
		public static Type randomType() {
			Type [] types = values();
			return types[Globals.rand.nextInt(types.length)];
		}
	}
	
	public Armor(Type armorType_, Pair<Float> pos, long cTime) {
		super(pos, cTime);
		
		armorType = armorType_;
		
		iconName = Armor.ICONNAME;
		duration = Armor.DURATION;
		pickup = AssetManager.getManager().getSound("powerup2");
	}

	@Override
	public void apply(Player player, long cTime) {
		player.addArmor(armorType.getArmorValue());
		duration = 0L;
		pickup.play(1.0f, AssetManager.getManager().getSoundVolume());
	}

	@Override
	public int getCost() {
		return Armor.COST;
	}
	
	@Override
	public String getName() {
		return (((armorType == Type.REINFORCED) ? "Reinforced " : "") + "Leather Armor");
	}

	@Override
	public String getDescription() {
		return "Thick leather armor that will protect you from getting bitten, but not much else...";
	}
}
