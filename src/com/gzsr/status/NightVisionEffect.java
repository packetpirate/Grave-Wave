package com.gzsr.status;

import com.gzsr.entities.Player;

public class NightVisionEffect extends StatusEffect {
	public NightVisionEffect(long duration_, long created_) {
		super(Status.NIGHT_VISION, duration_, created_);
	}

	@Override
	public void update(Player player, long cTime) {
		// No need for update logic here.
	}

	@Override
	public void onDestroy(Player player, long cTime) {
		// No need for destroy logic here.
	}
}
