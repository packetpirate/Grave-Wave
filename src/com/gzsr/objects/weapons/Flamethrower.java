package com.gzsr.objects.weapons;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import com.gzsr.AssetManager;
import com.gzsr.Globals;
import com.gzsr.entities.Player;
import com.gzsr.gfx.particles.Particle;
import com.gzsr.gfx.particles.ProjectileType;
import com.gzsr.gfx.particles.StatusProjectile;
import com.gzsr.misc.Pair;
import com.gzsr.states.GameState;
import com.gzsr.status.BurningEffect;
import com.gzsr.status.Status;

public class Flamethrower extends Weapon {
	private static final int PRICE = 3_000;
	private static final int AMMO_PRICE = 1_000; 
	private static final long COOLDOWN = 25L;
	private static final int CLIP_SIZE = 100;
	private static final int START_CLIPS = 2;
	private static final int MAX_CLIPS = 5;
	private static final long RELOAD_TIME = 3_000L;
	private static final int EMBER_COUNT = 5;
	private static final float EMBER_SPREAD = (float)(Math.PI / 18);
	private static final double EMBER_DAMAGE = 3.0;
	private static final String ICON_NAME = "GZS_Flammenwerfer";
	private static final String PROJECTILE_NAME = "GZS_FireParticle";
	private static final String FIRE_SOUND = "flamethrower2";
	private static final String RELOAD_SOUND = "buy_ammo2";
	
	public Flamethrower() {
		super();
		
		AssetManager assets = AssetManager.getManager();
		
		this.fireSound = assets.getSound(Flamethrower.FIRE_SOUND);
		this.reloadSound = assets.getSound(Flamethrower.RELOAD_SOUND);
	}
	
	@Override
	public void update(GameState gs, long cTime, int delta) {
		super.update(gs, cTime, delta);
	}
	
	@Override
	public void render(Graphics g, long cTime) {
		super.render(g, cTime);
	}
	
	@Override
	public void fire(Player player, Pair<Float> position, float theta, long cTime) {
		for(int i = 0; i < Flamethrower.EMBER_COUNT; i++) {
			Color color = getProjectile().getColor();
			float velocity = getProjectile().getVelocity();
			float width = getProjectile().getWidth();
			float height = getProjectile().getHeight();
			float devTheta = (theta + (Globals.rand.nextFloat() * Flamethrower.EMBER_SPREAD * (Globals.rand.nextBoolean()?1:-1)));
			long lifespan = getProjectile().getLifespan();
			Particle particle = new Particle(Flamethrower.PROJECTILE_NAME, color, position, velocity, devTheta,
											 0.0f, new Pair<Float>(width, height), 
											 lifespan, cTime);
			double damage = Flamethrower.EMBER_DAMAGE + (Flamethrower.EMBER_DAMAGE * (player.getIntAttribute("damageUp") * 0.10));
			StatusProjectile projectile = new StatusProjectile(particle, damage, new BurningEffect(cTime));
			projectiles.add(projectile);
		}
		
		if(!player.hasStatus(Status.UNLIMITED_AMMO)) ammoInClip--;
		lastFired = cTime;
		//fireSound.play(); TODO: Make a better sound for this.
	}
	
	@Override
	public double getDamage() {
		return (Flamethrower.EMBER_DAMAGE * Flamethrower.EMBER_COUNT);
	}
	
	@Override
	public float getKnockback() {
		return 0.0f;
	}
	
	@Override
	public boolean isReloading(long cTime) {
		long elapsed = cTime - reloadStart;
		return ((elapsed < Flamethrower.RELOAD_TIME) && reloading);
	}

	@Override
	public long getReloadTime() {
		return Flamethrower.RELOAD_TIME;
	}
	
	@Override
	public double getReloadTime(long cTime) {
		long elapsed = cTime - reloadStart;
		return ((double)elapsed / (double)Flamethrower.RELOAD_TIME);
	}
	
	@Override
	public String getName() {
		return "Flamethrower";
	}
	
	@Override
	public Image getInventoryIcon() { return AssetManager.getManager().getImage(Flamethrower.ICON_NAME); }

	@Override
	public int getClipSize() { return Flamethrower.CLIP_SIZE; }

	@Override
	protected int getStartClips() { return Flamethrower.START_CLIPS; }
	
	@Override
	protected int getMaxClips() { return Flamethrower.MAX_CLIPS; }

	@Override
	public long getCooldown() { return Flamethrower.COOLDOWN; }

	@Override
	public ProjectileType getProjectile() { return ProjectileType.FLAMETHROWER; }

	@Override
	public int getPrice() {
		return Flamethrower.PRICE;
	}
	
	@Override
	public int getAmmoPrice() {
		return Flamethrower.AMMO_PRICE;
	}
	
	@Override
	public String getDescription() {
		return "A long-barreled, gas-powered weapon that emits a stream of hellfire to roast your enemies.";
	}
}
