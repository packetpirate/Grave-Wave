package com.gzsr.objects.weapons.ranged;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;

import com.gzsr.AssetManager;
import com.gzsr.Globals;
import com.gzsr.achievements.Metrics;
import com.gzsr.controllers.Scorekeeper;
import com.gzsr.entities.Player;
import com.gzsr.gfx.Camera;
import com.gzsr.gfx.particles.Particle;
import com.gzsr.gfx.particles.ProjectileType;
import com.gzsr.gfx.particles.StatusProjectile;
import com.gzsr.math.Dice;
import com.gzsr.misc.Pair;
import com.gzsr.objects.crafting.Resources;
import com.gzsr.objects.weapons.WType;
import com.gzsr.status.BurningEffect;

public class FlakCannon extends RangedWeapon {
	private static final int PRICE = 0;
	private static final long COOLDOWN = 0L;
	private static final int CLIP_SIZE = 1;
	private static final int START_CLIPS = 0;
	private static final long RELOAD_TIME = 1_500L;
	private static final int SHOT_COUNT = 10;
	private static final float MAX_SPREAD = (float)(Math.PI / 9);
	private static final String FIRE_SOUND = "mossberg_shot_01";
	private static final String RELOAD_SOUND = "buy_ammo2";

	private static final Dice DAMAGE = new Dice(2, 8);
	private static final int DAMAGE_MOD = 12;

	public FlakCannon() {
		super(Size.LARGE);

		this.ammoInClip = 0;

		AssetManager assets = AssetManager.getManager();

		this.useSound = assets.getSound(FlakCannon.FIRE_SOUND);
		this.reloadSound = assets.getSound(FlakCannon.RELOAD_SOUND);

		this.shakeEffect = new Camera.ShakeEffect(100L, 20L, 20.0f);

		addMuzzleFlash();
	}

	@Override
	public void use(Player player, Pair<Float> position, float theta, long cTime) {
		for(int i = 0; i < FlakCannon.SHOT_COUNT; i++) {
			Color color = getProjectile().getColor();
			float velocity = getProjectile().getVelocity();
			float width = getProjectile().getWidth();
			float height = getProjectile().getHeight();
			float devTheta = (theta + (Globals.rand.nextFloat() * FlakCannon.MAX_SPREAD * (Globals.rand.nextBoolean()?1:-1)));
			long lifespan = getProjectile().getLifespan();
			Particle particle = new Particle(color, position, velocity, devTheta,
											 0.0f, new Pair<Float>(width, height),
											 lifespan, cTime);

			boolean critical = isCritical();
			double dmg = getDamageTotal(critical);

			BurningEffect effect = new BurningEffect(cTime);
			StatusProjectile projectile = new StatusProjectile(particle, dmg, critical, effect);
			projectiles.add(projectile);
		}

		super.use(player, position, theta, cTime);
		Scorekeeper.getInstance().addShotFired();
	}

	@Override
	protected void reload() {
		Player.getPlayer().getResources().add(Resources.METAL, -1);
		ammoInClip++;

		reloading = false;
	}

	@Override
	public Pair<Integer> getDamageRange() { return DAMAGE.getRange(DAMAGE_MOD); }

	@Override
	public double rollDamage(boolean critical) { return 0.0; }

	@Override
	public float getKnockback() { return 0.0f; }

	@Override
	public long getReloadTime() { return FlakCannon.RELOAD_TIME; }

	@Override
	public Image getInventoryIcon() { return WType.FLAK_CANNON.getImage(); }

	@Override
	public int getInventoryAmmo() { return Player.getPlayer().getResources().get(Resources.METAL); }

	@Override
	public int getClipSize() { return FlakCannon.CLIP_SIZE; }

	@Override
	public int getClipCapacity() { return getClipSize(); }

	@Override
	public int getStartClips() { return FlakCannon.START_CLIPS; }

	@Override
	public int getMaxClips() { return 0; }

	@Override
	public long getCooldown() { return FlakCannon.COOLDOWN; }

	@Override
	public ProjectileType getProjectile() { return ProjectileType.FLAK; }

	@Override
	public int getPrice() { return FlakCannon.PRICE; }

	@Override
	public int getAmmoPrice() { return -1; }

	@Override
	public WType getType() { return WType.FLAK_CANNON; }

	@Override
	public int getLevelRequirement() { return 15; }

	@Override
	public Metrics getWeaponMetric() { return Metrics.FLAK_CANNON; }

	@Override
	public String getName() { return WType.FLAK_CANNON.getName(); }

	@Override
	public String getDescription() { return WType.FLAK_CANNON.getDescription(); }
}
