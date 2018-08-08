package com.gzsr.objects.weapons.ranged;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;

import com.gzsr.AssetManager;
import com.gzsr.entities.Player;
import com.gzsr.gfx.particles.Particle;
import com.gzsr.gfx.particles.Projectile;
import com.gzsr.gfx.particles.ProjectileType;
import com.gzsr.misc.Pair;

public class ClaymoreWeapon extends RangedWeapon {
	private static final int PRICE = 4_000;
	private static final int AMMO_PRICE = 1_000;
	private static final long COOLDOWN = 1_500L;
	private static final int CLIP_SIZE = 1;
	private static final int START_CLIPS = 4;
	private static final int MAX_CLIPS = 8;
	private static final long RELOAD_TIME = 1_000L;
	private static final float KNOCKBACK = 5.0f;
	private static final String ICON_NAME = "GZS_ClaymoreWeapon";
	private static final String PARTICLE_NAME = "GZS_Claymore";
	private static final String FIRE_SOUND = "landmine_armed";
	private static final String RELOAD_SOUND = "buy_ammo2";
	
	public ClaymoreWeapon() {
		super();
		
		AssetManager assets = AssetManager.getManager();
		
		this.useSound = assets.getSound(ClaymoreWeapon.FIRE_SOUND);
		this.reloadSound = assets.getSound(ClaymoreWeapon.RELOAD_SOUND);
	}

	@Override
	public void use(Player player, Pair<Float> position, float theta, long cTime) {
		Color color = getProjectile().getColor();
		float velocity = getProjectile().getVelocity();
		float width = getProjectile().getWidth();
		float height = getProjectile().getHeight();
		long lifespan = getProjectile().getLifespan();
		Particle particle = new Particle(ClaymoreWeapon.PARTICLE_NAME, color, position, velocity, theta,
										 0.0f, new Pair<Float>(width, height), 
										 lifespan, cTime);
		Claymore clay = new Claymore(particle);
		projectiles.add(clay);
		
		super.use(player, position, theta, cTime);
	}
	
	@Override
	public Pair<Integer> getDamage() { return Claymore.getDamageRange(); }

	@Override
	public float getKnockback() { return ClaymoreWeapon.KNOCKBACK; }
	
	@Override
	public boolean isReloading(long cTime) {
		long elapsed = cTime - reloadStart;
		return ((elapsed < ClaymoreWeapon.RELOAD_TIME) && reloading);
	}

	@Override
	public long getReloadTime() { return ClaymoreWeapon.RELOAD_TIME; }
	
	@Override
	public double getReloadTime(long cTime) {
		long elapsed = cTime - reloadStart;
		return ((double)elapsed / (double)ClaymoreWeapon.RELOAD_TIME);
	}

	@Override
	public Image getInventoryIcon() { return AssetManager.getManager().getImage(ClaymoreWeapon.ICON_NAME); }
	
	@Override
	public int getClipSize() { return ClaymoreWeapon.CLIP_SIZE; }

	@Override
	public int getStartClips() { return ClaymoreWeapon.START_CLIPS; }
	
	@Override
	public int getMaxClips() { return ClaymoreWeapon.MAX_CLIPS; }

	@Override
	public long getCooldown() { return ClaymoreWeapon.COOLDOWN; }
	
	@Override
	public List<Projectile> getProjectiles() {
		List<Projectile> allProjectiles = new ArrayList<Projectile>();
		
		allProjectiles.addAll(projectiles);
		for(Projectile p : projectiles) {
			Claymore clay = (Claymore) p;
			allProjectiles.addAll(clay.getShrapnel());
		}
		
		return allProjectiles;
	}

	@Override
	public ProjectileType getProjectile() { return ProjectileType.CLAYMORE; }

	@Override
	public int getPrice() { return ClaymoreWeapon.PRICE; }
	
	@Override
	public int getAmmoPrice() { return ClaymoreWeapon.AMMO_PRICE; }

	@Override
	public int getLevelRequirement() { return 10; }
	
	@Override
	public String getName() {
		return "Claymore";
	}
	
	@Override
	public String getDescription() {
		return "A stationary motion-activated explosive that sends shrapnel hurtling through the air to rip your enemies to shreds.";
	}
}
