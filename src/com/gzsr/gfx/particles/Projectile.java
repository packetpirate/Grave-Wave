package com.gzsr.gfx.particles;

import com.gzsr.entities.Player;

public class Projectile extends Particle {
	private double damage;
	public double getDamage() { 
		if(isCritical()) return (damage * Player.getPlayer().getAttributes().getDouble("critMult"));
		return damage;
	}

	private boolean critical;
	public boolean isCritical() { return critical; }
	
	public Projectile(Particle p, double damage_, boolean critical_) {
		super(p);
		this.damage = damage_;
		this.critical = critical_;
	}
}
