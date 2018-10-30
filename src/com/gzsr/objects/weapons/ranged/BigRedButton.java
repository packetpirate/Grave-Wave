package com.gzsr.objects.weapons.ranged;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.state.BasicGameState;

import com.gzsr.AssetManager;
import com.gzsr.Globals;
import com.gzsr.achievements.Metrics;
import com.gzsr.entities.Player;
import com.gzsr.entities.enemies.Enemy;
import com.gzsr.entities.enemies.EnemyController;
import com.gzsr.gfx.Camera;
import com.gzsr.gfx.particles.ProjectileType;
import com.gzsr.math.Calculate;
import com.gzsr.math.Dice;
import com.gzsr.misc.Pair;
import com.gzsr.objects.weapons.Explosion;
import com.gzsr.states.GameState;

public class BigRedButton extends RangedWeapon {
	private static final int PRICE = 20_000;
	private static final int AMMO_PRICE = 10_000;
	private static final long COOLDOWN = 15_000L;
	private static final int CLIP_SIZE = 1;
	private static final int START_CLIPS = 1;
	private static final int MAX_CLIPS = 2;
	private static final long RELOAD_TIME = 10_000L;
	private static final float KNOCKBACK = 10.0f;
	private static final float EXP_RADIUS = 150.0f;
	private static final long EXP_DELAY = 500L;
	private static final int EXP_COUNT = 5;
	private static final String ICON_NAME = "GZS_BigRedButton";
	private static final String EXP_NAME = "GZS_Explosion";
	private static final String FIRE_SOUND = "landmine_armed";
	private static final String EXP_SOUND = "explosion2";
	private static final String RELOAD_SOUND = "buy_ammo2";
	
	private static final Dice DAMAGE = new Dice(5, 10);
	private static final int DAMAGE_MOD = 50;
	
	private Queue<Explosion> explosions;
	private long lastExplosion;
	
	public BigRedButton() {
		super(Size.LARGE);
		
		explosions = new LinkedList<Explosion>();
		lastExplosion = 0L;
		
		AssetManager assets = AssetManager.getManager();
		
		this.useSound = assets.getSound(BigRedButton.FIRE_SOUND);
		this.reloadSound = assets.getSound(BigRedButton.RELOAD_SOUND);
	}
	
	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
		// Basically just checking to see if the reload time has elapsed.
		super.update(gs, cTime, delta);
		
		long elapsed = cTime - lastExplosion;
		if(!explosions.isEmpty() && (elapsed >= BigRedButton.EXP_DELAY)) {
			int id = Globals.generateEntityID();
			Explosion exp = explosions.remove();
			exp.setPosition(getExplosionLocation((GameState)gs, Player.getPlayer(), Player.getPlayer().getPosition()));
			((GameState)gs).addEntity(String.format("explosion%d", id), exp);
			AssetManager.getManager().getSound(BigRedButton.EXP_SOUND).play(1.0f, AssetManager.getManager().getSoundVolume());
			lastExplosion = cTime;
			
			if(!Camera.getCamera().isShaking()) Camera.getCamera().shake(cTime, 200L, 20L, 15.0f);
			else Camera.getCamera().refreshShake(cTime);
		}
	}
	
	@Override
	public void render(Graphics g, long cTime) {
		if(!explosions.isEmpty()) {
			explosions.stream()
					  .filter(exp -> exp.isActive(cTime))
					  .forEach(exp -> exp.render(g, cTime));
		}
	}
	
	@Override
	public void use(Player player, Pair<Float> position, float theta, long cTime) {
		for(int i = 0; i < BigRedButton.EXP_COUNT; i++) {
			boolean critical = isCritical();
			double dmg = getDamageTotal(critical);
			
			Explosion exp = new Explosion(Explosion.Type.NORMAL, BigRedButton.EXP_NAME, 
										  new Pair<Float>(0.0f, 0.0f), 
										  dmg, BigRedButton.KNOCKBACK, BigRedButton.EXP_RADIUS, 
										  cTime);
			explosions.add(exp);
		}
		
		super.use(player, position, theta, cTime);
	}
	
	private Pair<Float> getExplosionLocation(GameState gs, Player player, Pair<Float> position) {
		int highCount = 0;
		List<Enemy> enemies = EnemyController.getInstance().getAliveEnemies();
		if(enemies.isEmpty()) return getRandomLocation(player, position);
		
		Enemy e = enemies.get(0);
		for(int i = 0; i < enemies.size(); i++) {
			int count = 1;
			Enemy current = enemies.get(i);
			for(int j = 0; j < enemies.size(); j++) {
				if(i != j) {
					float dist = Calculate.Distance(current.getPosition(), enemies.get(j).getPosition());
					if(dist <= BigRedButton.EXP_RADIUS) count++;
				}
			}
			
			if(count >= highCount) {
				highCount = count;
				e = current;
			}
		}
		
		return new Pair<Float>(e.getPosition().x, e.getPosition().y);
	}
	
	private Pair<Float> getRandomLocation(Player player, Pair<Float> position) {
		float x = 0.0f;
		float y = 0.0f;
		
		boolean valid = false;
		while(!valid) {
			x = BigRedButton.EXP_RADIUS + (Globals.rand.nextFloat() * (Globals.WIDTH - (BigRedButton.EXP_RADIUS * 2)));
			y = BigRedButton.EXP_RADIUS + (Globals.rand.nextFloat() * (Globals.HEIGHT - (BigRedButton.EXP_RADIUS * 2)));
			
			valid = (Calculate.Distance(new Pair<Float>(x, y), position) > BigRedButton.EXP_RADIUS);
		}
		
		return new Pair<Float>(x, y);
	}

	@Override
	public Pair<Integer> getDamage() { return BigRedButton.DAMAGE.getRange(BigRedButton.DAMAGE_MOD); }
	
	@Override
	public double rollDamage(boolean critical) { return BigRedButton.DAMAGE.roll(BigRedButton.DAMAGE_MOD, critical); }
	
	@Override
	public float getKnockback() { return 0.0f; }
	
	@Override
	public long getReloadTime() { return BigRedButton.RELOAD_TIME; }

	@Override
	public Image getInventoryIcon() { return AssetManager.getManager().getImage(BigRedButton.ICON_NAME); }
	
	@Override
	public int getClipSize() { return BigRedButton.CLIP_SIZE; }

	@Override
	public int getStartClips() { return BigRedButton.START_CLIPS; }

	@Override
	public int getMaxClips() { return BigRedButton.MAX_CLIPS; }
	
	@Override
	public long getCooldown() { return BigRedButton.COOLDOWN; }

	@Override
	public ProjectileType getProjectile() { return null; }

	@Override
	public int getPrice() { return BigRedButton.PRICE; }
	
	@Override
	public int getAmmoPrice() { return BigRedButton.AMMO_PRICE; }

	@Override
	public int getLevelRequirement() { return 18; }
	
	@Override
	public long getWeaponMetric() { return Metrics.BIG_RED_BUTTON; }
	
	@Override
	public String getName() {
		return "Big Red Button";
	}
	
	@Override
	public String getDescription() {
		return "A mysterious featureless box with a large red button on it... I wonder what it does?";
	}
}
