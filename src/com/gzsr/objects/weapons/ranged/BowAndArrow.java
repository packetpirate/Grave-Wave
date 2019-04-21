package com.gzsr.objects.weapons.ranged;

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
import com.gzsr.gfx.particles.emitters.BloodGenerator;
import com.gzsr.math.Dice;
import com.gzsr.misc.Pair;
import com.gzsr.objects.weapons.WType;

public class BowAndArrow extends RangedWeapon {
	private static final int PRICE = 500;
	private static final int AMMO_PRICE = 150;
	private static final long COOLDOWN = 1_000L;
	private static final int CLIP_SIZE = 30;
	private static final int START_CLIPS = 1;
	private static final int MAX_CLIPS = 3;
	private static final float KNOCKBACK = 5.0f;
	private static final float CHARGE_RATE = 0.0015f;
	private static final String PROJECTILE_NAME = "GZS_Arrow";
	private static final String FIRE_SOUND = "bow_fire";
	private static final String RELOAD_SOUND = "buy_ammo2";

	private static final Dice DAMAGE = new Dice(2, 10);
	private static final int DAMAGE_MOD = 10;

	protected boolean charging;
	protected float charge;

	public BowAndArrow() {
		super(Size.SMALL, false);

		AssetManager assets = AssetManager.getManager();

		this.useSound = assets.getSound(BowAndArrow.FIRE_SOUND);
		this.reloadSound = assets.getSound(BowAndArrow.RELOAD_SOUND);

		charging = false;
		charge = 0.0f;
	}

	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
		super.update(gs, cTime, delta);

		if(equipped) {
			if(charging) {
				// If we're charging, increase charge up to max of 1.0f.
				charge += (getChargeRate() * delta);
				if(charge > 1.0f) charge = 1.0f;
			}

			if(Controls.getInstance().getMouse().isLeftDown()) {
				// If the mouse is down and we're not charging, start charging.
				if(!charging && (getClipAmmo() > 0)) charging = true;
			} else {
				if(charging) {
					// If the mouse is released / down and we're currently
					// charging, release and stop charging!
					release = true;
					charging = false;

					Player player = Player.getPlayer();
					if(canUse(cTime)) use(player, player.getPosition(), player.getRotation(), cTime);
				}
			}
		}
	}

	@Override
	public void render(Graphics g, long cTime) {
		super.render(g, cTime);

		if(equipped && charging) {
			// Render the charge bar.
			Player player = Player.getPlayer();
			g.setColor(Color.white);
			g.drawRect((player.getPosition().x - 24.0f), (player.getPosition().y - 44.0f), 48.0f, 15.0f);

			if(charge < 0.3f) g.setColor(Color.red);
			else if(charge < 0.75f) g.setColor(Color.yellow);
			else g.setColor(Color.green);

			g.fillRect((player.getPosition().x - 23.0f), (player.getPosition().y - 43.0f), (charge * 46.0f), 13.0f);
		}
	}

	@Override
	public void use(Player player, Pair<Float> position, float theta, long cTime) {
		Color color = getProjectile().getColor();
		float velocity = getProjectile().getVelocity();
		float width = getProjectile().getWidth();
		float height = getProjectile().getHeight();
		long lifespan = getProjectile().getLifespan();
		Particle particle = new Particle(getProjectileName(), color, position, velocity, theta,
										 0.0f, new Pair<Float>(width, height),
										 lifespan, cTime);

		boolean critical = isCritical();
		double dmg = getDamageTotal(critical);

		Projectile projectile = new Projectile(particle, BloodGenerator.BURST, dmg, critical);

		projectiles.add(projectile);

		charge = 0.0f;

		super.use(player, position, theta, cTime);
	}

	@Override
	public void unequip() {
		super.unequip();

		// Prevents arrow from firing after we've switched weapons.
		charging = false;
		charge = 0.0f;
	}

	@Override
	public Pair<Integer> getDamageRange() { return BowAndArrow.DAMAGE.getRange(BowAndArrow.DAMAGE_MOD); }

	@Override
	public double rollDamage(boolean critical) { return BowAndArrow.DAMAGE.roll(BowAndArrow.DAMAGE_MOD, critical); }

	@Override
	public float getKnockback() { return BowAndArrow.KNOCKBACK; }

	@Override
	public boolean isReloading(long cTime) { return false; }

	@Override
	public long getReloadTime() { return 0L; }

	@Override
	public double getReloadTime(long cTime) { return 0.0; }

	@Override
	public Image getInventoryIcon() { return WType.BOW_AND_ARROW.getImage(); }

	protected float getChargeRate() { return BowAndArrow.CHARGE_RATE; }

	@Override
	public boolean isChargedWeapon() { return true; }

	@Override
	public boolean isCharging() { return charging; }

	@Override
	public int getClipSize() { return BowAndArrow.CLIP_SIZE; }

	@Override
	public int getStartClips() { return BowAndArrow.START_CLIPS; }

	@Override
	public int getMaxClips() { return BowAndArrow.MAX_CLIPS; }

	@Override
	public long getCooldown() { return BowAndArrow.COOLDOWN; }

	@Override
	public ProjectileType getProjectile() { return ProjectileType.ARROW; }

	@Override
	public String getProjectileName() { return BowAndArrow.PROJECTILE_NAME; }

	@Override
	public int getPrice() { return BowAndArrow.PRICE; }

	@Override
	public int getAmmoPrice() { return BowAndArrow.AMMO_PRICE; }

	@Override
	public WType getType() { return WType.BOW_AND_ARROW; }

	@Override
	public int getLevelRequirement() { return 5; }

	@Override
	public Metrics getWeaponMetric() { return Metrics.BOW_AND_ARROW; }

	@Override
	public String getName() { return WType.BOW_AND_ARROW.getName(); }

	@Override
	public String getDescription() { return WType.BOW_AND_ARROW.getDescription(); }
}
