package com.gzsr.status;

import org.newdawn.slick.Graphics;

import com.gzsr.Globals;
import com.gzsr.entities.Entity;
import com.gzsr.entities.Player;
import com.gzsr.entities.enemies.Enemy;
import com.gzsr.gfx.particles.emitters.BurningEmitter;
import com.gzsr.math.Dice;
import com.gzsr.misc.Pair;
import com.gzsr.objects.weapons.DamageType;
import com.gzsr.states.GameState;

public class BurningEffect extends StatusEffect {
	private static final Dice DAMAGE = new Dice(1, 4);
	private static final int DAMAGE_MOD = 2;
	public static final long DURATION = 5000L;
	public static final long DAMAGE_INTERVAL = 400L;
	
	private BurningEmitter emitter;
	
	private long lastDamage;
	
	public BurningEffect(long created_) {
		super(Status.BURNING, DURATION, created_);
		
		this.emitter = new BurningEmitter(Pair.ZERO);
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
			
			Player player = Player.getPlayer();
			long elapsed = (cTime - lastDamage);
			if(elapsed >= DAMAGE_INTERVAL) {
				if(e instanceof Enemy) {
					Enemy enemy = (Enemy) e;
					
					boolean critical = (Globals.rand.nextFloat() <= player.getAttributes().getFloat("critChance"));
					double dmg = rollDamage(critical);
					dmg += (dmg * (player.getAttributes().getInt("damageUp") * 0.10));
					if(critical) dmg *= player.getAttributes().getDouble("critMult");
					
					enemy.takeDamage(DamageType.FIRE, dmg, 0.0f, 0.0f, cTime, delta, false);
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
		if(emitter.isEmitting()) emitter.render(g, cTime);
	}
	
	public static Pair<Integer> getDamageRange() {
		return BurningEffect.DAMAGE.getRange(BurningEffect.DAMAGE_MOD);
	}
	
	public double rollDamage(boolean critical) { return BurningEffect.DAMAGE.roll(BurningEffect.DAMAGE_MOD, critical); }

	@Override
	public void onDestroy(Entity e, long cTime) {
		// No need for destroy logic here.
	}
}
