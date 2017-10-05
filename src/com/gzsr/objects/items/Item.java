package com.gzsr.objects.items;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import com.gzsr.AssetManager;
import com.gzsr.entities.Entity;
import com.gzsr.entities.Player;
import com.gzsr.misc.Pair;

public abstract class Item implements Entity {
	protected Pair<Float> position;
	public Pair<Float> getPosition() { return position; }
	protected String iconName;
	private Image getIcon() {
		return AssetManager.getManager().getImage(iconName);
	}
	
	protected long duration;
	protected long created;
	
	public Item(Pair<Float> pos, long cTime) {
		// If you do not use a custom constructor, the item will die instantly.
		position = pos;
		// You must also set the image or it will be null.
		iconName = null;
		created = cTime;
		duration = 0L;
	}
	
	@Override
	public void update(long cTime) {
		// Must be overridden.
	}

	@Override
	public void render(Graphics g, long cTime) {
		Image icon = getIcon();
		if(isActive(cTime) && (icon != null)) {
			float x = position.x - (icon.getWidth() / 2);
			float y = position.y - (icon.getHeight() / 2);
			g.drawImage(icon, x, y);
		}
	}
	
	public boolean isActive(long cTime) {
		long elapsed = cTime - created;
		return (elapsed <= duration);
	}
	
	public boolean isTouching(double distance) {
		Image icon = getIcon();
		if(icon != null) {
			float width = icon.getWidth();
			float height = icon.getHeight();
			return (distance <= ((width + height) / 2));
		} else return false;
	}
	
	public abstract void apply(Player player, long cTime);
}
