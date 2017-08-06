package gzs.game.objects.items;

import gzs.entities.Entity;
import gzs.entities.Player;
import gzs.game.misc.Pair;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public abstract class Item implements Entity {
	protected Pair<Double> position;
	public Pair<Double> getPosition() { return position; }
	protected Image icon;
	
	protected long duration;
	protected long created;
	
	public Item(Pair<Double> pos, long cTime) {
		// If you do not use a custom constructor, the item will die instantly.
		position = pos;
		// You must also set the image or it will be null.
		icon = null;
		created = cTime;
		duration = 0L;
	}
	
	@Override
	public void update(long cTime) {
		// Must be overridden.
	}

	@Override
	public void render(GraphicsContext gc, long cTime) {
		if(isActive(cTime) && (icon != null)) {
			double x = position.x - (icon.getWidth() / 2);
			double y = position.y - (icon.getHeight() / 2);
			gc.drawImage(icon, x, y);
		}
	}
	
	public boolean isActive(long cTime) {
		long elapsed = cTime - created;
		return (elapsed <= duration);
	}
	
	public boolean isTouching(double distance) {
		if(icon != null) {
			double width = icon.getWidth();
			double height = icon.getHeight();
			return (distance <= ((width + height) / 2));
		} else return false;
	}
	
	public abstract void apply(Player player, long cTime);
}
