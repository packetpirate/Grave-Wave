package com.gzsr.gfx.particles;

import com.gzsr.entities.Entity;
import com.gzsr.entities.Player;
import com.gzsr.entities.enemies.Enemy;
import com.gzsr.status.StatusEffect;

public class SpecialProjectile extends Projectile {
	private StatusEffect effect;
	public void applyEffect(Entity e, long cTime) {
		if(e instanceof Enemy) {
			Enemy enemy = (Enemy) e;
			enemy.addStatus(effect, cTime);
		} else if(e instanceof Player) {
			Player player = (Player) e;
			player.addStatus(effect, cTime);
		}
	}
	
	public SpecialProjectile(Particle p, double damage_, StatusEffect effect_) {
		super(p, damage_);
		this.effect = effect_;
	}
}
