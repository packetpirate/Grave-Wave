package com.gzsr.entities.enemies;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.util.FontUtils;

import com.gzsr.AssetManager;
import com.gzsr.Globals;
import com.gzsr.entities.Entity;
import com.gzsr.entities.Player;
import com.gzsr.misc.Pair;
import com.gzsr.states.GameState;

public class EnemyController implements Entity {
	private static final long DEFAULT_SPAWN = 2000L;
	private static final long WAVE_BREAK_TIME = 10_000L;
	private static final TrueTypeFont FONT_NORMAL = new TrueTypeFont(new Font("Lucida Console", Font.PLAIN, 32), true);
	
	private List<Enemy> unborn;
	public List<Enemy> getUnbornEnemies() { return unborn; }
	public void addUnborn(Enemy e) { unborn.add(e); }
	private List<Enemy> addImmediately;
	public void addNextUpdate(Enemy e) { addImmediately.add(e); }
	private List<Enemy> alive;
	public List<Enemy> getAliveEnemies() { return alive; }
	public void addAlive(Enemy e) { alive.add(e); }
	
	private long spawnRate;
	private long nextSpawn;
	private long lastEnemy;
	
	private int wave;
	private long lastWave;
	private boolean breakTime;
	public boolean waveClear() { return (unborn.isEmpty() && alive.isEmpty()); }
	public boolean isRestarting() { return breakTime; }
	
	public EnemyController() {
		unborn = new ArrayList<Enemy>();
		addImmediately = new ArrayList<Enemy>();
		alive = new ArrayList<Enemy>();
		
		spawnRate = EnemyController.DEFAULT_SPAWN;
		nextSpawn = (long)(Globals.rand.nextFloat() * spawnRate);
		lastEnemy = 0L;
		
		wave = 0;
		lastWave = 0L;
		breakTime = false;
		
		restart(0L);
	}
	
	/**
	 * Starts a new wave, spawning new enemies, and determines difficulty of wave.
	 * @param startTime The time this wave will start. Used for special-case enemy constructors.
	 */
	public void restart(long startTime) {
		wave++;
		unborn.clear();
		addImmediately.clear();
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
	public void update(GameState gs, long cTime, int delta) {
		// Add all enemies that need to be immediately added.
		alive.addAll(addImmediately);
		addImmediately.clear();
		
		// If there are unborn enemies left and the spawn time has elapsed, spawn the next enemy.
		if(!breakTime) {
			if(!unborn.isEmpty() && (cTime >= (lastEnemy + nextSpawn))) {
				lastEnemy = cTime;
				nextSpawn = (long)(Globals.rand.nextFloat() * spawnRate);
				
				Enemy e = unborn.remove(0);
				alive.add(e);
			}
		} else {
			long elapsed = cTime - lastWave;
			if(elapsed >= EnemyController.WAVE_BREAK_TIME) breakTime = false;
		}
	}
	
	public void updateEnemies(GameState gs, Player player, long cTime, int delta) {
		Iterator<Enemy> it = alive.iterator();
		while(it.hasNext()) {
			Enemy enemy = it.next();
			enemy.update(gs, cTime, delta);
			
			if(Globals.player.isAlive()) {
				// Check collisions with player projectiles, then see if the enemy is still alive.
				player.checkProjectiles(enemy, cTime);
				if(!enemy.isAlive(cTime)) {
					// If the enemy died, give the player experience and cash, and remove the enemy.
					player.addExperience(enemy.getExpValue());
					player.addIntAttribute("money", enemy.getCashValue());
					enemy.onDeath(gs, cTime);
					it.remove();
				}
				
				// Check if the player is touching the enemy.
				if(enemy.isAlive(cTime) && 
				   player.touchingEnemy(enemy)) {
					double damage = enemy.getDamage() / (1_000L / Globals.STEP_TIME);
					player.takeDamage(damage);
				}
			}
		}
		
		if(waveClear()) {
			breakTime = true;
			lastWave = cTime;
			restart(cTime + EnemyController.WAVE_BREAK_TIME);
		}
	}

	@Override
	public void render(Graphics g, long cTime) {
		UnicodeFont f = AssetManager.getManager().getFont("PressStart2P-Regular_large");
		if(breakTime) {
			// Render the countdown to the next wave.
			long elapsed = cTime - lastWave;
			int time = (int)((EnemyController.WAVE_BREAK_TIME / 1000) - (elapsed / 1000));
			String text = String.format("Next Wave: %d", time);
			int w = f.getWidth(text);
			
			g.setColor(Color.white);
			FontUtils.drawCenter(f, text, (Globals.WIDTH - 20 - w), 20, w);
		} else {
			// Render all living enemies.
			Iterator<Enemy> it = alive.iterator();
			while(it.hasNext()) {
				Enemy e = it.next();
				e.render(g, cTime);
			}
			
			// Render the wave counter.
			String text = String.format("Wave: %d", wave);
			int w = f.getWidth(text);
			
			g.setColor(Color.white);
			FontUtils.drawCenter(f, text, (Globals.WIDTH - 20 - w), 20, w);
			
		}
	}
	
	@Override
	public String getName() {
		return "Enemy Controller";
	}
	
	@Override
	public String getDescription() {
		return "Enemy Controller";
	}
}
