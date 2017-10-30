package com.gzsr.math;

import java.util.Iterator;
import java.util.List;

import com.gzsr.Globals;
import com.gzsr.entities.enemies.Enemy;
import com.gzsr.misc.Pair;

public class Calculate {
	/**
	 * Calculates the hypotenuse of the triangle between two points.
	 * @param src The source position.
	 * @param target The target position.
	 * @return The theta value representing the hypotenuse between the two points.
	 */
	public static float Hypotenuse(Pair<Float> src, Pair<Float> target) {
		return (float)Math.atan2((target.y - src.y), (target.x - src.x));
	}
	
	/**
	 * Calculates the distance between two points.
	 * @param src The source position.
	 * @param target The target position.
	 * @return The distance between the two points.
	 */
	public static float Distance(Pair<Float> src, Pair<Float> target) {
		float xD = target.x - src.x;
		float yD = target.y - src.y;
		return (float)Math.sqrt((xD * xD) + (yD * yD));
	}
	
	public Enemy raycast(List<Enemy> enemies, Pair<Float> startPos, float theta) {
		boolean outOfBounds = false;
		Pair<Float> position = new Pair<Float>(startPos.x, startPos.y);
		while(!outOfBounds) {
			// Update the position of the ray's end point.
			position.x += (float)Math.cos(theta) * 2.0f;
			position.y += (float)Math.sin(theta) * 2.0f;
			
			// Check if the position of the raycast is out of bounds.
			outOfBounds = (position.x < 0) && (position.y < 0) && 
						  (position.x >= Globals.WIDTH) && (position.y >= Globals.HEIGHT);
			
			// Check for collisions with living enemies.
			Iterator<Enemy> it = enemies.iterator();
			while(it.hasNext()) {
				Enemy e = it.next();
				if(e.checkCollision(position)) {
					// This enemy has been hit, so return the enemy reference.
					return e;
				}
			}
		}
		
		return null; // No collisions founds.
	}
}
