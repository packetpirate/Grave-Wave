package com.gzsr.gfx.particles;

import org.newdawn.slick.Color;

public enum ProjectileType {
	// Player projectiles.
	NAIL(new Color(0xB6B6B6), 2.0f, 8.0f, 16.0f, 100L),
	TASER(new Color(0xE6FF4B), 2.0f, 4.0f, 6.0f, 100L),
	HANDGUN(new Color(0xF2F28C), 2.0f, 3.0f, 10.0f, 150L),
	MAGNUM(new Color(0xF2F28C), 2.0f, 3.0f, 10.f, 250L),
	SMG(new Color(0xF2F28C), 2.5f, 3.0f, 10.0f, 200L),
	ASSAULT(new Color(0xF2F2BF), 2.5f, 3.0f, 12.0f, 240L),
	SHOTGUN(new Color(0xF2F28C), 1.0f, 4.0f, 4.0f, 400L),
	FLAK(new Color(0xEDA642), 1.2f, 4.0f, 4.0f, 500L),
	RIFLE(new Color(0xF2F2BC), 4.0f, 3.0f, 12.0f, 200L),
	ARROW(new Color(0xA58138), 3.0f, 30.0f, 4.0f, 150L),
	BOLT(new Color(0xA58138), 3.5f, 30.0f, 4.0f, 200L),
	FLAMETHROWER(new Color(0xEDA642), 0.25f, 4.0f, 4.0f, 1_200L),
	GRENADE(new Color(0x4DAD1A), 0.4f, 16.0f, 16.0f, 1_000L),
	MISSILE(new Color(0x4DAD1A), 2.0f, 64.0f, 16.0f, 1_000L),
	MOLOTOV(new Color(0x8F563B), 0.25f, 16.0f, 16.0f, 1_000L), // Lifespan set manually according to throw strength.
	CLAYMORE(new Color(0x4D661A), 0.0f, 8.0f, 4.0f, -1L),
	SHRAPNEL(new Color(0xF2F28C), 1.5f, 4.0f, 4.0f, 250L),
	LASERNODE(new Color(0x313C4F), 0.0f, 8.0f, 8.0f, -1L),
	ELECTRICNODE(new Color(0xAAAAAA), 0.8f, 8.0f, 8.0f, -1L),
	TURRET(new Color(0x7A221A), 0.0f, 16.0f, 16.0f, 0L),

	// Enemy projectiles.
	BILE(new Color(0x8BCE5E), 0.25f, 3.0f, 3.0f, 800);

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
