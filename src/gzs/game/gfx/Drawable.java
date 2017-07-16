package gzs.game.gfx;

import gzs.entities.Entity;
import gzs.game.misc.Pair;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Drawable implements Entity {
	private Image img;
	public Image getImage() { return img; }
	public void setImage(Image img_) { this.img = img_; }
	
	private Pair<Double> position;
	public Pair<Double> getPosition() { return position; }
	public void setPosition(Pair<Double> position_) { this.position = position_; }
	public void setPosition(double x, double y) {
		position.x = x;
		position.y = y;
	}
	
	private boolean center;
	public boolean isCentered() { return center; }
	public void setCentered(boolean center_) { this.center = center_; }
	
	/**
	 * Creates a new drawable entity. Used for static images.
	 * By default, the image will be drawn centered on the given position.
	 * @param img_ The image to be drawn.
	 * @param position_ The position to draw the entity at.
	 */
	public Drawable(Image img_, Pair<Double> position_) {
		this(img_, position_, true);
	}
	
	/**
	 * Creates a new drawable entity. Used for static images.
	 * Allows the user to specify whether or not to center the
	 * image on the given position.
	 * @param img_ The image to be drawn.
	 * @param position_ The position to draw the entity at.
	 * @param center_ Whether or not to center the image at the given position.
	 */
	public Drawable(Image img_, Pair<Double> position_, boolean center_) {
		this.img = img_;
		this.position = position_;
		this.center = center_;
	}
	
	@Override
	public void render(GraphicsContext gc, long cT) {
		if(img != null) {
			double locX = position.x - (center?(img.getWidth() / 2):0);
			double locY = position.y - (center?(img.getHeight() / 2):0);
			gc.drawImage(img, locX, locY);
		}
	}

	@Override
	public void update(long cTime) {
		
	}
}
