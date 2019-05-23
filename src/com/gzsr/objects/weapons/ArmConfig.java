package com.gzsr.objects.weapons;

import org.newdawn.slick.Image;

import com.gzsr.AssetManager;
import com.gzsr.misc.Pair;

public enum ArmConfig {
	// TODO: Make sure all arm and weapon image names are correct for that particular weapon.
	AK47("GZS_Player2_Arm1", "", 0, 0, 0, 0),
	AWP("GZS_Player2_Arm1", "", 0, 0, 0, 0),
	BERETTA("GZS_Player2_Arm1", "GZS_Beretta_W", 5.0f, -35.0f, 14.0f, -36.0f),
	BIG_RED_BUTTON("GZS_Player2_Arm1", "", 0, 0, 0, 0),
	BOW_AND_ARROW("GZS_Player2_Arm1", "", 0, 0, 0, 0),
	CLAYMORE("GZS_Player2_Arm1", "", 0, 0, 0, 0),
	CROSSBOW("GZS_Player2_Arm1", "", 0, 0, 0, 0),
	FLAMETHROWER("GZS_Player2_Arm1", "", 0, 0, 0, 0),
	GRENADE_LAUNCHER("GZS_Player2_Arm1", "", 0, 0, 0, 0),
	LASER_BARRIER("GZS_Player2_Arm1", "", 0, 0, 0, 0),
	MOSSBERG("GZS_Player2_Arm1", "", 0, 0, 0, 0),
	MP5("GZS_Player2_Arm2", "GZS_MP5_W", -8.0f, -43.0f, 0.0f, -43.0f),
	NAIL_GUN("GZS_Player2_Arm1", "", 0, 0, 0, 0),
	REMINGTON("GZS_Player2_Arm1", "", 0, 0, 0, 0),
	SAW_REVOLVER("GZS_Player2_Arm1", "", 0, 0, 0, 0),
	SENTRY_GUN("GZS_Player2_Arm1", "", 0, 0, 0, 0),
	STINGER("GZS_Player2_Arm1", "", 0, 0, 0, 0),
	TASER("GZS_Player2_Arm1", "", 0, 0, 0, 0),

	// Crafted Weapons
	MOLOTOV("GZS_Player2_Arm1", "", 0, 0, 0, 0),
	PIPE_BOMB("GZS_Player2_Arm1", "", 0, 0, 0, 0),
	COMPOSITE_BOW("GZS_Player2_Arm1", "", 0, 0, 0, 0),
	CROSSBOWGUN("GZS_Player2_Arm1", "", 0, 0, 0, 0),
	FLAK_CANNON("GZS_Player2_Arm1", "", 0, 0, 0, 0),
	ELECTRIC_NET_CANNON("GZS_Player2_Arm1", "", 0, 0, 0, 0);

	private String armImage;
	private String weaponImage;
	private Pair<Float> offset;
	private Pair<Float> muzzle;

	ArmConfig(String armImage_, String weaponImage_, float wx_, float wy_, float mx_, float my_) {
		this.armImage = armImage_;
		this.weaponImage = weaponImage_;
		this.offset = new Pair<Float>(wx_, wy_);
		this.muzzle = new Pair<Float>(mx_, my_);
	}

	public Image getArmImage() { return AssetManager.getManager().getImage(armImage); }
	public Image getWeaponImage() { return AssetManager.getManager().getImage(weaponImage); }
	public Pair<Float> getOffset() { return offset; }
	public Pair<Float> getMuzzle() { return muzzle; }
}
