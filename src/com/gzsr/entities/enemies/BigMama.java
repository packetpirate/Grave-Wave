package com.gzsr.entities.enemies;

import org.newdawn.slick.Sound;

import com.gzsr.AssetManager;
import com.gzsr.Globals;
import com.gzsr.math.Calculate;
import com.gzsr.misc.Pair;
import com.gzsr.objects.weapons.Explosion;
import com.gzsr.states.GameState;

public class BigMama extends Enemy {
	private static final float HEALTH = 300.0f;
	private static final float SPEED = 0.10f;
	private static final float DPS = 0.0f;
	private static final long LIFESPAN = 10_000L;
	private static final float EXP_DIST = 128.0f;
	private static final double EXP_DAMAGE = 75.0f;
	private static final int ZUMBY_COUNT = 10;
	
	private Sound explosion;
	
	private long created;
	private boolean exploded;
	
	public BigMama(Pair<Float> position, long cTime) {
		super(EnemyType.BIG_MAMA, position);
		this.health = BigMama.HEALTH;
		
		explosion = AssetManager.getManager().getSound("explosion2");
		
		created = cTime;
		exploded = false;
	}
	
	@Override
	public void update(GameState gs, long cTime, int delta) {
		if(!exploded) {
			theta = Calculate.Hypotenuse(position, Globals.player.getPosition());
			
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
				Explosion blood = new Explosion("GZS_BloodExplosion", new Pair<Float>(position.x, position.y), BigMama.EXP_DAMAGE, BigMama.EXP_DIST);
				gs.addEntity(String.format("bloodExplosion%d", Globals.generateEntityID()), blood);
				
				exploded = true;
				explosion.play();
			} else {
				animation.update(cTime);
				move(delta);
			}
		}
	}
	
	@Override
	public boolean isAlive(long cTime) {
		return (!exploded && (health > 0));
	}

	@Override
	public void move(int delta) {
		theta = Calculate.Hypotenuse(position, Globals.player.getPosition());
		
		if(!moveBlocked) {
			position.x += (float)Math.cos(theta) * BigMama.SPEED * delta;
			position.y += (float)Math.sin(theta) * BigMama.SPEED * delta;
		}
		
		moveBlocked = false;
		
		bounds.setCenterX(position.x);
		bounds.setCenterY(position.y);
	}
	
	private boolean nearPlayer() {
		return (Calculate.Distance(position, Globals.player.getPosition()) <= BigMama.EXP_DIST);
	}

	@Override
	public void takeDamage(double amnt) {
		health -= amnt;
	}

	@Override
	public double getDamage() {
		return BigMama.DPS;
	}
}
