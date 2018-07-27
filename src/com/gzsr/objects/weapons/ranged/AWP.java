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
import com.gzsr.math.Calculate;
import com.gzsr.math.Dice;
import com.gzsr.misc.Pair;
import com.gzsr.status.Status;

public class AWP extends RangedWeapon {
	private static final int PRICE = 25_000;
	private static final int AMMO_PRICE = 2_000;
	private static final long COOLDOWN = 1_000L;
	private static final int CLIP_SIZE = 10;
	private static final int START_CLIPS = 2;
	private static final int MAX_CLIPS = 5;
	private static final long RELOAD_TIME = 2_000L;
	private static final float LASER_SIGHT_RANGE = 300.0f;
	private static final int MIN_DAMAGE_COUNT = 5;
	private static final int MIN_DAMAGE_SIDES = 4;
	private static final int MIN_DAMAGE_MOD = 40;
	private static final float KNOCKBACK = 10.0f;
	private static final String ICON_NAME = "GZS_AWP";
	private static final String FIRE_SOUND = "sniper_shot";
	private static final String RELOAD_SOUND = "buy_ammo2";
	
	private Animation muzzleFlash;
	private boolean release;
	
	public AWP() {
		super();
		
		AssetManager assets = AssetManager.getManager();
		
		this.damage = new Dice(AWP.MIN_DAMAGE_COUNT, AWP.MIN_DAMAGE_SIDES);
		
		this.useSound = assets.getSound(AWP.FIRE_SOUND);
		this.reloadSound = assets.getSound(AWP.RELOAD_SOUND);
		
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
		
		Player player = Player.getPlayer();
		Pair<Float> mp = new Pair<Float>((player.getPosition().x + 5.0f), (player.getPosition().y - 28.0f));
		
		// Render a laser sight.
		float theta = (player.getRotation() - (float)(Math.PI / 2));
		Pair<Float> muzzlePos = new Pair<Float>((player.getPosition().x + 5.0f), (player.getPosition().y));
		Pair<Float> laserPos = Calculate.rotateAboutPoint(player.getPosition(), muzzlePos, theta);
		float x2 = (laserPos.x + ((float)Math.cos(theta) * AWP.LASER_SIGHT_RANGE));
		float y2 = (laserPos.y + ((float)Math.sin(theta) * AWP.LASER_SIGHT_RANGE));
		
		g.setColor(Color.red);
		g.drawLine(laserPos.x, laserPos.y, x2, y2);
		
		// Render muzzle flash.
		if(muzzleFlash.isActive(cTime)) muzzleFlash.render(g, mp, player.getPosition(), theta);
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
		double dmg = damage.roll(AWP.MIN_DAMAGE_MOD, critical);
		
		Projectile projectile = new Projectile(particle, BloodGenerator.BURST, dmg, critical);
		projectile.setPenetrations(2);
		projectiles.add(projectile);
		
		if(!player.hasStatus(Status.UNLIMITED_AMMO)) ammoInClip--;
		
		lastUsed = cTime;
		release = false;
		
		if(!Camera.getCamera().isShaking()) Camera.getCamera().shake(cTime, 200L, 50L, 15.0f);
		else Camera.getCamera().refreshShake(cTime);
		
		muzzleFlash.restart(cTime);
		useSound.play(1.0f, AssetManager.getManager().getSoundVolume());
	}
	
	@Override
	public Pair<Integer> getDamage() { return damage.getRange(AWP.MIN_DAMAGE_MOD); }
	
	@Override
	public float getKnockback() { return AWP.KNOCKBACK; }
	
	@Override
	public boolean isReloading(long cTime) {
		long elapsed = cTime - reloadStart;
		return ((elapsed < AWP.RELOAD_TIME) && reloading);
	}
	
	@Override
	public long getReloadTime() { return AWP.RELOAD_TIME; }
	
	@Override
	public double getReloadTime(long cTime) {
		long elapsed = cTime - reloadStart;
		return ((double)elapsed / (double)AWP.RELOAD_TIME);
	}
	
	@Override
	public Image getInventoryIcon() { return AssetManager.getManager().getImage(AWP.ICON_NAME); }
	
	@Override
	public int getClipSize() { return AWP.CLIP_SIZE; }
	
	@Override
	public int getStartClips() { return AWP.START_CLIPS; }
	
	@Override
	public int getMaxClips() { return AWP.MAX_CLIPS; }

	@Override
	public long getCooldown() { return AWP.COOLDOWN; }
	
	@Override
	public ProjectileType getProjectile() { return ProjectileType.RIFLE; }

	@Override
	public int getPrice() { return AWP.PRICE; }
	
	@Override
	public int getAmmoPrice() { return AWP.AMMO_PRICE; }

	@Override
	public String getName() {
		return "AWP";
	}
	
	@Override
	public String getDescription() {
		return "A military-grade AWP Sniper Rifle. Maybe a bit overkill, but you could hit several of these undead freaks at once!";
	}
}
