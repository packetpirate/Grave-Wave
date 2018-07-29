package com.gzsr.objects.weapons.ranged;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.state.BasicGameState;

import com.gzsr.AssetManager;
import com.gzsr.Controls;
import com.gzsr.entities.Player;
import com.gzsr.gfx.Camera;
import com.gzsr.gfx.particles.Particle;
import com.gzsr.gfx.particles.Projectile;
import com.gzsr.gfx.particles.ProjectileType;
import com.gzsr.gfx.particles.emitters.BloodGenerator;
import com.gzsr.math.Dice;
import com.gzsr.misc.Pair;
import com.gzsr.status.Status;

public class NailGun extends RangedWeapon {
	private static final int PRICE = 500;
	private static final int AMMO_PRICE = 50;
	private static final long COOLDOWN = 200L;
	private static final int CLIP_SIZE = 50;
	private static final int START_CLIPS = 2;
	private static final int MAX_CLIPS = 10;
	private static final long RELOAD_TIME = 1_500L;
	private static final int MIN_DAMAGE_COUNT = 1;
	private static final int MIN_DAMAGE_SIDES = 4;
	private static final int MIN_DAMAGE_MOD = 2;
	private static final float KNOCKBACK = 1.0f;
	private static final String ICON_NAME = "GZS_NailGun";
	private static final String PROJECTILE_IMAGE = "GZS_Nail";
	private static final String FIRE_SOUND = "nailgun";
	private static final String RELOAD_SOUND = "buy_ammo2";
	
	private boolean release;
	
	public NailGun() {
		super();
		
		AssetManager assets = AssetManager.getManager();
		
		this.damage = new Dice(NailGun.MIN_DAMAGE_COUNT, NailGun.MIN_DAMAGE_SIDES);
		
		this.useSound = assets.getSound(NailGun.FIRE_SOUND);
		this.reloadSound = assets.getSound(NailGun.RELOAD_SOUND);
		
		this.release = false;
	}
	
	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
		super.update(gs, cTime, delta);
		
		// If mouse released, release fire lock.
		if(!release && !Controls.getInstance().getMouse().isLeftDown()) release = true;
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
		Particle particle = new Particle(NailGun.PROJECTILE_IMAGE, color, position, velocity, theta,
										 0.0f, new Pair<Float>(width, height), 
										 lifespan, cTime);
		
		boolean critical = isCritical();
		double dmg = damage.roll(NailGun.MIN_DAMAGE_MOD, critical);
		
		Projectile projectile = new Projectile(particle, BloodGenerator.BURST, dmg, critical);
		projectiles.add(projectile);
		
		if(!player.hasStatus(Status.UNLIMITED_AMMO)) ammoInClip--;
		
		lastUsed = cTime;
		release = false;
		
		if(!Camera.getCamera().isShaking()) Camera.getCamera().shake(cTime, 100L, 20L, 5.0f);
		else Camera.getCamera().refreshShake(cTime);
		
		useSound.play(1.0f, AssetManager.getManager().getSoundVolume());
	}
	
	@Override
	public Pair<Integer> getDamage() { return damage.getRange(NailGun.MIN_DAMAGE_MOD); }
	
	@Override
	public float getKnockback() { return NailGun.KNOCKBACK; }
	
	@Override
	public boolean isReloading(long cTime) {
		long elapsed = cTime - reloadStart;
		return ((elapsed < NailGun.RELOAD_TIME) && reloading);
	}
	
	@Override
	public long getReloadTime() { return NailGun.RELOAD_TIME; }
	
	@Override
	public double getReloadTime(long cTime) {
		long elapsed = cTime - reloadStart;
		return ((double)elapsed / (double)NailGun.RELOAD_TIME);
	}
	
	@Override
	public Image getInventoryIcon() { return AssetManager.getManager().getImage(NailGun.ICON_NAME); }
	
	@Override
	public int getClipSize() { return NailGun.CLIP_SIZE; }
	
	@Override
	public int getStartClips() { return NailGun.START_CLIPS; }
	
	@Override
	public int getMaxClips() { return NailGun.MAX_CLIPS; }

	@Override
	public long getCooldown() { return NailGun.COOLDOWN; }
	
	@Override
	public ProjectileType getProjectile() { return ProjectileType.NAIL; }

	@Override
	public int getPrice() { return NailGun.PRICE; }
	
	@Override
	public int getAmmoPrice() { return NailGun.AMMO_PRICE; }

	@Override
	public String getName() {
		return "Nail Gun";
	}
	
	@Override
	public String getDescription() {
		return "Well it's not a gun, but it'll do... I guess.";
	}
}
