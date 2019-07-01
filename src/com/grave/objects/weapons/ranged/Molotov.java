package com.grave.objects.weapons.ranged;

import java.util.Iterator;

import org.newdawn.slick.Sound;
import org.newdawn.slick.state.BasicGameState;

import com.grave.AssetManager;
import com.grave.entities.enemies.Enemy;
import com.grave.entities.enemies.EnemyController;
import com.grave.gfx.Camera;
import com.grave.gfx.particles.Particle;
import com.grave.gfx.particles.Projectile;
import com.grave.gfx.particles.ProjectileType;
import com.grave.gfx.particles.emitters.FireFieldEmitter;
import com.grave.misc.Pair;
import com.grave.states.GameState;

public class Molotov extends Projectile {
	public static final float SPEED = ProjectileType.MOLOTOV.getVelocity();
	private static final float SPIN_VELOCITY = 0.126f;
	private static final float BURN_RADIUS = 100.0f;
	private static final long BURN_TIME = 3_000L;

	private Sound explosion;
	private boolean exploded;

	public Molotov(Particle p_) {
		super(p_, null, 0.0, false);

		this.explosion = AssetManager.getManager().getSound("explosion2");
		this.exploded = false;
	}

	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
		if(!exploded) {
			// Move the molotov along its path.
			position.x += (float)Math.cos(theta - (Math.PI / 2)) * Molotov.SPEED * delta;
			position.y += (float)Math.sin(theta - (Math.PI / 2)) * Molotov.SPEED * delta;
			rotation += SPIN_VELOCITY;
			bounds.setLocation((position.x - (size.x / 2)), (position.y - (size.y / 2)));

			// Check to see if the molotov has collided with an enemy. If so, explode.
			EnemyController ec = EnemyController.getInstance();
			Iterator<Enemy> it = ec.getAliveEnemies().iterator();
			while(it.hasNext()) {
				Enemy e = it.next();
				if(e.getCollider().intersects(getCollider())) explode((GameState)gs, cTime);
			}
		}
	}

	private void explode(GameState gs, long cTime) {
		FireFieldEmitter emitter = new FireFieldEmitter(new Pair<Float>(position), Molotov.BURN_TIME, cTime);
		emitter.setBurnRadius(Molotov.BURN_RADIUS);
		emitter.enable(cTime);
		gs.getLevel().addEntity("fire", emitter);

		explosion.play(1.0f, AssetManager.getManager().getSoundVolume());
		exploded = true;

		if(!Camera.getCamera().isShaking()) Camera.getCamera().shake(cTime, 200L, 50L, 2.0f);
		else Camera.getCamera().refreshShake(cTime);
	}

	@Override
	public void onDestroy(GameState gs, long cTime) {
		if(!exploded) explode(gs, cTime);
	}

	@Override
	public boolean isAlive(long cTime) {
		return (!exploded && super.isAlive(cTime));
	}
}
