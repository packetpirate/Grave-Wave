package com.gzsr.status;

import org.newdawn.slick.Graphics;

import com.gzsr.entities.Entity;
import com.gzsr.entities.Player;
import com.gzsr.states.GameState;

public class PoisonEffect extends StatusEffect {
	private double damage;
	
	public PoisonEffect(double damage_, long duration_, long created_) {
		super(Status.POISON, duration_, created_);
		this.damage = damage_;
	}
	
	@Override
	public void onApply(Entity e, long cTime) {
		
	}
	
	@Override
	public void handleEntity(Entity e, long cTime) {
		
	}

	@Override
	public void update(Entity e, GameState gs, long cTime, int delta) {
		if(e instanceof Player) {
			Player player = (Player) e;
			if(isActive(cTime)) player.takeDamage(damage, cTime);
		}
	}
	
	@Override
	public void render(Graphics g, long cTime) {
		
	}

	@Override
	public void onDestroy(Entity e, long cTime) {
		// No logic needed.
	}
}
