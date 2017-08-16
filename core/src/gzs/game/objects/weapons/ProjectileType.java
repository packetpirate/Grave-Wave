package gzs.game.objects.weapons;

import com.badlogic.gdx.graphics.Color;

public enum ProjectileType {
	HANDGUN(new Color(0.95f, 0.95f, 0.55f, 1.0f), 75.0, 3.0, 10.0, 1500),
	ASSAULT(new Color(0.95f, 0.95f, 0.75f, 1.0f), 75.0, 3.0, 12.0, 2000),
	SHOTGUN(new Color(0.95f, 0.95f, 0.55f, 1.0f), 40.0, 4.0, 4.0, 800);
	
	private Color color;
	public Color getColor() { return color; }
	
	private double velocity;
	public double getVelocity() { return velocity; }
	
	private double width;
	public double getWidth() { return width; }
	
	private double height;
	public double getHeight() { return height; }
	
	private long lifespan;
	public long getLifespan() { return lifespan; }
	
	ProjectileType(Color color_, double velocity_, double width_, double height_, long lifespan_) {
		this.color = color_;
		this.velocity = velocity_;
		this.width = width_;
		this.height = height_;
		this.lifespan = lifespan_;
	}
}