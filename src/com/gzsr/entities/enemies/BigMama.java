package com.gzsr.entities.enemies;

import java.util.Iterator;

import org.newdawn.slick.Sound;

import com.gzsr.AssetManager;
import com.gzsr.Globals;
import com.gzsr.entities.Player;
import com.gzsr.math.Calculate;
import com.gzsr.misc.Pair;
import com.gzsr.objects.items.Powerups;
import com.gzsr.objects.weapons.Explosion;
import com.gzsr.states.GameState;
import com.gzsr.status.StatusEffect;

public class BigMama extends Enemy {
	private static final int FIRST_WAVE = 15;
	private static final int SPAWN_COST = 10;
	private static final float HEALTH = 300.0f;
	private static final float SPEED = 0.10f;
	private static final float DPS = 0.0f;
	private static final long LIFESPAN = 10_000L;
	private static final float EXP_DIST = 128.0f;
	private static final double EXP_DAMAGE = 75.0f;
	private static final float EXP_KNOCKBACK = 10.0f;
	private static final int ZUMBY_COUNT = 10;
	
	public static final LootTable LOOT = new LootTable()
			.addItem(Powerups.Type.HEALTH, 0.75f)
			.addItem(Powerups.Type.AMMO, 0.40f)
			.addItem(Powerups.Type.EXTRA_LIFE, 0.15f)
			.addItem(Powerups.Type.INVULNERABILITY, 0.10f)
			.addItem(Powerups.Type.NIGHT_VISION, 0.30f)
			.addItem(Powerups.Type.UNLIMITED_AMMO, 0.30f);
	
	private Sound explosion;
	
	private long created;
	private boolean exploded;
	
	public BigMama(Pair<Float> position) {
		super(EnemyType.BIG_MAMA, position);
		this.health = BigMama.HEALTH;
		
		explosion = AssetManager.getManager().getSound("explosion2");
		
		created = -1L;
		exploded = false;
	}
	
	@Override
	public void update(GameState gs, long cTime, int delta) {
		if(created == -1L) created = cTime; // So that we don't have to pass spawn time into constructor.
		if(!exploded) {
			theta = Calculate.Hypotenuse(position, Player.getPlayer().getPosition());
			
			// Has the Big Mama's lifespan elapsed, or have they been killed? Are they near the player?
			boolean timesUp = (cTime - created) >= BigMama.LIFESPAN;
			boolean dead = health <= 0;
			if(nearPlayer() || timesUp || dead) {
				// Big Mama self-detonates.
				EnemyController ec = (EnemyController)gs.getEntity("enemyController");
				
				// Spawn zumbies around the Big Mama's explosion area.
				for(int i = 0; i < BigMama.ZUMBY_COUNT; i++) {
					float d = Globals.rand.nextFloat() * BigMama.EXP_DIST;
					float t = Globals.rand.nextFloat() * (float)(Math.PI * 2);
					float x = position.x + (d * (float)Math.cos(t));
					float y = position.y + (d * (float)Math.sin(t));
					
					TinyZumby tz = new TinyZumby(new Pair<Float>(x, y));
					ec.addNextUpdate(tz);
				}
				
				// Spawn a blood explosion centered on the Big Mama.
				Explosion blood = new Explosion(Explosion.Type.BLOOD, "GZS_BloodExplosion", new Pair<Float>(position.x, position.y), BigMama.EXP_DAMAGE, BigMama.EXP_KNOCKBACK, BigMama.EXP_DIST);
				gs.addEntity(String.format("bloodExplosion%d", Globals.generateEntityID()), blood);
				
				exploded = true;
				explosion.play();
			} else {
				// Need to make sure to update the status effects first.
				Iterator<StatusEffect> it = statusEffects.iterator();
				while(it.hasNext()) {
					StatusEffect status = (StatusEffect) it.next();
					if(status.isActive(cTime)) {
						status.update(this, gs, cTime, delta);
					} else {
						status.onDestroy(this, cTime);
						it.remove();
					}
				}
				
				updateFlash(cTime);
				animation.update(cTime);
				if(Player.getPlayer().isAlive() && !touchingPlayer()) move(gs, delta);
			}
		}
	}
	
	@Override
	public boolean isAlive(long cTime) {
		return (!exploded && !dead());
	}

	@Override
	public void move(GameState gs, int delta) {
		velocity.x = (float)Math.cos(theta) * BigMama.SPEED * delta;
		velocity.y = (float)Math.sin(theta) * BigMama.SPEED * delta;

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
		return (Calculate.Distance(position, Player.getPlayer().getPosition()) <= BigMama.EXP_DIST);
	}

	@Override
	public double getDamage() {
		return BigMama.DPS;
	}
	
	@Override
	public float getSpeed() {
		return BigMama.SPEED;
	}
	
	public static int appearsOnWave() {
		return FIRST_WAVE;
	}
	
	public static int getSpawnCost() { 
		return BigMama.SPAWN_COST; 
	}

	@Override
	public String getName() {
		return "Big Mama";
	}
	
	@Override
	public String getDescription() {
		return "Big Mama";
	}

	@Override
	public LootTable getLootTable() {
		return BigMama.LOOT;
	}
}
