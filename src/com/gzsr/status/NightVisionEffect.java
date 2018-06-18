package com.gzsr.status;

import com.gzsr.entities.Entity;

public class NightVisionEffect extends StatusEffect {
	public NightVisionEffect(long duration_, long created_) {
		super(Status.NIGHT_VISION, duration_, created_);
	}

	@Override
	public void update(Entity e, long cTime) {
		// No need for update logic here.
	}

	@Override
	public void onDestroy(Entity e, long cTime) {
		// No need for destroy logic here.
	}
}
