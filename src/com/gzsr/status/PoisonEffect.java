package com.gzsr.status;

import com.gzsr.entities.Entity;
import com.gzsr.entities.Player;

public class PoisonEffect extends StatusEffect {
	private double damage;
	
	public PoisonEffect(double damage_, long duration_, long created_) {
		super(Status.POISON, duration_, created_);
		this.damage = damage_;
	}

	@Override
	public void update(Entity e, long cTime) {
		if(e instanceof Player) {
			Player player = (Player) e;
			if(isActive(cTime)) player.takeDamage(damage);
		}
	}

	@Override
	public void onDestroy(Entity e, long cTime) {
		// No logic needed.
	}
}
