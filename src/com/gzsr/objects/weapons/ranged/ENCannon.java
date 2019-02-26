package com.gzsr.objects.weapons.ranged;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;

import com.gzsr.AssetManager;
import com.gzsr.achievements.Metrics;
import com.gzsr.controllers.Scorekeeper;
import com.gzsr.entities.Player;
import com.gzsr.gfx.particles.Particle;
import com.gzsr.gfx.particles.ProjectileType;
import com.gzsr.math.Dice;
import com.gzsr.misc.Pair;
import com.gzsr.objects.weapons.WType;

public class ENCannon extends RangedWeapon {
	private static final int PRICE = 0;
	private static final int AMMO_PRICE = 5_000;
	private static final long COOLDOWN = 0L;
	private static final int CLIP_SIZE = 1;
	private static final int START_CLIPS = 2;
	private static final int MAX_CLIPS = 4;
	private static final long RELOAD_TIME = 3_000L;
	private static final float ARC_SIZE = (float)(Math.PI / 2);
	private static final float ARC_INC = (float)(Math.PI / 8);
	private static final int NODES = 5;
	private static final String PROJECTILE_NAME = "GZS_LaserTerminal";
	private static final String FIRE_SOUND = "grenade_launcher";
	private static final String RELOAD_SOUND = "buy_ammo2";

	private static final Dice DAMAGE = new Dice(2, 4);
	private static final int DAMAGE_MOD = 2;

	public ENCannon() {
		super(Size.LARGE, false);

		AssetManager assets = AssetManager.getManager();
		this.useSound = assets.getSound(ENCannon.FIRE_SOUND);
		this.reloadSound = assets.getSound(ENCannon.RELOAD_SOUND);

		addMuzzleFlash();
	}

	@Override
	public void use(Player player, Pair<Float> position, float theta, long cTime) {
		Color color = getProjectile().getColor();
		float velocity = getProjectile().getVelocity();
		float width = getProjectile().getWidth();
		float height = getProjectile().getHeight();
		long lifespan = getProjectile().getLifespan();
		float pTheta = player.getRotation();

		ENNode lastNode = null;
		for(int i = 0; i < NODES; i++) {
			float nTheta = ((pTheta - (ARC_SIZE / 2)) + (i * ARC_INC));
			Particle particle = new Particle(ENCannon.PROJECTILE_NAME, color, position, velocity, nTheta,
											 0.0f, new Pair<Float>(width, height),
											 lifespan, cTime);
			ENNode node = new ENNode(particle);
			if(lastNode != null) lastNode.pair(node);
			lastNode = node;

			projectiles.add(node);
		}

		super.use(player, position, theta, cTime);
		Scorekeeper.getInstance().addShotsFired(-1); // We don't want to add to shots fired for this weapon.
	}

	@Override
	public Pair<Integer> getDamageRange() { return DAMAGE.getRange(DAMAGE_MOD); }

	@Override
	public double rollDamage(boolean critical) { return 0.0; }

	@Override
	public float getKnockback() { return 0.0f; }

	@Override
	public long getReloadTime() { return ENCannon.RELOAD_TIME; }

	@Override
	public Image getInventoryIcon() { return WType.ELECTRIC_NET_CANNON.getImage(); }

	@Override
	public int getClipSize() { return ENCannon.CLIP_SIZE; }

	@Override
	public int getClipCapacity() { return getClipSize(); }

	@Override
	public int getStartClips() { return ENCannon.START_CLIPS; }

	@Override
	public int getMaxClips() { return ENCannon.MAX_CLIPS; }

	@Override
	public long getCooldown() { return ENCannon.COOLDOWN; }

	@Override
	public ProjectileType getProjectile() { return ProjectileType.ELECTRICNODE; }

	@Override
	public int getPrice() { return ENCannon.PRICE; }

	@Override
	public int getAmmoPrice() { return ENCannon.AMMO_PRICE; }

	@Override
	public WType getType() { return WType.ELECTRIC_NET_CANNON; }

	@Override
	public int getLevelRequirement() { return 15; }

	@Override
	public Metrics getWeaponMetric() { return Metrics.ELECTRIC_NET_CANNON; }

	@Override
	public String getName() {
		return WType.ELECTRIC_NET_CANNON.getName();
	}

	@Override
	public String getDescription() {
		return WType.ELECTRIC_NET_CANNON.getDescription();
	}


}
