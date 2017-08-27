package com.gzsr.gfx.particles;

import com.gzsr.gfx.particles.Particle;

public class Projectile extends Particle {
	private double damage;
	public double getDamage() { return damage; }

	public Projectile(Particle p, double damage_) {
		super(p);
		this.damage = damage_;
	}
}
