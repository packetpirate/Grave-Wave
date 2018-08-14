package com.gzsr.status;

import org.newdawn.slick.Graphics;

import com.gzsr.Globals;
import com.gzsr.entities.Entity;
import com.gzsr.entities.Player;
import com.gzsr.entities.enemies.Enemy;
import com.gzsr.math.Dice;
import com.gzsr.misc.Pair;
import com.gzsr.objects.weapons.DamageType;
import com.gzsr.states.GameState;

public class AcidEffect extends StatusEffect {
	public static final int MIN_DAMAGE_COUNT = 1;
	public static final int MIN_DAMAGE_SIDES = 4;
	public static final int MIN_DAMAGE_MOD = 2;
	public static final long DURATION = 5000L;
	public static final long DAMAGE_INTERVAL = 1_000L;
	
	// TODO: Add emitter to show acid "bubbles" coming off the player. Maybe use the acid particle at 75% transparency?
	private long lastDamage;
	
	public AcidEffect(long created_) {
		super(Status.ACID, DURATION, created_);
		
		this.lastDamage = 0L;
	}
	
	@Override
	public void onApply(Entity e, long cTime) {
	}

	@Override
	public void handleEntity(Entity e, long cTime) {
	}

	@Override
	public void update(Entity e, GameState gs, long cTime, int delta) {
		if(isActive(cTime)) {
			Player player = Player.getPlayer();
			long elapsed = (cTime - lastDamage);
			
			if(elapsed >= DAMAGE_INTERVAL) {
				double dmg = Dice.roll(AcidEffect.MIN_DAMAGE_COUNT, AcidEffect.MIN_DAMAGE_SIDES, AcidEffect.MIN_DAMAGE_MOD);
				
				if(e instanceof Enemy) {
					Enemy enemy = (Enemy) e;
					
					boolean critical = (Globals.rand.nextFloat() <= player.getAttributes().getFloat("critChance"));
					dmg += (dmg * (player.getAttributes().getInt("damageUp") * 0.10));
					if(critical) dmg *= player.getAttributes().getDouble("critMult");
					
					enemy.takeDamage(DamageType.CORROSIVE, dmg, 0.0f, 0.0f, cTime, delta, false);
				} else if(e instanceof Player) {
					player.takeDamage(dmg, cTime);
				}
				
				lastDamage = cTime;
			}
		}
	}

	@Override
	public void render(Graphics g, long cTime) {
	}
	
	public static Pair<Integer> getDamageRange() {
		return Dice.getRange(AcidEffect.MIN_DAMAGE_COUNT, AcidEffect.MIN_DAMAGE_SIDES, AcidEffect.MIN_DAMAGE_MOD);
	}

	@Override
	public void onDestroy(Entity e, long cTime) {
	}
}
