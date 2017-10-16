package com.gzsr.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import com.gzsr.AssetManager;
import com.gzsr.Globals;
import com.gzsr.entities.enemies.Enemy;
import com.gzsr.gfx.Flashlight;
import com.gzsr.gfx.particles.Projectile;
import com.gzsr.math.Calculate;
import com.gzsr.misc.Pair;
import com.gzsr.objects.items.Item;
import com.gzsr.objects.weapons.AssaultRifle;
import com.gzsr.objects.weapons.Pistol;
import com.gzsr.objects.weapons.Shotgun;
import com.gzsr.objects.weapons.Weapon;
import com.gzsr.status.Status;
import com.gzsr.status.StatusEffect;

public class Player implements Entity {
	private static final float DEFAULT_SPEED = 0.15f;
	
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
	public void addIntAttribute(String key, int amnt) { iAttributes.put(key, (getIntAttribute(key) + amnt)); }
	private Map<String, Double> dAttributes;
	public double getDoubleAttribute(String key) { return dAttributes.get(key); }
	public void setDoubleAttribute(String key, double val) { dAttributes.put(key, val); }
	public void addDoubleAttribute(String key, double amnt) { dAttributes.put(key, (getDoubleAttribute(key) + amnt)); }
	
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
	public void setCurrentWeapon(int wi) {
		if((wi < weapons.size()) && weapons.get(wi).hasWeapon()) {
			// If the player actually has the weapon bound to the key that was pressed...
			weaponIndex = wi;
		}
	}
	public void weaponRotate(int direction) {
		int wc = weapons.size();
		// have to use floorMod because apparently Java % is remainder only, not modulus... -_-
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
		speed = Player.DEFAULT_SPEED;
		theta = 0.0f;
		
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
		
		// Check to see if the player is trying to change weapon by number.
		for(int i = 0; i < 10; i++) {
			if(Globals.inputs.contains(Integer.toString(i))) {
				if(i == 0) setCurrentWeapon(9);
				else setCurrentWeapon(i - 1);
				break; // To avoid conflicts when holding multiple numerical keys.
			}
		}
		
		if(Globals.mouse.isMouseDown() && getCurrentWeapon().canFire(cTime)) {
			getCurrentWeapon().fire(this, new Pair<Float>(position.x, position.y), 
							   theta, cTime);
		}
		
		// Call update for all weapon objects.
		weapons.stream().forEach(w -> w.update(cTime));
		
		// Calculate the player's rotation based on mouse position.
		theta = Calculate.Hypotenuse(position, Globals.mouse.getPosition()) + (float)(Math.PI / 2);
		flashlight.update(this, cTime);
	}

	@Override
	public void render(Graphics g, long cTime) {
		getCurrentWeapon().render(g, cTime);
		
		Image image = getImage();
		if(image != null) {
			g.rotate(position.x, position.y, (float)Math.toDegrees(theta));
			g.drawImage(image, (position.x - (image.getWidth() / 2)), 
							   (position.y - (image.getHeight() / 2)));
			g.resetTransform();
		} else {
			// Draw a shape to represent the missing player image.
			g.setColor(Color.red);
			g.fillOval((position.x - 20), (position.y - 20), 40, 40);
		}
	}
	
	/**
	 * Reset all dAttributes and iAttributes members.
	 */
	public void resetAttributes() {
		dAttributes.clear();
		iAttributes.clear();
		
		// Basic attributes.
		dAttributes.put("health", 100.0);
		dAttributes.put("maxHealth", 100.0);
		iAttributes.put("lives", 3);
		iAttributes.put("money", 0);
		
		// Experience related attributes.
		iAttributes.put("experience", 0);
		iAttributes.put("expToLevel", 100);
		iAttributes.put("level", 1);
		iAttributes.put("skillPoints", 10);
		
		// Upgrade level attributes.
		iAttributes.put("healthUp", 0);
		iAttributes.put("speedUp", 0);
		iAttributes.put("damageUp", 0);
		
		// Multipliers
		dAttributes.put("expMult", 1.0);
		dAttributes.put("spdMult", 1.0);
		dAttributes.put("damMult", 1.0);
	}
	
	/**
	 * Add health to the player's current health. Usually only used for applying health kits to the player.
	 * @param amnt The amount of health to give the player.
	 */
	public void addHealth(double amnt) {
		double currentHealth = getDoubleAttribute("health");
		double maxHealth = getDoubleAttribute("maxHealth");
		double adjusted = currentHealth + amnt;
		double newHealth = (adjusted > maxHealth) ? maxHealth : adjusted;
		setDoubleAttribute("health", newHealth);
	}
	
	/**
	 * Deal damage to the player.
	 * @param amnt The amount of damage to apply to the player's health.
	 */
	public void takeDamage(double amnt) {
		if(!hasStatus(Status.INVULNERABLE)) {
			double currentHealth = getDoubleAttribute("health");
			double adjusted = currentHealth - amnt;
			double newHealth = (adjusted < 0) ? 0 : adjusted;
			setDoubleAttribute("health", newHealth);
		}
	}
	
	public void addExperience(int amnt) {
		int currentExp = getIntAttribute("experience");
		int adjusted = currentExp + amnt;
		int expToLevel = getIntAttribute("expToLevel");
		int newLevel = getIntAttribute("level") + 1;
		
		setIntAttribute("experience", adjusted);
		
		if(adjusted >= expToLevel) {
			// Level up!
			int carryOver = adjusted % expToLevel;
			setIntAttribute("experience", carryOver);
			setIntAttribute("expToLevel", (expToLevel + (((newLevel / 2) * 100) + 50)));
			setIntAttribute("level", newLevel);
			addIntAttribute("skillPoints", 1);
			// TODO: Add level up sound.
		}
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
	 * @return Boolean value representing whether or not there was a collision.
	 */
	public boolean checkProjectiles(Enemy enemy, long cTime) {
		Iterator<Projectile> it = getCurrentWeapon().getProjectiles().iterator();
		while(it.hasNext()) {
			Projectile p = it.next();
			if(p.isAlive(cTime) && enemy.checkCollision(p.getPosition())) {
				p.collide();
				float damagePercentage = (1.0f + (iAttributes.get("damageUp") * 0.10f));
				enemy.takeDamage(p.getDamage() * damagePercentage);
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Check for a collision between the player and a given Item object.
	 * @param item The Item object to check for a collision with.
	 * @param cTime The current game time. Used for setting the start time for any Item effects to be applied.
	 */
	public void checkItem(Item item, long cTime) {
		float distance = Calculate.Distance(position, item.getPosition());
		if(item.isTouching(distance)) item.apply(this, cTime);
	}
}
