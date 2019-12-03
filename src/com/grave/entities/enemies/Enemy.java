package com.grave.entities.enemies;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.state.BasicGameState;

import com.grave.Globals;
import com.grave.achievements.Metrics;
import com.grave.controllers.AchievementController;
import com.grave.controllers.Scorekeeper;
import com.grave.entities.Entity;
import com.grave.entities.Player;
import com.grave.gfx.Animation;
import com.grave.gfx.AnimationState;
import com.grave.gfx.Layers;
import com.grave.gfx.ui.DamageText;
import com.grave.gfx.ui.StatusMessages;
import com.grave.math.Calculate;
import com.grave.misc.Pair;
import com.grave.misc.Vector2f;
import com.grave.objects.items.Powerups;
import com.grave.objects.items.ResourceDrop;
import com.grave.objects.weapons.DamageType;
import com.grave.states.GameState;
import com.grave.status.StatusHandler;
import com.grave.tmx.TMap;
import com.grave.world.pathing.Path;

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

	protected boolean sawPlayerLastStep;
	protected Pair<Integer> lastKnownPlayerPosition;
	protected Path path;
	protected Pair<Integer> currentNode;

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

		this.sawPlayerLastStep = false;
		this.lastKnownPlayerPosition = null;
		this.path = null;
		this.currentNode = null;

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
		GameState game = (GameState) gs;
		TMap map = game.getLevel().getMap();

		// All enemies should update.
		if(isAlive(cTime)) {
			// Need to make sure to update the status effects first.
			statusHandler.update((GameState)gs, cTime, delta);

			updateFlash(cTime);

			Animation state = animation.getCurrentAnimation();
			if(state != null) state.update(cTime);

			if(player.isAlive()) {
				calculatePath(map);

				if(touchingPlayer()) {
					if(currentNode != null) {
						if(path != null) path.clear();
						currentNode = null;
					}

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

	protected void calculatePath(TMap map) {
		Player player = Player.getPlayer();
		boolean playerSighted = playerInSights(map);
		Pair<Integer> gridCoords = map.worldToGridCoords(position);

		if(playerSighted) {
			// Follow flow field.
			Pair<Integer> pPos = map.worldToGridCoords(player.getPosition());
			if(pPos != lastKnownPlayerPosition) currentNode = player.getFlowField().cheapestNeighbor(gridCoords.x, gridCoords.y);
			lastKnownPlayerPosition = pPos;
			if(currentNode == null) currentNode = player.getFlowField().cheapestNeighbor(gridCoords.x, gridCoords.y);
			if(!sawPlayerLastStep) {
				// TODO: Make zombie noises?
				System.out.println("Found you!");
			}
		} else {
			if(sawPlayerLastStep) {
				System.out.println("Where'd you go?");
				// Just lost sight of player. Calculate A* path to last known player position.
				path = new Path(map, gridCoords, lastKnownPlayerPosition);
				if((path != null) && path.pathPossible()) currentNode = path.getNextNode();
			} else {
				if(path != null) {
					if(!path.pathPossible()) {
						System.out.println("I give up...");
						path = null;
						currentNode = null;
					}
				}
			}
		}

		if(currentNode != null) {
			// Travel towards node.
			Pair<Float> target = new Pair<Float>((float)((currentNode.x * map.getTileWidth()) + (map.getTileWidth() / 2)), (float)((currentNode.y * map.getTileHeight()) + (map.getTileHeight() / 2)));
			int ret = Calculate.FastDistanceCompare(position, target, (bounds.getWidth() / 2));
			if(ret <= 0) {
				if(path != null) currentNode = path.getNextNode();
				else if(playerSighted) currentNode = player.getFlowField().cheapestNeighbor(gridCoords.x, gridCoords.y);

				if(currentNode != null) {
					target = new Pair<Float>((float)((currentNode.x * map.getTileWidth()) + (map.getTileWidth() / 2)), (float)((currentNode.y * map.getTileHeight()) + (map.getTileHeight() / 2)));
					theta = Calculate.Hypotenuse(position, target);
				}
			}

			if(currentNode != null) theta = Calculate.Hypotenuse(position, target);
		}

		sawPlayerLastStep = playerSighted;



		// TODO: redesign this so path is only calculated when enemy is attacked, player is spotted, or noise is heard
		/*
		Player player = Player.getPlayer();
		if(path == null) {
			Pair<Integer> gridCoords = map.worldToGridCoords(position);
			Pair<Integer> playerGridCoords = map.worldToGridCoords(player.getPosition());
			path = new Path(map, gridCoords, playerGridCoords);
			currentNode = path.getNextNode();

			Pair<Float> target = new Pair<Float>((float)((currentNode.x * map.getTileWidth()) + (map.getTileWidth() / 2)), (float)((currentNode.y * map.getTileHeight()) + (map.getTileHeight() / 2)));
			theta = Calculate.Hypotenuse(position, target);
		} else {
			if(currentNode != null) {
				Pair<Float> target = new Pair<Float>((float)((currentNode.x * map.getTileWidth()) + (map.getTileWidth() / 2)), (float)((currentNode.y * map.getTileHeight()) + (map.getTileHeight() / 2)));
				int ret = Calculate.FastDistanceCompare(position, target, (bounds.getWidth() / 2));
				if(ret <= 0) {
					if(path.pathPossible()) { // if there are still nodes left in the path
						currentNode = path.getNextNode();
						if(currentNode != null) {
							target = new Pair<Float>((float)((currentNode.x * map.getTileWidth()) + (map.getTileWidth() / 2)), (float)((currentNode.y * map.getTileHeight()) + (map.getTileHeight() / 2)));
							theta = Calculate.Hypotenuse(position, target);
						}
					} else {
						// TODO: is player visible? if so, charge!
						// if not, recalculate path
					}
				}

				if(currentNode != null) theta = Calculate.Hypotenuse(position, target);
			}
		}*/
	}

	protected boolean hasTarget() {
		return (currentNode != null);
	}

	protected boolean playerInSights(TMap map) {
		Player player = Player.getPlayer();
		boolean inSight = true;

		// TODO: Implement line of sight test.
		float pTheta = Calculate.Hypotenuse(position, player.getPosition());
		Pair<Float> ray = new Pair<Float>(position);
		while(Calculate.FastDistanceCompare(ray, player.getPosition(), Globals.RAY_STEP_LENGTH) == 1) {
			ray.x += ((float)Math.cos(pTheta) * Globals.RAY_STEP_LENGTH);
			ray.y += ((float)Math.sin(pTheta) * Globals.RAY_STEP_LENGTH);
			Pair<Integer> gridPos = map.worldToGridCoords(ray);
			if(!map.isWalkable(gridPos.x, gridPos.y)) {
				inSight = false;
				break;
			}
		}

		return inSight;
	}

	protected void avoidObstacles(GameState gs, int delta) {
		EnemyController ec = EnemyController.getInstance();
		List<Enemy> allies = ec.getAliveEnemies();

		if(!allies.isEmpty()) {
			Vector2f alignment = computeAlignment(allies);
			Vector2f cohesion = computeCohesion(allies);
			Vector2f separation = computeSeparation(gs, allies);

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

	private Vector2f computeSeparation(GameState gs, List<Enemy> allies) {
		Vector2f v = new Vector2f();
		int neighbors = 0;

		for(Enemy e : allies) {
			if((e != this) && (Calculate.Distance(position, e.getPosition()) < getSeparationDistance())) {
				v.x += e.getPosition().x - position.x;
				v.y += e.getPosition().y - position.y;
				neighbors++;
			}
		}

		// Calculate separation from non-walkable tiles.
		TMap map = gs.getLevel().getMap();
		int mw = map.getMapWidth();
		int mh = map.getMapHeight();
		int tw = map.getTileWidth();
		int th = map.getTileHeight();
		int etx = ((int)((position.x / (mw * tw)) * mw) - 1);
		int ety = ((int)((position.y / (mh * th)) * mh) - 1);

		// Check the surrounding tiles for walkability and calculate separation. This is to avoid checking the entire map.
		for(int x = (etx - 1); x <= (etx + 1); x++) {
			for(int y = (ety - 1); y <= (ety + 1); y++) {
				// If this coordinate is within the bounds of the map.
				if((x > 0) && (x < mw) && (y > 0) && (y < mh)) {
					// ...and if the tile is not walkable.
					if(!map.isWalkable(x, y)) {
						// Use the center of the tile's bounds as the separation point.
						v.x += (((x * tw) + (tw / 2)) - position.x);
						v.y += (((y * th) + (th / 2)) - position.y);

						neighbors++;
					}
				}
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
	public void render(GameState gs, Graphics g, long cTime) {
		// All enemies should render their animation.
		//float pTheta = Calculate.Hypotenuse(position, Player.getPlayer().getPosition());
		if(isAlive(cTime)) animation.getCurrentAnimation().render(g, position, theta, shouldDrawFlash(cTime));
		statusHandler.render(g, cTime);

		if(Globals.SHOW_COLLIDERS) {
			g.setColor(Color.red);
			g.draw(bounds);

			TMap map = gs.getLevel().getMap();
			int mw = map.getMapWidth();
			int mh = map.getMapHeight();
			int tw = map.getTileWidth();
			int th = map.getTileHeight();
			int etx = ((int)((position.x / (mw * tw)) * mw) - 1);
			int ety = ((int)((position.y / (mh * th)) * mh) - 1);
			g.drawRect((etx * tw), (ety * th), (tw * 3), (th * 3));

			// draw line to next path node
			if(currentNode != null) {
				Pair<Float> target = new Pair<Float>((float)((currentNode.x * map.getTileWidth()) + (map.getTileWidth() / 2)), (float)((currentNode.y * map.getTileHeight()) + (map.getTileHeight() / 2)));
				g.drawLine(position.x, position.y, target.x, target.y);
			}
		}
	}

	public Shape getCollider() { return bounds; }
	public boolean touchingPlayer() {
		Shape pCollider = Player.getPlayer().getCollider();
		return (pCollider.intersects(bounds) || bounds.contains(pCollider));
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
			if(drop != null) gs.getLevel().addEntity("resource", drop);

			Scorekeeper.getInstance().addKill();
			postDamageTexts();
		}
		deathHandled = true;
	}

	public abstract LootTable getLootTable();

	@Override
	public String getTag() { return "enemy"; }

	@Override
	public int getLayer() { return Layers.ENEMIES.val(); }
}
