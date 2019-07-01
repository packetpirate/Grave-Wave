package com.grave.objects.items;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Sound;
import org.newdawn.slick.state.BasicGameState;

import com.grave.AssetManager;
import com.grave.entities.Entity;
import com.grave.entities.Player;
import com.grave.gfx.Layers;
import com.grave.misc.Pair;

public abstract class Item implements Entity {
	private static final long BLINK_START = 3000L;
	private static final long BLINK_INTERVAL = 500L;
	private static final long BLINK_DURATION = 250L;

	protected Pair<Float> position;
	public Pair<Float> getPosition() { return position; }
	protected String iconName;
	public Image getIcon() {
		return AssetManager.getManager().getImage(iconName);
	}

	protected Sound pickup;

	protected long duration;
	protected long created;

	protected boolean blinking;
	protected long lastBlink;

	public Item(Pair<Float> pos, long cTime) {
		// If you do not use a custom constructor, the item will die instantly.
		position = pos;
		// You must also set the image or it will be null.
		iconName = null;
		pickup = null;
		created = cTime;
		duration = 0L;
		blinking = false;
		lastBlink = 0L;
	}

	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
		if(!blinking && ((cTime - created) >= (duration - BLINK_START))) {
			blinking = true;
			lastBlink = cTime;
		} else if(blinking && ((cTime - lastBlink) >= BLINK_INTERVAL)) {
			lastBlink = cTime;
		}

	}

	@Override
	public void render(Graphics g, long cTime) {
		Image icon = getIcon();
		if(isActive(cTime) && (icon != null)) {
			boolean draw = !blinking || (blinking && ((cTime - lastBlink) >= BLINK_DURATION));
			if(draw) {
				float x = position.x - (icon.getWidth() / 2);
				float y = position.y - (icon.getHeight() / 2);
				g.drawImage(icon, x, y);
			}
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
	public abstract int getCost();

	@Override
	public String getTag() { return "item"; }

	@Override
	public int getLayer() { return Layers.ITEMS.val(); }
}
