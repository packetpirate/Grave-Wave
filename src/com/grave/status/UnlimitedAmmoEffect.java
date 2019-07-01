package com.grave.status;

import org.newdawn.slick.Graphics;

import com.grave.entities.Entity;
import com.grave.states.GameState;

public class UnlimitedAmmoEffect extends StatusEffect {
	public UnlimitedAmmoEffect(long duration_, long created_) {
		super(Status.UNLIMITED_AMMO, duration_, created_);
	}

	@Override
	public void onApply(Entity e, long cTime) {
		
	}
	
	@Override
	public void handleEntity(Entity e, long cTime) {
		
	}
	
	@Override
	public void update(Entity e, GameState gs, long cTime, int delta) {
		// Not needed.
	}
	
	@Override
	public void render(Graphics g, long cTime) {
		
	}

	@Override
	public void onDestroy(Entity e, long cTime) {
		// Not needed.
	}
}
