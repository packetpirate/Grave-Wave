package com.gzsr.status;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import com.gzsr.AssetManager;
import com.gzsr.entities.Entity;
import com.gzsr.gfx.ui.StatusMessages;
import com.gzsr.misc.Pair;
import com.gzsr.states.GameState;

public abstract class StatusEffect {
	protected Status status;
	public Status getStatus() { return status; }
	
	protected boolean drawn;
	public boolean isDrawn() { return drawn; }
	
	public Image getIcon() { 
		return AssetManager.getManager()
						   .getImage(status.getIconName());
	}
	
	protected long duration;
	public long getDuration() { return duration; }
	
	protected long created;
	public boolean isActive(long cTime) {
		long elapsed = cTime - created;
		return (elapsed <= duration);
	}
	
	public void refresh(long cTime) {
		this.created = cTime;
	}
	
	public float getPercentageTimeLeft(long cTime) {
		long elapsed = cTime - created;
		return (1.0f - ((float)elapsed / (float)duration));
	}
	
	public StatusEffect(Status status_, long duration_, long created_) {
		this.status = status_;
		this.duration = duration_;
		this.created = created_;
		
		this.drawn = true;
	}
	
	public void noEffect(Entity e, long cTime) {
		// Entity is immune to this effect. Call this method when effect is resisted.
		// Default behavior is to display a status message saying "Resisted!"
		StatusMessages.getInstance().addMessage("Resisted!", e, new Pair<Float>(0.0f, -32.0f), cTime, 2_000L);
	}
	
	public abstract void onApply(Entity e, long cTime);
	public abstract void handleEntity(Entity e, long cTime); // used in case special effects require entity knowledge, such as setting emitter position for particle effects
	public abstract void update(Entity e, GameState gs, long cTime, int delta);
	public abstract void render(Graphics g, long cTime);
	public abstract void onDestroy(Entity e, long cTime);
}
