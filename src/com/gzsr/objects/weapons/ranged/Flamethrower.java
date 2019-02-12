package com.gzsr.objects.weapons.ranged;

import org.newdawn.slick.Image;
import org.newdawn.slick.state.BasicGameState;

import com.gzsr.AssetManager;
import com.gzsr.Controls;
import com.gzsr.Globals;
import com.gzsr.achievements.Metrics;
import com.gzsr.controllers.Scorekeeper;
import com.gzsr.entities.Player;
import com.gzsr.gfx.Animation;
import com.gzsr.gfx.particles.Particle;
import com.gzsr.gfx.particles.ProjectileType;
import com.gzsr.gfx.particles.StatusProjectile;
import com.gzsr.misc.Pair;
import com.gzsr.objects.weapons.WType;
import com.gzsr.status.BurningEffect;

public class Flamethrower extends RangedWeapon {
	private static final int PRICE = 3_200;
	private static final int AMMO_PRICE = 300;
	private static final long COOLDOWN = 25L;
	private static final int CLIP_SIZE = 200;
	private static final int START_CLIPS = 2;
	private static final int MAX_CLIPS = 5;
	private static final long RELOAD_TIME = 3_000L;
	private static final int EMBER_COUNT = 5;
	private static final float EMBER_SPREAD = (float)(Math.PI / 18);
	private static final String FIRE_SOUND = "flamethrower3";
	private static final String RELOAD_SOUND = "buy_ammo2";

	public Flamethrower() {
		super(Size.LARGE);

		AssetManager assets = AssetManager.getManager();

		this.useSound = assets.getSound(Flamethrower.FIRE_SOUND);
		this.reloadSound = assets.getSound(Flamethrower.RELOAD_SOUND);
	}

	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
		super.update(gs, cTime, delta);
		if(!equipped || reloading || !Controls.getInstance().getMouse().isLeftDown()) useSound.stop();
	}

	@Override
	public void use(Player player, Pair<Float> position, float theta, long cTime) {
		for(int i = 0; i < Flamethrower.EMBER_COUNT; i++) {
			//Color color = getProjectile().getColor();
			Animation fire = AssetManager.getManager().getAnimation("GZS_FireAnimation1");

			float velocity = getProjectile().getVelocity();
			float width = getProjectile().getWidth();
			float height = getProjectile().getHeight();
			float devTheta = (theta + (Globals.rand.nextFloat() * Flamethrower.EMBER_SPREAD * (Globals.rand.nextBoolean()?1:-1)));
			long lifespan = getProjectile().getLifespan();
			Particle particle = new Particle(fire, position, velocity, devTheta,
											 0.0f, new Pair<Float>(width, height),
											 lifespan, cTime);

			StatusProjectile projectile = new StatusProjectile(particle, 0.0, false, new BurningEffect(cTime));
			projectiles.add(projectile);
		}

		if(!hasUnlimitedAmmo()) ammoInClip--;
		lastUsed = cTime;
		if(!useSound.playing()) useSound.loop(1.0f, AssetManager.getManager().getSoundVolume());

		Scorekeeper.getInstance().addShotsFired(EMBER_COUNT);
	}

	@Override
	public Pair<Integer> getDamageRange() { return BurningEffect.getDamageRange(); }

	@Override
	public double rollDamage(boolean critical) { return 0.0; }

	@Override
	public float getKnockback() { return 0.0f; }

	@Override
	public long getReloadTime() { return Flamethrower.RELOAD_TIME; }

	@Override
	public Image getInventoryIcon() { return WType.FLAMETHROWER.getImage(); }

	@Override
	public int getClipSize() { return Flamethrower.CLIP_SIZE; }

	@Override
	public int getStartClips() { return Flamethrower.START_CLIPS; }

	@Override
	public int getMaxClips() { return Flamethrower.MAX_CLIPS; }

	@Override
	public long getCooldown() { return Flamethrower.COOLDOWN; }

	@Override
	public ProjectileType getProjectile() { return ProjectileType.FLAMETHROWER; }

	@Override
	public int getPrice() { return Flamethrower.PRICE; }

	@Override
	public int getAmmoPrice() { return Flamethrower.AMMO_PRICE; }

	@Override
	public WType getType() { return WType.FLAMETHROWER; }

	@Override
	public int getLevelRequirement() { return 12; }

	@Override
	public long getWeaponMetric() { return Metrics.FLAMETHROWER; }

	@Override
	public String getName() {
		return WType.FLAMETHROWER.getName();
	}

	@Override
	public String getDescription() {
		return WType.FLAMETHROWER.getDescription();
	}
}
