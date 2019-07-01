package com.grave.math;

import com.grave.Globals;
import com.grave.misc.Pair;

public class Dice {
	private int count;
	public int getCount() { return count; }
	public void setCount(int count_) { this.count = count_; }
	
	private int sides;
	public int getSides() { return sides; }
	public void setSides(int sides_) { this.sides = sides_; }
	
	public Dice(int count_, int sides_) {
		this.count = count_;
		this.sides = sides_;
	}
	
	public Pair<Integer> getRange() {
		return getRange(0);
	}
	
	public Pair<Integer> getRange(int modifier) {
		int high = (count * sides) + modifier;
		return new Pair<Integer>((count + modifier), high);
	}
	
	public static Pair<Integer> getRange(int count, int sides, int modifier) {
		int high = (count * sides) + modifier;
		return new Pair<Integer>((count + modifier), high);
	}
	
	public int roll() {
		return roll(0);
	}
	
	public int roll(int modifier) {
		return roll(modifier, false);
	}
	
	public int roll(boolean critical) {
		return roll(0, critical);
	}
	
	public static int roll(int count, int sides, int modifier) {
		return roll(count, sides, modifier, false);
	}
	
	public static int roll(int count, int sides, int modifier, boolean critical) {
		if(critical) return ((count * sides) + modifier);
		
		int total = 0;
		
		for(int i = 0; i < count; i++) {
			total += (Globals.rand.nextInt(sides) + 1);
		}
		
		return (total + modifier);
	}
	
	public int roll(int modifier, boolean critical) {
		if(critical) return ((count * sides) + modifier);
		
		int total = 0;
		
		for(int i = 0; i < count; i++) {
			total += (Globals.rand.nextInt(sides) + 1);
		}
		
		return (total + modifier);
	}
}
