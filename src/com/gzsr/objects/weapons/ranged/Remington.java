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
import com.gzsr.objects.weapons.DamageType;

public class Remington extends RangedWeapon {
	private static final int PRICE = 400;
	private static final int AMMO_PRICE = 15;
	private static final long COOLDOWN = 1_000L;
	private static final int CLIP_SIZE = 6;
	private static final int START_CLIPS = 5;
	private static final int MAX_CLIPS = 10;
	private static final long RELOAD_TIME = 1_500L;
	private static final float KNOCKBACK = 10.0f;
	private static final String ICON_NAME = "GZS_Remington";
	private static final String FIRE_SOUND = "sniper_shot";
	private static final String RELOAD_SOUND = "buy_ammo2";
	
	private static final Dice DAMAGE = new Dice(5, 4);
	private static final int DAMAGE_MOD = 20;
	
	public Remington() {
		super(false);
		
		AssetManager assets = AssetManager.getManager();
		
		this.useSound = assets.getSound(Remington.FIRE_SOUND);
		this.reloadSound = assets.getSound(Remington.RELOAD_SOUND);
		
		this.shakeEffect = new Camera.ShakeEffect(200L, 20L, 10.0f);
		
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
		projectile.setPenetrations(1);
		projectiles.add(projectile);
		
		super.use(player, position, theta, cTime);
	}
	
	@Override
	public Pair<Integer> getDamage() { return Remington.DAMAGE.getRange(Remington.DAMAGE_MOD); }
	
	@Override
	public double rollDamage(boolean critical) { return Remington.DAMAGE.roll(Remington.DAMAGE_MOD, critical); }
	
	@Override
	public DamageType getDamageType() { return DamageType.PIERCING; }
	
	@Override
	public float getKnockback() { return Remington.KNOCKBACK; }
	
	@Override
	public boolean isReloading(long cTime) {
		long elapsed = cTime - reloadStart;
		return ((elapsed < Remington.RELOAD_TIME) && reloading);
	}
	
	@Override
	public long getReloadTime() { return Remington.RELOAD_TIME; }
	
	@Override
	public double getReloadTime(long cTime) {
		long elapsed = cTime - reloadStart;
		return ((double)elapsed / (double)Remington.RELOAD_TIME);
	}
	
	@Override
	public Image getInventoryIcon() { return AssetManager.getManager().getImage(Remington.ICON_NAME); }
	
	@Override
	public int getClipSize() { return Remington.CLIP_SIZE; }
	
	@Override
	public int getStartClips() { return Remington.START_CLIPS; }
	
	@Override
	public int getMaxClips() { return Remington.MAX_CLIPS; }

	@Override
	public long getCooldown() { return Remington.COOLDOWN; }
	
	@Override
	public ProjectileType getProjectile() { return ProjectileType.RIFLE; }

	@Override
	public int getPrice() { return Remington.PRICE; }
	
	@Override
	public int getAmmoPrice() { return Remington.AMMO_PRICE; }

	@Override
	public int getLevelRequirement() { return 8; }
	
	@Override
	public String getName() {
		return "Remington 783";
	}
	
	@Override
	public String getDescription() {
		return "A scoped Remington hunting rifle with an extended magazine. This thing looks like it packs a punch!";
	}
}
