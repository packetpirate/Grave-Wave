package com.gzsr.entities.enemies.bosses;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import com.gzsr.AssetManager;
import com.gzsr.Globals;
import com.gzsr.entities.enemies.BigMama;
import com.gzsr.entities.enemies.EnemyType;
import com.gzsr.gfx.particles.Particle;
import com.gzsr.math.Calculate;
import com.gzsr.misc.Pair;
import com.gzsr.objects.items.Powerups;
import com.gzsr.states.GameState;

public class Stitches extends Boss {
	private static final int FIRST_WAVE = 25;
	private static final int SPAWN_COST = 40;
	private static final float HEALTH = 15_000.0f;
	private static final float SPEED = 0.10f;
	private static final float DPS = 20.0f;
	private static final float POWERUP_CHANCE = 1.0f;
	private static final float ATTACK_DIST = 300.0f;
	private static final float RELEASE_DIST = 100.0f;
	private static final float HOOK_DAMAGE = 0.3f;
	private static final float HOOK_SPEED = 0.4f;
	private static final long HOOK_COOLDOWN = 5_000L;
	
	private Particle hook;
	private boolean hooked;
	private long lastHook;
	
	private boolean deathHandled;
	
	public Stitches(Pair<Float> position_) {
		super(EnemyType.STITCHES, position_);
		this.health = Stitches.HEALTH;
		
		hook = null;
		hooked = false;
		lastHook = -Stitches.HOOK_COOLDOWN;
		
		deathHandled = false;
	}
	
	@Override
	public void update(GameState gs, long cTime, int delta) {
		if(isAlive(cTime)) {
			updateFlash(cTime);
			theta = Calculate.Hypotenuse(position, Globals.player.getPosition());
			
			if((hook != null) && !hooked) {
				float distToPlayer = Calculate.Distance(hook.getPosition(), Globals.player.getPosition());
				if(distToPlayer > Stitches.ATTACK_DIST) {
					// If the hook missed the player, get rid of it.
					hook = null;
					hooked = false;
					lastHook = cTime;
				} else {
					// Move the hook toward the player until it collides or misses.
					hook.update(gs, cTime, delta);
					if(hook.checkCollision(Globals.player)) hooked = true;
				}
			}
			
			long sinceLastHook = (cTime - lastHook);
			if(Globals.player.isAlive() && (hook == null) && (sinceLastHook >= Stitches.HOOK_COOLDOWN) && nearPlayer(Stitches.ATTACK_DIST)) {
				// Throw the hook.
				Pair<Float> hookPos = new Pair<Float>(position.x, position.y);
				float direction = Calculate.Hypotenuse(position, Globals.player.getPosition()) + (float)(Math.PI / 2);
				hook = new Particle("GZS_Hook", Color.gray, hookPos, Stitches.HOOK_SPEED, direction, 0.0f, new Pair<Float>(16.0f, 16.0f), -1L, cTime);
				AssetManager.getManager().getSound("throw2").play();
			} else if(hooked) {
				// If we're close enough to the player now, release the hook.
				if(!Globals.player.isAlive() || nearPlayer(Stitches.RELEASE_DIST)) {
					hook = null;
					hooked = false;
					lastHook = cTime;
				} else {
					// Otherwise, reel the player in.
					float xOff = (float)(Math.cos(theta) * -(Stitches.HOOK_SPEED * 4));
					float yOff = (float)(Math.sin(theta) * -(Stitches.HOOK_SPEED * 4));
					Globals.player.move(xOff, yOff);
					hook.setPosition(new Pair<Float>(Globals.player.getPosition().x, Globals.player.getPosition().y));
					
					// Make the player take bleed damage.
					Globals.player.takeDamage(Stitches.HOOK_DAMAGE);
				}
			} else if(!hooked && (hook == null)) {
				animation.update(cTime);
				if(Globals.player.isAlive() && !touchingPlayer()) move(delta);
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
	public void move(int delta) {
		if(!moveBlocked) {
			position.x += (float)Math.cos(theta) * Stitches.SPEED * delta;
			position.y += (float)Math.sin(theta) * Stitches.SPEED * delta;
		}
		
		moveBlocked = false;
		
		bounds.setCenterX(position.x);
		bounds.setCenterY(position.y);
	}
	
	@Override
	public void onDeath(GameState gs, long cTime) {
		if(!deathHandled && (Globals.rand.nextFloat() <= Stitches.POWERUP_CHANCE)) {
			Powerups.spawnRandomPowerup(gs, position, cTime);
		}
		
		deathHandled = true;
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
}
