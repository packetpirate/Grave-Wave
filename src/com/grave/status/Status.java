package com.grave.status;

public enum Status {
	// TODO: Add images for acid and burning status effects.
	DAMAGE("GZS_Damage"),
	DEAFENED(""),
	FLASHBANG(""),
	PARALYSIS("GZS_Paralysis"),
	ACID("GZS_AcidEffect"),
	BURNING("GZS_BurningEffect"),
	POISON("GZS_PoisonIcon"),
	CRIT_CHANCE("GZS_CriticalChance"),
	EXP_MULTIPLIER("GZS_ExpMultiplier"),
	INVULNERABLE("GZS_Invulnerability"),
	NIGHT_VISION("GZS_NightVision"),
	SPEED_UP("GZS_SpeedUp"),
	SLOW_DOWN("GZS_SlowDown"),
	UNLIMITED_AMMO("GZS_UnlimitedAmmo");
	
	private String iconName;
	public String getIconName() { return iconName; }
	
	Status(String iconName_) {
		this.iconName = iconName_;
	}
}
