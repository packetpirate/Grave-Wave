package com.gzsr.entities.enemies;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.state.BasicGameState;

import com.gzsr.Globals;
import com.gzsr.achievements.Metrics;
import com.gzsr.controllers.AchievementController;
import com.gzsr.controllers.Scorekeeper;
import com.gzsr.entities.Entity;
import com.gzsr.entities.Player;
import com.gzsr.gfx.Animation;
import com.gzsr.gfx.AnimationState;
import com.gzsr.gfx.Layers;
import com.gzsr.gfx.ui.DamageText;
import com.gzsr.gfx.ui.StatusMessages;
import com.gzsr.math.Calculate;
import com.gzsr.misc.Pair;
import com.gzsr.misc.Vector2f;
import com.gzsr.objects.items.Powerups;
import com.gzsr.objects.items.ResourceDrop;
import com.gzsr.objects.weapons.DamageType;
import com.gzsr.states.GameState;
import com.gzsr.status.StatusHandler;

public abstract class Enemy implements Entity {
	private static long FLASH_DURATION = 100L;

	protected EnemyType type;
	protected AnimationState animation;
	public AnimationState getAnimation() { return animation; }
	protected Shape bounds;
	protected Pair<Float> position;
	public Pair<Float> getPosition() { return position; }
	protected float theta;
	public float getTheta() { return theta; }
	protected float speed;
	public float getSpeed() { return speed; }
	public void setSpeed(float speed_) { this.speed = speed_; }
	public abstract void resetSpeed();
	protected Vector2f velocity;
	public Vector2f getVelocity() { return velocity; }
	protected boolean moveBlocked;
	public void blockMovement() { moveBlocked = true; }
	protected boolean attacking;
	protected long lastAttack;
	public abstract long getAttackDelay();

	protected double health;
	public double getHealth() { return health; }
	public double getDamage() { return 0.0; }
	protected int cash;
	public int getCashValue() { return cash; }
	protected int experience;
	public int getExpValue() { return experience; }
	public abstract ResourceTable getResourceTable();

	protected List<DamageType> damageImmunities;

	protected StatusHandler statusHandler;
	public StatusHandler getStatusHandler() { return statusHandler; }

	protected boolean deathHandled;

	protected boolean hit;
	protected long hitTime;
	protected void updateFlash(long cTime) {
		if(hit) {
			long elapsed = (cTime - hitTime);
			if(elapsed > Enemy.FLASH_DURATION) {
				hit = false;
				hitTime = 0L;
			}
		}
	}
	protected boolean shouldDrawFlash(long cTime) {
		long elapsed = (cTime - hitTime);
		return (hit && (elapsed <= Enemy.FLASH_DURATION));
	}

	protected List<DamageText> damageTexts;

	public Enemy(EnemyType type_, Pair<Float> position_) {
		this.type = type_;

		Animation move = type.getAnimation();
		this.animation = new AnimationState();
		this.animation.addState("move", move);
		this.animation.setCurrent("move");

		this.position = position_;

		float w = move.getSrcSize().x;
		float h = move.getSrcSize().y;
		this.bounds = new Rectangle((position.x - (w / 2)), (position.y - (h / 2)), w, h);

		this.moveBlocked = false;
		this.attacking = false;
		this.lastAttack = -getAttackDelay();
		this.theta = 0.0f;
		this.speed = 0.0f;
		this.velocity = new Vector2f(0.0f, 0.0f);

		this.health = 0.0;
		this.cash = type.getCashValue();
		this.experience = type.getExperience();

		this.damageImmunities = new ArrayList<DamageType>();
		this.statusHandler = new StatusHandler(this);
		this.deathHandled = false;

		this.hit = false;
		this.hitTime = 0L;

		this.damageTexts = new ArrayList<DamageText>();
	}

	public boolean dead() {
		return (health <= 0);
	}

	public boolean isAlive(long cTime) {
		return !dead();
	}

	public abstract String print();

	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
		Player player = Player.getPlayer();

		// All enemies should update.
		if(isAlive(cTime)) {
			// Need to make sure to update the status effects first.
			statusHandler.update((GameState)gs, cTime, delta);

			updateFlash(cTime);
			animation.getCurrentAnimation().update(cTime);

			if(player.isAlive()) {
				if(touchingPlayer()) {
					if(!attacking) {
						long elapsed = (cTime - lastAttack);
						if(elapsed >= getAttackDelay()) {
							player.takeDamage(getDamage(), cTime);
							lastAttack = cTime;
							attacking = true;
							animation.setCurrent("attack"); // has no effect if this enemy has no attack animation
						}
					}
				} else {
					if(!animation.getCurrent().equals("move")) animation.setCurrent("move");
					move((GameState)gs, delta);
				}
			}

			if(attacking) attacking = false;
		}

		postDamageTexts();
	}

	protected void postDamageTexts() {
		if(!damageTexts.isEmpty()) {
			damageTexts.stream().forEach(dt -> StatusMessages.getInstance().addMessage(dt));
			damageTexts.clear();
		}
	}

	protected boolean nearPlayer(float attackDist) {
		return (Calculate.Distance(position, Player.getPlayer().getPosition()) <= attackDist);
	}

	public abstract void move(GameState gs, int delta);
	protected void avoidObstacles(GameState gs, int delta) {
		EnemyController ec = EnemyController.getInstance();
		List<Enemy> allies = ec.getAliveEnemies();

		if(!allies.isEmpty()) {
			Vector2f alignment = computeAlignment(allies);
			Vector2f cohesion = computeCohesion(allies);
			Vector2f separation = computeSeparation(allies);

			velocity.x += (alignment.x + cohesion.x + (separation.x * 5));
			velocity.y += (alignment.y + cohesion.y + (separation.y * 5));

			// Compute the new vector based on alignment, cohesion and separation. Then calculate new theta value.
			velocity = Vector2f.normalize(velocity, (getSpeed() * delta));
			theta = Calculate.Hypotenuse(position, new Pair<Float>((position.x + velocity.x), (position.y + velocity.y)));
		}
	}

	private Vector2f computeAlignment(List<Enemy> allies) {
		Vector2f v = new Vector2f();
		int neighbors = 0;

		for(Enemy e : allies) {
			if((e != this) && (Calculate.Distance(position, e.getPosition()) < 300)) {
				v.x += e.getVelocity().x;
				v.y += e.getVelocity().y;
				neighbors++;
			}
		}

		if(neighbors == 0) return v;

		v.x /= neighbors;
		v.y /= neighbors;
		v = Vector2f.normalize(v, 1);
		return v;
	}

	private Vector2f computeCohesion(List<Enemy> allies) {
		Vector2f v = new Vector2f();
		int neighbors = 0;

		for(Enemy e : allies) {
			if((e != this) && (Calculate.Distance(position, e.getPosition()) < getCohesionDistance())) {
				v.x += e.getPosition().x;
				v.y += e.getPosition().y;
				neighbors++;
			}
		}

		if(neighbors == 0) return v;

		v.x /= neighbors;
		v.y /= neighbors;
		v = new Vector2f((v.x - position.x), (v.y - position.y));
		v = Vector2f.normalize(v, 1);
		return v;
	}

	private Vector2f computeSeparation(List<Enemy> allies) {
		Vector2f v = new Vector2f();
		int neighbors = 0;

		for(Enemy e : allies) {
			if((e != this) && (Calculate.Distance(position, e.getPosition()) < getSeparationDistance())) {
				v.x += e.getPosition().x - position.x;
				v.y += e.getPosition().y - position.y;
				neighbors++;
			}
		}

		if(neighbors == 0) return v;

		v.x /= neighbors;
		v.y /= neighbors;
		v.x *= -1;
		v.y *= -1;
		v = Vector2f.normalize(v, 1);
		return v;
	}

	@Override
	public void render(Graphics g, long cTime) {
		// All enemies should render their animation.
		float pTheta = Calculate.Hypotenuse(position, Player.getPlayer().getPosition());
		if(isAlive(cTime)) animation.getCurrentAnimation().render(g, position, pTheta, shouldDrawFlash(cTime));
		statusHandler.render(g, cTime);

		if(Globals.SHOW_COLLIDERS) {
			g.setColor(Color.red);
			g.draw(bounds);
		}
	}

	public Shape getCollider() { return bounds; }
	public boolean touchingPlayer() {
		return bounds.intersects(Player.getPlayer().getCollider());
	}
	public abstract float getCohesionDistance();
	public abstract float getSeparationDistance();

	public void takeDamage(DamageType dType, double amnt, float knockback, Metrics sourceMetric, long cTime, int delta) {
		takeDamage(dType, amnt, knockback, (float)(theta + Math.PI), sourceMetric, cTime, delta, true);
	}

	public void takeDamage(DamageType dType, double amnt, float knockback, float knockbackTheta, Metrics sourceMetric,  long cTime, int delta, boolean flash) {
		takeDamage(dType, amnt, knockback, knockbackTheta, sourceMetric, cTime, delta, flash, false);
	}

	public void takeDamage(DamageType dType, double amnt, float knockback, float knockbackTheta, Metrics sourceMetric, long cTime, int delta, boolean flash, boolean isCritical) {
		if(!dead() && !damageImmunities.contains(dType)) {
			health -= amnt;

			createDamageText(amnt, 24.0f, knockbackTheta, cTime, isCritical);

			if(flash) {
				hit = true;
				hitTime = cTime;
			}

			if(knockback > 0.0f) {
				float dx = (float)(Math.cos(knockbackTheta) * knockback * delta);
				float dy = (float)(Math.sin(knockbackTheta) * knockback * delta);
				position.x += dx;
				position.y += dy;
			}

			AchievementController.getInstance().postMetric(Metrics.compose(type.getEnemyMetric(), sourceMetric, Metrics.ENEMY, Metrics.DAMAGE));
		}
	}

	protected void createDamageText(double amnt, float dist, float knockbackTheta, long cTime, boolean isCritical) {
		createDamageText(amnt, dist, knockbackTheta, cTime, (isCritical ? Color.red : Color.white));
	}

	protected void createDamageText(double amnt, float dist, float knockbackTheta, long cTime, Color color) {
		float tOff = Globals.rand.nextFloat() * (Globals.rand.nextBoolean() ? 1 : -1) * (float)(Math.PI / 9);
		float x = position.x + (float)(Math.cos(knockbackTheta + tOff) * (dist + (Globals.rand.nextFloat() * 10.0f)));
		float y = position.y + (float)(Math.sin(knockbackTheta + tOff) * dist + (Globals.rand.nextFloat() * 10.0f));
		Pair<Float> dtPos = new Pair<Float>(x, y);
		DamageText dt = new DamageText(Integer.toString((int)amnt), dtPos, (knockbackTheta + tOff), cTime, 500L, color);
		damageTexts.add(dt);
	}

	public void onDeath(GameState gs, long cTime) {
		if(!deathHandled) {
			AchievementController.getInstance().postMetric(Metrics.compose(Metrics.ENEMY, Metrics.KILL));

			Pair<Float> pos = new Pair<Float>(position);
			Powerups.spawnRandomPowerup(gs, this, pos, cTime);
			ResourceDrop drop = getResourceTable().getDrop(pos, cTime);
			if(drop != null) gs.addEntity(String.format("resource%d", Globals.generateEntityID()), drop);

			Scorekeeper.getInstance().addKill();
			postDamageTexts();
		}
		deathHandled = true;
	}

	public abstract LootTable getLootTable();

	@Override
	public int getLayer() {
		return Layers.ENEMIES.val();
	}
}
