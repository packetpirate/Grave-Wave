package com.gzsr.entities.enemies.bosses;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import com.gzsr.Globals;
import com.gzsr.entities.enemies.EnemyType;
import com.gzsr.math.Calculate;
import com.gzsr.misc.Pair;
import com.gzsr.objects.items.Powerups;
import com.gzsr.states.GameState;

public class Zombat extends Boss {
	private static final int FIRST_WAVE = 20;
	private static final int SPAWN_COST = 12;
	private static final float HEALTH = 1_500.0f;
	private static final float SPEED = 0.2f;
	private static final float DPS = 10.0f;
	private static final float POWERUP_CHANCE = 0.6f;
	private static final float SIPHON_RATE = 0.15f;
	private static final float ATTACK_DIST = 250.0f;
	
	private static final Color BLOOD_COLOR = new Color(0xAA0000);
	
	private boolean siphoningBlood;
	
	public Zombat(Pair<Float> position_) {
		super(EnemyType.ZOMBAT_SWARM, position_);
		this.health = Zombat.HEALTH;
		
		siphoningBlood = false;
	}
	
	@Override
	public void update(GameState gs, long cTime, int delta) {
		if(isAlive(cTime)) {
			theta = Calculate.Hypotenuse(position, Globals.player.getPosition());
			
			animation.update(cTime);
			if(!nearPlayer(Zombat.ATTACK_DIST)) {
				siphoningBlood = false;
				if(Globals.player.isAlive()) move(delta);
			} else siphoningBlood = Globals.player.isAlive(); // Only start siphoning if player is alive, obviously...
			
			if(Globals.player.isAlive() && siphoningBlood) {
				double damageTaken = Globals.player.takeDamage(SIPHON_RATE);
				if(damageTaken > 0.0) health += SIPHON_RATE;
			}
		}
	}
	
	@Override
	public void render(Graphics g, long cTime) {
		super.render(g, cTime);
		
		// Render the blood stream being siphoned from the player.
		if(siphoningBlood) {
			float x = position.x + ((float)Math.cos(theta) * 5.0f);
			float y = position.y + ((float)Math.sin(theta) * 5.0f);
			g.setColor(BLOOD_COLOR);
			g.setLineWidth(2.0f);
			g.drawLine(x, y, Globals.player.getPosition().x, Globals.player.getPosition().y);
			g.setLineWidth(1.0f);
		}
	}

	@Override
	public boolean isAlive(long cTime) {
		return (health > 0);
	}
	
	@Override
	public void blockMovement() {
		// Do nothing...
	}

	@Override
	public void move(int delta) {
		position.x += (float)Math.cos(theta) * Zombat.SPEED * delta;
		position.y += (float)Math.sin(theta) * Zombat.SPEED * delta;
		
		bounds.setCenterX(position.x);
		bounds.setCenterY(position.y);
	}

	@Override
	public void takeDamage(double amnt) {
		health -= amnt;
	}
	
	@Override
	public void onDeath(GameState gs, long cTime) {
		if(Globals.rand.nextFloat() <= Zombat.POWERUP_CHANCE) {
			Powerups.spawnRandomPowerup(gs, position, cTime);
		}
	}

	@Override
	public double getDamage() {
		return Zombat.DPS;
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
}