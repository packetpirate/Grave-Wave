package com.gzsr.gfx.particles;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import com.gzsr.AssetManager;
import com.gzsr.entities.Entity;
import com.gzsr.misc.Pair;
import com.gzsr.states.GameState;

public class Particle implements Entity {
	protected String image;
	public String getImageName() { return image; }
	public Image getImage() { return AssetManager.getManager().getImage(image); }
	protected Color color;
	public Color getColor() { return color; }
	protected Pair<Float> position;
	public Pair<Float> getPosition() { return position; }
	protected float velocity;
	public float getVelocity() { return velocity; }
	protected float theta;
	public float getTheta() { return theta; }
	protected float angularVelocity;
	public float getAngularVelocity() { return angularVelocity; }
	protected Pair<Float> size;
	public Pair<Float> getSize() { return size; }
	protected long lifespan;
	public long getLifespan() { return lifespan; }
	public boolean isAlive(long cTime) {
		long elapsed = cTime - created;
		return (!collision && (elapsed < lifespan));
	}
	protected long created;
	public long getCreated() { return created; }
	protected boolean collision;
	public void collide() { collision = true; }
	
	public Particle(Color color_, Pair<Float> position_, float velocity_, float theta_, 
					float angularVelocity_, Pair<Float> size_, long lifespan_, long created_) {
		this(null, color_, position_, velocity_, theta_, 
			 angularVelocity_, size_, lifespan_, created_);
	}
	
	public Particle(String image_, Color color_, Pair<Float> position_, float velocity_,
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
		this.image = p.getImageName();
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
	
	public void onDestroy(GameState gs, long cTime) {
		// To be overridden.
	}

	@Override
	public void update(GameState gs, long cTime) {
		if(isAlive(cTime)) {
			position.x += velocity * (float)Math.cos(theta - (Math.PI / 2));
			position.y += velocity * (float)Math.sin(theta - (Math.PI / 2));
			theta += angularVelocity;
		}
	}

	@Override
	public void render(Graphics g, long cTime) {
		g.rotate(position.x, position.y, (float)Math.toDegrees(theta));
		
		Image img = getImage();
		if(img != null) {
			g.drawImage(img, (position.x - (img.getWidth() / 2)), 
							 (position.y - (img.getHeight() / 2)));
		} else {
			float x = position.x - (size.x / 2);
			float y = position.y - (size.y / 2);
			
			g.setColor(color);
			g.fillRect(x, y, size.x, size.y);
		}
		
		g.resetTransform();
	}
}
