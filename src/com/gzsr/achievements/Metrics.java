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
	BOW_AND_ARROW(35), COMPOSITE_BOW(36), CLAYMORE(37), CROSSBOW(38), CROSSBOWGUN(39), ELECTRIC_NET_CANNON(40),
	FLAK_CANNON(41), FLAMETHROWER(42), GRENADE(43), STINGER(44), LASER_BARRIER(45), MOLOTOV(46),
	MOSSBERG(47), MP5(48), NAIL_GUN(49), PIPE_BOMB(50), REMINGTON(51), SAW_REVOLVER(52),
	SENTRY(53), TASER(54);

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
