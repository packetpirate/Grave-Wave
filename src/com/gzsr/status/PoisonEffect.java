package com.gzsr.status;

import com.gzsr.entities.Player;

public class PoisonEffect extends StatusEffect {
	private double damage;
	
	public PoisonEffect(double damage_, long duration_, long created_) {
		super(Status.POISON, duration_, created_);
		this.damage = damage_;
	}

	@Override
	public void update(Player player, long cTime) {
		if(isActive(cTime)) player.takeDamage(damage);
	}

	@Override
	public void onDestroy(Player player, long cTime) {
		// No logic needed.
	}
}
