package com.gzsr.objects.weapons;

import org.newdawn.slick.Image;

import com.gzsr.AssetManager;

public enum WType {
	// Melee Weapons
	BASEBALL_BAT("Baseball Bat", "A classic. This one has seen a lot of use, but can still bash their heads in just fine.", "GZS_Baseball_Bat_Icon"),
	BASTARD_SWORD("Bastard Sword", "Now this thing can do some damage! Go medieval on those undead freaks.", "GZS_Bastard_Sword_Icon"),
	LOLLIPOP("Lollipop", "Huh... maybe the drugs are finally kicking in...", "GZS_Lollipop_Icon"),
	MACHETE("Machete", "A simple blade, but it's sharp enough to take their heads off.", "GZS_Machete_Icon"),

	// Ranged Weapons
	AK47("AK47", "One of the world's most popular assault rifles... or at least it used to be, I guess.", "GZS_RTPS"),
	AWP("AWP", "A military-grade AWP Sniper Rifle. Maybe a bit overkill, but you could hit several of these undead freaks at once!", "GZS_AWP"),
	BERETTA("Beretta M9", "A fairly popular pistol. Enough to put a bullet in their heads, at least...", "GZS_Beretta"),
	BIG_RED_BUTTON("Big Red Button", "A mysterious featureless box with a large red button on it... I wonder what it does?", "GZS_BigRedButton"),
	BOW_AND_ARROW("Bow & Arrow", "A primitive weapon that takes a little bit of time to fire, but is well worth the wait.", "GZS_Bow"),
	CLAYMORE("M18 Claymore Mine", "A stationary motion-activated explosive that sends shrapnel hurtling through the air to rip your enemies to shreds.", "GZS_ClaymoreWeapon"),
	CROSSBOW("Crossbow", "It may not be the most efficient weapon when you're being swarmed, but it can do some serious damage.", "GZS_Crossbow"),
	FLAMETHROWER("Flamethrower", "A long-barreled, gas-powered weapon that emits a stream of hellfire to roast your enemies.", "GZS_Flammenwerfer"),
	GRENADE_LAUNCHER("M32 MGL-140", "A tube-barreled weapon with a revolving chamber full of grenades so you can rain concussive blasts of fire upon the undead horde.", "GZS_HandEgg"),
	LASER_BARRIER("Laser Barrier", "For those who need a break from fighting zombies to recuperate. Stops the enemies in their tracks.", "GZS_LaserWire"),
	MOLOTOV("Molotov Cocktail", "Start an undead barbecue with these flaming bottles of gas!", "GZS_Molotov_Icon"),
	MOSSBERG("Mossberg 500", "It was meant for hunting turkeys, but... well... it'll have to do.", "GZS_Boomstick"),
	MP5("MP5", "A standard military issue MP5.", "GZS_Mp5"),
	NAIL_GUN("Nail Gun", "Well it's not a gun, but it'll do... I guess.", "GZS_NailGun"),
	REMINGTON("Remington 783", "A scoped Remington hunting rifle with an extended magazine. This thing looks like it packs a punch!", "GZS_Remington"),
	SAW_REVOLVER("Smith & Wesson Model 500", "One of the most popular revolvers in the world... and one of the most powerful.", "GZS_SmithAndWesson"),
	SENTRY_GUN("Sentry Gun", "Feeling overwhelmed and need some automated assistance? Unleash a second, robotic apocalypse on the undead horde.", "GZS_Turret"),
	STINGER("Stinger FIM-92", "Who just leaves something like this lying around? This just got a lot easier...", "GZS_Stinger"),
	TASER("Taser", "Don't taze me, bro!", "GZS_Taser"),

	// Crafted Weapons
	SPIKED_BAT("Spiked Bat", "As if their faces weren't mangled enough already...", "GZS_Spiked_Bat_Icon"),
	CROSSBOWGUN("Crossbowgun", "Why keep the bow part? Because it's cool, now shut up...", "GZS_Crossbowgun"),
	ELECTRIC_NET_CANNON("Electric Net Cannon", "Get those undead extra crispy under a blanket of electric fury.", "GZS_Electric_Net_Cannon");

	private String name;
	public String getName() { return name; }
	private String description;
	public String getDescription() { return description; }
	private String image;
	public Image getImage() { return AssetManager.getManager().getImage(image); }

	WType(String name_, String description_, String image_) {
		this.name = name_;
		this.description = description_;
		this.image = image_;
	}
}
