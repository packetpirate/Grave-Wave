package com.gzsr.status;

import org.newdawn.slick.Graphics;

import com.gzsr.Globals;
import com.gzsr.entities.Entity;
import com.gzsr.entities.Player;
import com.gzsr.entities.enemies.Enemy;
import com.gzsr.gfx.particles.BurningEmitter;
import com.gzsr.misc.Pair;
import com.gzsr.states.GameState;

public class BurningEffect extends StatusEffect {
	public static final double DPS_INC = 0.25;
	public static final double MAX_DPS = 40.0;
	public static final long DURATION = 5000L;
	public static final long DAMAGE_INTERVAL = 1000L;
	
	private BurningEmitter emitter;
	private double cDamage;
	
	private double cumulativeDamage;
	private long lastDamage;
	
	public BurningEffect(long created_) {
		super(Status.BURNING, DURATION, created_);
		
		this.emitter = new BurningEmitter(Pair.ZERO);
		this.cDamage = 0.0;
		
		this.cumulativeDamage = 0.0;
		this.lastDamage = 0L;
	}
	
	@Override
	public void onApply(Entity e, long cTime) {
		if(e instanceof Enemy) {
			Enemy enemy = (Enemy) e;
			emitter.setBurnRadius(Math.min(enemy.getCollider().getWidth(), enemy.getCollider().getHeight()) / 2);
		} else if(e instanceof Player) {
			Player player = (Player) e;
			emitter.setBurnRadius(Math.min(player.getCollider().getWidth(), player.getCollider().getHeight()) / 2);
		}
		
		emitter.enable(cTime);
	}
	
	@Override
	public void handleEntity(Entity e, long cTime) {
		if(e instanceof Enemy) {
			Enemy enemy = (Enemy) e;
			emitter.setPosition(enemy.getPosition());
		} else if(e instanceof Player) {
			Player player = (Player) e;
			emitter.setPosition(player.getPosition());
		}
	}

	@Override
	public void update(Entity e, GameState gs, long cTime, int delta) {
		if(isActive(cTime)) {
			if(emitter.isEmitting()) emitter.update(gs, cTime, delta);
			
			if(e instanceof Enemy) {
				Enemy enemy = (Enemy) e;
				long elapsed = cTime - lastDamage;
				double totalDamage = (cDamage + (cDamage * Player.getPlayer().getAttributes().getInt("damageUp") * 0.10)) / (1000L / Globals.STEP_TIME);
				cumulativeDamage += totalDamage;
				
				if(elapsed >= DAMAGE_INTERVAL) {
					enemy.takeDamage(cumulativeDamage, 0.0f, 0.0f, cTime, 0, false);
					cumulativeDamage = 0.0;
					lastDamage = cTime;
				}
			} else if(e instanceof Player) {
				Player player = (Player) e;
				player.takeDamage(cDamage / (1000L / Globals.STEP_TIME), cTime);
			}
		}
	}
	
	@Override
	public void render(Graphics g, long cTime) {
		if(emitter.isEmitting()) emitter.render(g, cTime);
	}
	
	@Override
	public void refresh(long cTime) {
		super.refresh(cTime);
		
		// Add the DPS increment to the current DPS until we hit our max (fire gets more intense).
		if(cDamage <= (MAX_DPS - DPS_INC)) cDamage += DPS_INC;
	}

	@Override
	public void onDestroy(Entity e, long cTime) {
		// No need for destroy logic here.
	}
}
