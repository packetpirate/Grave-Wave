package gzs.game.objects.weapons;

import javafx.scene.paint.Color;

public enum ProjectileType {
	BULLET(Color.GOLD, 15.0, 5.0, 2000);
	
	private Color color;
	public Color getColor() { return color; }
	
	private double velocity;
	public double getVelocity() { return velocity; }
	
	private double size;
	public double getSize() { return size; }
	
	private long lifespan;
	public long getLifespan() { return lifespan; }
	
	ProjectileType(Color color_, double velocity_, double size_, long lifespan_) {
		this.color = color_;
		this.velocity = velocity_;
		this.size = size_;
		this.lifespan = lifespan_;
	}
}
