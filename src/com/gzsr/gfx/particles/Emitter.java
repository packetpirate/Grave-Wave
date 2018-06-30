package com.gzsr.gfx.particles;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.BasicGameState;

import com.gzsr.entities.Entity;
import com.gzsr.misc.Pair;

public class Emitter implements Entity {
	private List<Particle> particles;
	private Pair<Float> position;
	public void setPosition(Pair<Float> position_) { this.position = position_; }
	private Particle template;
	
	private boolean emitting;
	public boolean isEmitting() { return emitting; }
	public void enable(long cTime) { emitting = true; }
	public void disable() { emitting = false; }
	
	private long lastEmission;
	private long interval;
	public boolean canEmit(long cTime) {
		long elapsed = cTime - lastEmission;
		return (elapsed >= interval);
	}
	
	public Emitter(Pair<Float> position_, Particle template_, long interval_) {
		this.particles = new ArrayList<Particle>();
		this.position = position_;
		this.template = template_;
		
		this.emitting = false;
		this.lastEmission = 0L;
		this.interval = interval_;
	}
	
	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
		Iterator<Particle> it = particles.iterator();
		while(it.hasNext()) {
			Particle p = it.next();
			if(p.isAlive(cTime)) {
				p.update(gs, cTime, delta);
			} else it.remove();
		}
		
		// If the emitter is enabled and the interval has passed, emit a particle.
		if(isEmitting() && canEmit(cTime)) emit(cTime);
	}
	
	@Override
	public void render(Graphics g, long cTime) {
		Iterator<Particle> it = particles.iterator();
		while(it.hasNext()) {
			Particle p = it.next();
			if(p.isAlive(cTime)) p.render(g, cTime);
		}
	}
	
	public void emit(long cTime) {
		emit(1, cTime, null);
	}
	
	public void emit(int count, long cTime) {
		emit(count, cTime, null);
	}
	
	public void emit(int count, long cTime, Supplier<Float> deviator) {
		if(isEmitting()) {
			for(int i = 0; i < count; i++) {
				Particle p = new Particle(template);
				Pair<Float> transformed = transform(position);
				p.setCreated(cTime);
				p.setPosition(new Pair<Float>(transformed));
				p.resetBounds();
				
				if(deviator != null) {
					float theta = p.getTheta();
					p.setTheta(theta + deviator.get());
				}
				
				particles.add(p);
			}
			
			lastEmission = cTime;
		}
	}
	
	protected Pair<Float> transform(Pair<Float> start) {
		return start; // by default, no transformation
	}
	
	@Override
	public String getName() {
		return "Emitter";
	}
	
	@Override
	public String getDescription() {
		return "Emits particles of a specified type.";
	}
}
