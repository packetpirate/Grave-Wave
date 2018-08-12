package com.gzsr.objects.weapons;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.state.BasicGameState;

import com.gzsr.AssetManager;
import com.gzsr.entities.Entity;
import com.gzsr.entities.Player;
import com.gzsr.entities.enemies.Enemy;
import com.gzsr.entities.enemies.EnemyController;
import com.gzsr.entities.enemies.TinyZumby;
import com.gzsr.gfx.Animation;
import com.gzsr.gfx.Layers;
import com.gzsr.misc.Pair;
import com.gzsr.status.StatusEffect;

public class Explosion implements Entity {
	public enum Type {
		NORMAL(250L), 
		POISON(1_000L), 
		BLOOD(250L);
		
		private long damageWindow;
		public long getDamageWindow() { return damageWindow; }
		
		Type(long damageWindow_) {
			this.damageWindow = damageWindow_;
		}
	}
	
	private Animation anim;
	
	private Type type;
	public Type getType() { return type; }
	
	private Pair<Float> position;
	public Pair<Float> getPosition() { return position; }
	public void setPosition(Pair<Float> newPos) {
		position.x = newPos.x;
		position.y = newPos.y;
		bounds.setCenterX(position.x);
		bounds.setCenterY(position.y);
	}
	
	private Shape bounds;
	public Shape getCollider() { return bounds; }
	
	private StatusEffect status;
	private double damage;
	private float knockback;
	private float radius;
	private boolean started;
	private long created;
	
	private List<Entity> entitiesAffected;
	
	public Explosion(Type type_, String animName_, Pair<Float> position_, double damage_, float knockback_, float radius_, long cTime) {
		this(type_, animName_, position_, null, damage_, knockback_, radius_, cTime);
	}
	
	public Explosion(Type type_, String animName_, Pair<Float> position_, StatusEffect status_, double damage_, float knockback_, float radius_, long cTime) {
		this.type = type_;
		this.anim = AssetManager.getManager().getAnimation(animName_);
		this.position = position_;
		
		this.bounds = new Rectangle((position.x - radius_), (position.y - radius_), (radius_ * 2), (radius_ * 2));
		
		this.status = status_;
		this.damage = damage_;
		this.knockback = knockback_;
		this.radius = radius_;
		this.started = false;
		this.created = cTime;
		
		this.entitiesAffected = new ArrayList<Entity>();
	}
	
	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
		if(!started) {
			anim.restart(cTime);
			started = true; 
		}
		
		// Update the animation.
		if(anim != null) anim.update(cTime);
		
		long elapsed = (cTime - created);
		if(isActive(cTime) && (elapsed <= type.getDamageWindow())) {
			// TODO: In future, will have to also check for collision with player structures (turret, barrier, etc).
			// Check for collision with player.
			checkCollision(Player.getPlayer(), cTime, delta);
			
			// Check for collisions with enemies.
			EnemyController ec = EnemyController.getInstance();
			ec.getAliveEnemies().stream().forEach(e -> checkCollision(e, cTime, delta));
		}
	}

	@Override
	public void render(Graphics g, long cTime) {
		if(started && (anim != null) && anim.isActive(cTime)) {
			anim.render(g, position, new Pair<Float>((radius * 2), (radius * 2)));
		}
	}
	
	public boolean isActive(long cTime) { return anim.isActive(cTime); }
	
	/**
	 * Checks for collision with given entity and deals damage if a collision is detected.
	 * @param e The entity to check for a collision against.
	 * @return Whether or not the entity has collided with the explosion.
	 */
	public boolean checkCollision(Entity e, long cTime, int delta) {
		// If this entity has been damaged by this explosion already, ignore this collision.
		if(!entitiesAffected.contains(e)) {
			// If damage is taken, calculate damage based on distance from source.
			if(e instanceof Player) {
				Player player = Player.getPlayer();
				
				if(player.getCollider().intersects(getCollider())) {
					player.takeDamage(damage, cTime);
					if(status != null) player.getStatusHandler().addStatus(status, status.getDuration());
					entitiesAffected.add(player);
					return true;
				} else return false;
			} else if(e instanceof Enemy) {
				// TODO: Add support for adding status effect to an enemy.
				if(!(type.equals(Type.BLOOD)) && !(e instanceof TinyZumby)) {
					Enemy en = (Enemy)e;
					if(en.getCollider().intersects(getCollider())) {
						en.takeDamage(DamageType.CONCUSSIVE, damage, knockback, cTime, delta);
						entitiesAffected.add(en);
						return true;
					} else return false;
				}
			}
		}
		
		return false;
	}
	
	@Override
	public String getName() {
		return "Explosion";
	}
	
	@Override
	public String getDescription() {
		return "Explosion";
	}
	
	@Override
	public int getLayer() {
		return Layers.PARTICLES.val();
	}
}
