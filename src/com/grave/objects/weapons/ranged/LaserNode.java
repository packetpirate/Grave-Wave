package com.grave.objects.weapons.ranged;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Line;

import com.grave.entities.enemies.Enemy;
import com.grave.gfx.particles.Particle;
import com.grave.gfx.particles.Projectile;
import com.grave.states.GameState;

public class LaserNode extends Projectile {
	public static final Color BARRIER_COLOR = new Color(0xFFA0B9);
	public static final float BARRIER_WIDTH = 3.0f;
	private static final double DURABILITY_MAX = 10_000.0;

	private LaserNode other;
	private boolean host;
	public void setAsHost() { host = true; }
	private double durability;

	public LaserNode(Particle p) {
		super(p, 0.0, false);

		this.other = null;
		this.durability = LaserNode.DURABILITY_MAX;
	}

	@Override
	public void render(GameState gs, Graphics g, long cTime) {
		super.render(gs, g, cTime);

		// Render the barrier if this is the host node.
		if(host && (other != null)) {
			g.setColor(LaserNode.BARRIER_COLOR);
			g.setLineWidth(LaserNode.BARRIER_WIDTH);
			g.drawLine(position.x, position.y,
					   other.getPosition().x, other.getPosition().y);
			g.setLineWidth(1.0f);
		}
	}

	public void pair(LaserNode other_) {
		this.other = other_;
	}

	public void damage(double amnt) {
		durability -= amnt;
		if(host) other.damage(amnt);
	}

	@Override
	public boolean isAlive(long cTime) {
		return ((durability > 0) && (host ? other.isAlive(cTime) : true));
	}

	@Override
	public boolean checkCollision(Enemy enemy) {
		Line barrier = new Line(position.x, position.y,
								other.getPosition().x, other.getPosition().y);
		boolean barrierCollision = barrier.intersects(enemy.getCollider());
		return super.checkCollision(enemy) || barrierCollision;
	}
}
