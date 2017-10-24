package com.gzsr.entities.enemies;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.newdawn.slick.Graphics;

import com.gzsr.Globals;
import com.gzsr.entities.Entity;
import com.gzsr.entities.Player;
import com.gzsr.misc.Pair;
import com.gzsr.states.GameState;

public class EnemyController implements Entity {
	private static final long DEFAULT_SPAWN = 2000L;
	
	private List<Enemy> unborn;
	public void addUnborn(Enemy e) { unborn.add(e); }
	private List<Enemy> alive;
	public List<Enemy> getAliveEnemies() { return alive; }
	public void addAlive(Enemy e) { alive.add(e); }
	
	private long spawnRate;
	private long nextSpawn;
	private long lastEnemy;
	
	private int wave;
	public boolean waveClear() { return (unborn.isEmpty() && alive.isEmpty()); }
	
	public EnemyController() {
		unborn = new ArrayList<Enemy>();
		alive = new ArrayList<Enemy>();
		
		spawnRate = EnemyController.DEFAULT_SPAWN;
		nextSpawn = (long)(Globals.rand.nextFloat() * spawnRate);
		lastEnemy = 0L;
		
		wave = 10;
		
		restart();
	}
	
	/**
	 * Starts a new wave, spawning new enemies, and determines difficulty of wave.
	 */
	public void restart() {
		wave++;
		unborn.clear();
		alive.clear();
		
		// Determine number of zombies based on wave number.
		int eCount = 5 + (wave / 2);
		
		// Create unborn enemies according to eCount.
		for(int i = 0; i < eCount; i++) {
			int spawn = Globals.rand.nextInt(4); // Determine which side to spawn this enemy on.
			float x = 0.0f;
			float y = 0.0f;
			
			if(spawn == 0) {
				// Top Spawn
				x = Globals.rand.nextFloat() * Globals.WIDTH;
				y -= 48.0f;
			} else if(spawn == 1) {
				// Right Spawn
				x += Globals.WIDTH + 48.0f;
				y = Globals.rand.nextFloat() * Globals.HEIGHT;
			} else if(spawn == 2) {
				// Bottom Spawn
				x = Globals.rand.nextFloat() * Globals.WIDTH;
				y += Globals.HEIGHT + 48.0f;
			} else {
				// Left Spawn
				x -= 48.0f;
				y = Globals.rand.nextFloat() * Globals.HEIGHT;
			}
			
			// TODO: Have enemy type chosen randomly before spawning.
			Zumby z = new Zumby(new Pair<Float>(x, y));
			unborn.add(z);
		}
		
		nextSpawn = (long)(Globals.rand.nextFloat() * spawnRate);
	}

	@Override
	public void update(GameState gs, long cTime) {
		// If there are unborn enemies left and the spawn time has elapsed, spawn the next enemy.
		if(!unborn.isEmpty() && (cTime >= (lastEnemy + nextSpawn))) {
			lastEnemy = cTime;
			nextSpawn = (long)(Globals.rand.nextFloat() * spawnRate);
			
			Enemy e = unborn.remove(0);
			alive.add(e);
		}
	}
	
	public void updateEnemies(GameState gs, Player player, long cTime) {
		Iterator<Enemy> it = alive.iterator();
		while(it.hasNext()) {
			Enemy enemy = it.next();
			enemy.update(gs, cTime);
			
			// Check collisions with player projectiles, then see if the enemy is still alive.
			player.checkProjectiles(enemy, cTime);
			if(!enemy.isAlive(cTime)) {
				// If the enemy died, give the player experience and cash, and remove the enemy.
				player.addExperience(enemy.getExpValue());
				player.addIntAttribute("money", enemy.getCashValue());
				it.remove();
			}
			
			// Check if the player is touching the enemy.
			if(enemy.isAlive(cTime) && 
			   player.touchingEnemy(enemy)) {
				double damage = enemy.getDamage() / (1_000L / Globals.UPDATE_TIME);
				player.takeDamage(damage);
			}
		}
	}

	@Override
	public void render(Graphics g, long cTime) {
		Iterator<Enemy> it = alive.iterator();
		while(it.hasNext()) {
			Enemy e = it.next();
			e.render(g, cTime);
		}
	}
}
