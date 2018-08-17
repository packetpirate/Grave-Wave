package com.gzsr.achievements;

public class Metrics {
	public static final long ENEMY_DAMAGE = 1;
	public static final long ENEMY_KILL = 2;
	
	public static final long ZUMBY = 4;
	public static final long ROTDOG = 8;
	public static final long UPCHUCK = 16;
	public static final long GASBAG = 32;
	public static final long BIG_MAMA = 64;
	public static final long TINY_ZUMBY = 128;
	public static final long STARFRIGHT = 256;
	public static final long EL_SALVO = 512;
	public static final long PROWLER = 1_024;
	
	public static final long ABERRATION = 2_048;
	public static final long STITCHES = 4_096;
	public static final long ZOMBAT = 8_192;
	
	public static final long MACHETE = 16_384;
	public static final long BASTARD_SWORD = 32_768;
	public static final long LOLLIPOP = 65_536;
	
	public static final long AK47 = 131_072;
	public static final long AWP = 262_144;
	public static final long BERETTA = 524_288;
	public static final long BIG_RED_BUTTON = 1_048_576;
	public static final long BOW_AND_ARROW = 2_097_152;
	public static final long CLAYMORE = 4_194_304;
	public static final long CROSSBOW = 8_388_608;
	public static final long FLAMETHROWER = 16_777_216;
	public static final long GRENADE = 33_554_432;
	public static final long STINGER = 67_108_864;
	public static final long MOLOTOV = 134_217_728;
	public static final long MOSSBERG = 268_435_456;
	public static final long MP5 = 536_870_912;
	public static final long NAIL_GUN = 1_073_741_824;
	public static final long REMINGTON = 2_147_483_648L;
	public static final long SAW_REVOLVER = 4_294_967_296L;
	public static final long SENTRY = 8_589_934_592L;
	public static final long TASER = 17_179_869_184L;
	
	public static final long ACID = 34_359_738_368L;
	public static final long FIRE = 68_719_476_736L;
	public static final long CRIT = 137_438_953_472L;
	public static final long PARALYSIS = 274_877_906_944L;
	public static final long POISON = 549_755_813_888L;
	public static final long EXPLOSION = 1_099_511_627_776L;
	
	public static final long WAVE_START = 2_199_023_255_552L;
	public static final long WAVE_END = 4_398_046_511_104L;
	
	public static final long compose(long... flags) {
		long result = 0;
		
		for(long flag : flags) {
			result = (result | flag);
		}
		
		return result;
	}
}
