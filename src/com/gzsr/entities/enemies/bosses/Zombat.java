package com.gzsr.entities.enemies.bosses;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.BasicGameState;

import com.gzsr.entities.Player;
import com.gzsr.entities.enemies.EnemyType;
import com.gzsr.entities.enemies.LootTable;
import com.gzsr.math.Calculate;
import com.gzsr.math.Dice;
import com.gzsr.misc.Pair;
import com.gzsr.objects.items.Powerups;
import com.gzsr.states.GameState;

public class Zombat extends Boss {
	private static final int FIRST_WAVE = 20;
	private static final int SPAWN_COST = 12;
	private static final int MIN_HEALTH_COUNT = 30;
	private static final int MIN_HEALTH_SIDES = 10;
	private static final int MIN_HEALTH_MOD = 1_200;
	private static final int MIN_DAMAGE_COUNT = 2;
	private static final int MIN_DAMAGE_SIDES = 4;
	private static final int MIN_DAMAGE_MOD = 4;
	private static final long ATTACK_DELAY = 1_000L;
	private static final float SPEED = 0.2f;
	private static final float ATTACK_DIST = 250.0f;
	
	private static final Color BLOOD_COLOR = new Color(0xAA0000);
	
	public static final LootTable LOOT = new LootTable()
			.addItem(Powerups.Type.HEALTH, 1.0f)
			.addItem(Powerups.Type.AMMO, 0.75f)
			.addItem(Powerups.Type.EXTRA_LIFE, 0.15f)
			.addItem(Powerups.Type.CRIT_CHANCE, 0.25f)
			.addItem(Powerups.Type.NIGHT_VISION, 0.25f);
	
	private boolean siphoningBlood;
	
	public Zombat(Pair<Float> position_) {
		super(EnemyType.ZOMBAT_SWARM, position_);
		
		this.health = Dice.roll(Zombat.MIN_HEALTH_COUNT, Zombat.MIN_HEALTH_SIDES, Zombat.MIN_HEALTH_MOD);
		this.damage = new Dice(Zombat.MIN_DAMAGE_COUNT, Zombat.MIN_DAMAGE_SIDES);
		
		siphoningBlood = false;
	}
	
	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
		if(isAlive(cTime)) {
			// Need to make sure to update the status effects first.
			statusHandler.update((GameState)gs, cTime, delta);
			
			updateFlash(cTime);
			theta = Calculate.Hypotenuse(position, Player.getPlayer().getPosition());
			
			animation.getCurrentAnimation().update(cTime);
			if(!nearPlayer(Zombat.ATTACK_DIST)) {
				siphoningBlood = false;
				if(Player.getPlayer().isAlive() && !touchingPlayer()) move((GameState)gs, delta);
			} else siphoningBlood = Player.getPlayer().isAlive(); // Only start siphoning if player is alive, obviously...
			
			long elapsed = (cTime - lastAttack);
			if(Player.getPlayer().isAlive() && siphoningBlood && (elapsed >= Zombat.ATTACK_DELAY)) {
				double dmg = damage.roll(Zombat.MIN_DAMAGE_MOD);
				double damageTaken = Player.getPlayer().takeDamage(dmg, cTime);
				if(damageTaken > 0.0) health += damageTaken;
				
				lastAttack = cTime;
			}
		}
		
		postDamageTexts();
	}
	
	@Override
	public void render(Graphics g, long cTime) {
		// Render the blood stream being siphoned from the player.
		if(siphoningBlood) {
			float x = position.x + ((float)Math.cos(theta) * 5.0f);
			float y = position.y + ((float)Math.sin(theta) * 5.0f);
			g.setColor(BLOOD_COLOR);
			g.setLineWidth(2.0f);
			g.drawLine(x, y, Player.getPlayer().getPosition().x, Player.getPlayer().getPosition().y);
			g.setLineWidth(1.0f);
		}
		
		super.render(g, cTime);
	}
	
	@Override
	public void blockMovement() {
		// Do nothing... can't block a Zombat with laser barriers.
	}

	@Override
	public void move(GameState gs, int delta) {
		velocity.x = (float)Math.cos(theta) * Zombat.SPEED * delta;
		velocity.y = (float)Math.sin(theta) * Zombat.SPEED * delta;

		avoidObstacles(gs, delta);
		
		position.x += velocity.x;
		position.y += velocity.y;
		
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
		takeDamage(amnt, knockback, knockbackTheta, cTime, delta, flash, false);
	}
	
	@Override
	public void takeDamage(double amnt, float knockback, float knockbackTheta, long cTime, int delta, boolean flash, boolean isCritical) {
		if(!dead()) {
			health -= amnt;
			
			createDamageText(amnt, 32.0f, knockbackTheta, cTime, isCritical);
			
			if(flash) {
				hit = true;
				hitTime = cTime;
			}
		}
	}

	@Override
	public double getDamage() { return damage.roll(Zombat.MIN_DAMAGE_MOD); }
	
	@Override
	public long getAttackDelay() { return Zombat.ATTACK_DELAY; }
	
	@Override
	public float getSpeed() {
		return Zombat.SPEED;
	}
	
	public static int appearsOnWave() {
		return FIRST_WAVE;
	}

	public static int getSpawnCost() {
		return Zombat.SPAWN_COST;
	}
	
	@Override
	public String getName() {
		return "Zombat";
	}
	
	@Override
	public String getDescription() {
		return "Zombat";
	}
	
	@Override
	public LootTable getLootTable() {
		return Zombat.LOOT;
	}
}