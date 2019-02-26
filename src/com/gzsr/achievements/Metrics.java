package com.gzsr.achievements;

public enum Metrics {
	// Basic Metrics
	PLAYER(0), ENEMY(1), DAMAGE(2), KILL(3),
	WAVE_START(4), WAVE_END(5),

	// Enemy Metrics
	ZUMBY(6), ROTDOG(7), UPCHUCK(8), GASBAG(9), BIG_MAMA(10),
	TINY_ZUMBY(11), STARFRIGHT(12), EL_SALVO(13), PROWLER(14),

	// Boss Metrics
	ABERRATION(15), STITCHES(16), ZOMBAT(17),

	// Damage Type Metrics
	STATUS(18), ACID(19), FIRE(20), PARALYSIS(21), POISON(22), EXPLOSION(23),
	CORROSIVE(24), ELECTRIC(25),

	// Melee Weapon Metrics
	BASEBALL_BAT(26), SPIKED_BAT(27), MACHETE(28), BASTARD_SWORD(29), LOLLIPOP(30),

	// Ranged Weapon Metrics
	AK47(31), AWP(32), BERETTA(33), BIG_RED_BUTTON(34),
	BOW_AND_ARROW(35), CLAYMORE(36), CROSSBOW(37), CROSSBOWGUN(38), ELECTRIC_NET_CANNON(39),
	FLAMETHROWER(40), GRENADE(41), STINGER(42), LASER_BARRIER(43), MOLOTOV(44),
	MOSSBERG(45), MP5(46), NAIL_GUN(47), REMINGTON(48), SAW_REVOLVER(49),
	SENTRY(50), TASER(51);

	public final int index;

	Metrics(int index_) {
		this.index = index_;
	}

	public static Metric compose(Metrics... metrics) {
		return new Metric(metrics);
	}
}
