package com.gzsr.objects.weapons;

import java.util.Iterator;

import org.newdawn.slick.Sound;
import org.newdawn.slick.state.BasicGameState;

import com.gzsr.AssetManager;
import com.gzsr.Globals;
import com.gzsr.entities.enemies.Enemy;
import com.gzsr.entities.enemies.EnemyController;
import com.gzsr.gfx.Camera;
import com.gzsr.gfx.particles.Particle;
import com.gzsr.gfx.particles.Projectile;
import com.gzsr.states.GameState;

public class Grenade extends Projectile {
	public static final float SPEED = 0.25f;
	public static final long LIFESPAN = 1_000L;
	
	private Sound explode;
	private Explosion exp;
	private boolean exploded;
	
	public Grenade(Particle p_, Explosion exp_) {
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
			EnemyController ec = (EnemyController)((GameState)gs).getEntity("enemyController");
			Iterator<Enemy> it = ec.getAliveEnemies().iterator();
			while(it.hasNext()) {
				Enemy e = it.next();
				if(e.getCollider().intersects(getCollider())) explode((GameState)gs, cTime);
			}
		}
	}
	
	private void explode(GameState gs, long cTime) {
		int id = Globals.generateEntityID();
		exp.setPosition(position);
		gs.addEntity(String.format("explosion%d", id), exp);
		explode.play(1.0f, AssetManager.getManager().getSoundVolume());
		exploded = true;
		
		if(!Camera.getCamera().isShaking()) Camera.getCamera().shake(cTime, 200L, 20L, 15.0f);
		else Camera.getCamera().refreshShake(cTime);
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
