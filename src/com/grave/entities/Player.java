package com.grave.entities;

import java.util.Iterator;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Ellipse;
import org.newdawn.slick.state.BasicGameState;

import com.grave.AssetManager;
import com.grave.Controls;
import com.grave.Globals;
import com.grave.achievements.Metrics;
import com.grave.controllers.AchievementController;
import com.grave.controllers.Scorekeeper;
import com.grave.controllers.ShopController;
import com.grave.entities.components.HeartMonitor;
import com.grave.entities.enemies.Enemy;
import com.grave.entities.enemies.EnemyController;
import com.grave.gfx.Animation;
import com.grave.gfx.Camera;
import com.grave.gfx.Flashlight;
import com.grave.gfx.Layers;
import com.grave.gfx.particles.Projectile;
import com.grave.gfx.particles.StatusProjectile;
import com.grave.gfx.ui.StatusMessages;
import com.grave.math.Calculate;
import com.grave.misc.MouseInfo;
import com.grave.misc.Pair;
import com.grave.objects.Inventory;
import com.grave.objects.crafting.Resources;
import com.grave.objects.items.Item;
import com.grave.objects.weapons.ArmConfig;
import com.grave.objects.weapons.WType;
import com.grave.objects.weapons.Weapon;
import com.grave.objects.weapons.melee.Machete;
import com.grave.objects.weapons.melee.MeleeWeapon;
import com.grave.objects.weapons.ranged.Beretta;
import com.grave.objects.weapons.ranged.LaserNode;
import com.grave.objects.weapons.ranged.RangedWeapon;
import com.grave.states.GameState;
import com.grave.states.ShopState;
import com.grave.status.InvulnerableEffect;
import com.grave.status.Status;
import com.grave.status.StatusHandler;
import com.grave.talents.Talents;
import com.grave.tmx.TMap;
import com.grave.world.Level;
import com.grave.world.objects.DamageableObject;
import com.grave.world.pathing.FlowField;

public class Player implements Entity {
	private static final float DEFAULT_SPEED = 0.15f;
	private static final int MAX_LIVES = 5;
	private static final long RESPAWN_TIME = 3_000L;
	private static final long GRUNT_TIMER = 1_500L;
	private static final int INVENTORY_SIZE = 16;
	private static final float COLLECTION_DIST = 128.0f;
	private static final float COLLECTION_STRENGTH = 0.25f;

	public static final float INTERACT_DIST = 96.0f;

	//private static final Pair<Float> HAND_OFFSET = new Pair<Float>(16.0f, -5.0f);
	public static final Pair<Float> ABOVE_1 = new Pair<Float>(0.0f, -64.0f);
	public static final Pair<Float> BELOW_1 = new Pair<Float>(0.0f, 64.0f);
	public static final Pair<Float> BELOW_2 = new Pair<Float>(0.0f, 79.0f);

	private static Player instance = null;

	private boolean moving;
	private Pair<Float> position;
	public Pair<Float> getPosition() { return position; }
	private Pair<Float> velocity;
	public Pair<Float> getVelocity() { return velocity; }
	public void move(Level level, float xOff, float yOff) {
		if(isAlive()) {
			TMap map = level.getMap();

			if(!level.obstaclePresent(new Pair<Float>((position.x + xOff), (position.y + yOff)), new Pair<Float>(24.0f, 24.0f))) {
				moving = true;
				velocity.x = xOff;
				velocity.y = yOff;

				Pair<Integer> oldGridCoords = map.worldToGridCoords(position);

				position.x += velocity.x;
				position.y += velocity.y;

				// Constrain the player to the level.
				float w = map.getMapWidthTotal();
				float h = map.getMapHeightTotal();

				if((position.x - 32.0f) < 0.0f) position.x = 32.0f;
				else if((position.x + 32.0f) >= w) position.x = (w - 32.0f);

				if((position.y - 32.0f) < 0.0f) position.y = 32.0f;
				else if((position.y + 32.0f) >= h) position.y = (h - 32.0f);

				Pair<Integer> newGridCoords = map.worldToGridCoords(position);

				if(!oldGridCoords.equals(newGridCoords)) {
					if(flowField == null) flowField = new FlowField(map, newGridCoords);
					else flowField.recalculate(newGridCoords);
				}

				Camera.getCamera().focusOnPlayer(map);
			}
		}
	}

	private FlowField flowField;
	public FlowField getFlowField() { return flowField; }

	private Animation body;
	private Animation feet;

	private Ellipse bounds;
	public Ellipse getCollider() { return bounds; }

	private float speed;
	public float getSpeed() { return speed; }
	public void setSpeed(float speed_) { this.speed = speed_; }

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

	public float getMeleeCritChance() { return (attributes.getFloat("meleeCritChance") + attributes.getFloat("critBonus")); }
	public float getRangeCritChance() { return (attributes.getFloat("rangeCritChance") + attributes.getFloat("critBonus")); }

	private Resources resources;
	public Resources getResources() { return resources; }
	public float getCollectionDistance() { return Player.COLLECTION_DIST; }
	public float getCollectionStrength() { return Player.COLLECTION_STRENGTH; }

	private Talents talents;
	public Talents getTalents() { return talents; }

	private Inventory inventory;
	public Inventory getInventory() { return inventory; }
	public List<RangedWeapon> getRangedWeapons() { return inventory.getRangedWeapons(); }
	public List<MeleeWeapon> getMeleeWeapons() { return inventory.getMeleeWeapons(); }
	private int rangedIndex;
	public int getRangedIndex() { return rangedIndex; }
	private int meleeIndex;
	public int getMeleeIndex() { return meleeIndex; }
	private boolean canCycle; // Used to prevent rapid cycling with prev/next weapon keys.
	public RangedWeapon getCurrentRanged() {
		List<RangedWeapon> weapons = getRangedWeapons();
		if(rangedIndex >= weapons.size()) return weapons.get(0);
		if(!weapons.isEmpty()) return weapons.get(rangedIndex);
		else return null;
	}
	public MeleeWeapon getCurrentMelee() {
		List<MeleeWeapon> weapons = getMeleeWeapons();
		if(meleeIndex >= weapons.size()) return weapons.get(0);
		if(!weapons.isEmpty()) return weapons.get(meleeIndex);
		else return null;
	}
	public Weapon getWeaponByType(WType type) {
		List<Weapon> weapons = inventory.getWeapons();

		for(Weapon w : weapons) {
			if(w.getType().equals(type)) return w;
		}

		return null;
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

		w.equip();
	}
	public void weaponRotate(int direction) {
		int wc = getRangedWeapons().size();
		if(wc > 0) {
			// have to use floorMod because apparently Java % is remainder only, not modulus... -_-
			int i = Math.floorMod((rangedIndex + direction), wc);
			String prev = getCurrentRanged().getName();
			setCurrentRanged(i);
			if(Globals.debug) {
				System.out.printf("Weapon changed from %s to %s!\n", prev, getCurrentRanged().getName());
				int ec = 0;
				List<RangedWeapon> rws = getRangedWeapons();
				for(RangedWeapon rw : rws) {
					if(rw.isEquipped()) ec++;
				}
				System.out.printf("Equipped Ranged Weapons: %d\n\n", ec);
			}
		}
	}

	private HeartMonitor monitor;
	public HeartMonitor getHeartMonitor() { return monitor; }
	public boolean isExhausted() { return !monitor.getState().equals(HeartMonitor.State.SLOW_SINUS); }

	private StatusHandler statusHandler;
	public StatusHandler getStatusHandler() { return statusHandler; }

	private Flashlight flashlight;
	public Flashlight getFlashlight() { return flashlight; }

	public Player() {
		position = new Pair<Float>(0.0f, 0.0f);
		velocity = new Pair<Float>(0.0f, 0.0f);

		body = AssetManager.getManager().getAnimation("GZS_Player_Body");
		feet = AssetManager.getManager().getAnimation("GZS_Player_Feet");

		attributes = new Attributes();
		resources = new Resources();
		talents = new Talents();
		statusHandler = new StatusHandler(this);
		monitor = new HeartMonitor();

		reset();
	}

	public static Player getPlayer() {
		if(instance == null) instance = new Player();
		return instance;
	}

	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
		Camera camera = Camera.getCamera();
		Level level = ((GameState) gs).getLevel();
		TMap map = level.getMap();

		if(flowField == null) flowField = new FlowField(map, map.worldToGridCoords(position));

		if(!isAlive()) {
			if(!respawning) {
				int lives = attributes.getInt("lives") - 1;
				if(lives >= 0) {
					attributes.set("lives", lives);

					statusHandler.destroyAll(cTime);

					respawning = true;
					respawnTime = (cTime + Player.RESPAWN_TIME);
				}

				monitor.setBPM(0);

				AchievementController.getInstance().postMetric(Metrics.compose(Metrics.PLAYER, Metrics.KILL));
			} else {
				long elapsed = (cTime - respawnTime);
				if(elapsed >= 0) {
					respawning = false;
					respawnTime = 0L;

					// Make the player invincible for a brief period.
					attributes.set("health", attributes.getDouble("maxHealth"));
					attributes.set("penalizedMaxHealth", attributes.getDouble("maxHealth"));
					attributes.set("penalizedOverflow", 0.0);
					statusHandler.addStatus(new InvulnerableEffect(Player.RESPAWN_TIME, cTime), cTime);

					// Reset the player's position.
					//position.x = (float)(Globals.WIDTH / 2);
					//position.y = (float)(Globals.HEIGHT / 2);

					// Reset the EKG.
					monitor.reset(cTime);

					// Reset the camera position.
					camera.focusOnPlayer(map);
				}
			}
		}

		// Regenerate health and stamina every second.
		boolean refresh = ((cTime - attributes.getLong("lastRegen")) >= 1_000L);
		if(refresh) {
			double healthRegenRate = attributes.getDouble("healthRegen");
			addHealth(healthRegenRate);
			attributes.set("lastRegen", cTime);
		}

		// Need to make sure to update the status effects first.
		statusHandler.update((GameState)gs, cTime, delta);

		Controls controls = Controls.getInstance();
		boolean canMove = !statusHandler.hasStatus(Status.PARALYSIS);
		MeleeWeapon cMeleeWeapon = getCurrentMelee();
		RangedWeapon cRangedWeapon = getCurrentRanged();

		if(cMeleeWeapon != null) canMove = (canMove && !cMeleeWeapon.isAttacking()); // Can't move if melee attacking.
		if(cRangedWeapon != null) canMove = (canMove && !cRangedWeapon.blockingMovement());

		if(canMove) {
			moving = false;
			velocity.x = 0.0f;
			velocity.y = 0.0f;

			float adjSpeed = ((getSpeed() + (attributes.getInt("speedUp") * (DEFAULT_SPEED * 0.10f))) * (float)attributes.getDouble("spdMult") * delta);
			if(controls.isPressed(Controls.Layout.MOVE_UP)) move(level, 0.0f, -adjSpeed);
			if(controls.isPressed(Controls.Layout.MOVE_LEFT)) move(level, -adjSpeed, 0.0f);
			if(controls.isPressed(Controls.Layout.MOVE_DOWN)) move(level, 0.0f, adjSpeed);
			if(controls.isPressed(Controls.Layout.MOVE_RIGHT)) move(level, adjSpeed, 0.0f);
		}

		if(controls.isReleased(Controls.Layout.FLASHLIGHT)) flashlight.toggle();
		if(controls.isPressed(Controls.Layout.RELOAD) && (cRangedWeapon != null)) {
			if(!cRangedWeapon.isReloading(cTime) && (cRangedWeapon.getClipAmmo() != cRangedWeapon.getClipCapacity())) cRangedWeapon.reload(cTime);
		}

		// If the interact button has been pressed, check for game object interactions.
		if(controls.isReleased(Controls.Layout.INTERACT)) level.interact((GameState)gs, cTime);

		bounds.setCenterX(position.x);
		bounds.setCenterY(position.y + 4.0f);

		if(canCycle) {
			if(controls.isPressed(Controls.Layout.PREV_WEAPON)) {
				weaponRotate(1);
				cRangedWeapon = getCurrentRanged();
				canCycle = false;
			} else if(controls.isPressed(Controls.Layout.NEXT_WEAPON)) {
				weaponRotate(-1);
				cRangedWeapon = getCurrentRanged();
				canCycle = false;
			}
		} else {
			if(!controls.isPressed(Controls.Layout.PREV_WEAPON) && !controls.isPressed(Controls.Layout.NEXT_WEAPON)) {
				canCycle = true;
			}
		}

		MouseInfo mouse = controls.getMouse();

		if(canMove) {
			if(mouse.isLeftDown() && (cRangedWeapon != null)) {boolean clipEmpty = (cRangedWeapon.getClipAmmo() == 0);
				boolean inventoryEmpty = (cRangedWeapon.getInventoryAmmo() == 0);
				boolean reloading = cRangedWeapon.isReloading(cTime);

				if(!reloading) {
					if(clipEmpty) {
						long elapsed = (cTime - attributes.getLong("lastClick"));
						if(elapsed >= 1_000L) {
							Sound click = AssetManager.getManager().getSound("out-of-ammo_click");
							click.play(1.0f, AssetManager.getManager().getSoundVolume());
							attributes.set("lastClick", cTime);
						}

						if(inventoryEmpty) {
							StatusMessages.getInstance().addMessage("Out of Ammo!", this, Player.ABOVE_1, cTime, 1_000L);
						} else {
							StatusMessages.getInstance().addMessage("Reload!", this, Player.ABOVE_1, cTime, 1_000L);
						}
					} else if(!cRangedWeapon.isChargedWeapon() && cRangedWeapon.canUse(cTime)) {
						ArmConfig ac = cRangedWeapon.getArmConfig();
						Pair<Float> origin = Calculate.rotateAboutPoint(position, new Pair<Float>((position.x + ac.getMuzzle().x), (position.y + ac.getMuzzle().y)), theta);
						cRangedWeapon.use(this, origin, theta, cTime);
					}
				} else {
					StatusMessages.getInstance().addMessage("Reloading...", this, Player.ABOVE_1, cTime, 1_000L);
				}
			} else if(mouse.isRightDown() && (cMeleeWeapon != null)) {
				if(cMeleeWeapon.canUse(cTime)) cMeleeWeapon.use(this, new Pair<Float>(position), theta, cTime);
			}
		}

		// Update all the player's active weapons.
		getRangedWeapons().stream().forEach(w -> w.update(gs, cTime, delta));
		getMeleeWeapons().stream().forEach(w -> w.update(gs, cTime, delta));

		monitor.update(cTime);

		// Calculate the player's rotation based on mouse position.
		if(canMove) {
			float x = (position.x - camera.getOffset().x);
			float y = (position.y - camera.getOffset().y);
			theta = Calculate.Hypotenuse(new Pair<Float>(x, y), mouse.getPosition()) + (float)(Math.PI / 2);
			flashlight.update(this, cTime);
		}

		if(moving) {
			body.update(cTime);
			feet.update(cTime);
		} else {
			body.restart(cTime);
			feet.restart(cTime);
		}
	}

	@Override
	public void render(GameState gs, Graphics g, long cTime) {
		// Render all the player's active weapons.
		getRangedWeapons().stream().forEach(w -> w.render(gs, g, cTime));
		getMeleeWeapons().stream().forEach(w -> w.render(gs, g, cTime));

		if(isAlive()) {
			AssetManager assets = AssetManager.getManager();

			Image head = assets.getImage("GZS_Player2_Head");

			float deg = (float)Math.toDegrees(theta);

			// Draw the feet.
			feet.render(g, position, (float)(theta - (Math.PI / 2)));

			// Draw the arm and weapon.
			g.rotate(position.x, position.y, deg);
			RangedWeapon rw = getCurrentRanged();
			if(rw != null) {
				ArmConfig ac = rw.getArmConfig();
				Image arm = ac.getArmImage();
				Image weapon = ac.getWeaponImage();
				if(arm != null) arm.draw((position.x - (arm.getWidth() / 2)), (position.y - (arm.getHeight() / 2)));
				if(weapon != null) weapon.draw((position.x + ac.getOffset().x), (position.y + ac.getOffset().y));
			}
			g.rotate(position.x, position.y, -deg);

			// Draw the body.
			body.render(g, position, (float)(theta - (Math.PI / 2)));

			// Draw the head.
			g.rotate(position.x, position.y, deg);
			head.draw((position.x - (head.getWidth() / 2)), (position.y - (head.getHeight() / 2)));
			g.rotate(position.x, position.y, -deg);

			if(Globals.SHOW_COLLIDERS) {
				g.setColor(Color.red);
				g.draw(bounds);
			}
		}

		statusHandler.render(g, cTime);
		flashlight.render(g, cTime);
	}

	/**
	 * Reset all dAttributes and iAttributes members.
	 */
	public void reset() {
		moving = false;

		position.x = (float)(Globals.WIDTH / 2);
		position.y = (float)(Globals.HEIGHT / 2);

		velocity.x = 0.0f;
		velocity.y = 0.0f;

		bounds = new Ellipse(position.x, (position.y + 4.0f), 16.0f, 16.0f);

		body.restart(0L);
		feet.restart(0L);

		speed = Player.DEFAULT_SPEED;
		theta = 0.0f;

		respawning = false;
		respawnTime = 0L;

		lastGrunt = 0L;

		rangedIndex = 0;
		meleeIndex = 0;
		canCycle = true;
		inventory = new Inventory(Player.INVENTORY_SIZE);

		Machete machete = new Machete();
		Beretta beretta = new Beretta();

		machete.equip();
		beretta.equip();

		inventory.addItem(machete);
		inventory.addItem(beretta);

		attributes.reset();
		resources.reset();
		statusHandler.clearAll();
		monitor.reset();

		Talents.reset();

		// Basic attributes.
		attributes.set("health", 100.0);
		attributes.set("maxHealth", 100.0);
		attributes.set("healthRegen", 0.0); // per second
		attributes.set("penalizedMaxHealth", 100.0);
		attributes.set("penalizedOverflow", 0.0);

		attributes.set("armor", 0.0);
		attributes.set("maxArmor", 100.0);

		attributes.set("lastRegen", 0L);

		attributes.set("lives", 3);
		attributes.set("maxLives", Player.MAX_LIVES);

		attributes.set("money", 0);

		// Experience related attributes.
		attributes.set("experience", 0);
		attributes.set("expToLevel", 100);
		attributes.set("level", 1);
		attributes.set("skillPoints", 0);

		// Upgrade level attributes.
		attributes.set("speedUp", 0);
		attributes.set("damageUp", 0);

		// Miscellaneous Modifiers
		attributes.set("meleeCritChance", 0.05f);
		attributes.set("rangeCritChance", 0.05f);
		attributes.set("critBonus", 0.0f);

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
		maxHealth = ((!monitor.getState().equals(HeartMonitor.State.SLOW_SINUS)) ? Math.min(maxHealth, attributes.getDouble("penalizedMaxHealth")) : maxHealth);
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
			double currentHealth = attributes.getDouble("health");
			double maxHealth = attributes.getDouble("maxHealth");

			boolean unbreakable = Talents.Fortification.UNBREAKABLE.active();
			boolean lastStand = Talents.Fortification.LAST_STAND.active() && (currentHealth <= (maxHealth * 0.25));
			double reduction = 0.0;
			if(lastStand) reduction += 0.5;
			if(unbreakable) {
				int ranks = Talents.Fortification.UNBREAKABLE.ranks();
				reduction += (ranks * 0.05);
			}

			if(reduction > 0.0) {
				amnt -= (amnt * reduction);
				if(amnt <= 0.0) return -1.0;
			}

			if(!piercing) amnt = damageArmor(amnt); // First, deal damage to player's armor.

			// Deal leftover damage to health.
			double adjusted = currentHealth - amnt;
			double newHealth = (adjusted < 0) ? 0 : adjusted;
			attributes.set("health", newHealth);

			if(amnt > 0.0) AchievementController.getInstance().postMetric(Metrics.compose(Metrics.PLAYER, Metrics.DAMAGE));

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

	public void addMoney(int amnt) {
		attributes.addTo("money", amnt);
		Scorekeeper.getInstance().addMoney(amnt);
	}

	public void addSkillPoint(GameState gs, long cTime, int amnt) {
		if(amnt > 0) {
			attributes.addTo("skillPoints", amnt);
			String status = String.format("Got %d skill %s!", amnt, ((amnt > 1) ? "points" : "point"));
			StatusMessages.getInstance().addMessage(status, this, Player.ABOVE_1, cTime, 2_000L);
			AssetManager.getManager().getSound("level-up").play(1.0f, AssetManager.getManager().getSoundVolume());
		}
	}

	public void addExperience(GameState gs, int amnt, long cTime) {
		addExperience(gs, amnt, cTime, true);
	}

	public void addExperience(GameState gs, int amnt, long cTime, boolean playSound) {
		int totalAmnt = (int)(amnt * attributes.getDouble("expMult"));
		int currentExp = attributes.getInt("experience");
		int adjusted = currentExp + totalAmnt;
		int expToLevel = attributes.getInt("expToLevel");
		int newLevel = attributes.getInt("level") + 1;

		boolean maxedOut = (attributes.getInt("level") >= 31);

		if(!maxedOut) attributes.set("experience", adjusted);

		if(adjusted >= expToLevel) {
			// Level up!
			int carryOver = adjusted % expToLevel;
			if(!maxedOut) {
				attributes.set("experience", carryOver);
				attributes.set("expToLevel", (expToLevel + (((newLevel / 2) * 100) + 50)));
				attributes.set("level", newLevel);
				attributes.addTo("skillPoints", (newLevel + 10) / 10);

				{ // Make the player say "Ding!" and have chance for enemies to say "Gratz!"
					String reminder = String.format("Press \'%s\' to Level Up!", Controls.Layout.TALENTS_SCREEN.getDisplay());
					StatusMessages.getInstance().addMessage("Ding!", this, Player.ABOVE_1, cTime, 2_000L);
					StatusMessages.getInstance().addMessage(reminder, this, Player.BELOW_1, cTime, 2_000L);
				}

				{ // Random chance for the enemies to say "Gratz!".
					float chance = Globals.rand.nextFloat();
					if(chance <= 0.02f) {
						Iterator<Enemy> it = EnemyController.getInstance().getAliveEnemies().iterator();
						while(it.hasNext()) {
							Enemy e = it.next();
							if(e.isAlive(cTime)) {
								StatusMessages.getInstance().addMessage("Gratz!", e, Player.ABOVE_1, cTime, 2_000L);
							}
						}
					}
				}
			}

			ShopController.getInstance().release(ShopState.getShop(), cTime); // Add new weapons to the shop!
			if(!maxedOut && playSound) AssetManager.getManager().getSound("level-up").play(1.0f, AssetManager.getManager().getSoundVolume());
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
				if(p.isAlive(cTime) && !enemy.dead() && p.checkCollision(enemy)) {
					if(p instanceof LaserNode) {
						LaserNode node = (LaserNode) p;
						node.damage(enemy.getDamage());
						enemy.blockMovement();
					} else {
						boolean collided = p.collide(gs, enemy, cTime);
						if(collided) {
							// If this is a special projectile, apply its status effect to the target.
							if(p instanceof StatusProjectile) {
								StatusProjectile sp = (StatusProjectile) p;
								sp.applyEffect(enemy, cTime);
							}

							float damagePercentage = (1.0f + (attributes.getInt("damageUp") * 0.10f));
							double totalDamage = (p.getDamage() * damagePercentage);
							if(totalDamage > 0.0) enemy.takeDamage(rw.getDamageType(), totalDamage, rw.getKnockback(), (float)(p.getTheta() - (Math.PI / 2)), rw.getWeaponMetric(), cTime, delta, true, p.isCritical());

							Scorekeeper.getInstance().addShotHit();
						}
					}

					return true;
				}
			}
		}

		for(MeleeWeapon mw : getMeleeWeapons()) {
			if(mw.isAttacking() && mw.hit(gs, enemy, cTime)) {
				double damage = mw.getDamageTotal(mw.isCurrentCritical());
				enemy.takeDamage(mw.getDamageType(), damage, mw.getKnockback(),
								 (theta - (float)(Math.PI / 2)), mw.getWeaponMetric(),
								 cTime, delta, true, mw.isCurrentCritical());
				mw.onHit(gs, enemy, cTime);

				// Check for Relentless talent effect if enemy was killed by this attack.
				if(enemy.dead() && Talents.Fortification.RELENTLESS.active()) {
					float roll = Globals.rand.nextFloat();
					if(roll <= 0.1f) monitor.addBPM(-40);
				}
			}
		}

		return false;
	}

	public boolean checkWeapons(GameState gs, DamageableObject obj, long cTime) {
		for(RangedWeapon rw : getRangedWeapons()) {
			Iterator<Projectile> it = rw.getProjectiles().iterator();
			while(it.hasNext()) {
				Projectile p = it.next();
				if(p.isAlive(cTime) && p.checkCollision(obj.getCollider())) {
					if(p instanceof LaserNode) continue;
					else return p.collide(gs, obj, cTime);
				}
			}
		}

		for(MeleeWeapon mw : getMeleeWeapons()) {
			if(mw.isAttacking() && mw.hit(gs, obj, cTime)) {
				return true;
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
	public String getName() { return "Player"; }

	@Override
	public String getTag() { return "player"; }

	@Override
	public String getDescription() { return "Player"; }

	@Override
	public int getLayer() { return Layers.PLAYER.val(); }
}
