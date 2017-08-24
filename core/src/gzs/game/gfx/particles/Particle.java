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
	private Pair<Float> position;
	public Pair<Float> getPosition() { return position; }
	private float velocity;
	public float getVelocity() { return velocity; }
	private float theta;
	public float getTheta() { return theta; }
	private float angularVelocity;
	public float getAngularVelocity() { return angularVelocity; }
	private Pair<Float> size;
	public Pair<Float> getSize() { return size; }
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
	
	public Particle(Color color_, Pair<Float> position_, float velocity_, float theta_, 
					float angularVelocity_, Pair<Float> size_, long lifespan_, long created_) {
		this(null, color_, position_, velocity_, theta_, 
			 angularVelocity_, size_, lifespan_, created_);
	}
	
	public Particle(Texture image_, Color color_, Pair<Float> position_, float velocity_,
					float theta_, float angularVelocity_, Pair<Float> size_, long lifespan_,
					long created_) {
		this.image = image_;
		this.color = color_;
		this.position = position_;
		this.velocity = velocity_;
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
		this.position = new Pair<Float>(p.getPosition().x, p.getPosition().y);
		this.velocity = p.getVelocity();
		this.theta = p.getTheta();
		this.angularVelocity = p.getAngularVelocity();
		this.size = new Pair<Float>(p.getSize().x, p.getSize().y);
		this.lifespan = p.getLifespan();
		this.created = p.getCreated();
		this.collision = false;
	}

	@Override
	public void update(long cTime) {
		if(isAlive(cTime)) {
			position.x += velocity * (float)Math.cos(theta - (Math.PI / 2));
			position.y += velocity * (float)Math.sin(theta - (Math.PI / 2));
			theta += angularVelocity;
		}
	}

	@Override
	public void render(SpriteBatch batch, ShapeRenderer sr, long cTime) {
		if(image != null) {
			batch.begin();
			batch.draw(image, 
					   (position.x - (image.getWidth() / 2)), 
					   (position.y - (image.getHeight() / 2)), 
					   0.0f, 0.0f, 
					   image.getWidth(), image.getHeight(), 
					   1.0f, 1.0f, (float)Math.toDegrees(theta), 
					   0, 0, image.getWidth(), image.getHeight(), 
					   false, true);
			batch.end();
		} else {
			float x = position.x - (size.x / 2);
			float y = position.y - (size.y / 2);
			
			sr.begin(ShapeType.Filled);
			sr.setColor(color);
			sr.identity();
			sr.translate(position.x, position.y, 0.0f);
			sr.rotate(0.0f, 0.0f, 1.0f, (float)Math.toDegrees(theta));
			sr.translate(-position.x, -position.y, 0.0f);
			sr.rect(x, y, size.x, size.y);
			sr.end();
		}
	}
}