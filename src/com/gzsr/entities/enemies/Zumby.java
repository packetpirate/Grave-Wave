package com.gzsr.entities.enemies;

import org.newdawn.slick.Graphics;

import com.gzsr.entities.Player;
import com.gzsr.math.Calculate;
import com.gzsr.misc.Pair;

public class Zumby extends Enemy {
	private static final float HEALTH = 100.0f;
	private static final float SPEED = 0.15f;
	private static final float DPS = 5.0f;
	
	public Zumby(Pair<Float> position_) {
		super(EnemyType.ZUMBY, position_);
		this.health = Zumby.HEALTH;
	}

	@Override
	public boolean isAlive(long cTime) {
		return (health > 0);
	}

	@Override
	public void move(Player player) {
		theta = Calculate.Hypotenuse(position, player.getPosition());
		position.x += (float)Math.cos(theta) * Zumby.SPEED;
		position.y += (float)Math.sin(theta) * Zumby.SPEED;
	}

	@Override
	public void takeDamage(double amnt) {
		health -= amnt;
	}
	
	@Override
	public double getDamage() {
		return Zumby.DPS;
	}

	@Override
	public boolean checkCollision(Pair<Float> p) {
		return (Calculate.Distance(p, position) <= animation.getSize());
	}
	
	@Override
	public void update(long cTime) {
		super.update(cTime);
	}
	
	@Override
	public void render(Graphics g, long cTime) {
		super.render(g, cTime);
	}
}
