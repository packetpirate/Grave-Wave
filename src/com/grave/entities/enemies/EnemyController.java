package com.gzsr.entities.enemies;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.BasicGameState;

import com.gzsr.Globals;
import com.gzsr.achievements.Metric;
import com.gzsr.achievements.Metrics;
import com.gzsr.controllers.AchievementController;
import com.gzsr.controllers.Scorekeeper;
import com.gzsr.entities.Entity;
import com.gzsr.entities.Player;
import com.gzsr.entities.enemies.bosses.Aberration;
import com.gzsr.entities.enemies.bosses.Stitches;
import com.gzsr.entities.enemies.bosses.Zombat;
import com.gzsr.gfx.Layers;
import com.gzsr.misc.Pair;
import com.gzsr.objects.items.Armor;
import com.gzsr.states.GameState;
import com.gzsr.states.ShopState;
import com.gzsr.talents.Talents;

public class EnemyController implements Entity {
	private static final int SPAWN_POOL_START = 5;
	private static final long DEFAULT_SPAWN = 2_000L;
	private static final long MIN_SPAWN_RATE = 500L;
	private static final long WAVE_BREAK_TIME = 15_000L;

	@SuppressWarnings("serial")
	private static final List<EnemyType> SPAWNABLE_NAMES = new ArrayList<EnemyType>() {{
		// Normal Enemies
		add(EnemyType.ZUMBY);
		add(EnemyType.ROTDOG);
		add(EnemyType.CHUCK);
		add(EnemyType.GASBAG);
		add(EnemyType.BIG_MAMA);
		add(EnemyType.STARFRIGHT);
		add(EnemyType.ELSALVO);
		add(EnemyType.PROWLER);
		add(EnemyType.GLORP);
	}};

	private static EnemyController instance;
	public static EnemyController getInstance() {
		if(instance == null) instance = new EnemyController();
		return instance;
	}

	private List<Enemy> addImmediately;
	public List<Enemy> getImmediateEnemies() { return addImmediately; }
	public void addNextUpdate(Enemy e) { addImmediately.add(e); }
	private List<Enemy> alive;
	public List<Enemy> getAliveEnemies() { return alive; }
	public void addAlive(Enemy e) { alive.add(e); }

	private Queue<EnemyType> spawnQueue;

	private int spawnPool;
	private long spawnRate;
	private long nextSpawn;
	private long lastEnemy;

	private int wave;
	public int getWave() { return wave; }
	public void setWave(int wave_, long time) {
		this.wave = wave_ - 1;
		restart(time);
	}

	private boolean bossWave;
	public boolean isBossWave() { return bossWave; }
	private String bossTitle;
	public String getBossTitle() { return bossTitle; }
	private double bossWaveHealth;
	public double getBossWaveHealth() { return bossWaveHealth; }

	private long lastWave;
	private boolean breakTime;
	public boolean waveClear() { return (spawnQueue.isEmpty() && addImmediately.isEmpty() && alive.isEmpty()); }
	public boolean isRestarting() { return breakTime; }
	public void skipToNextWave() { breakTime = false; }
	public int timeToNextWave(long cTime) {
		long elapsed = cTime - lastWave;
		return (int)((EnemyController.WAVE_BREAK_TIME / 1000) - (elapsed / 1000));
	}

	private EnemyController() {
		addImmediately = new ArrayList<Enemy>();
		alive = new ArrayList<Enemy>();

		spawnQueue = new LinkedList<EnemyType>();

		spawnPool = EnemyController.SPAWN_POOL_START;
		spawnRate = EnemyController.DEFAULT_SPAWN;
		nextSpawn = (long)(Globals.rand.nextFloat() * spawnRate);
		lastEnemy = 0L;

		wave = 0;

		bossWave = false;
		bossTitle = "";
		bossWaveHealth = 0.0;

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

		bossWave = false;
		bossTitle = "";
		bossWaveHealth = 0.0;

		addImmediately.clear();
		alive.clear();

		spawnQueue.clear();

		spawnRate = (DEFAULT_SPAWN - (long)((Math.log(wave) / Math.log(8)) * 1000.0));
		if(spawnRate < MIN_SPAWN_RATE) spawnRate = MIN_SPAWN_RATE;

		// TODO: Need to rewrite this to accomodate new system.
		Metric metric = new Metric(Metrics.WAVE_START);

		if((wave % Stitches.appearsOnWave()) == 0) {
			Pair<Float> spawnPos = getSpawnPosition();
			Stitches st = new Stitches(spawnPos);
			addImmediately.add(st);

			bossWave = true;
			bossTitle = "Stitches";
			bossWaveHealth = st.getHealth();

			metric = metric.add(Metrics.STITCHES);
		} else if((wave % Zombat.appearsOnWave()) == 0) {
			for(int i = 0; i < 3; i++) {
				Pair<Float> spawnPos = getSpawnPosition();
				Zombat zb = new Zombat(spawnPos);
				addImmediately.add(zb);

				bossWaveHealth += zb.getHealth();
			}

			bossWave = true;
			bossTitle = "Zombats";

			metric = metric.add(Metrics.ZOMBAT);
		} else if((wave % Aberration.appearsOnWave()) == 0) {
			Pair<Float> spawnPos = getSpawnPosition();
			Aberration ab = new Aberration(spawnPos);
			addImmediately.add(ab);

			bossWave = true;
			bossTitle = "Aberration";
			bossWaveHealth = ab.getHealth();

			metric = metric.add(Metrics.ABERRATION);
		} else {
			// Determine number of zombies based on wave number.
			spawnPool = (int)((Math.log(wave) / Math.log(2)) * wave) + EnemyController.SPAWN_POOL_START;

			while(spawnPool > 0) { spawnQueue.add(randomEnemy()); }
		}

		// Shop updates? Determine if certain items should be added at this point?
		float armorChance = Globals.rand.nextFloat(); // All we really need for now.
		if(armorChance <= Armor.SHOP_SPAWN_CHANCE) ShopState.getShop().addItem(new Armor(Armor.Type.randomType(), Pair.ZERO, 0L));

		nextSpawn = (long)(Globals.rand.nextFloat() * spawnRate);

		AchievementController.getInstance().postMetric(metric);
		Scorekeeper.getInstance().waveCleared();
	}

	private Pair<Float> getSpawnPosition() {
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

		return new Pair<Float>(x, y);
	}

	private EnemyType randomEnemy() {
		EnemyType [] filteredSpawnables = EnemyController.SPAWNABLE_NAMES
				 		.stream()
				 		.filter(type -> ((EnemyType.spawnCost(type) <= spawnPool) && (wave >= EnemyType.appearsOnWave(type))))
				 		.toArray(EnemyType[]::new);

		int i = Globals.rand.nextInt(filteredSpawnables.length);
		EnemyType type = filteredSpawnables[i];
		spawnPool -= EnemyType.spawnCost(type);

		return type;
	}

	private Enemy spawnEnemy(Pair<Float> position) {
		EnemyType toSpawn = spawnQueue.poll();
		return EnemyType.createInstance(toSpawn, position);
	}

	public static void reset() {
		instance = new EnemyController();
	}

	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
		// If there are enemies left to spawn and the spawn time has elapsed, spawn the next enemy.
		if(!breakTime) {
			if(addImmediately.size() > 0) alive.addAll(addImmediately);
			addImmediately.clear();

			long elapsed = (cTime - lastEnemy);
			if(!spawnQueue.isEmpty() && (elapsed > nextSpawn)) {
				lastEnemy = cTime;
				nextSpawn = (long)(Globals.rand.nextFloat() * spawnRate);

				Enemy e = spawnEnemy(getSpawnPosition());
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
			player.checkWeapons(gs, enemy, cTime, delta);

			if(Player.getPlayer().isAlive()) {
				if(enemy.dead()) enemy.onDeath(gs, cTime);
				if(!enemy.isAlive(cTime)) {
					// If the enemy died, give the player experience and cash, and remove the enemy.
					player.addExperience(gs, enemy.getExpValue(), cTime);

					int money = enemy.getCashValue();
					if(Talents.Tactics.WINDFALL.active()) money += (money * (Talents.Tactics.WINDFALL.ranks() * 0.1));
					player.addMoney(money);

					it.remove();
				}
			}
		}

		if(waveClear()) {
			AchievementController.getInstance().postMetric(Metrics.WAVE_END);
			breakTime = true;
			lastWave = cTime;
			restart(cTime + EnemyController.WAVE_BREAK_TIME);
		}
	}

	@Override
	public void render(Graphics g, long cTime) {
		if(!isRestarting()) {
			// Render all living enemies.
			Iterator<Enemy> it = alive.iterator();
			while(it.hasNext()) {
				Enemy e = it.next();
				e.render(g, cTime);
			}
		}
	}

	@Override
	public String getName() {
		return "Enemy Controller";
	}

	@Override
	public String getTag() {
		return "enemyController";
	}

	@Override
	public String getDescription() {
		return "Enemy Controller";
	}

	@Override
	public int getLayer() {
		return Layers.ENEMIES.val();
	}
}
