package com.gzsr.status;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Sound;

import com.gzsr.AssetManager;
import com.gzsr.MusicPlayer;
import com.gzsr.entities.Entity;
import com.gzsr.states.GameState;

public class DeafenedEffect extends StatusEffect {
	private Sound earsRinging;
	
	public DeafenedEffect(long duration_, long created_) {
		super(Status.DEAFENED, duration_, created_);
		
		drawn = false;
		earsRinging = AssetManager.getManager().getSound("ears_ringing");
	}
	
	@Override
	public void onApply(Entity e, long cTime) {
		earsRinging.play(1.0f, AssetManager.getManager().getSoundVolume());
		
		// Deafen AFTER playing ears ringing sound, otherwise ears ringing will be silent as well.
		AssetManager.getManager().deafen(true);
		MusicPlayer.getInstance().deafen(true);
	}
	
	@Override
	public void handleEntity(Entity e, long cTime) {
		// Not used.
	}

	@Override
	public void update(Entity e, GameState gs, long cTime, int delta) {
		// Update the volume of other sound effects and music as the status wears off.
		float volume = (1.0f - getPercentageTimeLeft(cTime));
		
		AssetManager.getManager().setDeafenedVolume(volume);
		MusicPlayer.getInstance().setDeafenedVolume(volume);
	}
	
	@Override
	public void render(Graphics g, long cTime) {
		// Not used.
	}

	@Override
	public void onDestroy(Entity e, long cTime) {
		// Restore player hearing.
		AssetManager.getManager().deafen(false);
		MusicPlayer.getInstance().deafen(false);
	}
}
