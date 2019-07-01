package com.grave.gfx.particles;

import com.grave.entities.Entity;
import com.grave.entities.Player;
import com.grave.entities.enemies.Enemy;
import com.grave.status.StatusEffect;

public class StatusProjectile extends Projectile {
	private StatusEffect effect;
	
	public void applyEffect(Entity e, long cTime) {
		effect.refresh(cTime);
		effect.handleEntity(e, cTime);
		
		if(e instanceof Enemy) {
			Enemy enemy = (Enemy) e;
			enemy.getStatusHandler().addStatus(effect, cTime);
		} else if(e instanceof Player) {
			Player player = (Player) e;
			player.getStatusHandler().addStatus(effect, cTime);
		}
	}
	
	public StatusProjectile(Particle p, double damage_, boolean critical_, StatusEffect effect_) {
		super(p, damage_, critical_);
		this.effect = effect_;
	}
}
