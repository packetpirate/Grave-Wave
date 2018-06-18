package com.gzsr.status;

import com.gzsr.entities.Entity;

public class InvulnerableEffect extends StatusEffect {
	public InvulnerableEffect(long duration_, long created_) {
		super(Status.INVULNERABLE, duration_, created_);
	}

	@Override
	public void update(Entity e, long cTime) {
		// No need for update logic.
	}

	@Override
	public void onDestroy(Entity e, long cTime) {
		// No onDestroy effect required.
	}
}
