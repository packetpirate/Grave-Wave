package com.gzsr.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;

import com.gzsr.AssetManager;
import com.gzsr.Controls;
import com.gzsr.Controls.Layout;
import com.gzsr.Globals;
import com.gzsr.entities.enemies.Enemy;
import com.gzsr.entities.enemies.EnemyController;
import com.gzsr.gfx.Flashlight;
import com.gzsr.gfx.particles.Projectile;
import com.gzsr.gfx.particles.StatusProjectile;
import com.gzsr.gfx.ui.VanishingText;
import com.gzsr.math.Calculate;
import com.gzsr.misc.MouseInfo;
import com.gzsr.misc.Pair;
import com.gzsr.objects.Inventory;
import com.gzsr.objects.items.Item;
import com.gzsr.objects.weapons.LaserNode;
import com.gzsr.objects.weapons.Pistol;
import com.gzsr.objects.weapons.Weapon;
import com.gzsr.states.GameState;
import com.gzsr.status.InvulnerableEffect;
import com.gzsr.status.Status;
import com.gzsr.status.StatusEffect;

public class Player implements Entity {
	private static final double DEFAULT_MAX_HEALTH = 100.0;
	private static final float DEFAULT_SPEED = 0.15f;
	private static final double HEALTH_PER_SP = 20;
	private static final int MAX_LIVES = 5;
	private static final long RESPAWN_TIME = 3_000L;
	private static final int INVENTORY_SIZE = 16;
	
	private static Player instance = null;
	
	private Pair<Float> position;
	public Pair<Float> getPosition() { return position; }
	public void move(float xOff, float yOff) {
		if(isAlive()) {
			float tx = position.x + xOff;
			float ty = position.y + yOff;
			if((tx >= 0) && (tx < Globals.WIDTH) && 
			   (ty >= 0) && (ty < Globals.HEIGHT)) {
				position.x += xOff;
				position.y += yOff;
			}
		}
	}
	
	private Rectangle bounds;
	public Rectangle getCollider() { return bounds; }
	
	private float speed;
	public float getSpeed() { return speed; }
	
	private float theta;
	public float getRotation() { return theta; }
	
	private boolean respawning;
	public boolean isRespawning() { return respawning; }
	private long respawnTime;
	public long getTimeToRespawn(long cTime) {
		if(cTime > respawnTime) return 0L;
		return (respawnTime - cTime);
	}
	
	private Map<String, Integer> iAttributes;
	public int getIntAttribute(String key) { return iAttributes.get(key); }
	public void setAttribute(String key, int val) { iAttributes.put(key, val); }
	public void addIntAttribute(String key, int amnt) { iAttributes.put(key, (getIntAttribute(key) + amnt)); }
	private Map<String, Double> dAttributes;
	public double getDoubleAttribute(String key) { return dAttributes.get(key); }
	public void setAttribute(String key, double val) { dAttributes.put(key, val); }
	public void addDoubleAttribute(String key, double amnt) { dAttributes.put(key, (getDoubleAttribute(key) + amnt)); }
	
	private Inventory inventory;
	public Inventory getInventory() { return inventory; }
	public List<Weapon> getWeapons() { return inventory.getWeapons(); }
	private int weaponIndex;
	public int getWeaponIndex() { return weaponIndex; }
	public Weapon getCurrentWeapon() { return getWeapons().get(weaponIndex); }
	public void resetCurrentWeapon() {
		getWeapons().stream().forEach(w -> w.weaponChanged());
		weaponIndex = 0;
		getCurrentWeapon().equip();
	}
	public void setCurrentWeapon(int wi) {
		if(wi < getWeapons().size()) {
			// If the player actually has the weapon bound to the key that was pressed...
			getCurrentWeapon().weaponChanged();
			weaponIndex = wi;
			getCurrentWeapon().equip();
		}
	}
	public void weaponRotate(int direction) {
		int wc = getWeapons().size();
		getCurrentWeapon().weaponChanged(); // Notify the current weapon that we're switching.
		// have to use floorMod because apparently Java % is remainder only, not modulus... -_-
		int i = Math.floorMod((weaponIndex + direction), wc);
		weaponIndex = i;
		getCurrentWeapon().equip(); // Notify new weapon that it is equipped.
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
			if((s == Status.POISON) || (s == Status.BURNING)) it.remove();
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
	
	public static Player getPlayer() {
		if(instance == null) instance = new Player();
		return instance;
	}
	
	@Override
	public void update(GameState gs, long cTime, int delta) {
		if(!isAlive()) {
			if(!respawning) {
				int lives = getIntAttribute("lives") - 1;
				if(lives >= 0) {
					setAttribute("lives", lives);
					
					statusEffects.stream().forEach(status -> status.onDestroy(this, cTime));
					statusEffects.clear();
					
					respawning = true;
					respawnTime = (cTime + Player.RESPAWN_TIME);
				}
			} else {
				long elapsed = (cTime - respawnTime);
				if(elapsed >= 0) {
					respawning = false;
					respawnTime = 0L;
					
					// Make the player invincible for a brief period.
					setAttribute("health", getDoubleAttribute("maxHealth"));
					statusEffects.add(new InvulnerableEffect(Player.RESPAWN_TIME, cTime));
					
					// Reset the player's position.
					position.x = (float)(Globals.WIDTH / 2);
					position.y = (float)(Globals.HEIGHT / 2);
				}
			}
		}
		
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
				status.update(this, gs, cTime, delta);
			} else {
				status.onDestroy(this, cTime);
				it.remove();
			}
		}
		
		float adjSpeed = (getSpeed() + (getIntAttribute("speedUp") * (DEFAULT_SPEED * 0.10f))) * (float)getDoubleAttribute("spdMult") * delta;
		if(Controls.getInstance().isPressed(Controls.Layout.MOVE_UP)) move(0.0f, -adjSpeed);
		if(Controls.getInstance().isPressed(Controls.Layout.MOVE_LEFT)) move(-adjSpeed, 0.0f);
		if(Controls.getInstance().isPressed(Controls.Layout.MOVE_DOWN)) move(0.0f, adjSpeed);
		if(Controls.getInstance().isPressed(Controls.Layout.MOVE_RIGHT)) move(adjSpeed, 0.0f);
		if(Controls.getInstance().isPressed(Controls.Layout.RELOAD) && 
		   !getCurrentWeapon().isReloading(cTime) &&
		   (getCurrentWeapon().getClipAmmo() != getCurrentWeapon().getClipSize())) {
			getCurrentWeapon().reload(cTime);
		}
		bounds.setLocation((position.x - (getImage().getWidth() / 2)), (position.y - (getImage().getHeight() / 2)));
		
		// Check to see if the player is trying to change weapon by number.
		Layout [] keys = new Layout[] { Controls.Layout.WEAPON_1, Controls.Layout.WEAPON_2, Controls.Layout.WEAPON_3, Controls.Layout.WEAPON_4,
										Controls.Layout.WEAPON_5, Controls.Layout.WEAPON_6, Controls.Layout.WEAPON_7, Controls.Layout.WEAPON_8,
										Controls.Layout.WEAPON_9, Controls.Layout.WEAPON_10 };
		for(int i = 0; i < 10; i++) {
			if(Controls.getInstance().isPressed(keys[i])) {
				if(i == 0) setCurrentWeapon(9);
				else setCurrentWeapon(i - 1);
				break; // To avoid conflicts when holding multiple numerical keys.
			}
		}
		
		MouseInfo mouse = Controls.getInstance().getMouse();
		
		Weapon cWeapon = getCurrentWeapon();
		if((mouse.isMouseDown() || cWeapon.isChargedWeapon()) && cWeapon.canFire(cTime)) {
			cWeapon.fire(this, new Pair<Float>(position.x, position.y), 
						 theta, cTime);
		}
		
		// Update all the player's active weapons.
		getWeapons().stream().forEach(w -> w.update(gs, cTime, delta));
		
		// Calculate the player's rotation based on mouse position.
		theta = Calculate.Hypotenuse(position, mouse.getPosition()) + (float)(Math.PI / 2);
		flashlight.update(this, cTime);
	}

	@Override
	public void render(Graphics g, long cTime) {
		// Render all the player's active weapons.
		getWeapons().stream().forEach(w -> w.render(g, cTime));
		
		if(isAlive()) {
			Image image = getImage();
			if(image != null) {
				g.rotate(position.x, position.y, (float)Math.toDegrees(theta));
				g.drawImage(image, (position.x - (image.getWidth() / 2)), 
								   (position.y - (image.getHeight() / 2)));
				
				if(Globals.SHOW_COLLIDERS) {
					g.setColor(Color.red);
					g.drawRect((position.x - (image.getWidth() / 2)), 
							   (position.y - (image.getHeight() / 2)), 
							   image.getWidth(), image.getHeight());
				}
				
				g.resetTransform();
			} else {
				// Draw a shape to represent the missing player image.
				g.setColor(Color.red);
				g.fillOval((position.x - 20), (position.y - 20), 40, 40);
			}
		}
	}
	
	/**
	 * Reset all dAttributes and iAttributes members.
	 */
	public void reset() {
		position.x = (float)(Globals.WIDTH / 2);
		position.y = (float)(Globals.HEIGHT / 2);
		
		bounds = new Rectangle(position.x, position.y, getImage().getWidth(), getImage().getHeight());
		
		speed = Player.DEFAULT_SPEED;
		theta = 0.0f;
		
		respawning = false;
		respawnTime = 0L;
		
		weaponIndex = 0;
		inventory = new Inventory(Player.INVENTORY_SIZE);
		
		inventory.addItem(new Pistol());
		inventory.getWeapons().get(weaponIndex).equip();
		
		dAttributes.clear();
		iAttributes.clear();
		statusEffects.clear();
		
		// Basic attributes.
		setAttribute("health", 100.0);
		setAttribute("maxHealth", 100.0);
		setAttribute("lives", 3);
		setAttribute("maxLives", Player.MAX_LIVES);
		setAttribute("money", 0);
		
		// Experience related attributes.
		setAttribute("experience", 0);
		setAttribute("expToLevel", 100);
		setAttribute("level", 1);
		setAttribute("skillPoints", 0);
		
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
	public double takeDamage(double amnt) {
		if(isAlive() && !hasStatus(Status.INVULNERABLE)) {
			double currentHealth = getDoubleAttribute("health");
			double adjusted = currentHealth - amnt;
			double newHealth = (adjusted < 0) ? 0 : adjusted;
			setAttribute("health", newHealth);
			return amnt;
		} else return -1.0; // Indicates no damage taken.
	}
	
	public void addExperience(GameState gs, int amnt, long cTime) {
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
			
			{ // Make the player say "Ding!" and have chance for enemies to say "Gratz!"
				String key = String.format("vanishText%d", Globals.generateEntityID());
				VanishingText vt = new VanishingText("Ding!", "PressStart2P-Regular_small", 
													 new Pair<Float>(position.x, (position.y - 32.0f)), Color.white, 
													 cTime, 2_000L);
				GameState.addVanishingText(key, vt);
			}
			
			{ // Random chance for the enemies to say "Gratz!".
				float chance = Globals.rand.nextFloat();
				if(chance <= 0.02f) {
					Iterator<Enemy> it = ((EnemyController)gs.getEntity("enemyController")).getAliveEnemies().iterator();
					while(it.hasNext()) {
						Enemy e = it.next();
						if(e.isAlive(cTime)) {
							String key = String.format("vanishText%d", Globals.generateEntityID());
							VanishingText vt = new VanishingText("Gratz!", "PressStart2P-Regular_small", 
																 new Pair<Float>(e.getPosition().x, (e.getPosition().y - 32.0f)), Color.white, 
																 cTime, 2_000L);
							GameState.addVanishingText(key, vt);
						}
					}
				}
			}
			
			// TODO: Add level up sound.
		}
	}
	
	public boolean isAlive() {
		// TODO: May need to revise this in the future.
		return (getDoubleAttribute("health") > 0);
	}
	
	public boolean touchingEnemy(Enemy enemy) {
		return enemy.getCollider().intersects(bounds);
	}
	
	/**
	 * Checks for a collision between the enemy and the player's projectiles.
	 * TODO: This logic should be separated from player and put in Projectile class.
	 * @param enemy The enemy to test against the projectiles.
	 * @return Boolean value representing whether or not there was a collision.
	 */
	public boolean checkProjectiles(Enemy enemy, long cTime, int delta) {
		for(Weapon w : getWeapons()) {
			Iterator<Projectile> it = w.getProjectiles().iterator();
			while(it.hasNext()) {
				Projectile p = it.next();
				if(p.isAlive(cTime) && p.checkCollision(enemy) && enemy.isAlive(cTime)) {
					if(p instanceof LaserNode) {
						LaserNode node = (LaserNode) p;
						node.damage(enemy.getDamage());
						enemy.blockMovement();
					} else {
						p.collide();
						
						// If this is a special projectile, apply its status effect to the target.
						if(p instanceof StatusProjectile) {
							StatusProjectile sp = (StatusProjectile) p;
							sp.applyEffect(enemy, cTime);
						}
						
						float damagePercentage = (1.0f + (iAttributes.get("damageUp") * 0.10f));
						enemy.takeDamage((p.getDamage() * damagePercentage), w.getKnockback(), cTime, delta);
					}
					
					return true;
				}
			}
		}
		
		return false;
	}
	
	public boolean checkCollision(Projectile p) {
		return bounds.intersects(p.getCollider());
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
	
	@Override
	public String getName() {
		return "Player";
	}
	
	@Override
	public String getDescription() {
		return "Player";
	}
}
