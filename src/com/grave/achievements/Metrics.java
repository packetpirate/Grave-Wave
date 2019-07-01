package com.grave.achievements;

public enum Metrics {
	// Basic Metrics
	PLAYER(0), ENEMY(1), DAMAGE(2), KILL(3),
	WAVE_START(4), WAVE_END(5),

	// Enemy Metrics
	ZUMBY(6), ROTDOG(7), UPCHUCK(8), GASBAG(9), BIG_MAMA(10),
	TINY_ZUMBY(11), STARFRIGHT(12), EL_SALVO(13), PROWLER(14), GLORP(15),

	// Boss Metrics
	ABERRATION(16), STITCHES(17), ZOMBAT(18),

	// Damage Type Metrics
	STATUS(19), ACID(20), FIRE(21), PARALYSIS(22), POISON(23), EXPLOSION(24),
	CORROSIVE(25), ELECTRIC(26),

	// Melee Weapon Metrics
	BASEBALL_BAT(27), SPIKED_BAT(28), MACHETE(29), BASTARD_SWORD(30), LOLLIPOP(31),

	// Ranged Weapon Metrics
	AK47(32), AWP(33), BERETTA(34), BIG_RED_BUTTON(35),
	BOW_AND_ARROW(36), COMPOSITE_BOW(37), CLAYMORE(38), CROSSBOW(39), CROSSBOWGUN(40), ELECTRIC_NET_CANNON(41),
	FLAK_CANNON(42), FLAMETHROWER(43), GRENADE(44), STINGER(45), LASER_BARRIER(46), MOLOTOV(47),
	MOSSBERG(48), MP5(49), NAIL_GUN(50), PIPE_BOMB(51), REMINGTON(52), SAW_REVOLVER(53),
	SENTRY(54), TASER(55);

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
