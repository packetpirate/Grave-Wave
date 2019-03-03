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
	FLAK_CANNON(40), FLAMETHROWER(41), GRENADE(42), STINGER(43), LASER_BARRIER(44), MOLOTOV(45),
	MOSSBERG(46), MP5(47), NAIL_GUN(48), REMINGTON(49), SAW_REVOLVER(50),
	SENTRY(51), TASER(52);

	public final int index;

	Metrics(int index_) {
		this.index = index_;
	}

	public static Metrics getByIndex(int index) {
		Metrics [] metrics = Metrics.values();
		for(int i = 0; i < metrics.length; i++) {
			Metrics metric = metrics[i];
			if(metric.index == index) return metric;
		}

		return null;
	}

	public static Metric compose(Metrics... metrics) {
		return new Metric(metrics);
	}
}
