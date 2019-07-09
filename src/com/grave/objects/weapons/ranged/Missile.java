package com.grave.objects.weapons.ranged;

import java.util.Iterator;

import org.newdawn.slick.Sound;
import org.newdawn.slick.state.BasicGameState;

import com.grave.AssetManager;
import com.grave.entities.enemies.Enemy;
import com.grave.entities.enemies.EnemyController;
import com.grave.gfx.Animation;
import com.grave.gfx.Camera;
import com.grave.gfx.particles.Particle;
import com.grave.gfx.particles.Projectile;
import com.grave.gfx.particles.ProjectileType;
import com.grave.objects.weapons.Explosion;
import com.grave.states.GameState;

public class Missile extends Projectile {
	public static final float SPEED = ProjectileType.MISSILE.getVelocity();
	public static final long LIFESPAN = ProjectileType.MISSILE.getLifespan();

	private Sound explode;
	private Explosion exp;
	private boolean exploded;

	public Missile(Particle p_, Explosion exp_) {
		super(p_, 0.0, false);

		this.explode = AssetManager.getManager().getSound("explosion2");
		this.exp = exp_;
		this.exploded = false;
	}

	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
		if(!exploded) {
			// Move the grenade entity along its path.
			position.x += (float)Math.cos(theta - (Math.PI / 2)) * Grenade.SPEED * delta;
			position.y += (float)Math.sin(theta - (Math.PI / 2)) * Grenade.SPEED * delta;
			bounds.setLocation((position.x - (size.x / 2)), (position.y - (size.y / 2)));

			// Check to see if the grenade has collided with an enemy. If so, explode.
			EnemyController ec = EnemyController.getInstance();
			Iterator<Enemy> it = ec.getAliveEnemies().iterator();
			while(it.hasNext()) {
				Enemy e = it.next();
				if(e.getCollider().intersects(getCollider())) explode((GameState)gs, cTime);
			}

			Animation anim = getAnimation();
			if(anim != null) anim.update(cTime);
		}
	}

	private void explode(GameState gs, long cTime) {
		exp.setCreatedTime(cTime);
		exp.setPosition(position);
		gs.getLevel().addEntity(exp.getTag(), exp);

		explode.play(1.0f, AssetManager.getManager().getSoundVolume());
		exploded = true;

		if(!Camera.getCamera().isShaking()) Camera.getCamera().shake(cTime, 200L, 50L, 20.0f);
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
