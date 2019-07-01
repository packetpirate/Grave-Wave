package com.grave.objects.weapons.ranged;

import java.util.Iterator;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Line;
import org.newdawn.slick.state.BasicGameState;

import com.grave.entities.Player;
import com.grave.entities.enemies.Enemy;
import com.grave.entities.enemies.EnemyController;
import com.grave.gfx.effects.Lightning;
import com.grave.gfx.particles.Particle;
import com.grave.gfx.particles.Projectile;
import com.grave.math.Dice;
import com.grave.misc.Pair;
import com.grave.objects.weapons.DamageType;
import com.grave.states.GameState;
import com.grave.status.DamageEffect;
import com.grave.status.ParalysisEffect;

public class ENNode extends Projectile {
	private static final long FLY_TIME = 250L;
	private static final long DURATION = 3_000L;
	private static final long DAMAGE_INTERVAL = 500L;
	private static final float MAX_LIGHTNING_OFFSET = 15.0f;

	private static final Dice DAMAGE = new Dice(2, 4);
	private static final int DAMAGE_MOD = 2;

	private ENNode next;
	private boolean moving;
	private boolean electrified;

	private boolean started;
	public boolean hasStarted() { return started; }
	private long startTime;
	public void start(long cTime) {
		started = true;
		startTime = cTime;
	}

	public ENNode(Particle p) {
		super(p, 0.0, false);

		this.next = null;
		this.moving = true;
		this.electrified = false;
		this.started = false;
		this.startTime = 0L;
	}

	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
		if(moving) {
			super.update(gs, cTime, delta);
			long elapsed = (cTime - created);
			if(elapsed > FLY_TIME) moving = false;
			if(collision) {
				moving = false;
				started = true;
				startTime = cTime;
			}
		} else if(started) {
			if(!electrified && (next != null)) {
				Lightning lightning = new Lightning(new Pair<Float>(position), new Pair<Float>(next.getPosition()), MAX_LIGHTNING_OFFSET, DURATION, cTime);
				((GameState)gs).getLevel().addEntity("lightning", lightning);
				electrified = true;
			}

			EnemyController ec = EnemyController.getInstance();
			Iterator<Enemy> it = ec.getAliveEnemies().iterator();
			while(it.hasNext()) {
				Enemy e = it.next();
				if(checkCollision(e)) {
					DamageEffect dmg = new DamageEffect(DamageType.ELECTRIC, DAMAGE, DAMAGE_MOD, DAMAGE_INTERVAL, DURATION, cTime);
					ParalysisEffect para = new ParalysisEffect(DURATION, cTime);

					dmg.setCanRefresh(false);

					e.getStatusHandler().addStatus(dmg, cTime);
					e.getStatusHandler().addStatus(para, cTime);
				}
			}

			// Also check the player... electricity doesn't have friends. :(
			Player player = Player.getPlayer();
			if(checkCollision(player)) {
				DamageEffect dmg = new DamageEffect(DamageType.ELECTRIC, DAMAGE, DAMAGE_MOD, DAMAGE_INTERVAL, DURATION, cTime);
				ParalysisEffect para = new ParalysisEffect(DURATION, cTime);

				dmg.setCanRefresh(false);

				player.getStatusHandler().addStatus(dmg, cTime);
				player.getStatusHandler().addStatus(para, cTime);
			}
		} else {
			started = true;
			startTime = cTime;
		}
	}

	@Override
	public void render(Graphics g, long cTime) {
		if(started && (next != null)) {
			g.setColor(Lightning.ELECTRIC_BLUE);
			g.setLineWidth(3.0f);
			g.drawLine(position.x, position.y, next.getPosition().x, next.getPosition().y);
			g.setColor(Color.white);
			g.setLineWidth(1.0f);
			g.drawLine(position.x, position.y, next.getPosition().x, next.getPosition().y);
		}

		super.render(g, cTime);
	}

	public void pair(ENNode next_) { this.next = next_; }

	@Override
	public boolean isAlive(long cTime) {
		if(started) {
			long elapsed = (cTime - startTime);
			return (elapsed < DURATION);
		} else return true;
	}

	@Override
	public boolean checkCollision(Enemy enemy) {
		if(next != null) {
			Line barrier = new Line(position.x, position.y,
									next.getPosition().x, next.getPosition().y);
			boolean barrierCollision = barrier.intersects(enemy.getCollider());
			return super.checkCollision(enemy) || barrierCollision;
		} else return super.checkCollision(enemy);
	}

	@Override
	public boolean checkCollision(Player player) {
		if(next != null) {
			Line barrier = new Line(position.x, position.y,
									next.getPosition().x, next.getPosition().y);
			boolean barrierCollision = barrier.intersects(player.getCollider());
			return super.checkCollision(player) || barrierCollision;
		} else return super.checkCollision(player);
	}
}
