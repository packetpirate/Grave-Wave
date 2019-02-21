package com.gzsr.status;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import com.gzsr.AssetManager;
import com.gzsr.entities.Entity;
import com.gzsr.entities.Player;
import com.gzsr.entities.enemies.Enemy;
import com.gzsr.gfx.ui.StatusMessages;
import com.gzsr.misc.Pair;
import com.gzsr.states.GameState;
import com.gzsr.talents.Talents;

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
	public long getCreateTime() { return created; }
	public boolean isActive(long cTime) {
		long elapsed = cTime - created;
		return (elapsed <= duration);
	}

	private boolean refreshes;
	public void setCanRefresh(boolean val) { refreshes = val; }
	public boolean canRefresh() { return refreshes; }
	public void refresh(long cTime) {
		if(refreshes) {
			this.created = cTime;
		}
	}

	public float getPercentageTimeLeft(long cTime) {
		long elapsed = cTime - created;
		return (1.0f - ((float)elapsed / (float)duration));
	}

	public StatusEffect(Status status_, long duration_, long created_) {
		this.status = status_;
		this.duration = duration_;
		this.created = created_;

		this.refreshes = true;

		this.drawn = true;

		if(Talents.Tactics.SUSTAINABILITY.active()) {
			int ranks = Talents.Tactics.SUSTAINABILITY.ranks();
			long bonus = (long)(duration_ * (ranks * 0.2));
			this.duration += bonus;
		}
	}

	public void noEffect(Entity e, long cTime) {
		// Entity is immune to this effect. Call this method when effect is resisted.
		// Default behavior is to display a status message saying "Resisted!"
		Pair<Float> offset = new Pair<Float>(Player.ABOVE_1);
		if(e instanceof Enemy) {
			Enemy enemy = (Enemy) e;
			offset.y = -((float)enemy.getAnimation().getCurrentAnimation().getSrcSize().y);
		}

		StatusMessages.getInstance().addMessage("Resisted!", e, offset, cTime, 2_000L);
	}

	public abstract void onApply(Entity e, long cTime);
	public abstract void handleEntity(Entity e, long cTime); // used in case special effects require entity knowledge, such as setting emitter position for particle effects
	public abstract void update(Entity e, GameState gs, long cTime, int delta);
	public abstract void render(Graphics g, long cTime);
	public abstract void onDestroy(Entity e, long cTime);
}
