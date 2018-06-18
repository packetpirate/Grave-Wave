package com.gzsr.status;

import com.gzsr.entities.Entity;

public class UnlimitedAmmoEffect extends StatusEffect {
	public UnlimitedAmmoEffect(long duration_, long created_) {
		super(Status.UNLIMITED_AMMO, duration_, created_);
	}

	@Override
	public void update(Entity e, long cTime) {
		// Not needed.
	}

	@Override
	public void onDestroy(Entity e, long cTime) {
		// Not needed.
	}
}
