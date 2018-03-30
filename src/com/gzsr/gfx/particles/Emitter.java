package com.gzsr.gfx.particles;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

import org.newdawn.slick.Graphics;

import com.gzsr.misc.Pair;
import com.gzsr.states.GameState;

public class Emitter {
	private List<Particle> particles;
	private Pair<Float> position;
	private Particle template;
	
	private boolean emitting;
	public boolean isEmitting() { return emitting; }
	public void enable() { emitting = true; }
	public void disable() { emitting = false; }
	
	public Emitter(Pair<Float> position_, Particle template_) {
		this.particles = new ArrayList<Particle>();
		this.position = position_;
		this.template = template_;
		
		this.emitting = false;
	}
	
	public void update(GameState gs, long cTime, int delta) {
		Iterator<Particle> it = particles.iterator();
		while(it.hasNext()) {
			Particle p = it.next();
			if(p.isAlive(cTime)) {
				p.update(gs, cTime, delta);
			} else it.remove();
		}
	}
	
	public void render(Graphics g, long cTime) {
		Iterator<Particle> it = particles.iterator();
		while(it.hasNext()) {
			Particle p = it.next();
			if(p.isAlive(cTime)) p.render(g, cTime);
		}
	}
	
	public void emit(int count, long cTime) {
		emit(count, cTime, null);
	}
	
	public void emit(int count, long cTime, Supplier<Float> deviator) {
		if(isEmitting()) {
			for(int i = 0; i < count; i++) {
				Particle p = new Particle(template);
				p.setCreated(cTime);
				p.setPosition(new Pair<Float>(position));
				p.resetBounds();
				
				if(deviator != null) {
					float theta = p.getTheta();
					p.setTheta(theta + deviator.get());
				}
				
				particles.add(p);
			}
		}
	}
}
