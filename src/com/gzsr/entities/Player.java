package com.gzsr.entities;

import java.util.Iterator;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.BasicGameState;

import com.gzsr.AssetManager;
import com.gzsr.Controls;
import com.gzsr.Controls.Layout;
import com.gzsr.Globals;
import com.gzsr.controllers.ShopController;
import com.gzsr.entities.enemies.Enemy;
import com.gzsr.entities.enemies.EnemyController;
import com.gzsr.gfx.Camera;
import com.gzsr.gfx.Flashlight;
import com.gzsr.gfx.Layers;
import com.gzsr.gfx.particles.Projectile;
import com.gzsr.gfx.particles.StatusProjectile;
import com.gzsr.gfx.ui.StatusMessages;
import com.gzsr.math.Calculate;
import com.gzsr.misc.MouseInfo;
import com.gzsr.misc.Pair;
import com.gzsr.objects.Inventory;
import com.gzsr.objects.items.Item;
import com.gzsr.objects.weapons.Weapon;
import com.gzsr.objects.weapons.melee.Machete;
import com.gzsr.objects.weapons.melee.MeleeWeapon;
import com.gzsr.objects.weapons.ranged.Beretta;
import com.gzsr.objects.weapons.ranged.LaserNode;
import com.gzsr.objects.weapons.ranged.RangedWeapon;
import com.gzsr.states.GameState;
import com.gzsr.states.ShopState;
import com.gzsr.status.InvulnerableEffect;
import com.gzsr.status.Status;
import com.gzsr.status.StatusHandler;

public class Player implements Entity {
	private static final double DEFAULT_MAX_HEALTH = 100.0;
	private static final float DEFAULT_SPEED = 0.15f;
	private static final double HEALTH_PER_SP = 20;
	private static final int MAX_LIVES = 5;
	private static final long RESPAWN_TIME = 3_000L;
	private static final long GRUNT_TIMER = 1_500L;
	private static final int INVENTORY_SIZE = 16;
	
	private static Player instance = null;
	
	private Pair<Float> position;
	public Pair<Float> getPosition() { return position; }
	public void move(float xOff, float yOff) {
		if(isAlive() && !statusHandler.hasStatus(Status.PARALYSIS)) {
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
	
	private long lastGrunt;
	
	private Attributes attributes;
	public Attributes getAttributes() { return attributes; }
	
	private Inventory inventory;
	public Inventory getInventory() { return inventory; }
	public List<RangedWeapon> getRangedWeapons() { return inventory.getRangedWeapons(); }
	public List<MeleeWeapon> getMeleeWeapons() { return inventory.getMeleeWeapons(); }
	private int rangedIndex;
	public int getRangedIndex() { return rangedIndex; }
	private int meleeIndex;
	public int getMeleeIndex() { return meleeIndex; }
	public RangedWeapon getCurrentRanged() {
		List<RangedWeapon> weapons = getRangedWeapons();
		if(!weapons.isEmpty()) return weapons.get(rangedIndex);
		else return null;
	}
	public MeleeWeapon getCurrentMelee() {
		List<MeleeWeapon> weapons = getMeleeWeapons();
		if(!weapons.isEmpty()) return weapons.get(meleeIndex);
		else return null;
	}
	public void resetCurrentRanged() {
		List<RangedWeapon> weapons = getRangedWeapons();
		weapons.stream().forEach(w -> w.unequip());
		rangedIndex = 0;
		if(!weapons.isEmpty()) weapons.get(rangedIndex).equip();
	}
	public void setCurrentRanged(int wi) {
		List<RangedWeapon> weapons = getRangedWeapons();
		if(!weapons.isEmpty()) {
			if((wi >= 0) && (wi < weapons.size())) {
				// If the player actually has the weapon bound to the key that was pressed...
				getCurrentRanged().unequip();
				rangedIndex = wi;
				getCurrentRanged().equip();
			}
		}
	}
	public void setCurrentMelee(int wi) {
		List<MeleeWeapon> weapons = getMeleeWeapons();
		if(!weapons.isEmpty()) {
			if((wi > 0) && (wi < weapons.size())) {
				getCurrentMelee().unequip();
				meleeIndex = wi;
				getCurrentMelee().equip();
			}
		}
	}
	public void equip(Weapon w) {
		w.equip();
		
		if(w instanceof RangedWeapon) {
			List<RangedWeapon> weapons = getRangedWeapons();
			getCurrentRanged().unequip();
			for(int i = 0; i < weapons.size(); i++) {
				if(weapons.get(i) == w) {
					rangedIndex = i;
					break;
				}
			}
		} else if(w instanceof MeleeWeapon) {
			List<MeleeWeapon> weapons = getMeleeWeapons();
			getCurrentMelee().unequip();
			for(int i = 0; i < weapons.size(); i++) {
				if(weapons.get(i) == w) {
					meleeIndex = i;
					break;
				}
			}
		}
	}
	public void weaponRotate(int direction) {
		int wc = getRangedWeapons().size();
		if(wc > 0) {
			Weapon currentWeapon = getCurrentRanged();
			currentWeapon.unequip(); // Notify the current weapon that we're switching.
			// have to use floorMod because apparently Java % is remainder only, not modulus... -_-
			int i = Math.floorMod((rangedIndex + direction), wc);
			rangedIndex = i;
			currentWeapon.equip(); // Notify new weapon that it is equipped.
		}
	}
	
	private StatusHandler statusHandler;
	public StatusHandler getStatusHandler() { return statusHandler; }
	
	public Image getImage() { return AssetManager.getManager().getImage("GZS_Player"); }
	
	private Flashlight flashlight;
	public Flashlight getFlashlight() { return flashlight; }
	
	public Player() {
		position = new Pair<Float>(0.0f, 0.0f);
		
		attributes = new Attributes();
		statusHandler = new StatusHandler(this);
		
		reset();
	}
	
	public static Player getPlayer() {
		if(instance == null) instance = new Player();
		return instance;
	}
	
	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
		if(!isAlive()) {
			if(!respawning) {
				int lives = attributes.getInt("lives") - 1;
				if(lives >= 0) {
					attributes.set("lives", lives);
					
					statusHandler.destroyAll(cTime);
					
					respawning = true;
					respawnTime = (cTime + Player.RESPAWN_TIME);
				}
			} else {
				long elapsed = (cTime - respawnTime);
				if(elapsed >= 0) {
					respawning = false;
					respawnTime = 0L;
					
					// Make the player invincible for a brief period.
					attributes.set("health", attributes.getDouble("maxHealth"));
					statusHandler.addStatus(new InvulnerableEffect(Player.RESPAWN_TIME, cTime), cTime);
					
					// Reset the player's position.
					position.x = (float)(Globals.WIDTH / 2);
					position.y = (float)(Globals.HEIGHT / 2);
				}
			}
		}
		
		// Make sure player's health is up to date.
		double currentMax = attributes.getDouble("maxHealth");
		double healthBonus = attributes.getInt("healthUp") * HEALTH_PER_SP;
		if(currentMax != (DEFAULT_MAX_HEALTH + healthBonus)) {
			// Update player's max health.
			attributes.set("maxHealth", (DEFAULT_MAX_HEALTH + healthBonus));
		}
		
		// Need to make sure to update the status effects first.
		statusHandler.update((GameState)gs, cTime, delta);
		
		boolean canMove = true;
		MeleeWeapon cMeleeWeapon = getCurrentMelee();
		RangedWeapon cRangedWeapon = getCurrentRanged();
		
		if(cMeleeWeapon != null) canMove = !cMeleeWeapon.isAttacking(); // Can't move if melee attacking.
		
		if(canMove) {
			float adjSpeed = (getSpeed() + (attributes.getInt("speedUp") * (DEFAULT_SPEED * 0.10f))) * (float)attributes.getDouble("spdMult") * delta;
			if(Controls.getInstance().isPressed(Controls.Layout.MOVE_UP)) move(0.0f, -adjSpeed);
			if(Controls.getInstance().isPressed(Controls.Layout.MOVE_LEFT)) move(-adjSpeed, 0.0f);
			if(Controls.getInstance().isPressed(Controls.Layout.MOVE_DOWN)) move(0.0f, adjSpeed);
			if(Controls.getInstance().isPressed(Controls.Layout.MOVE_RIGHT)) move(adjSpeed, 0.0f);
		}
		
		if(Controls.getInstance().isReleased(Controls.Layout.FLASHLIGHT)) flashlight.toggle();
		if(Controls.getInstance().isPressed(Controls.Layout.RELOAD) && (cRangedWeapon != null)) {
			if(!cRangedWeapon.isReloading(cTime) && (cRangedWeapon.getClipAmmo() != cRangedWeapon.getClipSize())) cRangedWeapon.reload(cTime);
		}
		bounds.setLocation((position.x - (getImage().getWidth() / 2)), (position.y - (getImage().getHeight() / 2)));
		
		// Check to see if the player is trying to change weapon by number.
		Layout [] keys = new Layout[] { Controls.Layout.WEAPON_1, Controls.Layout.WEAPON_2, Controls.Layout.WEAPON_3, Controls.Layout.WEAPON_4,
										Controls.Layout.WEAPON_5, Controls.Layout.WEAPON_6, Controls.Layout.WEAPON_7, Controls.Layout.WEAPON_8,
										Controls.Layout.WEAPON_9, Controls.Layout.WEAPON_10 };
		for(int i = 0; i < 10; i++) {
			if(Controls.getInstance().isPressed(keys[i])) {
				setCurrentRanged(i);
				break; // To avoid conflicts when holding multiple numerical keys.
			}
		}
		
		MouseInfo mouse = Controls.getInstance().getMouse();
		
		if(cRangedWeapon != null) {
			if(mouse.isLeftDown()) {
				boolean empty = (cRangedWeapon.getClipAmmo() == 0);
				boolean canUse = !cRangedWeapon.isChargedWeapon() && cRangedWeapon.canUse(cTime);
				if(empty) {
					long elapsed = cTime - attributes.getLong("lastClick");
					if(elapsed >= 1_000L) {
						Sound click = AssetManager.getManager().getSound("out-of-ammo_click");
						click.play(1.0f, AssetManager.getManager().getSoundVolume());
						
						String message = "Out of Ammo!";
						StatusMessages.getInstance().addMessage(message, this, new Pair<Float>(0.0f, -32.0f), cTime, 1_000L);
						
						attributes.set("lastClick", cTime);
					}
				} else if(canUse) cRangedWeapon.use(this, new Pair<Float>(position), theta, cTime);
			} else if(mouse.isRightDown()) {
				MeleeWeapon mw = getCurrentMelee();
				if(mw.canUse(cTime)) mw.use(this, new Pair<Float>(position), theta, cTime);
			}
		}
		
		// Update all the player's active weapons.
		getRangedWeapons().stream().forEach(w -> w.update(gs, cTime, delta));
		getMeleeWeapons().stream().forEach(w -> w.update(gs, cTime, delta));
		
		// Calculate the player's rotation based on mouse position.
		if(canMove) {
			theta = Calculate.Hypotenuse(position, mouse.getPosition()) + (float)(Math.PI / 2);
			flashlight.update(this, cTime);
		}
	}

	@Override
	public void render(Graphics g, long cTime) {
		// Render all the player's active weapons.
		getRangedWeapons().stream().forEach(w -> w.render(g, cTime));
		getMeleeWeapons().stream().forEach(w -> w.render(g, cTime));
		
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
				
				g.rotate(position.x, position.y, -(float)Math.toDegrees(theta));
			} else {
				// Draw a shape to represent the missing player image.
				g.setColor(Color.red);
				g.fillOval((position.x - 20), (position.y - 20), 40, 40);
			}	
		}
		
		statusHandler.render(g, cTime);
		flashlight.render(g, cTime);
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
		
		lastGrunt = 0L;
		
		rangedIndex = 0;
		meleeIndex = 0;
		inventory = new Inventory(Player.INVENTORY_SIZE);
		
		Machete machete = new Machete();
		Beretta beretta = new Beretta();
		
		machete.equip();
		beretta.equip();
		
		inventory.addItem(machete);
		inventory.addItem(beretta);
		
		attributes.reset();
		statusHandler.clearAll();
		
		// Basic attributes.
		attributes.set("health", 100.0);
		attributes.set("maxHealth", 100.0);
		attributes.set("armor", 0.0);
		attributes.set("maxArmor", 100.0);
		attributes.set("lives", 3);
		attributes.set("maxLives", Player.MAX_LIVES);
		attributes.set("money", 0);
		
		// Experience related attributes.
		attributes.set("experience", 0);
		attributes.set("expToLevel", 100);
		attributes.set("level", 1);
		attributes.set("skillPoints", 0);
		
		// Upgrade level attributes.
		attributes.set("healthUp", 0);
		attributes.set("speedUp", 0);
		attributes.set("damageUp", 0);
		
		// Miscellaneous Modifiers
		attributes.set("critChance", 0.05f);
		
		// Multipliers
		attributes.set("expMult", 1.0);
		attributes.set("spdMult", 1.0);
		attributes.set("damMult", 1.0);
		attributes.set("critMult", 2.0);
		
		// Misc Properties
		attributes.set("lastClick", 0L);
		
		flashlight = new Flashlight();
	}
	
	/**
	 * Add health to the player's current health. Usually only used for applying health kits to the player.
	 * @param amnt The amount of health to give the player.
	 */
	public void addHealth(double amnt) {
		double currentHealth = attributes.getDouble("health");
		double maxHealth = attributes.getDouble("maxHealth");
		double adjusted = currentHealth + amnt;
		double newHealth = (adjusted > maxHealth) ? maxHealth : adjusted;
		attributes.set("health", newHealth);
	}
	
	/**
	 * Deal damage to the player.
	 * @param amnt The amount of damage to apply to the player's health.
	 */
	public double takeDamage(double amnt, long cTime) {
		return takeDamage(amnt, cTime, false);
	}
	
	public double takeDamage(double amnt, long cTime, boolean piercing) {
		if(isAlive() && !statusHandler.hasStatus(Status.INVULNERABLE)) {
			if(!piercing) amnt = damageArmor(amnt); // First, deal damage to player's armor.
			
			// Deal leftover damage to health.
			double currentHealth = attributes.getDouble("health");
			double adjusted = currentHealth - amnt;
			double newHealth = (adjusted < 0) ? 0 : adjusted;
			attributes.set("health", newHealth);
			
			if((cTime - lastGrunt) >= GRUNT_TIMER) {
				int grunt = Globals.rand.nextInt(4) + 1;
				AssetManager.getManager().getSound(String.format("grunt%d", grunt)).play();
				Camera.getCamera().shake(cTime, 500L, 100L, 4.0f);
				Camera.getCamera().damage(cTime);
				
				lastGrunt = cTime;
			}
			
			return amnt;
		} else return -1.0; // Indicates no damage taken.
	}
	
	/**
	 * Adds armor value to the player.
	 * @param amnt How much armor to add.
	 */
	public void addArmor(double amnt) {
		double currentArmor = attributes.getDouble("armor");
		double maxArmor = attributes.getDouble("maxArmor");
		double adjusted = currentArmor + amnt;
		double newArmor = (adjusted > maxArmor) ? maxArmor : adjusted;
		attributes.set("armor", newArmor);
	}
	
	/**
	 * Damages the player's armor before dealing damage to health.
	 * @param amnt How much damage should be mitigated by the armor.
	 * @return How much damage is left to deal to health after dealing damage to armor.
	 */
	public double damageArmor(double amnt) {
		double currentArmor = attributes.getDouble("armor");
		double adjusted = currentArmor - amnt;
		attributes.set("armor", ((adjusted > 0.0) ? adjusted : 0.0));
		if(adjusted < 0.0) return Math.abs(adjusted);
		return 0.0;
	}
	
	public void addExperience(GameState gs, int amnt, long cTime) {
		int totalAmnt = (int)(amnt * attributes.getDouble("expMult"));
		int currentExp = attributes.getInt("experience");
		int adjusted = currentExp + totalAmnt;
		int expToLevel = attributes.getInt("expToLevel");
		int newLevel = attributes.getInt("level") + 1;
		
		attributes.set("experience", adjusted);
		
		if(adjusted >= expToLevel) {
			// Level up!
			int carryOver = adjusted % expToLevel;
			attributes.set("experience", carryOver);
			attributes.set("expToLevel", (expToLevel + (((newLevel / 2) * 100) + 50)));
			attributes.set("level", newLevel);
			attributes.addTo("skillPoints", 1);
			
			{ // Make the player say "Ding!" and have chance for enemies to say "Gratz!"
				String reminder = String.format("Press \'%s\' to Level Up!", Controls.Layout.TRAIN_SCREEN.getDisplay());
				StatusMessages.getInstance().addMessage("Ding!", this, new Pair<Float>(0.0f, -32.0f), cTime, 2_000L);
				StatusMessages.getInstance().addMessage(reminder, this, new Pair<Float>(0.0f, 32.0f), cTime, 2_000L);
			}
			
			{ // Random chance for the enemies to say "Gratz!".
				float chance = Globals.rand.nextFloat();
				if(chance <= 0.02f) {
					Iterator<Enemy> it = EnemyController.getInstance().getAliveEnemies().iterator();
					while(it.hasNext()) {
						Enemy e = it.next();
						if(e.isAlive(cTime)) {
							StatusMessages.getInstance().addMessage("Gratz!", e, new Pair<Float>(0.0f, -32.0f), cTime, 2_000L);
						}
					}
				}
			}
			
			ShopController.getInstance().release(ShopState.getShop()); // Add new weapons to the shop!
			AssetManager.getManager().getSound("level-up").play(1.0f, AssetManager.getManager().getSoundVolume());
		}
	}
	
	public boolean isAlive() {
		// TODO: May need to revise this in the future.
		return (attributes.getDouble("health") > 0);
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
	public boolean checkWeapons(GameState gs, Enemy enemy, long cTime, int delta) {
		for(RangedWeapon rw : getRangedWeapons()) {
			Iterator<Projectile> it = rw.getProjectiles().iterator();
			while(it.hasNext()) {
				Projectile p = it.next();
				if(p.isAlive(cTime) && p.checkCollision(enemy) && !enemy.dead()) {
					if(p instanceof LaserNode) {
						LaserNode node = (LaserNode) p;
						node.damage(enemy.getDamage());
						enemy.blockMovement();
					} else {
						p.collide(gs, enemy, cTime);
						
						// If this is a special projectile, apply its status effect to the target.
						if(p instanceof StatusProjectile) {
							StatusProjectile sp = (StatusProjectile) p;
							sp.applyEffect(enemy, cTime);
						}
						
						float damagePercentage = (1.0f + (attributes.getInt("damageUp") * 0.10f));
						double totalDamage = (p.getDamage() * damagePercentage);
						if(totalDamage > 0.0) enemy.takeDamage(totalDamage, rw.getKnockback(), (float)(p.getTheta() - (Math.PI / 2)), cTime, delta, true, p.isCritical());
					}
					
					return true;
				}
			}
		}
		
		for(MeleeWeapon mw : getMeleeWeapons()) {
			if(mw.isAttacking() && mw.hit(gs, enemy, cTime)) {
				float damagePercentage = (1.0f + (attributes.getInt("damageUp") * 0.10f));
				double totalDamage = (mw.rollDamage() * damagePercentage);
				if(totalDamage > 0.0) enemy.takeDamage(totalDamage, mw.getKnockback(), (theta - (float)(Math.PI / 2)), cTime, delta, true, mw.isCurrentCritical());
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
	
	@Override
	public int getLayer() {
		return Layers.PLAYER.val();
	}
}
