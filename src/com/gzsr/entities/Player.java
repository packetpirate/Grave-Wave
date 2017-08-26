package com.gzsr.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.tests.xml.Item;

import com.gzsr.AssetManager;
import com.gzsr.Globals;
import com.gzsr.misc.Pair;

public class Player implements Entity {
	private Pair<Float> position;
	public Pair<Float> getPosition() { return position; }
	public void move(float xOff, float yOff) {
		float tx = position.x + xOff;
		float ty = position.y + yOff;
		if((tx >= 0) && (tx < Globals.WIDTH) && 
		   (ty >= 0) && (ty < Globals.HEIGHT)) {
			position.x += xOff;
			position.y += yOff;
		}
	}
	
	private float speed;
	public float getSpeed() { return speed; }
	
	private float theta;
	public float getRotation() { return theta; }
	
	private Map<String, Integer> iAttributes;
	public int getIntAttribute(String key) { return iAttributes.get(key); }
	public void setIntAttribute(String key, int val) { iAttributes.put(key, val); }
	private Map<String, Double> dAttributes;
	public double getDoubleAttribute(String key) { return dAttributes.get(key); }
	public void setDoubleAttribute(String key, double val) { dAttributes.put(key, val); }
	
	private List<Weapon> weapons;
	public List<Weapon> getWeapons() { return weapons; }
	public int activeWeapons() {
		return (int)weapons.stream()
						   .filter(w -> w.hasWeapon())
						   .count();
	}
	public List<Weapon> getActiveWeapons() {
		List<Weapon> activeWeapons = new ArrayList<Weapon>();
		for(Weapon w : weapons) {
			if(w.hasWeapon()) activeWeapons.add(w); 
		}
		return activeWeapons;
	}
	private int weaponIndex;
	public int getWeaponIndex() { return weaponIndex; }
	public Weapon getCurrentWeapon() { return weapons.get(weaponIndex); }
	public void weaponRotate(int direction) {
		int wc = weapons.size();
		// have to use floorMod because apparently Java % is remainder only, not modulus... -_-
		//weaponIndex = Math.floorMod((weaponIndex + direction), wc);
		int i = Math.floorMod((weaponIndex + direction), wc);
		while(!weapons.get(i).hasWeapon()) i += direction;
		weaponIndex = i;
	}
	
	private List<StatusEffect> statusEffects;
	public List<StatusEffect> getStatuses() { return statusEffects; }
	public void addStatus(StatusEffect effect, long cTime) {
		// First check to see if the player already has this status.
		for(StatusEffect se : statusEffects) {
			Status s = se.getStatus();
			if(s.equals(effect.getStatus())) {
				// Refresh the effect rather than adding it to the list.
				se.refresh(cTime);
			}
		}
		
		// The player does not have this effect. Add it.
		statusEffects.add(effect);
	}
	public boolean hasStatus(Status status) {
		for(StatusEffect se : statusEffects) {
			Status ses = se.getStatus();
			if(ses.equals(status)) return true;
		}
		
		return false;
	}
	
	public Image getImage() { return AssetManager.getManager().getImage("GZS_Player"); }
	
	private Flashlight flashlight;
	public Flashlight getFlashlight() { return flashlight; }
	
	public Player() {
		position = new Pair<Float>((float)(Globals.WIDTH / 2), 
								   (float)(Globals.HEIGHT / 2));
		
		iAttributes = new HashMap<String, Integer>();
		dAttributes = new HashMap<String, Double>();
		resetAttributes();
		
		weapons = new ArrayList<Weapon>() {{
			add(new Pistol());
			add(new AssaultRifle());
			add(new Shotgun());
		}};
		weaponIndex = 0;
		weapons.get(weaponIndex).activate(); // activate the Pistol by default
		weapons.get(weaponIndex + 1).activate();
		weapons.get(weaponIndex + 2).activate();
		
		statusEffects = new ArrayList<StatusEffect>();
		
		flashlight = new Flashlight();
	}
	
	@Override
	public void update(long cTime) {
		// Need to make sure to update the status effects first.
		Iterator<StatusEffect> it = statusEffects.iterator();
		while(it.hasNext()) {
			StatusEffect status = (StatusEffect) it.next();
			if(status.isActive(cTime)) {
				status.update(this, cTime);
			} else {
				status.onDestroy(this, cTime);
				it.remove();
			}
		}
		
		// TODO: rewrite to use LibGDX input handling
		float adjSpeed = getSpeed() * (float)getDoubleAttribute("spdMult");
		if(Globals.inputs.contains("W")) move(0.0f, -adjSpeed);
		if(Globals.inputs.contains("A")) move(-adjSpeed, 0.0f);
		if(Globals.inputs.contains("S")) move(0.0f, adjSpeed);
		if(Globals.inputs.contains("D")) move(adjSpeed, 0.0f);
		if(Globals.inputs.contains("R") && 
		   !getCurrentWeapon().isReloading(cTime) &&
		   (getCurrentWeapon().getClipAmmo() != getCurrentWeapon().getClipSize())) {
			getCurrentWeapon().reload(cTime);
		}
		
		if(Globals.mouse.isMouseDown() && getCurrentWeapon().canFire(cTime)) {
			getCurrentWeapon().fire(new Pair<Float>(position.x, position.y), 
							   theta, cTime);
		}
		
		weapons.stream().forEach(w -> w.update(cTime));
		
		// Calculate the player's rotation based on mouse position.
		theta = Calculate.Hypotenuse(position, Globals.mouse.getPosition()) + (float)(Math.PI / 2);
		flashlight.update(this, cTime);
	}

	@Override
	public void render(Graphics g, long cTime) {
		getCurrentWeapon().render(g, cTime);
		if(getImage() != null) {
			// Draw the player image.
		} else {
			// Draw a shape to represent the missing player image.
		}
	}
	
	private void resetAttributes() {
		dAttributes.clear();
		
		dAttributes.put("health", 100.0);
		dAttributes.put("maxHealth", 100.0);
		iAttributes.put("lives", 3);
		iAttributes.put("money", 0);
		
		iAttributes.put("experience", 0);
		iAttributes.put("expToLevel", 100);
		iAttributes.put("level", 1);
		iAttributes.put("skillPoints", 0);
		
		// Multipliers
		dAttributes.put("expMult", 1.0);
		dAttributes.put("spdMult", 1.0);
		dAttributes.put("damMult", 1.0);
	}
	
	public void addHealth(double amnt) {
		double currentHealth = getDoubleAttribute("health");
		double maxHealth = getDoubleAttribute("maxHealth");
		double adjusted = currentHealth + amnt;
		double newHealth = (adjusted > maxHealth) ? maxHealth : adjusted;
		setDoubleAttribute("health", newHealth);
	}
	
	public void takeDamage(double amnt) {
		double currentHealth = getDoubleAttribute("health");
		double adjusted = currentHealth - amnt;
		double newHealth = (adjusted < 0) ? 0 : adjusted;
		setDoubleAttribute("health", newHealth);
	}
	
	public boolean isAlive() {
		// TODO: May need to revise this in the future.
		return (getDoubleAttribute("health") > 0);
	}
	
	public boolean touchingEnemy(Enemy enemy) {
		return (isAlive() && enemy.checkCollision(position));
	}
	
	/**
	 * Checks for a collision between the enemy and the player's projectiles.
	 * @param enemy The enemy to test against the projectiles.
	 * @return If there is a collision, return true. Otherwise, false.
	 */
	public boolean checkCollisions(Enemy enemy, long cTime) {
		Iterator<Projectile> it = getCurrentWeapon().getProjectiles().iterator();
		while(it.hasNext()) {
			Projectile p = it.next();
			if(p.isAlive(cTime) && enemy.checkCollision(p.getPosition())) {
				p.collide();
				enemy.takeDamage(p.getDamage());
				return true;
			}
		}
		
		return false;
	}
	
	public void checkItem(Item item, long cTime) {
		float distance = Calculate.Distance(position, item.getPosition());
		if(item.isTouching(distance)) item.apply(this, cTime);
	}
}
