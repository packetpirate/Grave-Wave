package com.gzsr.status;

import com.gzsr.entities.Player;

public class InvulnerableEffect extends StatusEffect {
	public InvulnerableEffect(long duration_, long created_) {
		super(Status.INVULNERABLE, duration_, created_);
	}

	@Override
	public void update(Player player, long cTime) {
		// No need for update logic.
	}

	@Override
	public void onDestroy(Player player, long cTime) {
		// No onDestroy effect required.
	}
}
