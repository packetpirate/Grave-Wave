package gzs.game.gfx.particles;

public class Projectile extends Particle {
	private double damage;
	public double getDamage() { return damage; }

	public Projectile(Particle p, double damage_) {
		super(p);
		this.damage = damage_;
	}
}
