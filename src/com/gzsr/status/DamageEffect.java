package com.gzsr.status;

import org.newdawn.slick.Graphics;

import com.gzsr.entities.Entity;
import com.gzsr.entities.Player;
import com.gzsr.entities.enemies.Enemy;
import com.gzsr.math.Dice;
import com.gzsr.states.GameState;

public class DamageEffect extends StatusEffect {
	private Dice damage;
	private int modifier;
	
	private long interval;
	private long lastDamage;
	
	public DamageEffect(Dice damage_, int modifier_, long interval_, long duration_, long created_) {
		super(Status.DAMAGE, duration_, created_);
		
		this.damage = damage_;
		this.modifier = modifier_;
		
		this.interval = interval_;
		this.lastDamage = -interval_;
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
			long elapsed = (cTime - lastDamage);
			if(elapsed >= interval) {
				double amnt = damage.roll(modifier);
				if(e instanceof Player) Player.getPlayer().takeDamage(amnt, cTime);
				else if(e instanceof Enemy) ((Enemy)e).takeDamage(amnt, 0.0f, cTime, delta);
				lastDamage = cTime;
			}
		}
	}

	@Override
	public void render(Graphics g, long cTime) {
	}

	@Override
	public void onDestroy(Entity e, long cTime) {
	}

}
