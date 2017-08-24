package gzs.game.objects.weapons;

import java.util.List;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import gzs.game.gfx.particles.Projectile;
import gzs.game.misc.Pair;

public interface Weapon {
	public abstract void update(long cTime);
	public abstract void render(SpriteBatch batch, ShapeRenderer sr, long cTime);
	public abstract Texture getInventoryIcon();
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