package com.gzsr.talents;

import org.newdawn.slick.Image;

import com.gzsr.AssetManager;
import com.gzsr.entities.Attributes;
import com.gzsr.entities.Player;

public class Talents {
	public interface TalentType {
		public String getName();
		public String getDescription();
		public Image getIcon();
		
		public int row();
		public int col();
		
		public boolean active();
		
		public int ranks();
		public void ranks(int ranks_);
		public int maxRanks();
	}
	
	public enum Munitions implements TalentType {
		SCOUT(0, 1, 5, "Scout", "Increases small ranged weapon damage by 20% per rank.", "GZS_Talent_Scout"),
		
		INVENTOR(1, 0, 1, "Inventor", "Can craft basic weapons.", "GZS_Talent_Inventor"),
		QUICK_FINGERS(1, 2, 5, "Quick Fingers", "Decreases reload speed by 10%. Max 5 ranks.", "GZS_Talent_QuickFingers"),
		
		SOLDIER(2, 1, 5, "Soldier", "Increases medium ranged weapon damage by 20% per rank.", "GZS_Talent_Soldier"),
		
		GUN_GURU(3, 0, 1, "Gun Guru", "Crafted weapons do an additional 25% damage.", "GZS_Talent_GunGuru"),
		DEMOLITIONS(3, 1, 1, "Demolitions", "Increases damage of explosions by 50%.", "GZS_Talent_Demolitions"),
		RAPID_FIRE(3, 2, 2, "Rapid Fire", "Increases rate of fire by 25% per rank.", "GZS_Talent_RapidFire"),
		
		SCAVENGER(4, 0, 1, "Scavenger", "Increases resources dropped by enemies by 1. Increases resource drop rate by 25%.", "GZS_Talent_Scavenger"),
		COMMANDO(4, 1, 5, "Commando", "Increases large ranged weapon damage by 25% per rank.", "GZS_Talent_Commando"),
		MODDER(4, 2, 1, "Modder", "Increase magezine capacity by 50%.", "GZS_Talent_Modder"),
		
		ENGINEER(6, 0, 1, "Engineer", "Can craft advanced weaponry.", "GZS_Talent_Engineer"),
		DESPOT(6, 1, 1, "Despot", "Increases all damage done by 50%.", "GZS_Talent_Despot"),
		HASTE(6, 2, 1, "Haste", "Grants a 10% chance to reload instantly.", "GZS_Talent_Haste");
		
		Munitions(int row_, int col_, int maxRanks_, String name_, String description_, String icon_) {
			this.name = name_;
			this.description = description_;
			this.icon = icon_;
			
			this.row = row_;
			this.col = col_;
			
			this.ranks = 0;
			this.maxRanks = maxRanks_;
		}

		private String name;
		public String getName() { return name; }
		
		private String description;
		public String getDescription() { return description; }
		
		private String icon;
		public Image getIcon() { return AssetManager.getManager().getImage(icon); }

		private int row;
		public int row() { return row; }

		private int col;
		public int col() { return col; }

		public boolean active() { return (ranks > 0); }

		private int ranks;
		public int ranks() { return ranks; }
		public void ranks(int ranks_) { this.ranks = ranks_; }
		public void addRank() { ranks++; }
		
		private int maxRanks;
		public int maxRanks() { return maxRanks; }
	}
	
	public enum Fortification implements TalentType {
		HEARTY(0, 1, 5, "Hearty", "Increases HP by 20 per rank.", "GZS_Talent_Hearty"),
		
		MARATHON_MAN(1, 0, 5, "Marathon Man", "Increases Stamina by 10 per rank.", "GZS_Talent_MarathonMan"),
		TARGETING(1, 2, 1, "Targeting", "Increases construct range by 100%.", "GZS_Talent_Targeting"),
		
		VIGOR(2, 1, 2, "Vigor", "Grants a 5% chance per rank to resist poison and paralysis effects.", "GZS_Talent_Vigor"),
		
		INVIGORATED(3, 0, 3, "Invigorated", "Grants Stamina regeneration of 5/sec per rank.", "GZS_Talent_Invigorated"),
		UNBREAKABLE(3, 1, 5, "Unbreakable", "Reduces damage taken by 5% per rank.", "GZS_Talent_Unbreakable"),
		MANUFACTURING(3, 2, 5, "Manufacturing", "Increases construct HP by 20%.", "GZS_Talent_Manufacturing"),
		
		FIREPOWER(4, 2, 5, "Firepower", "Increases construct damage by 10% per rank.", "GZS_Talent_Firepower"),
		
		RELENTLESS(5, 0, 1, "Relentless", "Killing an enemy with a melee weapon has a 10% chance to restore 25% stamina.", "GZS_Talent_Relentless"),
		UNDYING(5, 1, 1, "Undying", "Regenerate 1 HP / second while not taking damage.", "GZS_Talent_Undying"),
		
		LAST_STAND(6, 1, 1, "Last Stand", "When the player is below 25% health, they take 50% less damage.", "GZS_Talent_LastStand"),
		DURABILITY(6, 2, 1, "Durability", "Increases construct duration by 100%.", "GZS_Talent_Durability");
		
		Fortification(int row_, int col_, int maxRanks_, String name_, String description_, String icon_) {
			this.name = name_;
			this.description = description_;
			this.icon = icon_;
			
			this.row = row_;
			this.col = col_;
			
			this.ranks = 0;
			this.maxRanks = maxRanks_;
		}

		private String name;
		public String getName() { return name; }
		
		private String description;
		public String getDescription() { return description; }

		private String icon;
		public Image getIcon() { return AssetManager.getManager().getImage(icon); }
		
		private int row;
		public int row() { return row; }

		private int col;
		public int col() { return col; }

		public boolean active() { return (ranks > 0); }

		private int ranks;
		public int ranks() { return ranks; }
		public void ranks(int ranks_) { this.ranks = ranks_; }
		public void addRank() { ranks++; }
		
		private int maxRanks;
		public int maxRanks() { return maxRanks; }
	}
	
	public enum Tactics implements TalentType {
		BRUTALITY(0, 1, 5, "Brutality", "Increases melee damage done by 10% per rank.", ""),
		
		MERCANTILE(1, 0, 5, "Mercantile", "Reduces shop prices by 10% per rank.", ""),
		SAVAGE(1, 1, 5, "Savage", "Increases melee critical chance by 5% per rank.", ""),
		
		WINDFALL(2, 0, 5, "Windfall", "Increases money dropped by 10% per rank.", ""),
		NIMBLE(2, 2, 5, "Nimble", "Increases movement speed by 10% per rank.", ""),
		
		STOCKPILE(3, 0, 2, "Stockpile", "Increases ammo capacity by 50% per rank.", ""),
		FEROCITY(3, 2, 1, "Ferocity", "Increases melee weapon attack speed by 50%.", ""),
		
		HEADSHOT(4, 1, 3, "Headshot!", "Increases ranged critical chance by 5% per rank.", ""),
		
		SUSTAINABILITY(5, 0, 5, "Sustainability", "Increases duration of power-ups by 20% per rank.", ""),
		ASSASSIN(5, 1, 1, "Assassin", "Critical hits now do 3x damage.", ""),
		
		STASIS(6, 2, 1, "Stasis", "Speed power-ups now also slow enemies.", "");
		
		Tactics(int row_, int col_, int maxRanks_, String name_, String description_, String icon_) {
			this.name = name_;
			this.description = description_;
			this.icon = icon_;
			
			this.row = row_;
			this.col = col_;
			
			this.ranks = 0;
			this.maxRanks = maxRanks_;
		}

		private String name;
		public String getName() { return name; }
		
		private String description;
		public String getDescription() { return description; }
		
		private String icon;
		public Image getIcon() { return AssetManager.getManager().getImage(icon); }

		private int row;
		public int row() { return row; }

		private int col;
		public int col() { return col; }

		public boolean active() { return (ranks > 0); }

		private int ranks;
		public int ranks() { return ranks; }
		public void ranks(int ranks_) { this.ranks = ranks_; }
		public void addRank() { ranks++; }
		
		private int maxRanks;
		public int maxRanks() { return maxRanks; }
	}
	
	/**
	 * Calls out to relevant classes to apply effects of a talent when points
	 * are invested in that talent. Ugly, but easy...
	 */
	public static void applyRanks(TalentType talent) {
		Player player = Player.getPlayer();
		Attributes attr = player.getAttributes();
		int ranks = talent.ranks();
		
		if(talent instanceof Munitions) {
			Munitions m = (Munitions)talent;
			switch(m) {
			
			}
		} else if(talent instanceof Fortification) {
			Fortification f = (Fortification)talent;
			switch(f) {
				case HEARTY:
					attr.set("maxHealth", ((ranks * 20.0) + 100.0));
					break;
				case MARATHON_MAN:
					attr.set("maxStamina", ((ranks * 10.0) + 100.0));
					break;
				case INVIGORATED:
					attr.set("staminaRefreshRate", ((ranks * 5.0) + 10.0));
					break;
				case UNBREAKABLE:
					break;
				case FIREPOWER:
					break;
				case UNDYING:
					attr.set("healthRegen", (ranks * 1.0));
					break;
			}
		} else if(talent instanceof Tactics) {
			Tactics t = (Tactics)talent;
			switch(t) {
				case SAVAGE:
					attr.set("meleeCritChance", ((ranks * 0.05f) + 0.05f));
					break;
				case NIMBLE:
					attr.set("speedUp", ranks);
					break;
				case HEADSHOT:
					attr.set("rangeCritChance", ((ranks * 0.05f) + 0.05f));
					break;
				case ASSASSIN:
					attr.set("critMult", ((ranks * 1.0) + 2.0));
					break;
			}
		}
	}
	
	public static void reset() {
		for(Munitions m : Munitions.values()) {
			m.ranks(0);
		}
		
		for(Fortification f : Fortification.values()) {
			f.ranks(0);
		}
		
		for(Tactics t : Tactics.values()) {
			t.ranks(0);
		}
	}
}
