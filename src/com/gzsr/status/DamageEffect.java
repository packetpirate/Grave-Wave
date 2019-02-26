package com.gzsr.status;

import org.newdawn.slick.Graphics;

import com.gzsr.achievements.Metrics;
import com.gzsr.entities.Entity;
import com.gzsr.entities.Player;
import com.gzsr.entities.enemies.Enemy;
import com.gzsr.math.Dice;
import com.gzsr.objects.weapons.DamageType;
import com.gzsr.states.GameState;

public class DamageEffect extends StatusEffect {
	private DamageType type;
	private Dice damage;
	private int modifier;

	private long interval;
	private long lastDamage;

	private boolean refreshes;
	@Override
	public void setCanRefresh(boolean val) { refreshes = val; }
	@Override
	public boolean canRefresh() { return refreshes; }

	public DamageEffect(DamageType type_, Dice damage_, int modifier_, long interval_, long duration_, long created_) {
		super(Status.DAMAGE, duration_, created_);

		this.type = type_;
		this.damage = damage_;
		this.modifier = modifier_;

		this.interval = interval_;
		this.lastDamage = -interval_;

		this.refreshes = false;
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
				else if(e instanceof Enemy) ((Enemy)e).takeDamage(type, amnt, 0.0f, Metrics.STATUS, cTime, delta);
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
