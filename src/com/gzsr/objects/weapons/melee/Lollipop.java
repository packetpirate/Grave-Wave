package com.gzsr.objects.weapons.melee;

import java.util.List;

import org.newdawn.slick.Image;

import com.gzsr.AssetManager;
import com.gzsr.Globals;
import com.gzsr.achievements.Metrics;
import com.gzsr.entities.enemies.Enemy;
import com.gzsr.gfx.particles.Particle;
import com.gzsr.gfx.particles.emitters.BloodGenerator;
import com.gzsr.math.Dice;
import com.gzsr.misc.Pair;
import com.gzsr.states.GameState;

public class Lollipop extends MeleeWeapon {
	private static final int PRICE = 1_000;
	private static final Pair<Float> HIT_AREA_SIZE = new Pair<Float>(96.0f, 32.0f);
	private static final float HIT_AREA_OFFSET = -32.0f;
	private static final float IMAGE_DISTANCE = -8.0f;
	private static final long ATTACK_TIME = 500L;
	private static final long COOLDOWN = 1_000L;
	private static final double STAMINA = 40.0;
	private static final float KNOCKBACK = 10.0f;
	private static final float THETA_OFFSET = (float)(Math.PI / 3.6);
	private static final String ICON_NAME = "GZS_Lollipop_Icon";
	private static final String WEAPON_IMAGE = "GZS_Lollipop";
	
	private static final Dice DAMAGE = new Dice(10, 5);
	private static final int DAMAGE_MOD = 10;
	
	public Lollipop() {
		super();
		
		img = AssetManager.getManager().getImage(Lollipop.WEAPON_IMAGE);
		useSound = AssetManager.getManager().getSound("throw2");
		
		multihit = true;
		
		bloodGenerator = BloodGenerator.RAINBOW;
	}
	
	@Override
	public boolean hit(GameState gs, Enemy enemy, long cTime) {
		if(multihit) {
			for(Enemy e : enemiesHit) {
				if(enemy.equals(e)) return false;
			}
		}
		
		boolean isHit = enemy.getCollider().intersects(getHitBox(cTime));
		if(isHit) {
			if(!multihit) stopAttack();
			else enemiesHit.add(enemy);
			
			if(bloodGenerator != null) {
				List<Particle> particles = bloodGenerator.apply(enemy, cTime);
				particles.stream().forEach(p -> gs.addEntity(String.format("blood%d", Globals.generateEntityID()),  p));
			}
			
			AssetManager.getManager().getSound("party_horn").play(1.0f, AssetManager.getManager().getSoundVolume());
		}
		
		return isHit;
	}
	
	@Override
	public float getDistance() { return Lollipop.HIT_AREA_OFFSET; }
	
	@Override
	public float getImageDistance() { return Lollipop.IMAGE_DISTANCE; }

	@Override
	public Pair<Float> getHitAreaSize() { return Lollipop.HIT_AREA_SIZE; }

	@Override
	public float getThetaOffset() { return Lollipop.THETA_OFFSET; }

	@Override
	public long getAttackTime() { return Lollipop.ATTACK_TIME; }
	
	@Override
	public long getCooldown() { return Lollipop.COOLDOWN; }

	@Override
	public int getPrice() { return Lollipop.PRICE; }

	@Override
	public Pair<Integer> getDamageRange() { return Lollipop.DAMAGE.getRange(Lollipop.DAMAGE_MOD); }
	
	@Override
	public double rollDamage(boolean critical) { return Lollipop.DAMAGE.roll(Lollipop.DAMAGE_MOD, critical); }
	
	@Override
	public double getStaminaCost() { return Lollipop.STAMINA; }

	@Override
	public float getKnockback() { return Lollipop.KNOCKBACK; }

	@Override
	public Image getInventoryIcon() { return AssetManager.getManager().getImage(ICON_NAME); }
	
	@Override
	public int getLevelRequirement() { return 18; }
	
	@Override
	public long getWeaponMetric() { return Metrics.LOLLIPOP; }
	
	@Override
	public String getName() {
		return "Lollipop";
	}

	@Override
	public String getDescription() {
		return "Huh... maybe the drugs are finally kicking in...";
	}
}
