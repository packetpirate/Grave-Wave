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

public class Flamethrower extends Weapon {
	private static final long COOLDOWN = 50L;
	private static final int CLIP_SIZE = 100;
	private static final int START_CLIPS = 3;
	private static final long RELOAD_TIME = 3000L;
	private static final int EMBER_COUNT = 10;
	private static final float EMBER_SPREAD = (float)(Math.PI / 18);
	private static final double EMBER_DAMAGE = 1.0;
	private static final String ICON_NAME = "GZS_Flammenwerfer";
	private static final String FIRE_SOUND = "flamethrower2";
	private static final String RELOAD_SOUND = "buy_ammo2";
	
	public Flamethrower() {
		super();
		
		AssetManager assets = AssetManager.getManager();
		
		this.fireSound = assets.getSound(Flamethrower.FIRE_SOUND);
		this.reloadSound = assets.getSound(Flamethrower.RELOAD_SOUND);
	}
	
	@Override
	public void update(GameState gs, long cTime) {
		super.update(gs, cTime);
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
			Particle particle = new Particle("GZS_FireParticle2", color, position, velocity, devTheta,
											 0.0f, new Pair<Float>(width, height), 
											 lifespan, cTime);
			double damage = Flamethrower.EMBER_DAMAGE + (Flamethrower.EMBER_DAMAGE * (player.getIntAttribute("damageUp") * 0.10));
			Projectile projectile = new Projectile(particle, damage);
			projectiles.add(projectile);
		}
		
		if(!player.hasStatus(Status.UNLIMITED_AMMO)) ammoInClip--;
		lastFired = cTime;
		//fireSound.play();
	}
	
	@Override
	public boolean isReloading(long cTime) {
		long elapsed = cTime - reloadStart;
		return ((elapsed < Flamethrower.RELOAD_TIME) && reloading);
	}

	@Override
	public double getReloadTime(long cTime) {
		long elapsed = cTime - reloadStart;
		return ((double)elapsed / (double)Flamethrower.RELOAD_TIME);
	}
	
	@Override
	public Image getInventoryIcon() { return AssetManager.getManager().getImage(Flamethrower.ICON_NAME); }

	@Override
	public int getClipSize() { return Flamethrower.CLIP_SIZE; }

	@Override
	protected int getStartClips() { return Flamethrower.START_CLIPS; }

	@Override
	public long getCooldown() { return Flamethrower.COOLDOWN; }

	@Override
	public ProjectileType getProjectile() { return ProjectileType.FLAMETHROWER; }
}
