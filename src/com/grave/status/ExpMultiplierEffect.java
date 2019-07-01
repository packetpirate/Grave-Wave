package com.grave.status;

import org.newdawn.slick.Graphics;

import com.grave.entities.Entity;
import com.grave.entities.Player;
import com.grave.states.GameState;

public class ExpMultiplierEffect extends StatusEffect {
	public ExpMultiplierEffect(long duration_, long created_) {
		super(Status.EXP_MULTIPLIER, duration_, created_);
	}
	
	@Override
	public void update(Entity e, GameState gs, long cTime, int delta) {
		// Unused
	}

	@Override
	public void render(Graphics g, long cTime) {
		// Unused
	}
	
	@Override
	public void onApply(Entity e, long cTime) {
		// Unused
	}

	@Override
	public void handleEntity(Entity e, long cTime) {
		// Unused
	}

	@Override
	public void onDestroy(Entity e, long cTime) {
		Player player = (Player) e;
		player.getAttributes().set("expMult", 1.0);
	}
}
