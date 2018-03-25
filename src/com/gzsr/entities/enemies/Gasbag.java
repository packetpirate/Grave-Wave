package com.gzsr.entities.enemies;

import org.newdawn.slick.Sound;

import com.gzsr.AssetManager;
import com.gzsr.Globals;
import com.gzsr.math.Calculate;
import com.gzsr.misc.Pair;
import com.gzsr.objects.items.Powerups;
import com.gzsr.objects.weapons.Explosion;
import com.gzsr.states.GameState;
import com.gzsr.status.PoisonEffect;

public class Gasbag extends Enemy {
	public static final int FIRST_WAVE = 8;
	private static final int SPAWN_COST = 5;
	private static final float HEALTH = 100.0f;
	private static final float SPEED = 0.2f;
	private static final float DPS = 0.5f;
	private static final float POWERUP_CHANCE = 0.4f;
	private static final float ATTACK_DIST = 100.0f;
	private static final float EXPLODE_RADIUS = 150.0f;
	private static final long POISON_DURATION = 5000L;
	private static final double POISON_DAMAGE = 0.05; // multiply by 1,000 to get damage done in 1 second
	private static final float POISON_KNOCKBACK = 5.0f;
	
	private Sound explode;
	
	private boolean deathHandled;
	
	public Gasbag(Pair<Float> position_) {
		super(EnemyType.GASBAG, position_);
		this.health = Gasbag.HEALTH;
		this.explode = AssetManager.getManager().getSound("poison_cloud");
		this.deathHandled = false;
	}
	
	@Override
	public void update(GameState gs, long cTime, int delta) {
		if(isAlive(cTime)) {
			updateFlash(cTime);
			theta = Calculate.Hypotenuse(position, Globals.player.getPosition());
			if(!nearPlayer()) {
				animation.update(cTime);
				if(Globals.player.isAlive() && !touchingPlayer()) move(delta);
			} else explode(gs, cTime);
		}
	}
	
	private void explode(GameState gs, long cTime) {
		int id = Globals.generateEntityID();
		PoisonEffect pe = new PoisonEffect(Gasbag.POISON_DAMAGE, Gasbag.POISON_DURATION, cTime);
		Explosion poison = new Explosion(Explosion.Type.POISON, "GZS_PoisonExplosion", new Pair<Float>(position.x, position.y), pe, 0.0, Gasbag.POISON_KNOCKBACK, Gasbag.EXPLODE_RADIUS);
		gs.addEntity(String.format("poisonExplosion%d", id), poison);
		
		explode.play();
		health = 0.0;
	}
	
	@Override
	public void onDeath(GameState gs, long cTime) {
		explode(gs, cTime);
		
		if(!deathHandled && (Globals.rand.nextFloat() <= Gasbag.POWERUP_CHANCE)) {
			Powerups.spawnRandomPowerup(gs, position, cTime);
		}
		
		deathHandled = true;
	}

	@Override
	public void move(int delta) {
		if(!moveBlocked) {
			position.x += (float)Math.cos(theta) * Gasbag.SPEED * delta;
			position.y += (float)Math.sin(theta) * Gasbag.SPEED * delta;
		}
		
		moveBlocked = false;
		
		bounds.setCenterX(position.x);
		bounds.setCenterY(position.y);
	}
	
	private boolean nearPlayer() {
		return (Calculate.Distance(position, Globals.player.getPosition()) <= Gasbag.ATTACK_DIST);
	}

	@Override
	public double getDamage() {
		return Gasbag.DPS;
	}
	
	@Override
	public float getSpeed() {
		return Gasbag.SPEED;
	}

	public static int appearsOnWave() {
		return FIRST_WAVE;
	}
	
	public static int getSpawnCost() {
		return Gasbag.SPAWN_COST;
	}
	
	@Override
	public String getName() {
		return "Gasbag";
	}
	
	@Override
	public String getDescription() {
		return "Gasbag";
	}
}
