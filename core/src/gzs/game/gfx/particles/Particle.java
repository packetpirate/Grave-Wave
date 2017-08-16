package gzs.game.gfx.particles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import gzs.entities.Entity;
import gzs.game.misc.Pair;

public class Particle implements Entity {
	private Texture image;
	public Texture getImage() { return image; }
	private Color color;
	public Color getColor() { return color; }
	private Pair<Double> position;
	public Pair<Double> getPosition() { return position; }
	private Pair<Double> velocity;
	public Pair<Double> getVelocity() { return velocity; }
	private double theta;
	public double getTheta() { return theta; }
	private double angularVelocity;
	public double getAngularVelocity() { return angularVelocity; }
	private Pair<Double> size;
	public Pair<Double> getSize() { return size; }
	private long lifespan;
	public long getLifespan() { return lifespan; }
	public boolean isAlive(long cTime) {
		long elapsed = cTime - created;
		return (!collision && (elapsed < lifespan));
	}
	private long created;
	public long getCreated() { return created; }
	private boolean collision;
	public void collide() { collision = true; }
	
	public Particle(Color color_, Pair<Double> position_, double velocity_, double theta_, 
					double angularVelocity_, Pair<Double> size_, long lifespan_, long created_) {
		this(null, color_, position_, velocity_, theta_, 
			 angularVelocity_, size_, lifespan_, created_);
	}
	
	public Particle(Texture image_, Color color_, Pair<Double> position_, double velocity_,
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
		this.collision = false;
	}
	
	public Particle(Particle p) {
		// Copy constructor.
		this.image = p.getImage();
		this.color = p.getColor();
		this.position = new Pair<Double>(p.getPosition().x, p.getPosition().y);
		this.velocity = new Pair<Double>(p.getVelocity().x, p.getVelocity().y);
		this.theta = p.getTheta();
		this.angularVelocity = p.getAngularVelocity();
		this.size = new Pair<Double>(p.getSize().x, p.getSize().y);
		this.lifespan = p.getLifespan();
		this.created = p.getCreated();
		this.collision = false;
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
	public void render(SpriteBatch batch, long cTime) {
		if(image != null) {
			batch.begin();
			batch.draw(image, 
					   (float)(position.x - (image.getWidth() / 2)), 
					   (float)(position.y - (image.getHeight() / 2)), 
					   0.0f, 0.0f, 
					   (float)image.getWidth(), (float)image.getHeight(), 
					   1.0f, 1.0f, (float)(Math.toDegrees(theta + (Math.PI / 2))), 
					   0, 0, image.getWidth(), image.getHeight(), 
					   false, true);
			batch.end();
		} else {
			double x = position.x - (size.x / 2);
			double y = position.y - (size.y / 2);
			ShapeRenderer sr = new ShapeRenderer();
			sr.begin(ShapeType.Filled);
			sr.setColor(color);
			sr.rotate(0.0f, 0.0f, 1.0f, (float)(Math.toDegrees(theta + (Math.PI / 2))));
			sr.rect((float)x, (float)y, 
					size.x.floatValue(), size.y.floatValue());
			sr.end();
		}
	}
}