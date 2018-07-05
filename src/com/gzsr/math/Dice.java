package com.gzsr.math;

import com.gzsr.Globals;

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
	
	public int roll() {
		return roll(0);
	}
	
	public int roll(int modifier) {
		int total = 0;
		
		for(int i = 0; i < count; i++) {
			total += (Globals.rand.nextInt(sides) + 1);
		}
		
		return (total + modifier);
	}
}
