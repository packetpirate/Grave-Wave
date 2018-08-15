package com.gzsr.objects.weapons.ranged;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;

import com.gzsr.AssetManager;
import com.gzsr.entities.Player;
import com.gzsr.gfx.Camera;
import com.gzsr.gfx.particles.Particle;
import com.gzsr.gfx.particles.Projectile;
import com.gzsr.gfx.particles.ProjectileType;
import com.gzsr.gfx.particles.emitters.BloodGenerator;
import com.gzsr.math.Dice;
import com.gzsr.misc.Pair;

public class Beretta extends RangedWeapon {
	private static final int AMMO_PRICE = 5;
	private static final long COOLDOWN = 500L;
	private static final int CLIP_SIZE = 12;
	private static final int START_CLIPS = 4;
	private static final int MAX_CLIPS = 10;
	private static final long RELOAD_TIME = 1_500L;
	private static final float KNOCKBACK = 1.0f;
	private static final String ICON_NAME = "GZS_Beretta";
	private static final String FIRE_SOUND = "beretta_shot_01";
	private static final String RELOAD_SOUND = "buy_ammo2";
	
	private static final Dice DAMAGE = new Dice(1, 12);
	private static final int DAMAGE_MOD = 8;
	
	public Beretta() {
		super(false);
		
		AssetManager assets = AssetManager.getManager();
		
		this.useSound = assets.getSound(Beretta.FIRE_SOUND);
		this.reloadSound = assets.getSound(Beretta.RELOAD_SOUND);
		
		this.shakeEffect = new Camera.ShakeEffect(100L, 20L, 5.0f);
		
		addMuzzleFlash();
	}

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
		double dmg = rollDamage(critical);
		
		Projectile projectile = new Projectile(particle, BloodGenerator.BURST, dmg, critical);
		projectiles.add(projectile);
		
		super.use(player, position, theta, cTime);
	}
	
	@Override
	public Pair<Integer> getDamage() { return Beretta.DAMAGE.getRange(Beretta.DAMAGE_MOD); }
	
	@Override
	public double rollDamage(boolean critical) { return Beretta.DAMAGE.roll(Beretta.DAMAGE_MOD, critical); }
	
	@Override
	public float getKnockback() { return Beretta.KNOCKBACK; }
	
	@Override
	public boolean isReloading(long cTime) {
		long elapsed = cTime - reloadStart;
		return ((elapsed < Beretta.RELOAD_TIME) && reloading);
	}
	
	@Override
	public long getReloadTime() { return Beretta.RELOAD_TIME; }
	
	@Override
	public double getReloadTime(long cTime) {
		long elapsed = cTime - reloadStart;
		return ((double)elapsed / (double)Beretta.RELOAD_TIME);
	}
	
	@Override
	public Image getInventoryIcon() { return AssetManager.getManager().getImage(Beretta.ICON_NAME); }
	
	@Override
	public int getClipSize() { return Beretta.CLIP_SIZE; }
	
	@Override
	public int getStartClips() { return Beretta.START_CLIPS; }
	
	@Override
	public int getMaxClips() { return Beretta.MAX_CLIPS; }

	@Override
	public long getCooldown() { return Beretta.COOLDOWN; }
	
	@Override
	public ProjectileType getProjectile() { return ProjectileType.HANDGUN; }

	@Override
	public int getPrice() { return 0; }
	
	@Override
	public int getAmmoPrice() { return Beretta.AMMO_PRICE; }

	@Override
	public int getLevelRequirement() { return 1; }
	
	@Override
	public String getName() {
		return "Beretta M9";
	}
	
	@Override
	public String getDescription() {
		return "A fairly popular pistol. Enough to put a bullet in their heads, at least...";
	}
}
