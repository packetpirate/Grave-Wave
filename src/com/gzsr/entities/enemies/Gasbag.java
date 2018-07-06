package com.gzsr.entities.enemies;

import java.util.Iterator;

import org.newdawn.slick.Sound;
import org.newdawn.slick.state.BasicGameState;

import com.gzsr.AssetManager;
import com.gzsr.Globals;
import com.gzsr.entities.Player;
import com.gzsr.math.Calculate;
import com.gzsr.misc.Pair;
import com.gzsr.objects.items.Powerups;
import com.gzsr.objects.weapons.Explosion;
import com.gzsr.states.GameState;
import com.gzsr.status.PoisonEffect;
import com.gzsr.status.StatusEffect;

public class Gasbag extends Enemy {
	public static final int FIRST_WAVE = 8;
	private static final int SPAWN_COST = 5;
	private static final float HEALTH = 100.0f;
	private static final float SPEED = 0.2f;
	private static final float DPS = 0.5f;
	private static final float ATTACK_DIST = 100.0f;
	private static final float EXPLODE_RADIUS = 150.0f;
	private static final long POISON_DURATION = 5000L;
	private static final double POISON_DAMAGE = 0.05; // multiply by 1,000 to get damage done in 1 second
	private static final float POISON_KNOCKBACK = 5.0f;
	
	public static final LootTable LOOT = new LootTable()
			.addItem(Powerups.Type.HEALTH, 0.20f)
			.addItem(Powerups.Type.AMMO, 0.20f)
			.addItem(Powerups.Type.EXTRA_LIFE, 0.05f)
			.addItem(Powerups.Type.CRIT_CHANCE, 0.10f)
			.addItem(Powerups.Type.EXP_MULTIPLIER, 0.10f)
			.addItem(Powerups.Type.NIGHT_VISION, 0.15f)
			.addItem(Powerups.Type.UNLIMITED_AMMO, 0.025f);
	
	private Sound explode;
	
	public Gasbag(Pair<Float> position_) {
		super(EnemyType.GASBAG, position_);
		this.health = Gasbag.HEALTH;
		this.explode = AssetManager.getManager().getSound("poison_cloud");
	}
	
	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
		if(isAlive(cTime)) {
			// Need to make sure to update the status effects first.
			Iterator<StatusEffect> it = statusEffects.iterator();
			while(it.hasNext()) {
				StatusEffect status = (StatusEffect) it.next();
				if(status.isActive(cTime)) {
					status.update(this, (GameState)gs, cTime, delta);
				} else {
					status.onDestroy(this, cTime);
					it.remove();
				}
			}
			
			updateFlash(cTime);
			theta = Calculate.Hypotenuse(position, Player.getPlayer().getPosition());
			if(!nearPlayer()) {
				animation.update(cTime);
				if(Player.getPlayer().isAlive() && !touchingPlayer()) move((GameState)gs, delta);
			} else explode((GameState)gs, cTime);
		}
		
		if(!damageTexts.isEmpty()) {
			damageTexts.stream().forEach(dt -> ((GameState)gs).addEntity(String.format("dt%d", Globals.generateEntityID()), dt));
			damageTexts.clear();
		}
	}
	
	private void explode(GameState gs, long cTime) {
		int id = Globals.generateEntityID();
		PoisonEffect pe = new PoisonEffect(Gasbag.POISON_DAMAGE, Gasbag.POISON_DURATION, cTime);
		Explosion poison = new Explosion(Explosion.Type.POISON, "GZS_PoisonExplosion", new Pair<Float>(position.x, position.y), pe, 0.0, Gasbag.POISON_KNOCKBACK, Gasbag.EXPLODE_RADIUS);
		gs.addEntity(String.format("poisonExplosion%d", id), poison);
		
		explode.play(1.0f, AssetManager.getManager().getSoundVolume());
		health = 0.0;
	}

	@Override
	public void move(GameState gs, int delta) {
		velocity.x = (float)Math.cos(theta) * Gasbag.SPEED * delta;
		velocity.y = (float)Math.sin(theta) * Gasbag.SPEED * delta;

		avoidObstacles(gs, delta);
		
		if(!moveBlocked) {
			position.x += velocity.x;
			position.y += velocity.y;
		}
		
		moveBlocked = false;
		
		bounds.setCenterX(position.x);
		bounds.setCenterY(position.y);
	}
	
	@Override
	public float getCohesionDistance() {
		return (Math.min(type.getFrameWidth(), type.getFrameHeight()) * 2);
	}
	
	@Override
	public float getSeparationDistance() {
		return Math.min(type.getFrameWidth(), type.getFrameHeight());
	}
	
	private boolean nearPlayer() {
		return (Calculate.Distance(position, Player.getPlayer().getPosition()) <= Gasbag.ATTACK_DIST);
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
	
	@Override
	public LootTable getLootTable() {
		return Gasbag.LOOT;
	}
}
