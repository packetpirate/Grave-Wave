package gzs.game.objects.weapons;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import gzs.game.gfx.particles.Particle;
import gzs.game.gfx.particles.Projectile;
import gzs.game.info.Globals;
import gzs.game.misc.Pair;
import gzs.game.utils.FileUtilities;
import gzs.game.utils.SoundManager;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.paint.Color;

public class Shotgun implements Weapon {
	private static final long COOLDOWN = 1200;
	private static final int CLIP_SIZE = 8;
	private static final int START_CLIPS = 5;
	private static final int SHOT_COUNT = 5;
	private static final double MAX_SPREAD = Math.PI / 12;
	private static final long RELOAD_TIME = 2500;
	private static final double DAMAGE = 60.0;
	private static final Image INV_ICON = FileUtilities.LoadImage("GZS_Boomstick.png");
	private static final Media FIRE_SOUND = SoundManager.LoadSound("shotgun1.wav");
	private static final Media RELOAD_SOUND = SoundManager.LoadSound("buy_ammo2.wav");
	
	private List<Projectile> projectiles;
	private int ammoInClip;
	private int ammoInInventory;
	private long lastFired;
	private boolean reloading;
	private long reloadStart;
	
	public Shotgun() {
		this.projectiles = new ArrayList<Projectile>();
		this.ammoInClip = CLIP_SIZE;
		this.ammoInInventory = (START_CLIPS - 1) * CLIP_SIZE;
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
				Particle p = it.next();
				if(p.isAlive(cTime)) p.render(gc, cTime);
			}
		}
	}

	@Override
	public Image getInventoryIcon() {
		return Shotgun.INV_ICON;
	}

	@Override
	public int getClipSize() {
		return Shotgun.CLIP_SIZE;
	}

	@Override
	public int getClipAmmo() {
		return ammoInClip;
	}

	@Override
	public int getInventoryAmmo() {
		return ammoInInventory;
	}

	@Override
	public boolean canFire(long cTime) {
		if(reloading) return false;
		boolean clipNotEmpty = ammoInClip > 0;
		boolean ammoLeft = ammoInInventory > 0;
		boolean cool = (cTime - lastFired) >= Shotgun.COOLDOWN;
		
		if(!clipNotEmpty) {
			reload(cTime);
			return false;
		}
		
		return ((clipNotEmpty || ammoLeft) && cool);
	}

	@Override
	public void fire(Pair<Double> position, double theta, long cTime) {
		for(int i = 0; i < Shotgun.SHOT_COUNT; i++) {
			Color color = getProjectile().getColor();
			double velocity = getProjectile().getVelocity();
			double width = getProjectile().getWidth();
			double height = getProjectile().getHeight();
			double devTheta = (theta + (Globals.rand.nextDouble() * Shotgun.MAX_SPREAD * (Globals.rand.nextBoolean()?1:-1)));
			long lifespan = getProjectile().getLifespan();
			Particle particle = new Particle(color, position, velocity, devTheta,
											 0.0, new Pair<Double>(width, height), 
											 lifespan, cTime);
			Projectile projectile = new Projectile(particle, Shotgun.DAMAGE);
			projectiles.add(projectile);
		}
		
		ammoInClip--;
		lastFired = cTime;
		SoundManager.PlaySound(Shotgun.FIRE_SOUND);
	}

	@Override
	public void reload(long cTime) {
		if(ammoInInventory > 0) {
			reloading = true;
			reloadStart = cTime;
			
			int newClip = (ammoInInventory < Shotgun.CLIP_SIZE) ? ammoInInventory : Shotgun.CLIP_SIZE;
			ammoInInventory -= (newClip - ammoInClip);
			ammoInClip = newClip;
			
			SoundManager.PlaySound(Shotgun.RELOAD_SOUND);
		}
	}

	@Override
	public boolean isReloading(long cTime) {
		long elapsed = cTime - reloadStart;
		return ((elapsed < Shotgun.RELOAD_TIME) && reloading);
	}

	@Override
	public double getReloadTime(long cTime) {
		long elapsed = cTime - reloadStart;
		return ((double)elapsed / (double)Shotgun.RELOAD_TIME);
	}

	@Override
	public List<Projectile> getProjectiles() {
		return projectiles;
	}

	@Override
	public ProjectileType getProjectile() {
		return ProjectileType.SHOTGUN;
	}

}
