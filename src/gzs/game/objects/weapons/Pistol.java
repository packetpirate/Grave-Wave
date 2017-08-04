package gzs.game.objects.weapons;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import gzs.game.gfx.particles.Particle;
import gzs.game.gfx.particles.Projectile;
import gzs.game.misc.Pair;
import gzs.game.utils.FileUtilities;
import gzs.game.utils.SoundManager;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.paint.Color;

public class Pistol implements Weapon {
	private static final long COOLDOWN = 500;
	private static final int CLIP_SIZE = 12;
	private static final int START_CLIPS = 4;
	private static final long RELOAD_TIME = 2000;
	private static final double DAMAGE = 40.0;
	private static final Image INV_ICON = FileUtilities.LoadImage("GZS_Popgun.png");
	private static final Media FIRE_SOUND = SoundManager.LoadSound("shoot4.wav");
	private static final Media RELOAD_SOUND = SoundManager.LoadSound("buy_ammo2.wav");
	
	private List<Projectile> projectiles;
	private int ammoInClip;
	private int ammoInInventory;
	private boolean active;
	private long lastFired;
	private boolean reloading;
	private long reloadStart;
	
	@Override
	public List<Projectile> getProjectiles() { return projectiles; }
	
	@Override
	public Image getInventoryIcon() { return Pistol.INV_ICON; }
	
	@Override
	public int getClipSize() { return Pistol.CLIP_SIZE; }
	
	@Override
	public int getClipAmmo() { return ammoInClip; }

	@Override
	public int getInventoryAmmo() { return ammoInInventory; }
	
	public Pistol() {
		this.projectiles = new ArrayList<Projectile>();
		this.ammoInClip = CLIP_SIZE;
		this.ammoInInventory = (START_CLIPS - 1) * CLIP_SIZE;
		this.active = false;
		this.lastFired = 0L;
		this.reloading = false;
		this.reloadStart = 0L;
	}
	
	@Override
	public void update(long cTime) {
		// Basically just checking to see if the reload time has elapsed.
		if(!isReloading(cTime)) reloading = false;
		
		// Update all projectiles.
		if(!projectiles.isEmpty()) {
			Iterator<Projectile> it = projectiles.iterator();
			while(it.hasNext()) {
				Particle p = it.next();
				if(p.isAlive(cTime)) {
					p.update(cTime);
				} else it.remove();
			}
		}
	}

	@Override
	public void render(GraphicsContext gc, long cTime) {
		// Render all projectiles.
		if(!projectiles.isEmpty()) {
			Iterator<Projectile> it = projectiles.iterator();
			while(it.hasNext()) {
				Projectile p = it.next();
				if(p.isAlive(cTime)) p.render(gc, cTime);
			}
		}
	}

	@Override
	public boolean canFire(long cTime) {
		if(reloading) return false;
		boolean clipNotEmpty = ammoInClip > 0;
		boolean ammoLeft = ammoInInventory > 0;
		boolean cool = (cTime - lastFired) >= Pistol.COOLDOWN;
		
		if(!clipNotEmpty) {
			reload(cTime);
			return false;
		}
		
		return ((clipNotEmpty || ammoLeft) && cool);
	}

	@Override
	public void fire(Pair<Double> position, double theta, long cTime) {
		Color color = getProjectile().getColor();
		double velocity = getProjectile().getVelocity();
		double width = getProjectile().getWidth();
		double height = getProjectile().getHeight();
		long lifespan = getProjectile().getLifespan();
		Particle particle = new Particle(color, position, velocity, theta,
										 0.0, new Pair<Double>(width, height), 
										 lifespan, cTime);
		Projectile projectile = new Projectile(particle, Pistol.DAMAGE);
		projectiles.add(projectile);
		ammoInClip--;
		lastFired = cTime;
		SoundManager.PlaySound(Pistol.FIRE_SOUND);
	}

	@Override
	public void reload(long cTime) {
		if(ammoInInventory > 0) {
			reloading = true;
			reloadStart = cTime;
			
			int newClip = (ammoInInventory < Pistol.CLIP_SIZE) ? ammoInInventory : Pistol.CLIP_SIZE;
			ammoInInventory -= (newClip - ammoInClip);
			ammoInClip = newClip;
			
			SoundManager.PlaySound(Pistol.RELOAD_SOUND);
		}
	}
	
	@Override
	public boolean isReloading(long cTime) {
		long elapsed = cTime - reloadStart;
		return ((elapsed < Pistol.RELOAD_TIME) && reloading);
	}
	
	@Override
	public double getReloadTime(long cTime) {
		long elapsed = cTime - reloadStart;
		return ((double)elapsed / (double)Pistol.RELOAD_TIME);
	}
	
	@Override
	public ProjectileType getProjectile() {
		return ProjectileType.HANDGUN;
	}

	@Override
	public boolean hasWeapon() {
		return active;
	}

	@Override
	public void activate() {
		active = true;
	}
}
