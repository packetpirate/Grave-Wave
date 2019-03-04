package com.gzsr.objects.weapons.ranged;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.state.BasicGameState;

import com.gzsr.AssetManager;
import com.gzsr.Controls;
import com.gzsr.achievements.Metrics;
import com.gzsr.controllers.Scorekeeper;
import com.gzsr.entities.Player;
import com.gzsr.gfx.particles.Particle;
import com.gzsr.gfx.particles.Projectile;
import com.gzsr.gfx.particles.ProjectileType;
import com.gzsr.math.Dice;
import com.gzsr.misc.Pair;
import com.gzsr.objects.weapons.WType;
import com.gzsr.states.GameState;

public class PipeBombWeapon extends RangedWeapon {
	private static final int PRICE = 0;
	private static final int AMMO_PRICE = 0;
	private static final long COOLDOWN = 0L;
	private static final int CLIP_SIZE = 1;
	private static final int START_CLIPS = 4;
	private static final int MAX_CLIPS = 8;
	private static final float CHARGE_RATE = 0.0015f;
	private static final long RELOAD_TIME = 1_000L;
	private static final String PROJECTILE_NAME = "GZS_Pipe_Bomb";
	private static final String FIRE_SOUND = "throw2";

	private static final Dice DAMAGE = new Dice(4, 4);
	private static final int DAMAGE_MOD = 4;

	private boolean charging;
	private float charge;

	public PipeBombWeapon() {
		super(Size.SMALL, false);

		this.useSound = AssetManager.getManager().getSound(PipeBombWeapon.FIRE_SOUND);
	}

	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
		// Basically just checking to see if the reload time has elapsed.
		if(reloading && !isReloading(cTime)) {
			int takeFromInv = getClipSize() - ammoInClip;
			int taken = Math.min(takeFromInv, ammoInInventory);
			ammoInInventory -= taken;
			ammoInClip += taken;

			reloading = false;
		}

		// Update all projectiles.
		List<Projectile> proj = getProjectiles();
		if(!proj.isEmpty()) {
			Iterator<Projectile> it = proj.iterator();
			while(it.hasNext()) {
				Particle p = it.next();
				if(p.isAlive(cTime)) {
					p.update(gs, cTime, delta);
				} else {
					p.onDestroy((GameState)gs, cTime);
					it.remove();
				}
			}
		}

		// Remove destroyed particles.
		Iterator<Projectile> it = projectiles.iterator();
		while(it.hasNext()) {
			Projectile p = it.next();
			if(p.isDestroyed()) it.remove();
		}

		if(equipped) {
			if(charging) {
				// If we're charging, increase charge up to max of 1.0f.
				charge += PipeBombWeapon.CHARGE_RATE * delta;
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
		long lifespan = (long)(getProjectile().getLifespan() * charge);
		Particle particle = new Particle(getProjectileName(), color, position, velocity, theta,
										 0.0f, new Pair<Float>(width, height),
										 lifespan, cTime);

		boolean critical = isCritical();
		double dmg = getDamageTotal(critical);
		PipeBomb pipeBomb = new PipeBomb(particle, dmg, critical);
		projectiles.add(pipeBomb);

		charge = 0.0f;
		super.use(player, position, theta, cTime);
		Scorekeeper.getInstance().addShotsFired(-1); // Pipe Bomb throws shouldn't count against accuracy.
	}

	@Override
	public void unequip() {
		super.unequip();

		// Prevents molotov from being thrown after we've switched weapons.
		charging = false;
		charge = 0.0f;
	}

	@Override
	public long getReloadTime() { return PipeBombWeapon.RELOAD_TIME; }

	@Override
	public long getCooldown() { return PipeBombWeapon.COOLDOWN; }

	@Override
	public int getClipSize() { return PipeBombWeapon.CLIP_SIZE; }

	@Override
	public int getClipCapacity() { return getClipSize(); }

	@Override
	public int getStartClips() { return PipeBombWeapon.START_CLIPS; }

	@Override
	public int getMaxClips() { return PipeBombWeapon.MAX_CLIPS; }

	@Override
	public int getPrice() { return PipeBombWeapon.PRICE; }

	@Override
	public int getAmmoPrice() { return PipeBombWeapon.AMMO_PRICE; }

	@Override
	public List<Projectile> getProjectiles() {
		List<Projectile> allProjectiles = new ArrayList<Projectile>();

		allProjectiles.addAll(projectiles);
		for(Projectile p : projectiles) {
			PipeBomb pipe = (PipeBomb) p;
			allProjectiles.addAll(pipe.getShrapnel());
		}

		return allProjectiles;
	}

	@Override
	public ProjectileType getProjectile() { return ProjectileType.PIPE_BOMB; }

	@Override
	public String getProjectileName() { return PipeBombWeapon.PROJECTILE_NAME; }

	@Override
	public boolean isChargedWeapon() { return true; }

	@Override
	public boolean isCharging() { return charging; }

	@Override
	public Pair<Integer> getDamageRange() {
		Pair<Integer> range = PipeBombWeapon.DAMAGE.getRange(PipeBombWeapon.DAMAGE_MOD);

		range.x *= PipeBomb.SHRAPNEL_COUNT;
		range.y *= PipeBomb.SHRAPNEL_COUNT;

		return range;
	}

	@Override
	public double rollDamage(boolean critical) { return PipeBombWeapon.DAMAGE.roll(PipeBombWeapon.DAMAGE_MOD, critical); }

	@Override
	public float getKnockback() { return 4.0f; }

	@Override
	public Image getInventoryIcon() { return WType.PIPE_BOMB.getImage(); }

	@Override
	public WType getType() { return WType.PIPE_BOMB; }

	@Override
	public int getLevelRequirement() { return 8; }

	@Override
	public Metrics getWeaponMetric() { return Metrics.PIPE_BOMB; }

	@Override
	public String getName() { return WType.PIPE_BOMB.getName(); }

	@Override
	public String getDescription() { return WType.PIPE_BOMB.getDescription(); }
}
