package com.gzsr.status;

import org.newdawn.slick.Graphics;

import com.gzsr.entities.Entity;
import com.gzsr.gfx.ui.StatusMessages;
import com.gzsr.misc.Pair;
import com.gzsr.states.GameState;

public class ParalysisEffect extends StatusEffect {
	public ParalysisEffect(long duration_, long created_) {
		super(Status.PARALYSIS, duration_, created_);
	}

	@Override
	public void onApply(Entity e, long cTime) {
		StatusMessages.getInstance().addMessage("Paralyzed!", e, new Pair<Float>(0.0f, -32.0f), cTime, 2_000L);
	}

	@Override
	public void handleEntity(Entity e, long cTime) {
	}

	@Override
	public void update(Entity e, GameState gs, long cTime, int delta) {
	}

	@Override
	public void render(Graphics g, long cTime) {
	}

	@Override
	public void onDestroy(Entity e, long cTime) {
	}
}
