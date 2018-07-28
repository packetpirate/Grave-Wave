package com.gzsr.gfx;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import com.gzsr.AssetManager;
import com.gzsr.misc.Pair;

public class Animation {
	private String imageName;
	public String getImageName() { return imageName; }
	private Image getImage() { return AssetManager.getManager().getImage(imageName); }
	private Pair<Integer> srcPos;
	public Pair<Integer> getSrcPos() { return srcPos; }
	private Pair<Integer> srcSize;
	public Pair<Integer> getSrcSize() { return srcSize; }
	private int frames, current;
	public int getFrames() { return frames; }
	public int getCurrentFrame() { return current; }
	private long delay, lastUpdate;
	public long getDelay() { return delay; }
	private long lifespan, created;
	public long getLifespan() { return lifespan; }
	private boolean started;
	
	/**
	 * Creates a new Animation used for various game entities.
	 * @param image_ The string representing the name of the asset containing the sprite sheet.
	 * @param fw_ The width of each frame of the animation.
	 * @param fh_ The height of each frame of the animation.
	 * @param frames_ The number of frames in the animation.
	 * @param delay_ The delay (in milliseconds) between each frame of the animation.
	 */
	public Animation(String imageName_, int fw_, int fh_, int frames_, long delay_) {
		this(imageName_, fw_, fh_, frames_, delay_, -1L, -1L);
		this.started = true;
	}
	
	/**
	 * Creates a new Animation used for various game entities.
	 * Instantiating an Animation this way creates an animation with an expiration.
	 * @param image_ The string representing the name of the asset containing the sprite sheet.
	 * @param fw_ The width of each frame of the animation.
	 * @param fh_ The height of each frame of the animation.
	 * @param frames_ The number of frames in the animation.
	 * @param delay_ The delay (in milliseconds) between each frame of the animation.
	 * @param lifespan_ The length (in milliseconds) of the animation.
	 * @param created_ The time the Animation was created. Animation expires after time has elapsed equal to its lifespan from this time.
	 */
	public Animation(String imageName_, int fw_, int fh_, int frames_,
					 long delay_, long lifespan_, long created_) {
		this.imageName = imageName_;
		
		this.srcPos = new Pair<Integer>(0, 0);
		this.srcSize = new Pair<Integer>(fw_, fh_);
		
		this.frames = frames_;
		this.current = 0;
		this.delay = delay_;
		this.lastUpdate = 0L;
		this.lifespan = lifespan_;
		this.created = created_;
		this.started = false;
	}
	
	/**
	 * Copy constructor. Used by AssetManager to retrieve copies of an animation to be used by individual entities.
	 * @param copy The animation to make a copy of.
	 */
	public Animation(Animation copy) {
		this.imageName = copy.getImageName();
		
		this.srcPos = new Pair<Integer>(copy.getSrcPos().x, copy.getSrcPos().y);
		this.srcSize = new Pair<Integer>(copy.getSrcSize().x, copy.getSrcSize().y);
		
		this.frames = copy.getFrames();
		this.current = 0;
		this.delay = copy.getDelay();
		this.lastUpdate = 0L;
		this.lifespan = copy.getLifespan();
		this.created = copy.getLifespan();
	}
	
	/**
	 * Determines if the Animation is still active.
	 * @param cTime The current time (in milliseconds).
	 * @return A boolean representing the state of the Animation.
	 */
	public boolean isActive(long cTime) {
		long elapsed = cTime - created;
		return ((lifespan == -1) || (started && (elapsed < lifespan)));
	}
	
	public void restart(long cTime) {
		srcPos.x = 0;
		srcPos.y = 0;
		current = 0;
		lastUpdate = 0L;
		created = cTime;
		started = true;
	}
	
	public double getSize() {
		return Math.min(srcSize.x, srcSize.y);
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
			srcPos.x = current * srcSize.x;
			lastUpdate = cTime;
		}
		
		if(!isActive(cTime)) started = false;
	}

	/**
	 * Render the current frame of the animation.
	 * @param g The graphics context used for drawing.
	 * @param position The position to render the animation at.
	 */
	public void render(Graphics g, Pair<Float> position) {
		render(g, position, 0.0f);
	}
	
	/**
	 * Render the current frame of the animation.
	 * @param g The graphics context used for drawing.
	 * @param position The position to render the animation at.
	 * @param theta The angle to rotate the animation by. Measured in radians.
	 */
	public void render(Graphics g, Pair<Float> position, float theta) {
		render(g, position, position, theta, false);
	}
	
	/**
	 * Render the current frame of the animation.
	 * @param g The graphics context used for drawing.
	 * @param position The position to render the animation at.
	 * @param theta The angle to rotate the animation by. Measured in radians.
	 * @param drawFlash Whether or not to draw this frame of the animation as a white silhouette.
	 */
	public void render(Graphics g, Pair<Float> position, float theta, boolean drawFlash) {
		render(g, position, position, theta, drawFlash);
	}
	
	/**
	 * Render the current frame of the animation.
	 * @param g The graphics context used for drawing.
	 * @param position The position to render the animation at.
	 * @param pivot The pivot point by which to rotate the animation around.
	 * @param theta The angle to rotate the animation by. Measured in radians.
	 */
	public void render(Graphics g, Pair<Float> position, Pair<Float> pivot, float theta) {
		render(g, position, pivot, theta, false);
	}
	
	/**
	 * Render the current frame of the animation.
	 * @param g The graphics context used for drawing.
	 * @param position The position to render the animation at.
	 * @param pivot The pivot point by which to rotate the animation around.
	 * @param theta The angle to rotate the animation by. Measured in radians.
	 */
	public void render(Graphics g, Pair<Float> position, Pair<Float> pivot, float theta, boolean drawFlash) {
		float tlx = position.x - (srcSize.x / 2);
		float tly = position.y - (srcSize.y / 2);
		Image image = getImage().getSubImage((int)srcPos.x, (int)srcPos.y, srcSize.x, srcSize.y);
		
		if(image != null) {
			float a = (float)Math.toDegrees(theta + (float)(Math.PI / 2));
			g.rotate(pivot.x, pivot.y, a);
			
			if(drawFlash) image.drawFlash(tlx, tly);
			else g.drawImage(image, tlx, tly);
			
			g.rotate(pivot.x, pivot.y, -a);
		}
	}
	
	/**
	 * Render the current frame of the animation.
	 * @param g The graphics context used for drawing.
	 * @param position The position to render the animation at.
	 * @param size The size to override the size of the image with.
	 */
	public void render(Graphics g, Pair<Float> position, Pair<Float> size) {
		float tlx = position.x - (size.x / 2);
		float tly = position.y - (size.y / 2);
		Image image = getImage().getSubImage((int)srcPos.x, (int)srcPos.y, srcSize.x, srcSize.y);
		
		if(image != null) {
			float scale = (size.x / image.getWidth());
			image.draw(tlx, tly, scale);
		}
	}
}
