package com.gzsr.objects.weapons.ranged;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.state.BasicGameState;

import com.gzsr.AssetManager;
import com.gzsr.entities.Player;
import com.gzsr.gfx.Animation;
import com.gzsr.gfx.particles.Particle;
import com.gzsr.gfx.particles.ProjectileType;
import com.gzsr.math.Dice;
import com.gzsr.misc.Pair;
import com.gzsr.objects.weapons.Explosion;
import com.gzsr.status.Status;

public class Stinger extends RangedWeapon {
	private static final int PRICE = 38_000;
	private static final int AMMO_PRICE = 5_000;
	private static final long COOLDOWN = 0L;
	private static final int CLIP_SIZE = 1;
	private static final int START_CLIPS = 2;
	private static final int MAX_CLIPS = 4;
	private static final long RELOAD_TIME = 3_000L;
	private static final int MIN_DAMAGE_COUNT = 25;
	private static final int MIN_DAMAGE_SIDES = 10;
	private static final int MIN_DAMAGE_MOD = 250;
	private static final float KNOCKBACK = 20.0f;
	private static final float EXP_RADIUS = 128.0f;
	private static final String ICON_NAME = "GZS_Stinger";
	private static final String ANIMATION_NAME = "GZS_Stinger_Missile";
	private static final String EXP_NAME = "GZS_Explosion";
	private static final String FIRE_SOUND = "missile";
	private static final String RELOAD_SOUND = "buy_ammo2";
	
	private Animation muzzleFlash;
	
	public Stinger() {
		super();
		
		AssetManager assets = AssetManager.getManager();
		
		this.damage = new Dice(Stinger.MIN_DAMAGE_COUNT, Stinger.MIN_DAMAGE_SIDES);
		
		this.muzzleFlash = assets.getAnimation("GZS_MuzzleFlash");
		this.useSound = assets.getSound(Stinger.FIRE_SOUND);
		this.reloadSound = assets.getSound(Stinger.RELOAD_SOUND);
	}
	
	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
		super.update(gs, cTime, delta);
		
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
	public void use(Player player, Pair<Float> position, float theta, long cTime) {
		Animation animation = AssetManager.getManager().getAnimation(Stinger.ANIMATION_NAME);
		
		float velocity = getProjectile().getVelocity();
		float width = getProjectile().getWidth();
		float height = getProjectile().getHeight();
		long lifespan = getProjectile().getLifespan();
		Particle particle = new Particle(animation, position, velocity, theta,
										 0.0f, new Pair<Float>(width, height), 
										 lifespan, cTime);
		
		boolean critical = isCritical();
		double dmg = damage.roll(Stinger.MIN_DAMAGE_MOD, critical);
		dmg += (dmg * (player.getAttributes().getInt("damageUp") * 0.10));
		if(isCritical()) dmg *= player.getAttributes().getDouble("critMult");

		Explosion exp = new Explosion(Explosion.Type.NORMAL, Stinger.EXP_NAME, new Pair<Float>(0.0f, 0.0f), dmg, Stinger.KNOCKBACK, Stinger.EXP_RADIUS);
		Missile missile = new Missile(particle, exp);
		projectiles.add(missile);
		
		if(!player.hasStatus(Status.UNLIMITED_AMMO)) ammoInClip--;
		lastUsed = cTime;
		
		muzzleFlash.restart(cTime);
		useSound.play(1.0f, AssetManager.getManager().getSoundVolume());
	}
	
	@Override
	public Pair<Integer> getDamage() { return damage.getRange(Stinger.MIN_DAMAGE_MOD); }
	
	@Override
	public float getKnockback() { return Stinger.KNOCKBACK; }

	@Override
	public boolean isReloading(long cTime) {
		long elapsed = cTime - reloadStart;
		return ((elapsed < Stinger.RELOAD_TIME) && reloading);
	}

	@Override
	public long getReloadTime() { return Stinger.RELOAD_TIME; }
	
	@Override
	public double getReloadTime(long cTime) {
		long elapsed = cTime - reloadStart;
		return ((double)elapsed / (double)Stinger.RELOAD_TIME);
	}

	@Override
	public Image getInventoryIcon() { return AssetManager.getManager().getImage(Stinger.ICON_NAME); }
	
	@Override
	public int getClipSize() { return Stinger.CLIP_SIZE; }

	@Override
	public int getStartClips() { return Stinger.START_CLIPS; }
	
	@Override
	public int getMaxClips() { return Stinger.MAX_CLIPS; }

	@Override
	public long getCooldown() { return Stinger.COOLDOWN; }

	@Override
	public ProjectileType getProjectile() { return ProjectileType.MISSILE; }

	@Override
	public int getPrice() { return Stinger.PRICE; }
	
	@Override
	public int getAmmoPrice() { return Stinger.AMMO_PRICE; }

	@Override
	public int getLevelRequirement() { return 15; }
	
	@Override
	public String getName() {
		return "Stinger FIM-92";
	}
	
	@Override
	public String getDescription() {
		return "Who just leaves something like this lying around? This just got a lot easier...";
	}
}
