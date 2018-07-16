package com.gzsr.entities.enemies;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.state.BasicGameState;

import com.gzsr.Globals;
import com.gzsr.entities.Entity;
import com.gzsr.entities.Player;
import com.gzsr.gfx.Animation;
import com.gzsr.gfx.Layers;
import com.gzsr.gfx.ui.DamageText;
import com.gzsr.math.Calculate;
import com.gzsr.misc.Pair;
import com.gzsr.misc.Vector2f;
import com.gzsr.objects.items.Powerups;
import com.gzsr.states.GameState;
import com.gzsr.status.Status;
import com.gzsr.status.StatusEffect;

public abstract class Enemy implements Entity {
	private static long FLASH_DURATION = 100L;
	
	// TODO: Add support for enemies having status effects.
	protected EnemyType type;
	protected Animation animation;
	protected Shape bounds;
	protected Pair<Float> position;
	public Pair<Float> getPosition() { return position; }
	protected float theta;
	public float getTheta() { return theta; }
	protected Vector2f velocity;
	public Vector2f getVelocity() { return velocity; }
	protected boolean moveBlocked;
	public void blockMovement() { moveBlocked = true; }
	
	protected double health;
	protected int cash;
	public int getCashValue() { return cash; }
	protected int experience;
	public int getExpValue() { return experience; }
	protected List<StatusEffect> statusEffects;
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
		this.animation = type.getAnimation();
		this.position = position_;
		
		float w = animation.getSrcSize().x;
		float h = animation.getSrcSize().y;
		this.bounds = new Rectangle((position.x - (w / 2)), (position.y - (h / 2)), w, h);
		
		this.moveBlocked = false;
		this.theta = 0.0f;
		this.velocity = new Vector2f(0.0f, 0.0f);
		this.health = 0.0;
		this.cash = type.getCashValue();
		this.experience = type.getExperience();
		this.statusEffects = new ArrayList<StatusEffect>();
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
	
	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
		// All enemies should update.
		if(isAlive(cTime)) {
			// Need to make sure to update the status effects first.
			Iterator<StatusEffect> it = statusEffects.iterator();
			while(it.hasNext()) {
				StatusEffect status = (StatusEffect) it.next();
				if(status.isActive(cTime)) {
					status.update(this, (GameState)gs, cTime, delta);
				} else {
					status.onDestroy(this, cTime);
					it.remove();
				}
			}
			
			updateFlash(cTime);
			animation.update(cTime);
			if(Player.getPlayer().isAlive() && !touchingPlayer()) move((GameState)gs, delta);
		}
		
		postDamageTexts();
	}
	
	protected void postDamageTexts() {
		if(!damageTexts.isEmpty()) {
			damageTexts.stream().forEach(dt -> GameState.addVanishingText(String.format("dt%d", Globals.generateEntityID()), dt));
			damageTexts.clear();
		}
	}
	
	protected boolean nearPlayer(float attackDist) {
		return (Calculate.Distance(position, Player.getPlayer().getPosition()) <= attackDist);
	}
	
	public abstract void move(GameState gs, int delta);
	protected void avoidObstacles(GameState gs, int delta) {
		EnemyController ec = (EnemyController)gs.getEntity("enemyController");
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
		if(isAlive(cTime)) animation.render(g, position, pTheta, shouldDrawFlash(cTime));
		if(!statusEffects.isEmpty()) statusEffects.stream().filter(status -> status.isActive(cTime)).forEach(status -> status.render(g, cTime));
		
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
	
	public void addStatus(StatusEffect effect, long cTime) {
		// First check to see if the enemy already has this status.
		for(StatusEffect se : statusEffects) {
			Status s = se.getStatus();
			if(s.equals(effect.getStatus())) {
				// Refresh the effect rather than adding it to the list.
				se.refresh(cTime);
				return;
			}
		}
		
		// The enemy does not have this effect. Add it.
		statusEffects.add(effect);
	}
	
	public boolean hasStatus(Status status) {
		for(StatusEffect se : statusEffects) {
			Status ses = se.getStatus();
			if(ses.equals(status)) return true;
		}
		
		return false;
	}
	
	public void takeDamage(double amnt, float knockback, long cTime, int delta) {
		takeDamage(amnt, knockback, (float)(theta + Math.PI), cTime, delta, true);
	}
	
	public void takeDamage(double amnt, float knockback, float knockbackTheta, long cTime, int delta, boolean flash) {
		takeDamage(amnt, knockback, knockbackTheta, cTime, delta, flash, false);
	}
	
	public void takeDamage(double amnt, float knockback, float knockbackTheta, long cTime, int delta, boolean flash, boolean isCritical) {
		if(!dead()) {
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
		}
	}
	
	protected void createDamageText(double amnt, float dist, float knockbackTheta, long cTime, boolean isCritical) {
		float tOff = Globals.rand.nextFloat() * (Globals.rand.nextBoolean() ? 1 : -1) * (float)(Math.PI / 9);
		float x = position.x + (float)(Math.cos(knockbackTheta + tOff) * (dist + (Globals.rand.nextFloat() * 10.0f)));
		float y = position.y + (float)(Math.sin(knockbackTheta + tOff) * dist + (Globals.rand.nextFloat() * 10.0f));
		Pair<Float> dtPos = new Pair<Float>(x, y);
		DamageText dt = new DamageText(Integer.toString((int)amnt), dtPos, (knockbackTheta + tOff), cTime, 500L, isCritical);
		damageTexts.add(dt);
	}
	
	public void onDeath(GameState gs, long cTime) {
		if(!deathHandled) {
			Powerups.spawnRandomPowerup(gs, this, new Pair<Float>(position), cTime);
			postDamageTexts();
		}
		deathHandled = true;
	}
	
	public abstract double getDamage();
	public abstract float getSpeed();
	public abstract LootTable getLootTable();
	
	@Override
	public int getLayer() {
		return Layers.ENEMIES.val();
	}
}
