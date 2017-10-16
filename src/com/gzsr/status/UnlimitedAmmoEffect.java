package com.gzsr.status;

import com.gzsr.entities.Player;

public class UnlimitedAmmoEffect extends StatusEffect {
	public UnlimitedAmmoEffect(Status status_, long duration_, long created_) {
		super(status_, duration_, created_);
	}

	@Override
	public void update(Player player, long cTime) {
		// Not needed.
	}

	@Override
	public void onDestroy(Player player, long cTime) {
		// Not needed.
	}
}
