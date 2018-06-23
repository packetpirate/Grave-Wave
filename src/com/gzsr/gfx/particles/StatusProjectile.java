package com.gzsr.gfx.particles;

import com.gzsr.entities.Entity;
import com.gzsr.entities.Player;
import com.gzsr.entities.enemies.Enemy;
import com.gzsr.status.StatusEffect;

public class StatusProjectile extends Projectile {
	private StatusEffect effect;
	
	public void applyEffect(Entity e, long cTime) {
		effect.refresh(cTime);
		effect.handleEntity(e, cTime);
		
		if(e instanceof Enemy) {
			Enemy enemy = (Enemy) e;
			effect.onApply(enemy, cTime);
			enemy.addStatus(effect, cTime);
		} else if(e instanceof Player) {
			Player player = (Player) e;
			effect.onApply(player, cTime);
			player.addStatus(effect, cTime);
		}
	}
	
	public StatusProjectile(Particle p, double damage_, StatusEffect effect_) {
		super(p, damage_);
		this.effect = effect_;
	}
}