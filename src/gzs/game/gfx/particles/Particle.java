package gzs.game.gfx.particles;

import gzs.entities.Entity;
import gzs.game.misc.Pair;
import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;

public class Particle implements Entity {
	private Image image;
	private Color color;
	private Pair<Double> position;
	public Pair<Double> getPosition() { return position; }
	private Pair<Double> velocity;
	private double theta;
	private double angularVelocity;
	private Pair<Double> size;
	private long lifespan;
	public boolean isAlive(long cTime) {
		long elapsed = cTime - created;
		return (elapsed < lifespan);
	}
	private long created;
	
	public Particle(Color color_, Pair<Double> position_, double velocity_, double theta_, 
					double angularVelocity_, Pair<Double> size_, long lifespan_, long created_) {
		this(null, color_, position_, velocity_, theta_, 
			 angularVelocity_, size_, lifespan_, created_);
	}
	
	public Particle(Image image_, Color color_, Pair<Double> position_, double velocity_,
					double theta_, double angularVelocity_, Pair<Double> size_, long lifespan_,
					long created_) {
		this.image = image_;
		this.color = color_;
		this.position = position_;
		this.velocity = new Pair<Double>((Math.cos(theta_) * velocity_), 
										 (Math.sin(theta_) * velocity_));
		this.theta = theta_;
		this.angularVelocity = angularVelocity_;
		this.size = size_;
		this.lifespan = lifespan_;
		this.created = created_;
	}

	@Override
	public void update(long cTime) {
		if(isAlive(cTime)) {
			this.position.x += this.velocity.x;
			this.position.y += this.velocity.y;
			this.theta += this.angularVelocity;
		}
	}

	@Override
	public void render(GraphicsContext gc, long cTime) {
		if(image != null) {
			ImageView iv = new ImageView(image);
			SnapshotParameters params = new SnapshotParameters();
			params.setFill(Color.TRANSPARENT);
		    params.setTransform(new Rotate(Math.toDegrees(theta + (Math.PI / 2)), 
		    							  (image.getWidth() / 2), image.getHeight() / 2));
		    params.setViewport(new Rectangle2D(0, 0, image.getWidth(), image.getHeight()));
			gc.drawImage(iv.snapshot(params, null), (position.x - (image.getWidth() / 2)), 
							  						(position.y - (image.getHeight() / 2)));
		} else {
			double x = position.x - (size.x / 2);
			double y = position.y - (size.y / 2);
			gc.setFill(color);
			gc.fillOval(x, y, size.x, size.y);
		}
	}
}
