package com.grave.gfx.particles;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import com.grave.entities.Entity;
import com.grave.states.GameState;

public class Projectile extends Particle {
	private BiFunction<Entity, Long, List<Particle>> bloodGenerator;

	private double damage;
	public double getDamage() { return damage; }

	private boolean critical;
	public boolean isCritical() { return critical; }

	private List<Entity> penetrated; // giggity
	private int penetrations;
	public int getPenetrations() { return penetrations; }
	public void setPenetrations(int penetrations_) {
		this.penetrated = new ArrayList<Entity>();
		this.penetrations = penetrations_;
	}

	public Projectile(Particle p_, double damage_, boolean critical_) {
		this(p_, null, damage_, critical_);
	}

	public Projectile(Particle p_, BiFunction<Entity, Long, List<Particle>> bloodGenerator_, double damage_, boolean critical_) {
		super(p_);
		this.bloodGenerator = bloodGenerator_;
		this.damage = damage_;
		this.critical = critical_;

		this.penetrated = null;
		this.penetrations = 0;
	}

	@Override
	public boolean collide(GameState gs, Entity e, long cTime) {
		if(penetrated != null) {
			for(Entity ent : penetrated) {
				if(ent.equals(e)) return false;
			}

			if(penetrations == 0) collision = true;
			else {
				penetrated.add(e);
				penetrations--;
			}
		} else collision = true;

		if(bloodGenerator != null) {
			List<Particle> particles = bloodGenerator.apply(e, cTime);
			particles.stream().forEach(p -> gs.getLevel().addEntity("blood", p));
		}

		return true;
	}
}
