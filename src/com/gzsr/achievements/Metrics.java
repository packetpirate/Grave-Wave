package com.gzsr.achievements;

public class Metrics {
	public static final long ENEMY = 1;
	public static final long PLAYER = 2;
	public static final long DAMAGE = 4;
	public static final long KILL = 8;
	
	public static final long ZUMBY = 16;
	public static final long ROTDOG = 32;
	public static final long UPCHUCK = 64;
	public static final long GASBAG = 128;
	public static final long BIG_MAMA = 256;
	public static final long TINY_ZUMBY = 512;
	public static final long STARFRIGHT = 1_024;
	public static final long EL_SALVO = 2_048;
	public static final long PROWLER = 4_096;
	
	public static final long ABERRATION = 8_192;
	public static final long STITCHES = 16_384;
	public static final long ZOMBAT = 32_768;
	
	public static final long BASEBALL_BAT = 65_536; 
	public static final long MACHETE = 131_072;
	public static final long BASTARD_SWORD = 262_144;
	public static final long LOLLIPOP = 524_288;
	
	public static final long AK47 = 1_048_576;
	public static final long AWP = 2_097_152;
	public static final long BERETTA = 4_194_304;
	public static final long BIG_RED_BUTTON = 8_388_608;
	public static final long BOW_AND_ARROW = 16_777_216;
	public static final long CLAYMORE = 33_554_432;
	public static final long CROSSBOW = 67_108_864;
	public static final long FLAMETHROWER = 134_217_728;
	public static final long GRENADE = 268_435_456;
	public static final long STINGER = 536_870_912;
	public static final long MOLOTOV = 1_073_741_824;
	public static final long MOSSBERG = 2_147_483_648L;
	public static final long MP5 = 4_294_967_296L;
	public static final long NAIL_GUN = 8_589_934_592L;
	public static final long REMINGTON = 17_179_869_184L;
	public static final long SAW_REVOLVER = 34_359_738_368L;
	public static final long SENTRY = 68_719_476_736L;
	public static final long TASER = 137_438_953_472L;
	
	public static final long ACID = 274_877_906_944L;
	public static final long FIRE = 549_755_813_888L;
	public static final long CRIT = 1_099_511_627_776L;
	public static final long PARALYSIS = 2_199_023_255_552L;
	public static final long POISON = 4_398_046_511_104L;
	public static final long EXPLOSION = 8_796_093_022_208L;
	
	public static final long WAVE_START = 17_592_186_044_416L;
	public static final long WAVE_END = 35_184_372_088_832L;
	
	/**
	 * Uses bitwise AND to compose several game flags into a single long value.
	 * @param flags The defined game metrics to be composed into a single long value.
	 * @return All game metrics combined into a single value.
	 */
	public static final long compose(long... flags) {
		long result = 0;
		
		for(long flag : flags) {
			result = (result | flag);
		}
		
		return result;
	}
	
	/**
	 * Checks to see if a flag is set or unset in the given metric.
	 * @param metric The flag set to check for the given flag.
	 * @param flag One of the defined game metrics.
	 * @return True if the flag is set.
	 */
	public static final boolean has(long metric, long flag) {
		return ((metric & flag) == flag);
	}
}
