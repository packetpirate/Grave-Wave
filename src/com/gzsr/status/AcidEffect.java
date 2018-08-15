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
	private static final Dice DAMAGE = new Dice(1, 4);
	private static final int DAMAGE_MOD = 2;
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
				if(e instanceof Enemy) {
					Enemy enemy = (Enemy) e;
					
					boolean critical = (Globals.rand.nextFloat() <= player.getAttributes().getFloat("critChance"));
					double dmg = rollDamage(critical);
					dmg += (dmg * (player.getAttributes().getInt("damageUp") * 0.10));
					if(critical) dmg *= player.getAttributes().getDouble("critMult");
					
					enemy.takeDamage(DamageType.CORROSIVE, dmg, 0.0f, 0.0f, cTime, delta, false);
				} else if(e instanceof Player) {
					double dmg = rollDamage(false);
					player.takeDamage(dmg, cTime);
				}
				
				lastDamage = cTime;
			}
		}
	}

	@Override
	public void render(Graphics g, long cTime) {
	}
	
	public static Pair<Integer> getDamageRange() { return AcidEffect.DAMAGE.getRange(AcidEffect.DAMAGE_MOD); }
	
	public double rollDamage(boolean critical) { return AcidEffect.DAMAGE.roll(AcidEffect.DAMAGE_MOD, critical); }

	@Override
	public void onDestroy(Entity e, long cTime) {
	}
}
