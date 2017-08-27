package com.gzsr.status;

public enum Status {
	// TODO: Add images for acid and burning status effects.
	ACID(""),
	BURNING(""),
	EXP_MULTIPLIER("GZS_ExpMultiplier"),
	INVULNERABLE("GZS_Invulnerability"),
	NIGHT_VISION("GZS_NightVision"),
	SPEED_UP("GZS_SpeedUp"),
	UNLIMITED_AMMO("GZS_UnlimitedAmmo");
	
	private String iconName;
	public String getIconName() { return iconName; }
	
	Status(String iconName_) {
		this.iconName = iconName_;
	}
}
