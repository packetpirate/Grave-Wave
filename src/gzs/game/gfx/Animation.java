package gzs.game.gfx;

import gzs.game.misc.Pair;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Animation {
	private Image image;
	private double sx, sy, sw, sh;
	private int frames, current;
	private long delay, lastUpdate, lifespan;
	
	public Animation(Image image_, double fw_, double fh_, int frames_, long delay_) {
		this(image_, fw_, fh_, frames_, delay_, -1);
	}
	
	public Animation(Image image_, double fw_, double fh_, int frames_,
					 long delay_, long lifespan_) {
		this.image = image_;
		
		this.sx = 0;
		this.sy = 0;
		this.sw = fw_;
		this.sy = fh_;
		
		this.frames = frames_;
		this.current = 0;
		this.delay = delay_;
		this.lifespan = lifespan_;
		this.lastUpdate = 0;
	}
	
	// TODO: Figure out if there's a way to handle this with just the variables we already have.
	public boolean isActive() {
		return true;
	}

	public void update(long cTime) {
		long elapsed = cTime - lastUpdate;
		if(elapsed >= delay) {
			current = (current + 1) % frames;
			sx = current * sw;
			lastUpdate = cTime;
		}
	}

	public void render(GraphicsContext gc, Pair<Double> position, long cTime) {
		if(image != null) {
			gc.drawImage(image, sx, sy, sw, sh, 
						 position.x, position.y, sw, sh);
		}
	}
}
