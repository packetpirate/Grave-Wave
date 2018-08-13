package com.gzsr.status;

import org.newdawn.slick.Graphics;

import com.gzsr.entities.Entity;
import com.gzsr.entities.Player;
import com.gzsr.entities.enemies.Enemy;
import com.gzsr.gfx.ui.StatusMessages;
import com.gzsr.misc.Pair;
import com.gzsr.states.GameState;

public class ParalysisEffect extends StatusEffect {
	public ParalysisEffect(long duration_, long created_) {
		super(Status.PARALYSIS, duration_, created_);
	}
	
	@Override
	public void onApply(Entity e, long cTime) {
		Pair<Float> offset = new Pair<Float>(Player.ABOVE_1);
		if(e instanceof Enemy) {
			Enemy enemy = (Enemy) e;
			offset.y = -((float)enemy.getAnimation().getCurrentAnimation().getSrcSize().y);
		}
		
		StatusMessages.getInstance().addMessage("Paralyzed!", e, offset, cTime, 2_000L);
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
