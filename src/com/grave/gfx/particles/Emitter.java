package com.grave.gfx.particles;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.BasicGameState;

import com.grave.entities.Entity;
import com.grave.gfx.Layers;
import com.grave.misc.Pair;
import com.grave.states.GameState;

public class Emitter implements Entity {
	protected List<Particle> particles;
	protected Pair<Float> position;
	public void setPosition(Pair<Float> position_) { this.position = position_; }
	protected Particle template;

	protected boolean emitting;
	public boolean isEmitting() { return emitting; }
	public void enable(long cTime) { emitting = true; }
	public void disable() { emitting = false; }

	protected long lifespan;
	protected long created;
	protected long lastEmission;
	protected long interval;
	public boolean canEmit(long cTime) {
		long elapsed = cTime - lastEmission;
		return (elapsed >= interval);
	}

	public Emitter(Pair<Float> position_, Particle template_, long lifespan_, long interval_, long cTime) {
		this.particles = new ArrayList<Particle>();
		this.position = position_;
		this.template = template_;

		this.emitting = false;

		this.lifespan = lifespan_;
		this.created = cTime;
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
	public void render(GameState gs, Graphics g, long cTime) {
		Iterator<Particle> it = particles.iterator();
		while(it.hasNext()) {
			Particle p = it.next();
			if(p.isAlive(cTime)) p.render(gs, g, cTime);
		}
	}

	public boolean isAlive(long cTime) {
		if(lifespan == -1L) return true;
		long elapsed = (cTime - created);
		return (elapsed < lifespan);
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
	public String getName() { return "Emitter"; }

	@Override
	public String getTag() { return "emitter"; }

	@Override
	public String getDescription() { return "Emits particles of a specified type."; }

	@Override
	public int getLayer() { return Layers.NONE.val(); }
}
