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
import com.gzsr.entities.enemies.bosses.Aberration;
import com.gzsr.entities.enemies.bosses.Stitches;
import com.gzsr.entities.enemies.bosses.Zombat;
import com.gzsr.misc.Pair;
import com.gzsr.states.GameState;

public class EnemyController implements Entity {
	private static final int SPAWN_POOL_START = 5;
	private static final long DEFAULT_SPAWN = 2000L;
	private static final long WAVE_BREAK_TIME = 10_000L;
	private static final TrueTypeFont FONT_NORMAL = new TrueTypeFont(new Font("Lucida Console", Font.PLAIN, 32), true);
	
	private static final List<String> SPAWNABLE_NAMES = new ArrayList<String>() {{
		// Normal Enemies
		add("Zumby");
		add("Rotdog");
		add("Upchuck");
		add("Gasbag");
		add("BigMama");
		
		// Bosses
		add("Aberration");
		add("Zombat");
		add("Stitches");
	}};
	
	private List<Enemy> unborn;
	public List<Enemy> getUnbornEnemies() { return unborn; }
	public void addUnborn(Enemy e) { unborn.add(e); }
	private List<Enemy> addImmediately;
	public void addNextUpdate(Enemy e) { addImmediately.add(e); }
	private List<Enemy> alive;
	public List<Enemy> getAliveEnemies() { return alive; }
	public void addAlive(Enemy e) { alive.add(e); }
	
	private int spawnPool;
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
		
		spawnPool = EnemyController.SPAWN_POOL_START;
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
		updateSpawnPool();
		
		// Create unborn enemies according to spawn pool.
		while(spawnPool > 0) {
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
			
			spawnEnemy(new Pair<Float>(x, y));
		}
		
		nextSpawn = (long)(Globals.rand.nextFloat() * spawnRate);
	}
	
	private void updateSpawnPool() {
		// Determine spawn pool for next wave.
		spawnPool = (int)((Math.log(wave) / Math.log(2)) * wave) + EnemyController.SPAWN_POOL_START;
		System.out.println("INFO: Spawn Pool = " + spawnPool);
	}
	
	private void spawnEnemy(Pair<Float> position) {
		// Which enemies can still be spawned with our current spawning pool?
		String [] filteredSpawnables = EnemyController.SPAWNABLE_NAMES.stream()
													  .filter(name -> ((getCostByName(name) <= spawnPool) && canSpawnThisWave(name)))
													  .toArray(String[]::new);
		// Choose a random enemy from the filtered list.
		int i = Globals.rand.nextInt(filteredSpawnables.length);
		
		// Spawn the enemy and deduct their cost from the spawning pool.
		String toSpawn = filteredSpawnables[i];
		if(toSpawn.equals("Zumby")) {
			Zumby z = new Zumby(position);
			spawnPool -= Zumby.getSpawnCost();
			unborn.add(z);
		} else if(toSpawn.equals("Rotdog")) {
			Rotdog r = new Rotdog(position);
			spawnPool -= Rotdog.getSpawnCost();
			unborn.add(r);
		} else if(toSpawn.equals("Upchuck")) {
			Upchuck u = new Upchuck(position);
			spawnPool -= Upchuck.getSpawnCost();
			unborn.add(u);
		} else if(toSpawn.equals("Gasbag")) {
			Gasbag g = new Gasbag(position);
			spawnPool -= Gasbag.getSpawnCost();
			unborn.add(g);
		} else if(toSpawn.equals("BigMama")) {
			BigMama b = new BigMama(position);
			spawnPool -= BigMama.getSpawnCost();
			unborn.add(b);
		} else if(toSpawn.equals("Aberration")) {
			Aberration a = new Aberration(position);
			spawnPool -= Aberration.getSpawnCost();
			unborn.add(a);
		} else if(toSpawn.equals("Zombat")) {
			Zombat z = new Zombat(position);
			spawnPool -= Zombat.getSpawnCost();
			unborn.add(z);
		} else if(toSpawn.equals("Stitches")) {
			Stitches s = new Stitches(position);
			spawnPool -= Stitches.getSpawnCost();
			unborn.add(s);
		}
	}
	
	private boolean canSpawnThisWave(String name) {
		int appearsOn = -1;
		switch(name) {
			case "Zumby": 
				appearsOn = Zumby.appearsOnWave();
				break;
			case "Rotdog": 
				appearsOn = Rotdog.appearsOnWave();
				break;
			case "Upchuck": 
				appearsOn = Upchuck.appearsOnWave();
				break;
			case "Gasbag": 
				appearsOn = Gasbag.appearsOnWave();
				break;
			case "BigMama": 
				appearsOn = BigMama.appearsOnWave();
				break;
			case "Aberration": 
				appearsOn = Aberration.appearsOnWave();
				break;
			case "Zombat": 
				appearsOn = Zombat.appearsOnWave();
				break;
			case "Stitches": 
				appearsOn = Stitches.appearsOnWave();
				break;
			default: 
				appearsOn = 1;
				break;
		}
		
		return (wave >= appearsOn);
	}
	
	private int getCostByName(String name) {
		switch(name) {
			case "Zumby": return Zumby.getSpawnCost();
			case "Rotdog": return Rotdog.getSpawnCost();
			case "Upchuck": return Upchuck.getSpawnCost();
			case "Gasbag": return Gasbag.getSpawnCost();
			case "BigMama": return BigMama.getSpawnCost();
			case "Aberration": return Aberration.getSpawnCost();
			case "Zombat": return Zombat.getSpawnCost();
			case "Stitches": return Stitches.getSpawnCost();
			default: return 1;
		}
	}

	@Override
	public void update(GameState gs, long cTime, int delta) {
		// Add all enemies that need to be immediately added.
		if(addImmediately.size() > 0) alive.addAll(addImmediately);
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
			
			// Check collisions with player projectiles, then see if the enemy is still alive.
			player.checkProjectiles(enemy, cTime);
			
			if(Globals.player.isAlive()) {
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
