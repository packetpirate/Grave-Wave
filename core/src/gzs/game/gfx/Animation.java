package gzs.game.gfx;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import gzs.game.misc.Pair;

public class Animation {
	private Texture image;
	private int sx, sy, sw, sh;
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
	public Animation(Texture image_, int fw_, int fh_, int frames_, long delay_) {
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
	public Animation(Texture image_, int fw_, int fh_, int frames_,
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
	 * @param batch The sprite batch used for drawing.
	 * @param position The position to render the animation at.
	 * @param cTime The current time (in milliseconds).
	 */
	public void render(SpriteBatch batch, Pair<Float> position, long cTime) {
		if(image != null) {
			batch.begin();
			batch.draw(image, 
					   position.x, position.y,
					   sw, sh,
					   sx, sy, sw, sh, 
					   false, true);
			batch.end();
		}
	}
	
	/**
	 * Render the current frame of the animation.
	 * @param batch The sprite batch used for drawing.
	 * @param position The position to render the animation at.
	 * @param theta The angle to rotate the animation by. Measured in radians.
	 * @param cTime The current time (in milliseconds).
	 */
	public void render(SpriteBatch batch, Pair<Float> position, float theta, long cTime) {
		if(image != null) {
			batch.begin();
			batch.draw(image, 
					   position.x, position.y, 
					   0.0f, 0.0f, sw, sh, 1, 1, 
					   (float)(Math.toDegrees(theta + (Math.PI / 2))), 
					   sx, sy, sw, sh, 
					   false, true);
			batch.end();
		}
	}
}
