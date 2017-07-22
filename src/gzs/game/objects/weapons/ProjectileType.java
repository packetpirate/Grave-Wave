package gzs.game.objects.weapons;

import javafx.scene.paint.Color;

public enum ProjectileType {
	HANDGUN(new Color(0.95, 0.95, 0.55, 1.0), 75.0, 3.0, 10.0, 1500),
	ASSAULT(new Color(0.95, 0.95, 0.75, 1.0), 90.0, 3.0, 12.0, 2000);
	
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
