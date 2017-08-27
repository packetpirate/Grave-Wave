package com.gzsr.objects.weapons;

import java.util.List;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import com.gzsr.gfx.particles.Projectile;
import com.gzsr.gfx.particles.ProjectileType;
import com.gzsr.misc.Pair;

public interface Weapon {
	public abstract void update(long cTime);
	public abstract void render(Graphics g, long cTime);
	public abstract Image getInventoryIcon();
	public abstract int getClipSize();
	public abstract int getClipAmmo();
	public abstract int getInventoryAmmo();
	public abstract void addInventoryAmmo(int amnt);
	public abstract boolean hasWeapon();
	public abstract void activate();
	public abstract boolean canFire(long cTime);
	public abstract void fire(Pair<Float> position, float theta, long cTime);
	public abstract void reload(long cTime);
	public abstract boolean isReloading(long cTime);
	public abstract double getReloadTime(long cTime);
	public abstract List<Projectile> getProjectiles();
	public abstract ProjectileType getProjectile();
}