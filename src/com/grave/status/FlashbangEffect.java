package com.grave.status;

public class FlashbangEffect extends DeafenedEffect {
	public static final long DURATION = 3_000L;
	
	public FlashbangEffect(long duration_, long created_) {
		super(duration_, created_);
		
		status = Status.FLASHBANG;
	}
}
