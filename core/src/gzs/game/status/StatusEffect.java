package gzs.game.status;

import com.badlogic.gdx.graphics.Texture;

import gzs.entities.Player;

public abstract class StatusEffect {
	protected Status status;
	public Status getStatus() { return status; }
	public Texture getIcon() { return status.getIcon(); }
	
	protected long duration;
	protected long created;
	public boolean isActive(long cTime) {
		long elapsed = cTime - created;
		return (elapsed <= duration);
	}
	public void refresh(long cTime) {
		this.created = cTime;
	}
	public double getPercentageTimeLeft(long cTime) {
		long elapsed = cTime - created;
		return (1.0 - ((double)elapsed / (double)duration));
	}
	
	public StatusEffect(Status status_, long duration_, long created_) {
		this.status = status_;
		this.duration = duration_;
		this.created = created_;
	}
	
	public abstract void update(Player player, long cTime);
	public abstract void onDestroy(Player player, long cTime);
}