package com.grave.gfx.effects;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.BasicGameState;

import com.grave.entities.Entity;
import com.grave.gfx.Layers;
import com.grave.states.GameState;

public class VisualEffect implements Entity {
	protected long lifespan, created;

	public VisualEffect(long lifespan_, long cTime_) {
		this.lifespan = lifespan_;
		this.created = cTime_;
	}

	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
		// Override me!
	}

	@Override
	public void render(GameState gs, Graphics g, long cTime) {
		// Override me!
	}

	public boolean isActive(long cTime) {
		long elapsed = (cTime - created);
		return (elapsed < lifespan);
	}

	@Override
	public String getName() { return "Visual Effect"; }

	@Override
	public String getTag() { return "effect"; }

	@Override
	public String getDescription() { return "Visual Effect"; }

	@Override
	public int getLayer() { return Layers.NONE.val(); }
}
