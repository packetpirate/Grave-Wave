package com.gzsr.entities.enemies.bosses;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import com.gzsr.Globals;
import com.gzsr.entities.enemies.EnemyType;
import com.gzsr.gfx.particles.Projectile;
import com.gzsr.math.Calculate;
import com.gzsr.misc.Pair;
import com.gzsr.states.GameState;

public class Aberration extends Boss {
	private static final float HEALTH = 10_000.0f;
	private static final float SPEED = 0.10f;
	private static final float DPS = 20.0f;
	
	private List<Projectile> bile;
	
	public Aberration(Pair<Float> position_) {
		super(EnemyType.ABERRATION, position_);
		this.health = Aberration.HEALTH;
		
		this.bile = new ArrayList<Projectile>();
	}
	
	@Override
	public void update(GameState gs, long cTime, int delta) {
		// TODO: Implement Aberration AI
	}
	
	private void vomit(long cTime) {
		
	}
	
	@Override
	public void render(Graphics g, long cTime) {
		// Only render the Aberration until it dies.
		if(!dead()) animation.render(g, position, theta);
		// Even if Aberration is dead, render its particles until they all die.
		if(!bile.isEmpty()) bile.stream().filter(p -> p.isAlive(cTime)).forEach(p -> p.render(g, cTime));
		
		if(Globals.SHOW_COLLIDERS) {
			g.setColor(Color.red);
			g.draw(bounds);
		}
	}

	@Override
	public boolean isAlive(long cTime) {
		return (!dead() || !bile.isEmpty());
	}
	
	private boolean dead() {
		return (health <= 0);
	}

	@Override
	public void move(int delta) {
		theta = Calculate.Hypotenuse(position, Globals.player.getPosition());
		
		if(!moveBlocked) {
			position.x += (float)Math.cos(theta) * Aberration.SPEED * delta;
			position.y += (float)Math.sin(theta) * Aberration.SPEED * delta;
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
		return Aberration.DPS;
	}

}
