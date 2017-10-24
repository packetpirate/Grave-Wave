package com.gzsr.objects.weapons;

import java.util.Iterator;

import com.gzsr.Globals;
import com.gzsr.entities.enemies.Enemy;
import com.gzsr.entities.enemies.EnemyController;
import com.gzsr.gfx.particles.Particle;
import com.gzsr.gfx.particles.Projectile;
import com.gzsr.states.GameState;

public class Grenade extends Projectile {
	public static final float SPEED = 0.25f;
	public static final long LIFESPAN = 1000L;
	
	private Explosion exp;
	private boolean exploded;
	
	public Grenade(Particle p_, Explosion exp_) {
		super(p_, 0.0);
		this.exp = exp_;
		this.exploded = false;
	}

	@Override
	public void update(GameState gs, long cTime) {
		if(!exploded) {
			// Move the grenade entity along its path.
			position.x += (float)Math.cos(theta - (Math.PI / 2)) * Grenade.SPEED;
			position.y += (float)Math.sin(theta - (Math.PI / 2)) * Grenade.SPEED;
			
			// Check to see if the grenade has collided with an enemy. If so, explode.
			EnemyController ec = (EnemyController)gs.getEntity("enemyController");
			Iterator<Enemy> it = ec.getAliveEnemies().iterator();
			while(it.hasNext()) {
				Enemy e = it.next();
				if(e.checkCollision(position)) explode(gs, cTime);
			}
		}
	}
	
	private void explode(GameState gs, long cTime) {
		int id = Globals.generateEntityID();
		exp.setPosition(position);
		gs.addEntity(String.format("explosion%d", id), exp);
		exploded = true;
	}
	
	@Override
	public void onDestroy(GameState gs, long cTime) {
		explode(gs, cTime);
	}
	
	@Override
	public boolean isAlive(long cTime) {
		return (!exploded && super.isAlive(cTime));
	}
}
