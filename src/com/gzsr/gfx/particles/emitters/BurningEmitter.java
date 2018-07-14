package com.gzsr.gfx.particles.emitters;

import com.gzsr.AssetManager;
import com.gzsr.Globals;
import com.gzsr.gfx.particles.Emitter;
import com.gzsr.gfx.particles.Particle;
import com.gzsr.misc.Pair;

public class BurningEmitter extends Emitter {
	private static final float VELOCITY = 0.2f;
	private static final Pair<Float> SIZE = new Pair<Float>(8.0f, 8.0f);
	
	private float burnRadius;
	public void setBurnRadius(float burnRadius_) { this.burnRadius = burnRadius_; }
	
	public BurningEmitter(Pair<Float> position_) {
		super(position_, BurningEmitter.generateTemplate(), 25L);
		this.burnRadius = 0.0f;
	}
	
	private static Particle generateTemplate() {
		return new Particle(AssetManager.getManager().getAnimation("GZS_FireAnimation1"), Pair.ZERO, VELOCITY, 0.0f, 0.0f, SIZE, 500L, 0L);
	}
	
	@Override
	protected Pair<Float> transform(Pair<Float> start) {
		Pair<Float> end = new Pair<Float>(start.x, start.y);
		
		double tx = Globals.rand.nextDouble() * (Math.PI * 2);
		double ty = Globals.rand.nextDouble() * (Math.PI * 2);
		
		float dx = Globals.rand.nextFloat() * burnRadius;
		float dy = Globals.rand.nextFloat() * burnRadius;
		
		end.x += (float)Math.cos(tx) * dx;
		end.y += (float)Math.sin(ty) * dy;
		
		return end;
	}
}
