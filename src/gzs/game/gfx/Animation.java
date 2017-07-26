package gzs.game.gfx;

import gzs.game.misc.Pair;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Animation {
	private Image image;
	private double sx, sy, sw, sh;
	private int frames, current;
	private long delay, lastUpdate;
	private long lifespan, created;
	
	/**
	 * Creates a new Animation used for various game entities.
	 * @param image_ The sprite sheet used to represent this animation.
	 * @param fw_ The width of each frame of the animation.
	 * @param fh_ The height of each frame of the animation.
	 * @param frames_ The number of frames in the animation.
	 * @param delay_ The delay (in milliseconds) between each frame of the animation.
	 */
	public Animation(Image image_, double fw_, double fh_, int frames_, long delay_) {
		this(image_, fw_, fh_, frames_, delay_, -1, -1);
	}
	
	/**
	 * Creates a new Animation used for various game entities.
	 * Instantiating an Animation this way creates an animation with an expiration.
	 * @param image_ The sprite sheet used to represent this animation.
	 * @param fw_ The width of each frame of the animation.
	 * @param fh_ The height of each frame of the animation.
	 * @param frames_ The number of frames in the animation.
	 * @param delay_ The delay (in milliseconds) between each frame of the animation.
	 * @param lifespan_ The length (in milliseconds) of the animation.
	 * @param created_ The time the Animation was created. Animation expires after time has elapsed equal to its lifespan from this time.
	 */
	public Animation(Image image_, double fw_, double fh_, int frames_,
					 long delay_, long lifespan_, long created_) {
		this.image = image_;
		
		this.sx = 0;
		this.sy = 0;
		this.sw = fw_;
		this.sh = fh_;
		
		this.frames = frames_;
		this.current = 0;
		this.delay = delay_;
		this.lastUpdate = 0;
		this.lifespan = lifespan_;
		this.created = created_;
	}
	
	/**
	 * Determines if the Animation is still active.
	 * @param cTime The current time (in milliseconds).
	 * @return A boolean representing the state of the Animation.
	 */
	public boolean isActive(long cTime) {
		long elapsed = cTime - created;
		return ((lifespan == -1) || (elapsed < lifespan));
	}
	
	public double getSize() {
		return Math.min(sw, sh);
	}

	/**
	 * Determines if the current frame of the Animation should be updated according
	 * to the current time (in milliseconds).
	 * @param cTime The current time (in milliseconds).
	 */
	public void update(long cTime) {
		long elapsed = cTime - lastUpdate;
		if(elapsed >= delay) {
			current = (current + 1) % frames;
			sx = current * sw;
			lastUpdate = cTime;
		}
	}

	/**
	 * Render the current frame of the animation.
	 * @param gc The graphics context of the scene.
	 * @param position The position to render the animation at.
	 * @param cTime The current time (in milliseconds).
	 */
	public void render(GraphicsContext gc, Pair<Double> position, long cTime) {
		if(image != null) {
			gc.drawImage(image, sx, sy, sw, sh, 
						 (position.x - (sw / 2)), 
						 (position.y - (sh / 2)), 
						 sw, sh);
		}
	}
	
	/**
	 * Render the current frame of the animation.
	 * @param gc The graphics context of the scene.
	 * @param position The position to render the animation at.
	 * @param theta The angle to rotate the animation by. Measured in radians.
	 * @param cTime The current time (in milliseconds).
	 */
	public void render(GraphicsContext gc, Pair<Double> position, double theta, long cTime) {
		if(image != null) {
			gc.save();
			gc.translate(position.x, position.y);
			gc.rotate(Math.toDegrees(theta + (Math.PI / 2)));
			gc.translate(-position.x, -position.y);
			gc.drawImage(image, sx, sy, sw, sh, 
						 (position.x - (sw / 2)), 
						 (position.y - (sh / 2)), 
						 sw, sh);
			gc.restore();
		}
	}
}
