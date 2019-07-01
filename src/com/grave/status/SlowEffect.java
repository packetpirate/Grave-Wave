package com.grave.status;

import org.newdawn.slick.Graphics;

import com.grave.entities.Entity;
import com.grave.entities.Player;
import com.grave.entities.enemies.Enemy;
import com.grave.states.GameState;

public class SlowEffect extends StatusEffect {
	public static final float EFFECT = 0.5f;
	
	private float oSpeed;
	
	public SlowEffect(long duration_, long created_) {
		super(Status.SLOW_DOWN, duration_, created_);
		this.oSpeed = 0.0f;
	}
	
	@Override
	public void onApply(Entity e, long cTime) {
		if(e instanceof Player) {
			Player player = (Player) e;
			oSpeed = player.getSpeed();
			player.setSpeed(oSpeed * EFFECT);
		} else if(e instanceof Enemy) {
			Enemy enemy = (Enemy) e;
			oSpeed = enemy.getSpeed();
			enemy.setSpeed(oSpeed * EFFECT);
		}
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
		if(e instanceof Player) {
			Player player = (Player) e;
			player.setSpeed(oSpeed);
		} else if(e instanceof Enemy) {
			Enemy enemy = (Enemy) e;
			enemy.resetSpeed();
		}
	}
}
