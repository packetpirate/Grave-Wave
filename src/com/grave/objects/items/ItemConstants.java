package com.grave.objects.items;

public enum ItemConstants {
	ONE(1, 5.0, 15_000L, 10_000L),
	TWO(2, 20.0, 10_000L, 7_500L),
	THREE(3, 40.0, 7_500L, 5_000L),
	FOUR(4, 60.0, 5_000L, 3_000L),
	FIVE(5, 80.0, 2_500L, 2_000L),
	SIX(6, 100.0, 1_000L, 1_500L),
	SEVEN(7, 125.0, 750L, 1_000L),
	EIGHT(8, 180.0, 500L, 750L),
	NINE(9, 200.0, 250L, 500L),
	TEN(10, 250.0, 100L, 250L);
	
	private final int pipCount;
	public int getPipCount() { return pipCount; }
	
	private final double damage;
	private double getDamage() { return damage; }
	
	private final long rateOfFire;
	private long getRateOfFire() { return rateOfFire; }
	
	private final long reloadTime;
	private long getReloadTime() { return reloadTime; }
	
	ItemConstants(int pipCount_, double damage_, long rateOfFire_, long reloadTime_) {
		this.pipCount = pipCount_;
		this.damage = damage_;
		this.rateOfFire = rateOfFire_;
		this.reloadTime = reloadTime_;
	}
	
	public static ItemConstants getDamageClass(double dmg) {
		if(dmg >= TEN.getDamage()) return TEN;
		else if(dmg >= NINE.getDamage()) return NINE;
		else if(dmg >= EIGHT.getDamage()) return EIGHT;
		else if(dmg >= SEVEN.getDamage()) return SEVEN;
		else if(dmg >= SIX.getDamage()) return SIX;
		else if(dmg >= FIVE.getDamage()) return FIVE;
		else if(dmg >= FOUR.getDamage()) return FOUR;
		else if(dmg >= THREE.getDamage()) return THREE;
		else if(dmg >= TWO.getDamage()) return TWO;
		else return ONE;
	}
	
	public static ItemConstants getRateOfFireClass(long rof) {
		if(rof <= TEN.getRateOfFire()) return TEN;
		else if(rof <= NINE.getRateOfFire()) return NINE;
		else if(rof <= EIGHT.getRateOfFire()) return EIGHT;
		else if(rof <= SEVEN.getRateOfFire()) return SEVEN;
		else if(rof <= SIX.getRateOfFire()) return SIX;
		else if(rof <= FIVE.getRateOfFire()) return FIVE;
		else if(rof <= FOUR.getRateOfFire()) return FOUR;
		else if(rof <= THREE.getRateOfFire()) return THREE;
		else if(rof <= TWO.getRateOfFire()) return TWO;
		else return ONE;
	}
	
	public static ItemConstants getReloadTimeClass(long rlt) {
		if(rlt <= TEN.getReloadTime()) return TEN;
		else if(rlt <= NINE.getReloadTime()) return NINE;
		else if(rlt <= EIGHT.getReloadTime()) return EIGHT;
		else if(rlt <= SEVEN.getReloadTime()) return SEVEN;
		else if(rlt <= SIX.getReloadTime()) return SIX;
		else if(rlt <= FIVE.getReloadTime()) return FIVE;
		else if(rlt <= FOUR.getReloadTime()) return FOUR;
		else if(rlt <= THREE.getReloadTime()) return THREE;
		else if(rlt <= TWO.getReloadTime()) return TWO;
		else return ONE;
	}
}
