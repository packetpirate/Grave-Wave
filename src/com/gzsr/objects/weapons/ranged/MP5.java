package com.gzsr.objects.weapons.ranged;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.state.BasicGameState;

import com.gzsr.AssetManager;
import com.gzsr.Globals;
import com.gzsr.entities.Player;
import com.gzsr.gfx.Animation;
import com.gzsr.gfx.Camera;
import com.gzsr.gfx.particles.Particle;
import com.gzsr.gfx.particles.Projectile;
import com.gzsr.gfx.particles.ProjectileType;
import com.gzsr.gfx.particles.emitters.BloodGenerator;
import com.gzsr.math.Dice;
import com.gzsr.misc.Pair;

public class MP5 extends RangedWeapon {
	private static final int PRICE = 1_000;
	private static final int AMMO_PRICE = 250;
	private static final long COOLDOWN = 75L;
	private static final int CLIP_SIZE = 40;
	private static final int START_CLIPS = 4;
	private static final int MAX_CLIPS = 10;
	private static final long RELOAD_TIME = 1_500L;
	private static final int MIN_DAMAGE_COUNT = 1;
	private static final int MIN_DAMAGE_SIDES = 8;
	private static final int MIN_DAMAGE_MOD = 2;
	private static final float KNOCKBACK = 1.0f;
	private static final float MAX_DEVIATION = (float)(Math.PI / 18.0);
	private static final String ICON_NAME = "GZS_Mp5";
	private static final String FIRE_SOUND = "m4a1_shot_01";
	private static final String RELOAD_SOUND = "buy_ammo2";
	
	private Animation muzzleFlash;
	
	public MP5() {
		super();
		
		AssetManager assets = AssetManager.getManager();
		
		this.damage = new Dice(MP5.MIN_DAMAGE_COUNT, MP5.MIN_DAMAGE_SIDES);
		
		this.muzzleFlash = assets.getAnimation("GZS_MuzzleFlash");
		this.useSound = assets.getSound(MP5.FIRE_SOUND);
		this.reloadSound = assets.getSound(MP5.RELOAD_SOUND);
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
		double dmg = damage.roll(MP5.MIN_DAMAGE_MOD, critical);
		
		float deviation = Globals.rand.nextFloat() * (MAX_DEVIATION / 2) * (Globals.rand.nextBoolean() ? 1 : -1);
		
		Particle particle = new Particle(color, position, velocity, (theta + deviation),
										 0.0f, new Pair<Float>(width, height), 
										 lifespan, cTime);
		
		Projectile projectile = new Projectile(particle, BloodGenerator.BURST, dmg, critical);
		projectiles.add(projectile);
		
		if(!hasUnlimitedAmmo()) ammoInClip--;
		lastUsed = cTime;

		if(!Camera.getCamera().isShaking()) Camera.getCamera().shake(cTime, 150L, 50L, 5.0f);
		else Camera.getCamera().refreshShake(cTime);
		
		muzzleFlash.restart(cTime);
		useSound.play(1.0f, AssetManager.getManager().getSoundVolume());
	}
	
	@Override
	public Pair<Integer> getDamage() { return damage.getRange(MP5.MIN_DAMAGE_MOD); }
	
	@Override
	public float getKnockback() { return MP5.KNOCKBACK; }

	@Override
	public boolean isReloading(long cTime) {
		long elapsed = cTime - reloadStart;
		return ((elapsed < MP5.RELOAD_TIME) && reloading);
	}

	@Override
	public long getReloadTime() { return MP5.RELOAD_TIME; }
	
	@Override
	public double getReloadTime(long cTime) {
		long elapsed = cTime - reloadStart;
		return ((double)elapsed / (double)MP5.RELOAD_TIME);
	}
	
	@Override
	public Image getInventoryIcon() { return AssetManager.getManager().getImage(MP5.ICON_NAME); }

	@Override
	public int getClipSize() { return MP5.CLIP_SIZE; }

	@Override
	public int getStartClips() { return MP5.START_CLIPS; }
	
	@Override
	public int getMaxClips() { return MP5.MAX_CLIPS; }

	@Override
	public long getCooldown() { return MP5.COOLDOWN; }
	
	@Override
	public ProjectileType getProjectile() { return ProjectileType.SMG; }

	@Override
	public int getPrice() { return MP5.PRICE; }
	
	@Override
	public int getAmmoPrice() { return MP5.AMMO_PRICE; }

	@Override
	public int getLevelRequirement() { return 3; }
	
	@Override
	public String getName() {
		return "MP5";
	}
	
	@Override
	public String getDescription() {
		return "A standard military issue MP5.";
	}
}
