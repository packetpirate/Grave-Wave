package com.grave.gfx.particles.emitters;

import java.util.Iterator;
import java.util.function.Supplier;

import org.newdawn.slick.state.BasicGameState;

import com.grave.AssetManager;
import com.grave.Globals;
import com.grave.entities.enemies.Enemy;
import com.grave.entities.enemies.EnemyController;
import com.grave.gfx.particles.Emitter;
import com.grave.gfx.particles.Particle;
import com.grave.gfx.particles.StatusProjectile;
import com.grave.misc.Pair;
import com.grave.states.GameState;
import com.grave.status.BurningEffect;

public class FireFieldEmitter extends Emitter {
	private static final float VELOCITY = 0.2f;
	private static final Pair<Float> SIZE = new Pair<Float>(8.0f, 8.0f);
	
	private float burnRadius;
	public void setBurnRadius(float burnRadius_) { this.burnRadius = burnRadius_; }
	
	public FireFieldEmitter(Pair<Float> position_, long lifespan, long cTime) {
		super(position_, FireFieldEmitter.generateTemplate(), lifespan, 10L, cTime);
		this.burnRadius = 0.0f;
	}
	
	private static Particle generateTemplate() {
		return new Particle(AssetManager.getManager().getAnimation("GZS_FireAnimation1"), Pair.ZERO, VELOCITY, 0.0f, 0.0f, SIZE, 500L, 0L);
	}
	
	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
		Iterator<Particle> it = particles.iterator();
		while(it.hasNext()) {
			StatusProjectile sp = (StatusProjectile) it.next();
			sp.update(gs, cTime, delta);
			if(sp.isAlive(cTime)) {
				checkEnemies((GameState)gs, cTime, sp);
			} else it.remove();
		}
		
		// If the emitter is enabled and the interval has passed, emit a particle.
		if(isEmitting() && canEmit(cTime)) emit(cTime);
	}
	
	private void checkEnemies(GameState gs, long cTime, StatusProjectile sp) {
		Iterator<Enemy> it = EnemyController.getInstance().getAliveEnemies().iterator();
		while(it.hasNext()) {
			Enemy enemy = it.next();
			if(!enemy.dead() && sp.checkCollision(enemy)) {
				sp.collide(gs, enemy, cTime);
				sp.applyEffect(enemy, cTime);
				
				return;
			}
		}
	}
	
	@Override
	public void emit(int count, long cTime, Supplier<Float> deviator) {
		if(isEmitting()) {
			for(int i = 0; i < count; i++) {
				Particle p = new Particle(template);
				
				Pair<Float> transformed = transform(position);
				
				p.setCreated(cTime);
				p.setPosition(new Pair<Float>(transformed));
				p.resetBounds();
				
				StatusProjectile sp = new StatusProjectile(p, 0.0, false, new BurningEffect(cTime));
				
				if(deviator != null) {
					float theta = p.getTheta();
					p.setTheta(theta + deviator.get());
				}
				
				particles.add(sp);
			}
			
			lastEmission = cTime;
		}
	}
	
	@Override
	protected Pair<Float> transform(Pair<Float> start) {
		Pair<Float> end = new Pair<Float>(start.x, start.y);
		
		double tx = Globals.rand.nextDouble() * (Math.PI * 2);
		double ty = Globals.rand.nextDouble() * (Math.PI * 2);
		
		float dx = Globals.rand.nextFloat() * burnRadius;
		float dy = Globals.rand.nextFloat() * burnRadius;
		
		end.x += (float)Math.cos(tx) * dx;
		end.y += (float)Math.sin(ty) * dy;
		
		return end;
	}
}
