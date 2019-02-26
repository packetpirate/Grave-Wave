package com.gzsr.objects.weapons.ranged;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.state.BasicGameState;

import com.gzsr.AssetManager;
import com.gzsr.Controls;
import com.gzsr.achievements.Metrics;
import com.gzsr.entities.Player;
import com.gzsr.gfx.particles.Particle;
import com.gzsr.gfx.particles.Projectile;
import com.gzsr.gfx.particles.ProjectileType;
import com.gzsr.math.Dice;
import com.gzsr.misc.Pair;
import com.gzsr.objects.weapons.WType;

public class ClaymoreWeapon extends RangedWeapon {
	private static final int PRICE = 500;
	private static final int AMMO_PRICE = 120;
	private static final long COOLDOWN = 1_500L;
	private static final int CLIP_SIZE = 1;
	private static final int START_CLIPS = 4;
	private static final int MAX_CLIPS = 8;
	private static final long RELOAD_TIME = 1_000L;
	private static final float KNOCKBACK = 5.0f;
	private static final float CHARGE_RATE = 0.001f;
	private static final String PARTICLE_NAME = "GZS_Claymore";
	private static final String FIRE_SOUND = "landmine_armed";
	private static final String RELOAD_SOUND = "buy_ammo2";

	private static final Dice DAMAGE = new Dice(2, 8);
	private static final int DAMAGE_MOD = 2;

	private boolean deploying;
	private float progress;

	public ClaymoreWeapon() {
		super(Size.SMALL, false);

		AssetManager assets = AssetManager.getManager();

		this.useSound = assets.getSound(ClaymoreWeapon.FIRE_SOUND);
		this.reloadSound = assets.getSound(ClaymoreWeapon.RELOAD_SOUND);

		deploying = false;
		progress = 0.0f;
	}

	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
		super.update(gs, cTime, delta);

		if(equipped) {
			boolean mouseDown = Controls.getInstance().getMouse().isLeftDown();
			if(mouseDown && !deploying && (getClipAmmo() > 0)) deploying = true;

			if(deploying) {
				progress += (ClaymoreWeapon.CHARGE_RATE * delta);
				if(progress > 1.0f) progress = 1.0f;
			}

			if(deploying && (progress == 1.0f)) {
				deploying = false;
				release = true;

				Player player = Player.getPlayer();
				if(canUse(cTime)) use(player, player.getPosition(), player.getRotation(), cTime);
			}
		}
	}

	@Override
	public void render(Graphics g, long cTime) {
		super.render(g, cTime);

		if(equipped && deploying) {
			// Render the charge bar.
			Player player = Player.getPlayer();

			g.setColor(Color.green);
			g.fillRect((player.getPosition().x - 23.0f), (player.getPosition().y - 38.0f), (progress * 46.0f), 8.0f);
		}
	}

	@Override
	public boolean blockingMovement() { return deploying; }

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

		boolean critical = isCritical();
		double dmg = getDamageTotal(critical);
		Claymore clay = new Claymore(particle, dmg, critical);
		projectiles.add(clay);

		progress = 0.0f;

		super.use(player, position, theta, cTime);
	}

	@Override
	public void unequip() {
		super.unequip();

		// Prevents deploying of Claymore further if you switch weapons.
		deploying = false;
		progress = 0.0f;
	}

	@Override
	public Pair<Integer> getDamageRange() {
		Pair<Integer> range = ClaymoreWeapon.DAMAGE.getRange(ClaymoreWeapon.DAMAGE_MOD);

		range.x *= Claymore.SHRAPNEL_COUNT;
		range.y *= Claymore.SHRAPNEL_COUNT;

		return range;
	}

	@Override
	public double rollDamage(boolean critical) { return ClaymoreWeapon.DAMAGE.roll(ClaymoreWeapon.DAMAGE_MOD, critical); }

	@Override
	public float getKnockback() { return ClaymoreWeapon.KNOCKBACK; }

	@Override
	public long getReloadTime() { return ClaymoreWeapon.RELOAD_TIME; }

	@Override
	public Image getInventoryIcon() { return WType.CLAYMORE.getImage(); }

	@Override
	public boolean isChargedWeapon() { return true; }

	@Override
	public boolean isCharging() { return deploying; }

	@Override
	public int getClipSize() { return ClaymoreWeapon.CLIP_SIZE; }

	@Override
	public int getClipCapacity() { return getClipSize(); }

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
	public WType getType() { return WType.CLAYMORE; }

	@Override
	public int getLevelRequirement() { return 10; }

	@Override
	public Metrics getWeaponMetric() { return Metrics.CLAYMORE; }

	@Override
	public String getName() {
		return WType.CLAYMORE.getName();
	}

	@Override
	public String getDescription() {
		return WType.CLAYMORE.getDescription();
	}
}
