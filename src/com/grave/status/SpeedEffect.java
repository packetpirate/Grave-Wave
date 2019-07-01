package com.grave.status;

import java.util.List;

import org.newdawn.slick.Graphics;

import com.grave.entities.Entity;
import com.grave.entities.Player;
import com.grave.entities.enemies.Enemy;
import com.grave.entities.enemies.EnemyController;
import com.grave.states.GameState;
import com.grave.talents.Talents;

public class SpeedEffect extends StatusEffect {
	public static final double EFFECT = 2.0;

	public SpeedEffect(long duration_, long created_) {
		super(Status.SPEED_UP, duration_, created_);
	}
	
	@Override
	public void onApply(Entity e, long cTime) {
		// If the player has the Stasis talent, apply slow to all enemies.
		if(e instanceof Player) {
			if(Talents.Tactics.STASIS.active()) {
				List<Enemy> enemies = EnemyController.getInstance().getAliveEnemies();
				for(Enemy enemy : enemies) {
					SlowEffect slow = new SlowEffect(duration, created);
					enemy.getStatusHandler().addStatus(slow, cTime);
				}
			}
		}
	}
	
	@Override
	public void handleEntity(Entity e, long cTime) {
	}

	@Override
	public void update(Entity e, GameState gs, long cTime, int delta) {
		// Make sure to apply slow to any newly spawned enemies or enemies from new waves while effect persists.
		if(isActive(cTime)) {
			if(e instanceof Player) {
				if(Talents.Tactics.STASIS.active()) {
					List<Enemy> enemies = EnemyController.getInstance().getAliveEnemies();
					for(Enemy enemy : enemies) {
						if(!enemy.getStatusHandler().hasStatus(Status.SLOW_DOWN)) {
							SlowEffect slow = new SlowEffect(duration, created);
							enemy.getStatusHandler().addStatus(slow, cTime);
						}
					}
				}
			}
		}
	}
	
	@Override
	public void render(Graphics g, long cTime) {
	}

	@Override
	public void onDestroy(Entity e, long cTime) {
		if(e instanceof Player) {
			Player player = (Player) e;
			player.getAttributes().set("spdMult", 1.0);
		}
	}
}
