package gzs.game.objects.weapons;

import java.util.List;

import gzs.game.gfx.particles.Particle;
import gzs.game.misc.Pair;
import javafx.scene.canvas.GraphicsContext;

public interface Weapon {
	public abstract void update(long cTime);
	public abstract void render(GraphicsContext gc, long cTime);
	public abstract int getClipSize();
	public abstract int getClipAmmo();
	public abstract int getInventoryAmmo();
	public abstract boolean canFire(long cTime);
	public abstract void fire(Pair<Double> position, double theta, long cTime);
	public abstract void reload(long cTime);
	public abstract boolean isReloading(long cTime);
	public abstract List<Particle> getProjectiles();
	public abstract ProjectileType getProjectile();
}
