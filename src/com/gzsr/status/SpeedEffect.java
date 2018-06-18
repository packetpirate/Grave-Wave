package com.gzsr.status;

import com.gzsr.entities.Entity;
import com.gzsr.entities.Player;

public class SpeedEffect extends StatusEffect {
	public static final double EFFECT = 2.0;

	public SpeedEffect(long duration_, long created_) {
		super(Status.SPEED_UP, duration_, created_);
	}

	@Override
	public void update(Entity e, long cTime) {
		// No need for update logic here.
	}

	@Override
	public void onDestroy(Entity e, long cTime) {
		if(e instanceof Player) {
			Player player = (Player) e;
			player.setAttribute("spdMult", 1.0);
		}
	}
}
