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
	
	public Animation(Image image_, double fw_, double fh_, int frames_, long delay_) {
		this(image_, fw_, fh_, frames_, delay_, -1, -1);
	}
	
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
	
	public boolean isActive(long cTime) {
		long elapsed = cTime - created;
		return ((lifespan == -1) || (elapsed < lifespan));
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
