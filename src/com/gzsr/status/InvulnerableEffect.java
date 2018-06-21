package com.gzsr.status;

import org.newdawn.slick.Graphics;

import com.gzsr.entities.Entity;
import com.gzsr.states.GameState;

public class InvulnerableEffect extends StatusEffect {
	public InvulnerableEffect(long duration_, long created_) {
		super(Status.INVULNERABLE, duration_, created_);
	}
	
	@Override
	public void onApply(Entity e, long cTime) {
		
	}
	
	@Override
	public void handleEntity(Entity e, long cTime) {
		
	}

	@Override
	public void update(Entity e, GameState gs, long cTime, int delta) {
		// No need for update logic.
	}
	
	@Override
	public void render(Graphics g, long cTime) {
		
	}

	@Override
	public void onDestroy(Entity e, long cTime) {
		// No onDestroy effect required.
	}
}
