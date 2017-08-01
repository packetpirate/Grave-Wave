package gzs.game.objects.weapons;

import java.util.List;

import gzs.game.gfx.particles.Projectile;
import gzs.game.misc.Pair;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public interface Weapon {
	public abstract void update(long cTime);
	public abstract void render(GraphicsContext gc, long cTime);
	public abstract Image getInventoryIcon();
	public abstract int getClipSize();
	public abstract int getClipAmmo();
	public abstract int getInventoryAmmo();
	public abstract boolean canFire(long cTime);
	public abstract void fire(Pair<Double> position, double theta, long cTime);
	public abstract void reload(long cTime);
	public abstract boolean isReloading(long cTime);
	public abstract double getReloadTime(long cTime);
	public abstract List<Projectile> getProjectiles();
	public abstract ProjectileType getProjectile();
}
