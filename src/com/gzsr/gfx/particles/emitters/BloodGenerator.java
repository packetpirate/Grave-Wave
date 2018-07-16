package com.gzsr.gfx.particles.emitters;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import org.newdawn.slick.Color;

import com.gzsr.Globals;
import com.gzsr.entities.Entity;
import com.gzsr.entities.enemies.Enemy;
import com.gzsr.gfx.ColorGenerator;
import com.gzsr.gfx.particles.Particle;
import com.gzsr.misc.Pair;

public class BloodGenerator {
	private static final long BLOOD_DRAW_TIME = 5_000L;
	
	public static final BiFunction<Entity, Long, List<Particle>> BURST = new BiFunction<Entity, Long, List<Particle>>() {
		private static final int PARTICLE_COUNT = 30;
		private static final float MIN_VELOCITY = 0.25f;
		private static final float MAX_VELOCITY = 0.5f;
		private static final float MIN_SIZE = 2.0f;
		private static final float MAX_SIZE = 4.0f;
		private static final long MIN_LIFESPAN = 50L;
		private static final long MAX_LIFESPAN = 100L;
		private static final float ANGLE_DEV = (float)(Math.PI / 9.0f);
		
		@Override
		public List<Particle> apply(Entity e, Long cTime) {
			if(e instanceof Enemy) {
				ColorGenerator colorGenerator = new ColorGenerator(new Color(0xAA490000), new Color(0xFF910000));
				Enemy enemy = (Enemy) e;
				List<Particle> particles = new ArrayList<Particle>();
				for(int i = 0; i < PARTICLE_COUNT; i++) {
					float velocity = (Globals.rand.nextFloat() * MAX_VELOCITY) + MIN_VELOCITY;
					float size = (Globals.rand.nextFloat() * MAX_SIZE) + MIN_SIZE;
					float theta = (enemy.getTheta() - (float)(Math.PI / 2));
					long lifespan = (long)((Globals.rand.nextFloat() * MAX_LIFESPAN) + MIN_LIFESPAN);
					
					theta += Globals.rand.nextFloat() * (ANGLE_DEV / 2) * (Globals.rand.nextBoolean() ? -1 : 1);
					
					Particle p = new Particle(colorGenerator, new Pair<Float>(enemy.getPosition()), velocity, 
											  theta, 0.0f, new Pair<Float>(size, size), lifespan, cTime);
					p.setDrawTime(lifespan + BLOOD_DRAW_TIME);
					particles.add(p);
				}
				
				return particles;
			}
			
			return null;
		}
	};
}
