package com.gzsr.entities.enemies;

import org.newdawn.slick.Sound;

import com.gzsr.AssetManager;
import com.gzsr.Globals;
import com.gzsr.math.Calculate;
import com.gzsr.misc.Pair;
import com.gzsr.objects.weapons.Explosion;
import com.gzsr.states.GameState;
import com.gzsr.status.PoisonEffect;

public class Gasbag extends Enemy {
	private static final float COLLISION_DIST = 16.0f;
	private static final float HEALTH = 100.0f;
	private static final float SPEED = 0.2f;
	private static final float DPS = 0.5f;
	private static final float ATTACK_DIST = 100.0f;
	private static final float EXPLODE_RADIUS = 150.0f;
	private static final long POISON_DURATION = 5000L;
	private static final double POISON_DAMAGE = 0.01; // multiply by 1,000 to get damage done in 1 second
	
	private Sound explode;
	
	public Gasbag(Pair<Float> position_) {
		super(EnemyType.GASBAG, position_);
		this.health = Gasbag.HEALTH;
		this.explode = AssetManager.getManager().getSound("poison_cloud");
	}
	
	@Override
	public void update(GameState gs, long cTime) {
		if(isAlive(cTime)) {
			theta = Calculate.Hypotenuse(position, Globals.player.getPosition());
			if(!nearPlayer()) {
				animation.update(cTime);
				move();
			} else explode(gs, cTime);
		}
	}
	
	private void explode(GameState gs, long cTime) {
		int id = Globals.generateEntityID();
		PoisonEffect pe = new PoisonEffect(Gasbag.POISON_DAMAGE, Gasbag.POISON_DURATION, cTime);
		Explosion poison = new Explosion("GZS_PoisonExplosion", new Pair<Float>(position.x, position.y), pe, 0.0, Gasbag.EXPLODE_RADIUS);
		gs.addEntity(String.format("poisonExplosion%d", id), poison);
		
		explode.play();
		health = 0.0;
	}

	@Override
	public boolean isAlive(long cTime) {
		return (health > 0);
	}
	
	@Override
	public void onDeath(GameState gs, long cTime) {
		explode(gs, cTime);
	}

	@Override
	public void move() {
		position.x += (float)Math.cos(theta) * Gasbag.SPEED;
		position.y += (float)Math.sin(theta) * Gasbag.SPEED;
	}

	@Override
	public boolean checkCollision(Pair<Float> p) {
		return (Calculate.Distance(p, position) <= Gasbag.COLLISION_DIST);
	}
	
	private boolean nearPlayer() {
		return (Calculate.Distance(position, Globals.player.getPosition()) <= Gasbag.ATTACK_DIST);
	}

	@Override
	public void takeDamage(double amnt) {
		health -= amnt;
	}

	@Override
	public double getDamage() {
		return Gasbag.DPS;
	}
}
