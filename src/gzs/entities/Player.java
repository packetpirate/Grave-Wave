package gzs.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import gzs.entities.enemies.Enemy;
import gzs.game.gfx.Flashlight;
import gzs.game.gfx.particles.Projectile;
import gzs.game.info.Globals;
import gzs.game.misc.Pair;
import gzs.game.objects.items.Item;
import gzs.game.objects.weapons.AssaultRifle;
import gzs.game.objects.weapons.Pistol;
import gzs.game.objects.weapons.Shotgun;
import gzs.game.objects.weapons.Weapon;
import gzs.game.status.Status;
import gzs.game.status.StatusEffect;
import gzs.game.utils.FileUtilities;
import gzs.math.Calculate;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Player implements Entity {
	private Pair<Double> position;
	public Pair<Double> getPosition() { return position; }
	public void move(double xOff, double yOff) {
		double tx = position.x + xOff;
		double ty = position.y + yOff;
		if((tx >= 0) && (tx < Globals.WIDTH) && 
		   (ty >= 0) && (ty < Globals.HEIGHT)) {
			position.x += xOff;
			position.y += yOff;
		}
	}
	
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
	
	private Image img;
	public Image getImage() { return img; }
	
	private Flashlight flashlight;
	public Flashlight getFlashlight() { return flashlight; }
	
	public Player() {
		position = new Pair<Double>((Globals.WIDTH / 2), (Globals.HEIGHT / 2));
		
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
		
		img = FileUtilities.LoadImage("GZS_Player.png");
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
		
		double speed = getDoubleAttribute("speed") * getDoubleAttribute("spdMult");
		if(Globals.inputs.contains("W")) move(0, -speed);
		if(Globals.inputs.contains("A")) move(-speed, 0);
		if(Globals.inputs.contains("S")) move(0, speed);
		if(Globals.inputs.contains("D")) move(speed, 0);
		if(Globals.inputs.contains("R") && 
		   !getCurrentWeapon().isReloading(cTime) &&
		   (getCurrentWeapon().getClipAmmo() != getCurrentWeapon().getClipSize())) {
			getCurrentWeapon().reload(cTime);
		}
		
		if(Globals.mouse.isMouseDown() && getCurrentWeapon().canFire(cTime)) {
			getCurrentWeapon().fire(new Pair<Double>(position.x, position.y), 
							   getDoubleAttribute("theta"), cTime);
		}
		
		weapons.stream().forEach(w -> w.update(cTime));
		
		// Calculate the player's rotation based on mouse position.
		setDoubleAttribute("theta", Calculate.Hypotenuse(position, Globals.mouse.getPosition()));
		flashlight.update(this, cTime);
	}

	@Override
	public void render(GraphicsContext gc, long cTime) {
		getCurrentWeapon().render(gc, cTime);
		if(img != null) {
			gc.save();
			gc.translate(position.x, position.y);
			gc.rotate(Math.toDegrees(getDoubleAttribute("theta") + (Math.PI / 2)));
			gc.translate(-position.x, -position.y);
			gc.drawImage(img, (position.x - (img.getWidth() / 2)), 
							  (position.y - (img.getHeight() / 2)));
			gc.restore();
		} else {
			gc.setStroke(Color.BLACK);
			gc.setFill(Color.RED);
			gc.fillOval((position.x - 20), (position.y - 20), 40, 40);
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
		
		dAttributes.put("speed", 3.0);
		
		// Multipliers
		dAttributes.put("expMult", 1.0);
		dAttributes.put("spdMult", 1.0);
		dAttributes.put("damMult", 1.0);
		
		// Game Parameters
		dAttributes.put("theta", 0.0);
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
		double distance = Calculate.Distance(position, item.getPosition());
		if(item.isTouching(distance)) item.apply(this, cTime);
	}
}
