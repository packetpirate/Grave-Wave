package com.gzsr.entities.enemies.bosses;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import com.gzsr.Globals;
import com.gzsr.entities.enemies.EnemyType;
import com.gzsr.math.Calculate;
import com.gzsr.misc.Pair;
import com.gzsr.states.GameState;

public class Zombat extends Boss {
	private static final float HEALTH = 500.0f;
	private static final float SPEED = 0.2f;
	private static final float DPS = 10.0f;
	private static final float SIPHON_RATE = 0.15f;
	private static final float ATTACK_DIST = 250.0f;
	
	private boolean siphoningBlood;
	
	public Zombat(Pair<Float> position_) {
		super(EnemyType.ZOMBAT_SWARM, position_);
		this.health = Zombat.HEALTH;
		
		siphoningBlood = false;
	}
	
	@Override
	public void update(GameState gs, long cTime, int delta) {
		if(isAlive(cTime)) {
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
			g.setColor(Color.red);
			g.setLineWidth(2.0f);
			g.drawLine(position.x, position.y, Globals.player.getPosition().x, Globals.player.getPosition().y);
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
		theta = Calculate.Hypotenuse(position, Globals.player.getPosition());
		
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
	public double getDamage() {
		return Zombat.DPS;
	}

}
