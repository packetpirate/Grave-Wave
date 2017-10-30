package com.gzsr.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;

import com.gzsr.AssetManager;
import com.gzsr.Globals;
import com.gzsr.entities.enemies.Enemy;
import com.gzsr.gfx.Flashlight;
import com.gzsr.gfx.particles.Projectile;
import com.gzsr.math.Calculate;
import com.gzsr.misc.Pair;
import com.gzsr.objects.items.Item;
import com.gzsr.objects.weapons.AssaultRifle;
import com.gzsr.objects.weapons.Flamethrower;
import com.gzsr.objects.weapons.GrenadeLauncher;
import com.gzsr.objects.weapons.Pistol;
import com.gzsr.objects.weapons.Shotgun;
import com.gzsr.objects.weapons.Weapon;
import com.gzsr.states.GameState;
import com.gzsr.status.Status;
import com.gzsr.status.StatusEffect;

public class Player implements Entity {
	private static final double DEFAULT_MAX_HEALTH = 100.0;
	private static final float DEFAULT_SPEED = 0.15f;
	private static final double HEALTH_PER_SP = 20;
	private static final float COLLISION_DIST = 16.0f;
	
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
	public void setAttribute(String key, int val) { iAttributes.put(key, val); }
	public void addIntAttribute(String key, int amnt) { iAttributes.put(key, (getIntAttribute(key) + amnt)); }
	private Map<String, Double> dAttributes;
	public double getDoubleAttribute(String key) { return dAttributes.get(key); }
	public void setAttribute(String key, double val) { dAttributes.put(key, val); }
	public void addDoubleAttribute(String key, double amnt) { dAttributes.put(key, (getDoubleAttribute(key) + amnt)); }
	
	private List<Weapon> weapons;
	public List<Weapon> getWeapons() { return weapons; }
	public int activeWeapons() {
		return (int)weapons.stream()
						   .filter(w -> w.hasWeapon())
						   .count();
	}
	public List<Weapon> getActiveWeapons() {
		return weapons.stream()
					  .filter(w -> w.hasWeapon())
					  .collect(Collectors.toList());
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
				return;
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
	public void clearHarmful() {
		// TODO: Update this if more harmful status effects are added in the future.
		Iterator<StatusEffect> it = statusEffects.iterator();
		while(it.hasNext()) {
			Status s = it.next().getStatus();
			if(s == Status.POISON) it.remove();
		}
	}
	
	public Image getImage() { return AssetManager.getManager().getImage("GZS_Player"); }
	
	private Flashlight flashlight;
	public Flashlight getFlashlight() { return flashlight; }
	
	public Player() {
		position = new Pair<Float>(0.0f, 0.0f);
		
		iAttributes = new HashMap<String, Integer>();
		dAttributes = new HashMap<String, Double>();
		statusEffects = new ArrayList<StatusEffect>();
		
		reset();
	}
	
	@Override
	public void update(GameState gs, long cTime, int delta) {
		// Make sure player's health is up to date.
		double currentMax = getDoubleAttribute("maxHealth");
		double healthBonus = getIntAttribute("healthUp") * HEALTH_PER_SP;
		if(currentMax != (DEFAULT_MAX_HEALTH + healthBonus)) {
			// Update player's max health.
			setAttribute("maxHealth", (DEFAULT_MAX_HEALTH + healthBonus));
		}
		
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
		
		float adjSpeed = (getSpeed() + (getIntAttribute("speedUp") * (DEFAULT_SPEED * 0.10f))) * (float)getDoubleAttribute("spdMult") * delta;
		if(Globals.inputs.contains(Input.KEY_W)) move(0.0f, -adjSpeed);
		if(Globals.inputs.contains(Input.KEY_A)) move(-adjSpeed, 0.0f);
		if(Globals.inputs.contains(Input.KEY_S)) move(0.0f, adjSpeed);
		if(Globals.inputs.contains(Input.KEY_D)) move(adjSpeed, 0.0f);
		if(Globals.inputs.contains(Input.KEY_R) && 
		   !getCurrentWeapon().isReloading(cTime) &&
		   (getCurrentWeapon().getClipAmmo() != getCurrentWeapon().getClipSize())) {
			getCurrentWeapon().reload(cTime);
		}
		
		// Check to see if the player is trying to change weapon by number.
		int [] codes = new int[] { Input.KEY_0, Input.KEY_1, Input.KEY_2, Input.KEY_3, Input.KEY_4,
								   Input.KEY_5, Input.KEY_6, Input.KEY_7, Input.KEY_8, Input.KEY_9 };
		for(int i = 0; i < 10; i++) {
			if(Globals.inputs.contains(codes[i])) {
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
		weapons.stream().forEach(w -> w.update(gs, cTime, delta));
		
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
	@SuppressWarnings("serial")
	public void reset() {
		position.x = (float)(Globals.WIDTH / 2);
		position.y = (float)(Globals.HEIGHT / 2);
		
		speed = Player.DEFAULT_SPEED;
		theta = 0.0f;
		
		// TODO: When in testing phase, deactivate all but Pistol (index 0).
		weapons = new ArrayList<Weapon>() {{
			add(new Pistol());
			add(new AssaultRifle());
			add(new Shotgun());
			add(new Flamethrower());
			add(new GrenadeLauncher());
		}};
		weaponIndex = 0;
		weapons.get(weaponIndex).activate(); // activate the Pistol by default
		weapons.get(weaponIndex + 1).activate();
		weapons.get(weaponIndex + 2).activate();
		weapons.get(weaponIndex + 3).activate();
		weapons.get(weaponIndex + 4).activate();
		
		dAttributes.clear();
		iAttributes.clear();
		statusEffects.clear();
		
		// Basic attributes.
		setAttribute("health", 100.0);
		setAttribute("maxHealth", 100.0);
		setAttribute("lives", 3);
		setAttribute("money", 0);
		
		// Experience related attributes.
		setAttribute("experience", 0);
		setAttribute("expToLevel", 100);
		setAttribute("level", 1);
		setAttribute("skillPoints", 10);
		
		// Upgrade level attributes.
		setAttribute("healthUp", 0);
		setAttribute("speedUp", 0);
		setAttribute("damageUp", 0);
		
		// Multipliers
		setAttribute("expMult", 1.0);
		setAttribute("spdMult", 1.0);
		setAttribute("damMult", 1.0);
		
		flashlight = new Flashlight();
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
		setAttribute("health", newHealth);
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
			setAttribute("health", newHealth);
		}
	}
	
	public void addExperience(int amnt) {
		int currentExp = getIntAttribute("experience");
		int adjusted = currentExp + amnt;
		int expToLevel = getIntAttribute("expToLevel");
		int newLevel = getIntAttribute("level") + 1;
		
		setAttribute("experience", adjusted);
		
		if(adjusted >= expToLevel) {
			// Level up!
			int carryOver = adjusted % expToLevel;
			setAttribute("experience", carryOver);
			setAttribute("expToLevel", (expToLevel + (((newLevel / 2) * 100) + 50)));
			setAttribute("level", newLevel);
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
	
	public boolean checkCollision(Projectile p) {
		float dist = Calculate.Distance(position, p.getPosition());
		return (dist <= Player.COLLISION_DIST); 
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
