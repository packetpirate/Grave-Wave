package com.gzsr.objects.weapons.ranged;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.state.BasicGameState;

import com.gzsr.AssetManager;
import com.gzsr.Controls;
import com.gzsr.entities.Player;
import com.gzsr.gfx.Animation;
import com.gzsr.gfx.Camera;
import com.gzsr.gfx.particles.Particle;
import com.gzsr.gfx.particles.Projectile;
import com.gzsr.gfx.particles.ProjectileType;
import com.gzsr.gfx.particles.emitters.BloodGenerator;
import com.gzsr.math.Dice;
import com.gzsr.misc.Pair;
import com.gzsr.status.Status;

public class SAWRevolver extends RangedWeapon {
	private static final int PRICE = 2_500;
	private static final int AMMO_PRICE = 400;
	private static final long COOLDOWN = 750L;
	private static final int CLIP_SIZE = 6;
	private static final int START_CLIPS = 5;
	private static final int MAX_CLIPS = 10;
	private static final long RELOAD_TIME = 2_000L;
	private static final int MIN_DAMAGE_COUNT = 3;
	private static final int MIN_DAMAGE_SIDES = 8;
	private static final int MIN_DAMAGE_MOD = 12;
	private static final float KNOCKBACK = 7.5f;
	private static final String ICON_NAME = "GZS_SmithAndWesson";
	private static final String FIRE_SOUND = "revolver_shot_01";
	private static final String RELOAD_SOUND = "buy_ammo2";
	
	private Animation muzzleFlash;
	private boolean release;
	
	public SAWRevolver() {
		super();
		
		AssetManager assets = AssetManager.getManager();
		
		this.damage = new Dice(SAWRevolver.MIN_DAMAGE_COUNT, SAWRevolver.MIN_DAMAGE_SIDES);
		
		this.useSound = assets.getSound(SAWRevolver.FIRE_SOUND);
		this.reloadSound = assets.getSound(SAWRevolver.RELOAD_SOUND);
		
		this.muzzleFlash = assets.getAnimation("GZS_MuzzleFlash");
		this.release = false;
	}
	
	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
		super.update(gs, cTime, delta);
		
		// If mouse released, release fire lock.
		if(!release && !Controls.getInstance().getMouse().isLeftDown()) release = true;
		
		// Update muzzle flash animation.
		if(muzzleFlash.isActive(cTime)) muzzleFlash.update(cTime);
	}

	@Override
	public void render(Graphics g, long cTime) {
		super.render(g, cTime);
		
		// Render muzzle flash.
		Pair<Float> mp = new Pair<Float>((Player.getPlayer().getPosition().x + 5.0f), (Player.getPlayer().getPosition().y - 28.0f));
		if(muzzleFlash.isActive(cTime)) muzzleFlash.render(g, mp, Player.getPlayer().getPosition(), (Player.getPlayer().getRotation() - (float)(Math.PI / 2)));
	}

	@Override
	public boolean canUse(long cTime) { return (super.canUse(cTime) && release); }

	@Override
	public void use(Player player, Pair<Float> position, float theta, long cTime) {
		Color color = getProjectile().getColor();
		float velocity = getProjectile().getVelocity();
		float width = getProjectile().getWidth();
		float height = getProjectile().getHeight();
		long lifespan = getProjectile().getLifespan();
		Particle particle = new Particle(color, position, velocity, theta,
										 0.0f, new Pair<Float>(width, height), 
										 lifespan, cTime);
		
		boolean critical = isCritical();
		double dmg = damage.roll(SAWRevolver.MIN_DAMAGE_MOD, critical);
		
		Projectile projectile = new Projectile(particle, BloodGenerator.BURST, dmg, critical);
		projectile.setPenetrations(1);
		projectiles.add(projectile);
		
		if(!player.hasStatus(Status.UNLIMITED_AMMO)) ammoInClip--;
		
		lastUsed = cTime;
		release = false;
		
		if(!Camera.getCamera().isShaking()) Camera.getCamera().shake(cTime, 100L, 20L, 5.0f);
		else Camera.getCamera().refreshShake(cTime);
		
		muzzleFlash.restart(cTime);
		useSound.play(1.0f, AssetManager.getManager().getSoundVolume());
	}
	
	@Override
	public Pair<Integer> getDamage() { return damage.getRange(SAWRevolver.MIN_DAMAGE_MOD); }
	
	@Override
	public float getKnockback() { return SAWRevolver.KNOCKBACK; }
	
	@Override
	public boolean isReloading(long cTime) {
		long elapsed = cTime - reloadStart;
		return ((elapsed < SAWRevolver.RELOAD_TIME) && reloading);
	}
	
	@Override
	public long getReloadTime() { return SAWRevolver.RELOAD_TIME; }
	
	@Override
	public double getReloadTime(long cTime) {
		long elapsed = cTime - reloadStart;
		return ((double)elapsed / (double)SAWRevolver.RELOAD_TIME);
	}
	
	@Override
	public Image getInventoryIcon() { return AssetManager.getManager().getImage(SAWRevolver.ICON_NAME); }
	
	@Override
	public int getClipSize() { return SAWRevolver.CLIP_SIZE; }
	
	@Override
	public int getStartClips() { return SAWRevolver.START_CLIPS; }
	
	@Override
	public int getMaxClips() { return SAWRevolver.MAX_CLIPS; }

	@Override
	public long getCooldown() { return SAWRevolver.COOLDOWN; }
	
	@Override
	public ProjectileType getProjectile() { return ProjectileType.HANDGUN; }

	@Override
	public int getPrice() { return SAWRevolver.PRICE; }
	
	@Override
	public int getAmmoPrice() { return SAWRevolver.AMMO_PRICE; }

	@Override
	public String getName() {
		return "Smith & Wesson Model 500";
	}
	
	@Override
	public String getDescription() {
		return "One of the most popular revolvers in the world... and one of the most powerful.";
	}
}
