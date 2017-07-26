package gzs.entities.enemies;

import gzs.entities.Player;
import gzs.game.misc.Pair;
import gzs.math.Calculate;
import javafx.scene.canvas.GraphicsContext;

public class Zumby extends Enemy {
	private static final double HEALTH = 100;
	private static final double SPEED = 3.5;
	private static final double DPS = 5.0;
	
	public Zumby(Pair<Double> position_) {
		super(EnemyType.ZUMBY, position_);
		this.health = Zumby.HEALTH;
	}

	@Override
	public boolean isAlive(long cTime) {
		return (health > 0);
	}

	@Override
	public void move(Player player) {
		theta = Calculate.Hypotenuse(position, player.getPosition());
		position.x += Math.cos(theta) * Zumby.SPEED;
		position.y += Math.sin(theta) * Zumby.SPEED;
	}

	@Override
	public void takeDamage(double amnt) {
		health -= amnt;
	}
	
	@Override
	public double getDamage() {
		return Zumby.DPS;
	}

	@Override
	public boolean checkCollision(Pair<Double> p) {
		return (Calculate.Distance(p, position) <= animation.getSize());
	}
	
	@Override
	public void update(long cTime) {
		super.update(cTime);
	}
	
	@Override
	public void render(GraphicsContext gc, long cTime) {
		super.render(gc, cTime);
	}
}
