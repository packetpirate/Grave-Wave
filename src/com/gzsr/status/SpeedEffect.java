package com.gzsr.status;

import org.newdawn.slick.Graphics;

import com.gzsr.entities.Entity;
import com.gzsr.entities.Player;
import com.gzsr.states.GameState;

public class SpeedEffect extends StatusEffect {
	public static final double EFFECT = 2.0;

	public SpeedEffect(long duration_, long created_) {
		super(Status.SPEED_UP, duration_, created_);
	}
	
	@Override
	public void onApply(Entity e, long cTime) {
		
	}
	
	@Override
	public void handleEntity(Entity e, long cTime) {
		
	}

	@Override
	public void update(Entity e, GameState gs, long cTime, int delta) {
		// No need for update logic here.
	}
	
	@Override
	public void render(Graphics g, long cTime) {
		
	}

	@Override
	public void onDestroy(Entity e, long cTime) {
		if(e instanceof Player) {
			Player player = (Player) e;
			player.setAttribute("spdMult", 1.0);
		}
	}
}
