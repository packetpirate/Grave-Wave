package com.gzsr.status;

import com.gzsr.Globals;
import com.gzsr.entities.Entity;
import com.gzsr.entities.Player;
import com.gzsr.entities.enemies.Enemy;

public class BurningEffect extends StatusEffect {
	public static final double DPS_INC = 0.25;
	public static final double MAX_DPS = 40.0;
	public static final long DURATION = 5000L;
	
	private double cDamage;
	
	public BurningEffect(long created_) {
		super(Status.BURNING, DURATION, created_);
		this.cDamage = 0.0;
	}

	@Override
	public void update(Entity e, long cTime) {
		if(isActive(cTime)) {
			if(e instanceof Enemy) {
				Enemy enemy = (Enemy) e;
				double totalDamage = (cDamage + (cDamage * Globals.player.getIntAttribute("damageUp") * 0.10)) / (1000L / Globals.STEP_TIME);
				enemy.takeDamage(totalDamage, 0.0f, cTime, 0, false);
				System.out.printf("Entity \"%s\" burning for %.2f damage!\n", enemy.getName(), totalDamage);
			} else if(e instanceof Player) {
				Player player = (Player) e;
				player.takeDamage(cDamage / (1000L / Globals.STEP_TIME));
				System.out.printf("Entity \"%s\" burning for %.2f damage!\n", player.getName(), cDamage);
			}
		}
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
