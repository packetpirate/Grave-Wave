package com.gzsr.entities.enemies;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Sound;
import org.newdawn.slick.state.BasicGameState;

import com.gzsr.AssetManager;
import com.gzsr.Globals;
import com.gzsr.entities.Player;
import com.gzsr.math.Calculate;
import com.gzsr.math.Dice;
import com.gzsr.misc.Pair;
import com.gzsr.objects.items.Powerups;
import com.gzsr.objects.weapons.Explosion;
import com.gzsr.states.GameState;

public class ElSalvo extends Enemy {
	public static final int FIRST_WAVE = 35;
	private static final int SPAWN_COST = 5;
	private static final int MIN_HEALTH_COUNT = 5;
	private static final int MIN_HEALTH_SIDES = 10;
	private static final int MIN_HEALTH_MOD = 50;
	private static final float SPEED = 0.2f;
	private static final float ATTACK_DIST = 100.0f;
	private static final double EXPLODE_DAMAGE = 250.0f;
	private static final float EXPLODE_RADIUS = 256.0f;
	private static final long EXPLOSION_DELAY = 500L;
	private static final long FLASH_DURATION = 100L;
	private static final long FLASH_LENGTH = 50L;
	
	public static final LootTable LOOT = new LootTable()
			.addItem(Powerups.Type.HEALTH, 0.25f)
			.addItem(Powerups.Type.AMMO, 0.25f)
			.addItem(Powerups.Type.EXTRA_LIFE, 0.05f)
			.addItem(Powerups.Type.CRIT_CHANCE, 0.10f)
			.addItem(Powerups.Type.EXP_MULTIPLIER, 0.10f)
			.addItem(Powerups.Type.NIGHT_VISION, 0.025f)
			.addItem(Powerups.Type.UNLIMITED_AMMO, 0.025f);
	
	private Sound explode;
	
	private boolean exploding, flashing, exploded;
	private long flashStart;
	private long lastFlash;
	
	public ElSalvo(Pair<Float> position_) {
		super(EnemyType.ELSALVO, position_);
		this.health = Dice.roll(ElSalvo.MIN_HEALTH_COUNT, ElSalvo.MIN_HEALTH_SIDES, ElSalvo.MIN_HEALTH_MOD);
		this.explode = AssetManager.getManager().getSound("explosion2");
		
		exploding = false;
		flashing = false;
		exploded = false;
		flashStart = 0L;
		lastFlash = 0L;
	}
	
	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
		if(exploding) {
			long elapsed = (cTime - lastFlash);
			if((cTime - flashStart) >= EXPLOSION_DELAY) {
				explode((GameState)gs, cTime);
			} else if(elapsed >= FLASH_DURATION) {
				flashing = true;
				lastFlash = cTime;
			} else if(elapsed >= FLASH_LENGTH) {
				flashing = false;
			}
		} else if(isAlive(cTime)) {
			// Need to make sure to update the status effects first.
			statusHandler.update((GameState)gs, cTime, delta);
			
			updateFlash(cTime);
			theta = Calculate.Hypotenuse(position, Player.getPlayer().getPosition());
			if(!nearPlayer()) {
				animation.getCurrentAnimation().update(cTime);
				if(Player.getPlayer().isAlive() && !touchingPlayer()) move((GameState)gs, delta);
			} else {
				exploding = true;
				flashing = true;
				flashStart = cTime;
				lastFlash = cTime;
			}
		}
		
		if(!damageTexts.isEmpty()) {
			damageTexts.stream().forEach(dt -> ((GameState)gs).addEntity(String.format("dt%d", Globals.generateEntityID()), dt));
			damageTexts.clear();
		}
	}
	
	@Override
	public void render(Graphics g, long cTime) {
		float pTheta = Calculate.Hypotenuse(position, Player.getPlayer().getPosition());
		if(!dead()) animation.getCurrentAnimation().render(g, position, pTheta, (flashing || shouldDrawFlash(cTime)));
		statusHandler.render(g, cTime);
		
		if(Globals.SHOW_COLLIDERS) {
			g.setColor(Color.red);
			g.draw(bounds);
		}
	}
	
	private void explode(GameState gs, long cTime) {
		int id = Globals.generateEntityID();
		Explosion exp = new Explosion(Explosion.Type.NORMAL, "GZS_Explosion", new Pair<Float>(position.x, position.y), ElSalvo.EXPLODE_DAMAGE, 0.0f, ElSalvo.EXPLODE_RADIUS);
		gs.addEntity(String.format("explosion%d", id), exp);
		
		explode.play(1.0f, AssetManager.getManager().getSoundVolume());
		health = 0.0;
		exploded = true;
	}

	@Override
	public void move(GameState gs, int delta) {
		velocity.x = (float)Math.cos(theta) * ElSalvo.SPEED * delta;
		velocity.y = (float)Math.sin(theta) * ElSalvo.SPEED * delta;

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
	
	private boolean nearPlayer() {
		return (Calculate.Distance(position, Player.getPlayer().getPosition()) <= ElSalvo.ATTACK_DIST);
	}

	@Override
	public double getDamage() { return 0.0; }
	
	@Override
	public int getExpValue() { return (exploded ? 0 : experience); }
	
	@Override
	public long getAttackDelay() { return 0L; }
	
	@Override
	public float getSpeed() { return ElSalvo.SPEED; }

	public static int appearsOnWave() { return ElSalvo.FIRST_WAVE; }
	
	public static int getSpawnCost() { return ElSalvo.SPAWN_COST; }
	
	@Override
	public String getName() {
		return "El Salvo";
	}
	
	@Override
	public String getDescription() {
		return "El Salvo";
	}
	
	@Override
	public LootTable getLootTable() { return ElSalvo.LOOT; }
}
