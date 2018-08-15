package com.gzsr.gfx.particles;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.state.BasicGameState;

import com.gzsr.AssetManager;
import com.gzsr.Globals;
import com.gzsr.entities.Entity;
import com.gzsr.entities.Player;
import com.gzsr.entities.enemies.Enemy;
import com.gzsr.gfx.Animation;
import com.gzsr.gfx.ColorGenerator;
import com.gzsr.gfx.Layers;
import com.gzsr.misc.Pair;
import com.gzsr.states.GameState;

public class Particle implements Entity {
	protected Animation animation;
	public Animation getAnimation() { return animation; }
	protected String image;
	public String getImageName() { return image; }
	public Image getImage() { return AssetManager.getManager().getImage(image); }
	protected Color color;
	public Color getColor() { return color; }
	protected ColorGenerator colorGenerator;
	public ColorGenerator getColorGenerator() { return colorGenerator; }
	
	protected Pair<Float> position;
	public Pair<Float> getPosition() { return position; }
	public void setPosition(Pair<Float> newPos) {
		position.x = newPos.x;
		position.y = newPos.y;
	}
	protected Shape bounds;
	public Shape getCollider() { return bounds; }
	
	protected float velocity;
	public float getVelocity() { return velocity; }
	public void setVelocity(float velocity_) { velocity = velocity_; }
	protected float theta;
	public float getTheta() { return theta; }
	public void setTheta(float theta_) { theta = theta_; }
	protected float rotation;
	public float getRotation() { return rotation; }
	public void setRotation(float rotation_) { rotation = rotation_; }
	protected float angularVelocity;
	public float getAngularVelocity() { return angularVelocity; }
	protected Pair<Float> size;
	public Pair<Float> getSize() { return size; }
	
	protected long lifespan;
	public long getLifespan() { return lifespan; }
	public boolean isActive(long cTime) { return (isAlive(cTime) || shouldDraw(cTime)); }
	public boolean isAlive(long cTime) {
		long elapsed = cTime - created;
		return ((lifespan == -1L) || (!collision && (elapsed < lifespan)));
	}
	protected long drawTime; // The time to draw the particle for (can last longer than lifespan).
	public boolean shouldDraw(long cTime) { return ((lifespan == -1L) || ((cTime - created) < drawTime)); }
	public long getDrawTime() { return drawTime; }
	public void setDrawTime(long val_) { drawTime = val_; }
	protected long created;
	public long getCreated() { return created; }
	public void setCreated(long created_) { created = created_; }
	
	protected boolean collision;
	public boolean collide(GameState gs, Entity e, long cTime) { 
		collision = true;
		return true;
	}
	
	public Particle(Color color_, Pair<Float> position_, float velocity_, float theta_, 
					float angularVelocity_, Pair<Float> size_, long lifespan_, long created_) {
		this(null, color_, position_, velocity_, theta_, 
			 angularVelocity_, size_, lifespan_, created_);
	}
	
	public Particle(String image_, Color color_, Pair<Float> position_, float velocity_,
					float theta_, float angularVelocity_, Pair<Float> size_, long lifespan_,
					long created_) {
		this.animation = null;
		this.image = image_;
		this.color = color_;
		
		this.position = position_;
		this.velocity = velocity_;
		this.theta = theta_;
		this.rotation = theta_;
		this.angularVelocity = angularVelocity_;
		this.size = size_;
		
		this.lifespan = lifespan_;
		this.drawTime = lifespan_;
		this.created = created_;
		this.collision = false;
		
		resetBounds();
	}
	
	public Particle(ColorGenerator colorGenerator_, Pair<Float> position_, float velocity_, float theta_, 
				float angularVelocity_, Pair<Float> size_, long lifespan_, long created_) {
		this(null, colorGenerator_, position_, velocity_, theta_, 
			 angularVelocity_, size_, lifespan_, created_);
	}
	
	public Particle(String image_, ColorGenerator colorGenerator_, Pair<Float> position_, float velocity_,
				float theta_, float angularVelocity_, Pair<Float> size_, long lifespan_,
				long created_) {
		this.animation = null;
		this.image = image_;
		this.colorGenerator = colorGenerator_;
		
		this.position = position_;
		this.velocity = velocity_;
		this.theta = theta_;
		this.rotation = theta_;
		this.angularVelocity = angularVelocity_;
		this.size = size_;
		
		this.lifespan = lifespan_;
		this.drawTime = lifespan_;
		this.created = created_;
		this.collision = false;
		
		resetBounds();
	}
	
	public Particle(Animation animation_, Pair<Float> position_, float velocity_, float theta_,
					float angularVelocity_, Pair<Float> size_, long lifespan_, long created_) {
		this.animation = animation_;
		this.image = null;
		this.color = null;
		this.colorGenerator = null;
		
		this.position = position_;
		this.velocity = velocity_;
		this.theta = theta_;
		this.rotation = (theta_ - (float)(Math.PI / 2)); // Need to subtract 90 degrees because render call doesn't rotate when rendering an animation.
		this.angularVelocity = angularVelocity_;
		this.size = size_;
		
		this.lifespan = lifespan_;
		this.drawTime = lifespan_;
		this.created = created_;
		this.collision = false;
		
		resetBounds();
	}
	
	public Particle(Particle p) {
		// Copy constructor.
		this.animation = ((p.getAnimation() != null) ? new Animation(p.getAnimation()) : null);
		this.image = p.getImageName();
		this.color = p.getColor();
		this.colorGenerator = p.getColorGenerator();
		this.position = new Pair<Float>(p.getPosition().x, p.getPosition().y);
		this.velocity = p.getVelocity();
		this.theta = p.getTheta();
		this.rotation = p.getRotation();
		this.angularVelocity = p.getAngularVelocity();
		this.size = new Pair<Float>(p.getSize().x, p.getSize().y);
		this.lifespan = p.getLifespan();
		this.drawTime = p.getDrawTime();
		this.created = p.getCreated();
		this.collision = false;
		this.bounds = p.getCollider();
	}
	
	public void onDestroy(GameState gs, long cTime) {
		// To be overridden.
	}

	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
		if(isAlive(cTime)) {
			if(animation != null) animation.update(cTime);
			position.x += velocity * delta * (float)Math.cos(theta - (Math.PI / 2));
			position.y += velocity * delta * (float)Math.sin(theta - (Math.PI / 2));
			theta += angularVelocity;
			bounds.setLocation((position.x - (size.x / 2)), (position.y - (size.y / 2)));
		}
	}

	@Override
	public void render(Graphics g, long cTime) {
		if(shouldDraw(cTime)) {
			float a = (float)Math.toDegrees(rotation);
			
			
			Image img = getImage();
			if(animation != null) {
				animation.render(g, position, rotation);
			} else if(img != null) {
				g.rotate(position.x, position.y, a);
				g.drawImage(img, (position.x - (img.getWidth() / 2)), 
								 (position.y - (img.getHeight() / 2)));
				g.rotate(position.x, position.y, -a);
			} else {
				g.rotate(position.x, position.y, a);
				
				float x = position.x - (size.x / 2);
				float y = position.y - (size.y / 2);
				
				if(colorGenerator == null) g.setColor(color);
				else g.setColor(colorGenerator.generate());
				g.fillRect(x, y, size.x, size.y);
				
				g.rotate(position.x, position.y, -a);
			}
			
			if(Globals.SHOW_COLLIDERS) {
				g.rotate(position.x, position.y, a);
				g.setColor(Color.red);
				g.draw(bounds);
				g.rotate(position.x, position.y, -a);
			}
		}
	}
	
	public void resetBounds() {
		bounds = new Rectangle((position.x - (size.x / 2)), (position.y - (size.y / 2)), size.x, size.y);
	}
	
	public boolean checkCollision(Enemy enemy) {
		return enemy.getCollider().intersects(bounds);
	}
	
	public boolean checkCollision(Player player) {
		return player.getCollider().intersects(bounds);
	}
	
	@Override
	public String getName() {
		return "Particle";
	}
	
	@Override
	public String getDescription() {
		return "Particle";
	}
	
	@Override
	public int getLayer() {
		return Layers.PARTICLES.val();
	}
}
