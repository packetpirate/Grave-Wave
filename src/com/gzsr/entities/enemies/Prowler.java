package com.gzsr.entities.enemies;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.BasicGameState;

import com.gzsr.Globals;
import com.gzsr.entities.Player;
import com.gzsr.math.Calculate;
import com.gzsr.math.Dice;
import com.gzsr.misc.Pair;
import com.gzsr.objects.items.Powerups;
import com.gzsr.states.GameState;
import com.gzsr.status.ParalysisEffect;
import com.gzsr.status.Status;

public class Prowler extends Enemy {
	private static final int FIRST_WAVE = 40;
	private static final int SPAWN_COST = 15;
	private static final float SPEED = 0.10f;
	private static final long ATTACK_DELAY = 1_000L;
	private static final float JUMP_RANGE = 300.0f;
	private static final long JUMP_DURATION = 1_000L;
	private static final float JUMP_SPEED = 0.50f;
	private static final long JUMP_COOLDOWN = 5_000L;
	private static final long PINNED_DURATION = 3_000L;
	
	private static final Dice HEALTH = new Dice(10, 6);
	private static final int HEALTH_MOD = 60;
	
	private static final Dice DAMAGE = new Dice(2, 4);
	private static final int DAMAGE_MOD = 4;
	
	public static final LootTable LOOT = new LootTable()
			.addItem(Powerups.Type.HEALTH, 0.40f)
			.addItem(Powerups.Type.AMMO, 0.30f)
			.addItem(Powerups.Type.EXTRA_LIFE, 0.10f)
			.addItem(Powerups.Type.CRIT_CHANCE, 0.10f)
			.addItem(Powerups.Type.EXP_MULTIPLIER, 0.10f)
			.addItem(Powerups.Type.NIGHT_VISION, 0.05f)
			.addItem(Powerups.Type.UNLIMITED_AMMO, 0.025f);
	
	private boolean jumping;
	private float jumpTheta;
	private long lastJump;
	
	public Prowler(Pair<Float> position_) {
		super(EnemyType.PROWLER, position_);
		
		this.health = Prowler.HEALTH.roll(Prowler.HEALTH_MOD);
		
		this.animation.addState("attack", type.createLayerAnimation(1, 4, 250L, -1L, -1L));
		
		jumping = false;
		jumpTheta = 0.0f;
		lastJump = -JUMP_COOLDOWN;
	}

	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
		if(isAlive(cTime)) {
			Player player = Player.getPlayer();
			
			// Need to make sure to update the status effects first.
			statusHandler.update((GameState)gs, cTime, delta);
			
			updateFlash(cTime);
			if(!jumping) theta = Calculate.Hypotenuse(position, player.getPosition());
			animation.getCurrentAnimation().update(cTime);

			if(player.isAlive()) {
				long sinceLastJump = (cTime - lastJump);
				if(!attacking && !jumping && nearPlayer(Prowler.JUMP_RANGE) && (sinceLastJump >= Prowler.JUMP_COOLDOWN)) {
					// Lunge at the player.
					jumping = true;
					jumpTheta = Calculate.Hypotenuse(position, player.getPosition());
					lastJump = cTime;
				} else if(!attacking && jumping && touchingPlayer()) {
					attacking = true;
					jumping = false;
					
					animation.setCurrent("attack");
					
					ParalysisEffect pinned = new ParalysisEffect(Prowler.PINNED_DURATION, cTime);
					player.getStatusHandler().addStatus(pinned, cTime);
				} else if(!attacking && jumping && ((cTime - lastJump) >= Prowler.JUMP_DURATION)) {
					jumping = false;
					lastJump = cTime;
				}
				
				if(attacking) {
					if(player.getStatusHandler().hasStatus(Status.PARALYSIS)) {
						long elapsed = (cTime - lastAttack);
						if(elapsed >= getAttackDelay()) {
							player.takeDamage(getDamage(), cTime);
							lastAttack = cTime;
						}
					} else {
						animation.setCurrent("move");
						attacking = false;
						lastJump = cTime;
					}
				}
				
				if(!attacking && !touchingPlayer()) move((GameState)gs, delta);
			}
		}
		
		if(!damageTexts.isEmpty()) {
			damageTexts.stream().forEach(dt -> ((GameState)gs).addEntity(String.format("dt%d", Globals.generateEntityID()), dt));
			damageTexts.clear();
		}
		
		postDamageTexts();
	}
	
	@Override
	public void render(Graphics g, long cTime) {
		// All enemies should render their animation.
		float adjTheta = (jumping ? jumpTheta : theta);
		if(isAlive(cTime)) animation.getCurrentAnimation().render(g, position, adjTheta, shouldDrawFlash(cTime));
		statusHandler.render(g, cTime);
		
		if(Globals.SHOW_COLLIDERS) {
			g.setColor(Color.red);
			g.draw(bounds);
		}
	}
	
	@Override
	public void move(GameState gs, int delta) {
		if(!statusHandler.hasStatus(Status.PARALYSIS)) {
			float adjTheta = (jumping ? jumpTheta : theta);
			velocity.x = (float)Math.cos(adjTheta) * getSpeed() * delta;
			velocity.y = (float)Math.sin(adjTheta) * getSpeed() * delta;
	
			avoidObstacles(gs, delta);
			
			if(!moveBlocked) {
				position.x += velocity.x;
				position.y += velocity.y;
			}
			
			moveBlocked = false;
			
			bounds.setCenterX(position.x);
			bounds.setCenterY(position.y);
		}
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
	public long getAttackDelay() { return Prowler.ATTACK_DELAY; }
	
	@Override
	public double getDamage() { return Prowler.DAMAGE.roll(Prowler.DAMAGE_MOD); }
	
	@Override
	public float getSpeed() { return (jumping ? Prowler.JUMP_SPEED : Prowler.SPEED); }
	
	public static int appearsOnWave() { return Prowler.FIRST_WAVE; }
	
	public static int getSpawnCost() { return Prowler.SPAWN_COST; }

	@Override
	public String getName() {
		return "Prowler";
	}
	
	@Override
	public String getDescription() {
		return "Prowler";
	}
	
	@Override
	public String print() {
		return String.format("%s at (%.2f, %.2f) - %.2f health",
							 getName(), position.x, position.y, health);
	}
	
	@Override
	public LootTable getLootTable() { return Prowler.LOOT; }
}
