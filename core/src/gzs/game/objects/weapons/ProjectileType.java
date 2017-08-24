package gzs.game.objects.weapons;

import com.badlogic.gdx.graphics.Color;

public enum ProjectileType {
	HANDGUN(new Color(0.95f, 0.95f, 0.55f, 1.0f), 75.0f, 3.0f, 10.0f, 1500),
	ASSAULT(new Color(0.95f, 0.95f, 0.75f, 1.0f), 75.0f, 3.0f, 12.0f, 2000),
	SHOTGUN(new Color(0.95f, 0.95f, 0.55f, 1.0f), 40.0f, 4.0f, 4.0f, 800);
	
	private Color color;
	public Color getColor() { return color; }
	
	private float velocity;
	public float getVelocity() { return velocity; }
	
	private float width;
	public float getWidth() { return width; }
	
	private float height;
	public float getHeight() { return height; }
	
	private long lifespan;
	public long getLifespan() { return lifespan; }
	
	ProjectileType(Color color_, float velocity_, float width_, float height_, long lifespan_) {
		this.color = color_;
		this.velocity = velocity_;
		this.width = width_;
		this.height = height_;
		this.lifespan = lifespan_;
	}
}