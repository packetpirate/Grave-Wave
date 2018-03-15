package com.gzsr.objects.weapons;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import com.gzsr.AssetManager;
import com.gzsr.Globals;
import com.gzsr.entities.Player;
import com.gzsr.gfx.particles.Particle;
import com.gzsr.gfx.particles.Projectile;
import com.gzsr.gfx.particles.ProjectileType;
import com.gzsr.misc.Pair;
import com.gzsr.states.GameState;
import com.gzsr.status.Status;

public class BowAndArrow extends Weapon {
	private static final int PRICE = 2_000;
	private static final int AMMO_PRICE = 400;
	private static final long COOLDOWN = 1_000L;
	private static final int CLIP_SIZE = 30;
	private static final int START_CLIPS = 1;
	private static final int MAX_CLIPS = 3;
	private static final double DAMAGE = 150.0;
	private static final float CHARGE_RATE = 0.00075f;
	private static final String ICON_NAME = "GZS_Bow";
	private static final String PROJECTILE_NAME = "GZS_Arrow";
	private static final String FIRE_SOUND = "shoot4"; // TODO: Change this to a more appropriate sound.
	private static final String RELOAD_SOUND = "buy_ammo2"; // TODO: Change this to a more appropriate sound.
	
	private boolean release;
	private boolean charging;
	private float charge;
	
	public BowAndArrow() {
		super();
		
		AssetManager assets = AssetManager.getManager();
		
		this.fireSound = assets.getSound(BowAndArrow.FIRE_SOUND);
		this.reloadSound = assets.getSound(BowAndArrow.RELOAD_SOUND);
		
		release = false;
		charging = false;
		charge = 0.0f;
	}
	
	@Override
	public void update(GameState gs, long cTime, int delta) {
		super.update(gs, cTime, delta);
		
		if(equipped) {
			if(charging) {
				// If we're charging, increase charge up to max of 1.0f.
				charge += BowAndArrow.CHARGE_RATE * delta;
				if(charge > 1.0f) charge = 1.0f;
			}
			
			if(Globals.mouse.isMouseDown()) {
				// If the mouse is down and we're not charging, start charging.
				if(!charging) charging = true;
			} else {
				if(charging) {
					// If the mouse is released / down and we're currently
					// charging, release and stop charging!
					release = true;
					charging = false;
				}
			}
		}
	}
	
	@Override
	public void render(Graphics g, long cTime) {
		super.render(g, cTime);
		
		if(equipped && charging) {
			// Render the charge bar.
			Player player = Globals.player;
			g.setColor(Color.white);
			g.drawRect((player.getPosition().x - 24.0f), (player.getPosition().y - 44.0f), 48.0f, 15.0f);
			
			if(charge < 0.3f) g.setColor(Color.red);
			else if(charge < 0.75f) g.setColor(Color.yellow);
			else g.setColor(Color.green);
			
			g.fillRect((player.getPosition().x - 23.0f), (player.getPosition().y - 43.0f), (charge * 46.0f), 13.0f);
		}
	}
	
	@Override
	public void fire(Player player, Pair<Float> position, float theta, long cTime) {
		Color color = getProjectile().getColor();
		float velocity = getProjectile().getVelocity();
		float width = getProjectile().getWidth();
		float height = getProjectile().getHeight();
		long lifespan = getProjectile().getLifespan();
		Particle particle = new Particle(BowAndArrow.PROJECTILE_NAME, color, position, velocity, theta,
										 0.0f, new Pair<Float>(width, height), 
										 lifespan, cTime);
		double damage = (BowAndArrow.DAMAGE + (BowAndArrow.DAMAGE * (player.getIntAttribute("damageUp") * 0.10))) * charge;
		Projectile projectile = new Projectile(particle, damage);
		
		projectiles.add(projectile);
		if(!player.hasStatus(Status.UNLIMITED_AMMO)) ammoInClip--;
		
		release = false;
		charge = 0.0f;
		lastFired = cTime;
		fireSound.play();
	}
	
	@Override
	public boolean canFire(long cTime) {
		return super.canFire(cTime) && release;
	}
	
	@Override
	public void weaponChanged() {
		super.weaponChanged();
		// Prevents arrow from firing after we've switching weapons.
		charging = false;
		charge = 0.0f;
	}
	
	@Override
	public boolean isChargedWeapon() {
		return true;
	}
	
	@Override
	public double getDamage() {
		return BowAndArrow.DAMAGE;
	}

	@Override
	public boolean isReloading(long cTime) {
		return false;
	}
	
	@Override
	public long getReloadTime() {
		return 0L;
	}

	@Override
	public double getReloadTime(long cTime) {
		return 0.0;
	}
	
	@Override
	public String getName() {
		return "Bow & Arrow";
	}
	
	@Override
	public String getDescription() {
		return "A primitive weapon that takes a little bit of time to fire, but is well worth the wait.";
	}

	@Override
	public Image getInventoryIcon() {
		return AssetManager.getManager().getImage(BowAndArrow.ICON_NAME);
	}

	@Override
	public int getClipSize() {
		return BowAndArrow.CLIP_SIZE;
	}

	@Override
	protected int getStartClips() {
		return BowAndArrow.START_CLIPS;
	}
	
	@Override
	protected int getMaxClips() { return BowAndArrow.MAX_CLIPS; }

	@Override
	public long getCooldown() {
		return BowAndArrow.COOLDOWN;
	}

	@Override
	public ProjectileType getProjectile() {
		return ProjectileType.ARROW;
	}

	@Override
	public int getPrice() {
		return BowAndArrow.PRICE;
	}

	@Override
	public int getAmmoPrice() {
		return BowAndArrow.AMMO_PRICE;
	}
}
