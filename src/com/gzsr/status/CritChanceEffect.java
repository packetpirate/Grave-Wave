package com.gzsr.status;

import org.newdawn.slick.Graphics;

import com.gzsr.entities.Entity;
import com.gzsr.entities.Player;
import com.gzsr.states.GameState;

public class CritChanceEffect extends StatusEffect {
	public static final float CRIT_CHANCE = 0.25f; // How much this power up adds to the player's crit chance.
	
	public CritChanceEffect(long duration_, long created_) {
		super(Status.CRIT_CHANCE, duration_, created_);
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
		// Subtract the multiplier amount from the player's current multiplier.
		Player player = (Player) e;
		float critChance = player.getAttributes().getFloat("critChance");
		player.getAttributes().set("critChance", (critChance - CritChanceEffect.CRIT_CHANCE));
	}

}
