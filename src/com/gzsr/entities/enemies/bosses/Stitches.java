package com.gzsr.entities.enemies.bosses;

import java.util.Iterator;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.BasicGameState;

import com.gzsr.AssetManager;
import com.gzsr.entities.Player;
import com.gzsr.entities.enemies.EnemyType;
import com.gzsr.entities.enemies.LootTable;
import com.gzsr.gfx.particles.Particle;
import com.gzsr.math.Calculate;
import com.gzsr.misc.Pair;
import com.gzsr.objects.items.Powerups;
import com.gzsr.states.GameState;
import com.gzsr.status.StatusEffect;

public class Stitches extends Boss {
	private static final int FIRST_WAVE = 25;
	private static final int SPAWN_COST = 40;
	private static final float HEALTH = 15_000.0f;
	private static final float SPEED = 0.10f;
	private static final float DPS = 20.0f;
	private static final float ATTACK_DIST = 300.0f;
	private static final float RELEASE_DIST = 100.0f;
	private static final float HOOK_DAMAGE = 0.3f;
	private static final float HOOK_SPEED = 0.4f;
	private static final long HOOK_COOLDOWN = 5_000L;
	
	public static final LootTable LOOT = new LootTable()
			.addItem(Powerups.Type.HEALTH, 1.0f)
			.addItem(Powerups.Type.AMMO, 1.0f)
			.addItem(Powerups.Type.EXTRA_LIFE, 0.75f)
			.addItem(Powerups.Type.NIGHT_VISION, 0.50f);
	
	private Particle hook;
	private boolean hooked;
	private long lastHook;
	
	public Stitches(Pair<Float> position_) {
		super(EnemyType.STITCHES, position_);
		this.health = Stitches.HEALTH;
		
		hook = null;
		hooked = false;
		lastHook = -Stitches.HOOK_COOLDOWN;
	}
	
	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
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
			theta = Calculate.Hypotenuse(position, Player.getPlayer().getPosition());
			
			if((hook != null) && !hooked) {
				float distToPlayer = Calculate.Distance(hook.getPosition(), Player.getPlayer().getPosition());
				if(distToPlayer > Stitches.ATTACK_DIST) {
					// If the hook missed the player, get rid of it.
					hook = null;
					hooked = false;
					lastHook = cTime;
				} else {
					// Move the hook toward the player until it collides or misses.
					hook.update(gs, cTime, delta);
					if(hook.checkCollision(Player.getPlayer())) hooked = true;
				}
			}
			
			long sinceLastHook = (cTime - lastHook);
			if(Player.getPlayer().isAlive() && (hook == null) && (sinceLastHook >= Stitches.HOOK_COOLDOWN) && nearPlayer(Stitches.ATTACK_DIST)) {
				// Throw the hook.
				Pair<Float> hookPos = new Pair<Float>(position.x, position.y);
				float direction = Calculate.Hypotenuse(position, Player.getPlayer().getPosition()) + (float)(Math.PI / 2);
				hook = new Particle("GZS_Hook", Color.gray, hookPos, Stitches.HOOK_SPEED, direction, 0.0f, new Pair<Float>(16.0f, 16.0f), -1L, cTime);
				AssetManager.getManager().getSound("throw2").play(1.0f, AssetManager.getManager().getSoundVolume());
			} else if(hooked) {
				// If we're close enough to the player now, release the hook.
				if(!Player.getPlayer().isAlive() || nearPlayer(Stitches.RELEASE_DIST)) {
					hook = null;
					hooked = false;
					lastHook = cTime;
				} else {
					// Otherwise, reel the player in.
					float xOff = (float)(Math.cos(theta) * -(Stitches.HOOK_SPEED * 4));
					float yOff = (float)(Math.sin(theta) * -(Stitches.HOOK_SPEED * 4));
					Player.getPlayer().move(xOff, yOff);
					hook.setPosition(new Pair<Float>(Player.getPlayer().getPosition().x, Player.getPlayer().getPosition().y));
					
					// Make the player take bleed damage.
					Player.getPlayer().takeDamage(Stitches.HOOK_DAMAGE, cTime);
				}
			} else if(!hooked && (hook == null)) {
				animation.update(cTime);
				if(Player.getPlayer().isAlive() && !touchingPlayer()) move((GameState)gs, delta);
			}
		} else {
			hook = null;
			hooked = false;
		}
	}
	
	@Override
	public void render(Graphics g, long cTime) {
		if(hook != null) {
			// Draw the hook's tether.
			g.setColor(Color.black);
			g.setLineWidth(2.0f);
			g.drawLine(position.x, position.y, hook.getPosition().x, hook.getPosition().y);
			g.setLineWidth(1.0f);
			
			hook.render(g, cTime);
		}
		
		super.render(g, cTime);
	}

	@Override
	public void move(GameState gs, int delta) {
		velocity.x = (float)Math.cos(theta) * Stitches.SPEED * delta;
		velocity.y = (float)Math.sin(theta) * Stitches.SPEED * delta;

		avoidObstacles(gs, delta);
		
		if(!moveBlocked) {
			position.x += velocity.x;
			position.y += velocity.y;
		}
		
		moveBlocked = false;
		
		bounds.setCenterX(position.x);
		bounds.setCenterY(position.y);
	}
	
	@Override
	public float getCohesionDistance() {
		return (Math.min(type.getFrameWidth(), type.getFrameHeight()) * 2);
	}
	
	@Override
	public float getSeparationDistance() {
		return Math.min(type.getFrameWidth(), type.getFrameHeight());
	}

	@Override
	public void takeDamage(double amnt, float knockback, long cTime, int delta) {
		takeDamage(amnt, knockback, (float)(theta + Math.PI), cTime, delta, true);
	}
	
	@Override
	public void takeDamage(double amnt, float knockback, float knockbackTheta, long cTime, int delta, boolean flash) {
		if(!dead()) {
			health -= amnt;
			
			if(flash) {
				hit = true;
				hitTime = cTime;
			}
		}
	}
	
	@Override
	public double getDamage() {
		return Stitches.DPS;
	}
	
	@Override
	public float getSpeed() {
		return Stitches.SPEED;
	}
	
	public static int appearsOnWave() {
		return FIRST_WAVE;
	}

	public static int getSpawnCost() {
		return Stitches.SPAWN_COST;
	}
	
	@Override
	public String getName() {
		return "Stitches";
	}
	
	@Override
	public String getDescription() {
		return "Stitches";
	}
	
	@Override
	public LootTable getLootTable() {
		return Stitches.LOOT;
	}
}
