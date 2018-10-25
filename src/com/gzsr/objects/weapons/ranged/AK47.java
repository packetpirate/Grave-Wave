package com.gzsr.objects.weapons.ranged;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.state.BasicGameState;

import com.gzsr.AssetManager;
import com.gzsr.Globals;
import com.gzsr.achievements.Metrics;
import com.gzsr.entities.Player;
import com.gzsr.gfx.Animation;
import com.gzsr.gfx.Camera;
import com.gzsr.gfx.particles.Particle;
import com.gzsr.gfx.particles.Projectile;
import com.gzsr.gfx.particles.ProjectileType;
import com.gzsr.gfx.particles.emitters.BloodGenerator;
import com.gzsr.math.Dice;
import com.gzsr.misc.Pair;

public class AK47 extends RangedWeapon {
	private static final int PRICE = 2_500;
	private static final int AMMO_PRICE = 80;
	private static final long COOLDOWN = 100L;
	private static final int CLIP_SIZE = 30;
	private static final int START_CLIPS = 4;
	private static final int MAX_CLIPS = 10;
	private static final long RELOAD_TIME = 2_000L;
	private static final float KNOCKBACK = 1.0f;
	private static final float MAX_DEVIATION = (float)(Math.PI / 18.0);
	private static final String ICON_NAME = "GZS_RTPS";
	private static final String FIRE_SOUND = "m4a1_shot_01";
	private static final String RELOAD_SOUND = "buy_ammo2";
	
	private static final Dice DAMAGE = new Dice(1, 10);
	private static final int DAMAGE_MOD = 4;
	
	private Animation muzzleFlash;
	
	public AK47() {
		super();
		
		AssetManager assets = AssetManager.getManager();
		
		this.muzzleFlash = assets.getAnimation("GZS_MuzzleFlash");
		this.useSound = assets.getSound(AK47.FIRE_SOUND);
		this.reloadSound = assets.getSound(AK47.RELOAD_SOUND);
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
		Color color = getProjectile().getColor();
		float velocity = getProjectile().getVelocity();
		float width = getProjectile().getWidth();
		float height = getProjectile().getHeight();
		long lifespan = getProjectile().getLifespan();
		
		boolean critical = isCritical();
		double dmg = rollDamage(critical);
		
		float deviation = Globals.rand.nextFloat() * (MAX_DEVIATION / 2) * (Globals.rand.nextBoolean() ? 1 : -1);
		
		Particle particle = new Particle(color, position, velocity, (theta + deviation),
										 0.0f, new Pair<Float>(width, height), 
										 lifespan, cTime);
		Projectile projectile = new Projectile(particle, BloodGenerator.BURST, dmg, critical);
		projectiles.add(projectile);
		
		if(!hasUnlimitedAmmo()) ammoInClip--;
		lastUsed = cTime;

		if(!Camera.getCamera().isShaking()) Camera.getCamera().shake(cTime, 200L, 50L, 5.0f);
		else Camera.getCamera().refreshShake(cTime);
		
		muzzleFlash.restart(cTime);
		useSound.play(1.0f, AssetManager.getManager().getSoundVolume());
	}
	
	@Override
	public Pair<Integer> getDamage() { return AK47.DAMAGE.getRange(AK47.DAMAGE_MOD); }
	
	@Override
	public double rollDamage(boolean critical) { return AK47.DAMAGE.roll(AK47.DAMAGE_MOD, critical); }
	
	@Override
	public float getKnockback() { return AK47.KNOCKBACK; }

	@Override
	public long getReloadTime() { return AK47.RELOAD_TIME; }
	
	@Override
	public Image getInventoryIcon() { return AssetManager.getManager().getImage(AK47.ICON_NAME); }

	@Override
	public int getClipSize() { return AK47.CLIP_SIZE; }

	@Override
	public int getStartClips() { return AK47.START_CLIPS; }
	
	@Override
	public int getMaxClips() { return AK47.MAX_CLIPS; }

	@Override
	public long getCooldown() { return AK47.COOLDOWN; }
	
	@Override
	public ProjectileType getProjectile() { return ProjectileType.ASSAULT; }

	@Override
	public int getPrice() { return AK47.PRICE; }
	
	@Override
	public int getAmmoPrice() { return AK47.AMMO_PRICE; }

	@Override
	public int getLevelRequirement() { return 8; }
	
	@Override
	public long getWeaponMetric() { return Metrics.AK47; }
	
	@Override
	public String getName() {
		return "AK47";
	}
	
	@Override
	public String getDescription() {
		return "One of the world's most popular assault rifles... or at least it used to be, I guess.";
	}
}
