package com.gzsr.entities.enemies.bosses;

import org.newdawn.slick.Graphics;

import com.gzsr.Globals;
import com.gzsr.entities.enemies.EnemyType;
import com.gzsr.math.Calculate;
import com.gzsr.misc.Pair;
import com.gzsr.states.GameState;

public class Zombat extends Boss {
	private static final float HEALTH = 2_000.0f;
	private static final float SPEED = 0.3f;
	private static final float DPS = 10.0f;
	
	public Zombat(Pair<Float> position_) {
		super(EnemyType.ZOMBAT_SWARM, position_);
		this.health = Zombat.HEALTH;
	}
	
	@Override
	public void update(GameState gs, long cTime, int delta) {
		// TODO: Implement Zombat AI
	}
	
	@Override
	public void render(Graphics g, long cTime) {
		// TODO: Do normal rendering and then also render the blood stream being siphoned from the player.
	}

	@Override
	public boolean isAlive(long cTime) {
		return (health > 0);
	}

	@Override
	public void move(int delta) {
		theta = Calculate.Hypotenuse(position, Globals.player.getPosition());
		
		if(!moveBlocked) {
			position.x += (float)Math.cos(theta) * Zombat.SPEED * delta;
			position.y += (float)Math.sin(theta) * Zombat.SPEED * delta;
		}
		
		moveBlocked = false;
		
		bounds.setCenterX(position.x);
		bounds.setCenterY(position.y);
	}

	@Override
	public void takeDamage(double amnt) {
		health -= amnt;
	}

	@Override
	public double getDamage() {
		return Zombat.DPS;
	}

}
