package com.gzsr.objects.weapons;

import java.util.LinkedList;
import java.util.Queue;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import com.gzsr.AssetManager;
import com.gzsr.Globals;
import com.gzsr.entities.Player;
import com.gzsr.gfx.particles.ProjectileType;
import com.gzsr.math.Calculate;
import com.gzsr.misc.Pair;
import com.gzsr.states.GameState;
import com.gzsr.status.Status;

public class BigRedButton extends Weapon {
	private static final long COOLDOWN = 15000L;
	private static final int CLIP_SIZE = 1;
	private static final int START_CLIPS = 1;
	private static final long RELOAD_TIME = 0L;
	private static final double DAMAGE = 250.0;
	private static final float EXP_RADIUS = 150.0f;
	private static final long EXP_DELAY = 500L;
	private static final int EXP_COUNT = 5;
	private static final String ICON_NAME = "GZS_BigRedButton";
	private static final String EXP_NAME = "GZS_Explosion";
	private static final String FIRE_SOUND = "landmine_armed";
	private static final String EXP_SOUND = "explosion2";
	private static final String RELOAD_SOUND = "buy_ammo2";
	
	private Queue<Explosion> explosions;
	private long lastExplosion;
	
	public BigRedButton() {
		super();
		
		explosions = new LinkedList<Explosion>();
		lastExplosion = 0L;
		
		AssetManager assets = AssetManager.getManager();
		
		this.fireSound = assets.getSound(BigRedButton.FIRE_SOUND);
		this.reloadSound = assets.getSound(BigRedButton.RELOAD_SOUND);
	}
	
	@Override
	public void update(GameState gs, long cTime, int delta) {
		// Basically just checking to see if the reload time has elapsed.
		if(!isReloading(cTime)) reloading = false;
		
		long elapsed = cTime - lastExplosion;
		if(!explosions.isEmpty() && (elapsed >= BigRedButton.EXP_DELAY)) {
			int id = Globals.generateEntityID();
			Explosion exp = explosions.remove();
			gs.addEntity(String.format("explosion%d", id), exp);
			AssetManager.getManager().getSound(BigRedButton.EXP_SOUND).play();
			lastExplosion = cTime;
		}
	}
	
	@Override
	public void render(Graphics g, long cTime) {
		if(!explosions.isEmpty()) {
			explosions.stream()
					  .filter(exp -> exp.isActive(cTime))
					  .forEach(exp -> exp.render(g, cTime));
		}
	}
	
	@Override
	public void fire(Player player, Pair<Float> position, float theta, long cTime) {
		for(int i = 0; i < BigRedButton.EXP_COUNT; i++) {
			Pair<Float> location = getExplosionLocation(player, position);
			double damage = BigRedButton.DAMAGE + (BigRedButton.DAMAGE * (player.getIntAttribute("damageUp") * 0.10));
			Explosion exp = new Explosion(BigRedButton.EXP_NAME, location, damage, BigRedButton.EXP_RADIUS);
			explosions.add(exp);
		}
		
		if(!player.hasStatus(Status.UNLIMITED_AMMO)) ammoInClip--;
		lastFired = cTime;
		fireSound.play();
	}
	
	private Pair<Float> getExplosionLocation(Player player, Pair<Float> position) {
		float x = 0.0f;
		float y = 0.0f;
		
		boolean valid = false;
		while(!valid) {
			x = BigRedButton.EXP_RADIUS + (Globals.rand.nextFloat() * (Globals.WIDTH - (BigRedButton.EXP_RADIUS * 2)));
			y = BigRedButton.EXP_RADIUS + (Globals.rand.nextFloat() * (Globals.HEIGHT - (BigRedButton.EXP_RADIUS * 2)));
			
			valid = (Calculate.Distance(new Pair<Float>(x, y), position) > BigRedButton.EXP_RADIUS);
		}
		
		return new Pair<Float>(x, y);
	}

	@Override
	public boolean isReloading(long cTime) {
		long elapsed = cTime - reloadStart;
		return ((elapsed < BigRedButton.RELOAD_TIME) && reloading);
	}

	@Override
	public double getReloadTime(long cTime) {
		long elapsed = cTime - reloadStart;
		return ((double)elapsed / (double)BigRedButton.RELOAD_TIME);
	}

	@Override
	public Image getInventoryIcon() {
		return AssetManager.getManager().getImage(BigRedButton.ICON_NAME);
	}

	@Override
	public int getClipSize() {
		return BigRedButton.CLIP_SIZE;
	}

	@Override
	protected int getStartClips() {
		return BigRedButton.START_CLIPS;
	}

	@Override
	public long getCooldown() {
		return BigRedButton.COOLDOWN;
	}

	@Override
	public ProjectileType getProjectile() {
		return null;
	}

}