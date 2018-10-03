package com.gzsr.talents;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.gzsr.math.Calculate;

public class Talents {
	public interface TalentType {
		public int val();
		public int row();
		public int col();
		public int maxRanks();
	}
	
	public enum Munitions implements TalentType {
		SCOUT(125),
		
		INVENTOR(211),
		QUICK_FINGERS(235),
		
		SOLDIER(325),
		
		GUN_GURU(411),
		DEMOLITIONS(421),
		RAPID_FIRE(432),
		
		SCAVENGER(511),
		COMMANDO(525),
		MODDER(531),
		
		ENGINEER(711),
		DESPOT(721),
		HASTE(731);
		
		private int qualifier;
		public int val() { return qualifier; }
		
		public int row() { return Calculate.ExtractDigit(qualifier, 2); }
		public int col() { return Calculate.ExtractDigit(qualifier, 1); }
		public int maxRanks() { return Calculate.ExtractDigit(qualifier, 0); }
		
		Munitions(int qualifier_) {
			this.qualifier = qualifier_;
		}
	}
	
	public enum Fortification implements TalentType {
		HEARTY(125),
		
		MARATHON_MAN(215),
		TARGETING(231),
		
		VIGOR(322),
		
		INVIGORATED(413),
		UNBREAKABLE(425),
		MANUFACTURING(435),
		
		FIREPOWER(535),
		
		RELENTLESS(611),
		UNDYING(621),
		
		LAST_STAND(721),
		DURABILITY(731);
		
		private int qualifier;
		public int val() { return qualifier; }
		
		public int row() { return Calculate.ExtractDigit(qualifier, 2); }
		public int col() { return Calculate.ExtractDigit(qualifier, 1); }
		public int maxRanks() { return Calculate.ExtractDigit(qualifier, 0); }
		
		Fortification(int qualifier_) {
			this.qualifier = qualifier_;
		}
	}
	
	public enum Tactics implements TalentType {
		BRUTALITY(125),
		
		MERCANTILE(215),
		SAVAGE(225),
		
		WINDFALL(315),
		NIMBLE(335),
		
		STOCKPILE(412),
		FEROCITY(431),
		
		HEADSHOT(523),
		
		SUSTAINABILITY(615),
		ASSASSIN(621),
		
		STASIS(731);
		
		private int qualifier;
		public int val() { return qualifier; }
		
		public int row() { return Calculate.ExtractDigit(qualifier, 2); }
		public int col() { return Calculate.ExtractDigit(qualifier, 1); }
		public int maxRanks() { return Calculate.ExtractDigit(qualifier, 0); }
		
		Tactics(int qualifier_) {
			this.qualifier = qualifier_;
		}
	}
	
	private Map<TalentType, Talent> talents;
	
	public Talent getTalent(TalentType type_) { return talents.get(type_); }
	public boolean hasTalent(TalentType type_) { return talents.containsKey(type_); }
	public void registerTalent(TalentType type_, Talent talent_) {
		talents.put(type_, talent_);
	}
	
	public void reset() {
		Iterator<Entry<TalentType, Talent>> it = talents.entrySet().iterator();
		while(it.hasNext()) {
			Entry<TalentType, Talent> entry = it.next();
			entry.getValue().reset();
		}
	}
	
	public Talents() {
		talents = new HashMap<TalentType, Talent>();
	}
}
