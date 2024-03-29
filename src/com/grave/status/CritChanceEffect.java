package com.grave.status;

import org.newdawn.slick.Graphics;

import com.grave.entities.Entity;
import com.grave.entities.Player;
import com.grave.states.GameState;

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
		float critBonus = player.getAttributes().getFloat("critBonus");
		player.getAttributes().set("critBonus", (critBonus - CritChanceEffect.CRIT_CHANCE));
	}

}
