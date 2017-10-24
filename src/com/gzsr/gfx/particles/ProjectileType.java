package com.gzsr.gfx.particles;

import org.newdawn.slick.Color;

import com.gzsr.objects.weapons.Grenade;

public enum ProjectileType {
	// Player projectiles.
	HANDGUN(new Color(0xF2F28C), 4.0f, 3.0f, 10.0f, 1500L),
	ASSAULT(new Color(0xF2F2BF), 5.0f, 3.0f, 12.0f, 2000L),
	SHOTGUN(new Color(0xF2F28C), 2.0f, 4.0f, 4.0f, 800L),
	FLAMETHROWER(new Color(0xEDA642), 0.5f, 4.0f, 4.0f, 600L),
	GRENADE(new Color(0x4DAD1A), 0.5f, 4.0f, 4.0f, Grenade.LIFESPAN),
	
	// Enemy projectiles.
	BILE(new Color(0x8BCE5E), 0.5f, 3.0f, 3.0f, 400);
	
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
