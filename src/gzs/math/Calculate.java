package gzs.math;

import gzs.game.misc.Pair;

public class Calculate {
	/**
	 * Calculates the hypotenuse of the triangle between two points.
	 * @param src The source position.
	 * @param target The target position.
	 * @return The theta value representing the hypotenuse between the two points.
	 */
	public static double Hypotenuse(Pair<Double> src, Pair<Double> target) {
		return Math.atan2((target.y - src.y), (target.x - src.x));
	}
	
	/**
	 * Calculates the distance between two points.
	 * @param src The source position.
	 * @param target The target position.
	 * @return The distance between the two points.
	 */
	public static double Distance(Pair<Double> src, Pair<Double> target) {
		double xD = target.x - src.x;
		double yD = target.y - src.y;
		return Math.sqrt((xD * xD) + (yD * yD));
	}
}
