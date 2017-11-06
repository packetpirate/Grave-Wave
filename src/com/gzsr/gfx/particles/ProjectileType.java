package com.gzsr.gfx.particles;

import org.newdawn.slick.Color;

import com.gzsr.objects.weapons.Grenade;

public enum ProjectileType {
	// Player projectiles.
	HANDGUN(new Color(0xF2F28C), 2.0f, 3.0f, 10.0f, 1500L),
	ASSAULT(new Color(0xF2F2BF), 2.5f, 3.0f, 12.0f, 2000L),
	SHOTGUN(new Color(0xF2F28C), 1.0f, 4.0f, 4.0f, 800L),
	FLAMETHROWER(new Color(0xEDA642), 0.25f, 4.0f, 4.0f, 600L),
	GRENADE(new Color(0x4DAD1A), 0.25f, 4.0f, 4.0f, Grenade.LIFESPAN),
	CLAYMORE(new Color(0x4D661A), 0.0f, 8.0f, 4.0f, 0L),
	SHRAPNEL(new Color(0xF2F28C), 0.5f, 4.0f, 4.0f, 800L),
	
	// Enemy projectiles.
	BILE(new Color(0x8BCE5E), 0.25f, 3.0f, 3.0f, 400);
	
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
