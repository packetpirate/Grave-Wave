package com.gzsr.objects.weapons.melee;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.state.BasicGameState;

import com.gzsr.Globals;
import com.gzsr.entities.Entity;
import com.gzsr.entities.Player;
import com.gzsr.entities.enemies.Enemy;
import com.gzsr.gfx.particles.Particle;
import com.gzsr.misc.Pair;
import com.gzsr.objects.weapons.DamageType;
import com.gzsr.objects.weapons.Weapon;
import com.gzsr.states.GameState;
import com.gzsr.talents.Talents;

public abstract class MeleeWeapon extends Weapon {
	protected Image img;
	protected Shape attackArea;

	protected boolean attacking;
	public boolean isAttacking() { return attacking; }
	protected boolean multihit;
	protected List<Enemy> enemiesHit;

	protected boolean currentCritical;
	public boolean isCurrentCritical() { return currentCritical; }
	protected float attackTheta;
	public float getAttackTheta() { return attackTheta; }
	protected long lastAttack;

	protected BiFunction<Entity, Long, List<Particle>> bloodGenerator;

	public MeleeWeapon() {
		super();

		img = null;
		attackArea = null;

		attacking = false;
		multihit = false;
		enemiesHit = new ArrayList<Enemy>();

		currentCritical = false;

		attackTheta = 0.0f;
		lastAttack = -(getAttackTimeTotal() + getCooldown());

		bloodGenerator = null;
	}

	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
		if(attacking) {
			long elapsed = (cTime - lastAttack);

			// Check to see if we're already attacking, and if so, the attack time has elapsed.
			if(elapsed > getAttackTimeTotal()) stopAttack();
		}
	}

	@Override
	public void render(Graphics g, long cTime) {
		if(attacking && (img != null)) {
			if(Globals.SHOW_COLLIDERS) {
				g.setColor(Color.red);
				g.draw(attackArea);

				Shape hitBox = getHitBox(cTime);
				g.fill(hitBox);
			}

			Pair<Float> player = Player.getPlayer().getPosition();
			float offset = getImageDistance();
			float theta = getCurrentTheta(cTime);

			float rot = (theta + (float)Math.PI);
			float x = (player.x + ((float)Math.cos(theta) * offset));
			float y = (player.y + ((float)Math.sin(theta) * offset));

			g.rotate(x, y, (float)Math.toDegrees(rot));
			img.draw(x, y);
			g.rotate(x, y, (float)Math.toDegrees(-rot));
		}
	}

	@Override
	public void use(Player player, Pair<Float> position, float theta, long cTime) {
		attacking = true;
		enemiesHit.clear();
		currentCritical = isCritical();

		attackTheta = theta;
		lastAttack = cTime;

		attackArea = transformHitbox(position, theta, getHitAreaSize().x);

		//player.useStamina(getStaminaCost());
		player.getHeartMonitor().addBPM(getBPMCost());

		if(useSound != null) useSound.play();
	}

	@Override
	public boolean canUse(long cTime) {
		//Player player = Player.getPlayer();
		long elapsed = (cTime - lastAttack);

		// Check to see if we're already attacking, and if so, the attack time has elapsed.
		if(attacking && (elapsed > getAttackTimeTotal())) stopAttack();

		boolean cooledDown = (elapsed > (getAttackTimeTotal() + getCooldown()));
		//boolean enoughStamina = (player.getAttributes().getDouble("stamina") >= getStaminaCost());

		return (!attacking && cooledDown /**&& enoughStamina**/);
	}

	protected void stopAttack() {
		attackArea = null;

		attacking = false;
		enemiesHit.clear();

		attackTheta = 0.0f;
	}

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
				particles.stream().forEach(p -> gs.getLevel().addEntity("blood", p));
			}
		}

		return isHit;
	}

	public void onHit(GameState gs, Enemy enemy, long cTime) {
		// Apply effects when weapon hits an enemy.
	}

	// Gets the width of the collision rectangle that can hit.
	protected float getAttackTimeRatio(long cTime) {
		long elapsed = (cTime - lastAttack);
		return ((float)elapsed / (float)getAttackTimeTotal());
	}

	protected float getCurrentTheta(long cTime) {
		float tOff = getThetaOffset();
		return ((attackTheta - tOff) + (getAttackTimeRatio(cTime) * (tOff * 2)) + (float)(Math.PI / 2));
	}

	public Shape getHitBox(long cTime) {
		Player player = Player.getPlayer();

		float atr = getAttackTimeRatio(cTime);
		Pair<Float> hitAreaSize = getHitAreaSize();
		Shape hitbox = transformHitbox(player.getPosition(), attackTheta, (hitAreaSize.x * atr));

		return hitbox;
	}

	protected Shape transformHitbox(Pair<Float> position, float theta, float width) {
		Pair<Float> hitAreaSize = getHitAreaSize();
		float cx = (position.x + ((float)Math.cos(theta + (Math.PI / 2)) * getDistance()));
		float cy = (position.y + ((float)Math.sin(theta + (Math.PI / 2)) * getDistance()));

		Shape rect = new Rectangle((cx - (hitAreaSize.x / 2)), (cy - (hitAreaSize.y / 2)), width, hitAreaSize.y);
		rect = rect.transform(Transform.createRotateTransform(theta, cx, cy));

		return rect;
	}

	@Override
	public Pair<Integer> getDamageRangeTotal() {
		Pair<Integer> range = getDamageRange();

		double bonus = 0.0;
		if(Talents.Tactics.BRUTALITY.active()) bonus += (Talents.Tactics.BRUTALITY.ranks() * 0.1);
		range.x = (int)(range.x + (range.x * bonus));
		range.y = (int)(range.y + (range.y * bonus));

		return range;
	}

	@Override
	public double getDamageTotal(boolean critical) {
		double dmg = rollDamage(critical);

		// TODO: Implement melee damage enhancing talents here.
		double bonus = 0.0;
		if(Talents.Tactics.BRUTALITY.active()) bonus += (Talents.Tactics.BRUTALITY.ranks() * 0.1);

		dmg += (dmg * bonus);

		return dmg;
	}

	@Override
	public DamageType getDamageType() { return DamageType.BLUNT; }

	//public abstract double getStaminaCost();
	public abstract int getBPMCost();

	public abstract float getDistance();
	public abstract float getImageDistance();
	public abstract Pair<Float> getHitAreaSize();
	public abstract float getThetaOffset();
	public abstract long getAttackTime();
	public long getAttackTimeTotal() {
		long time = getAttackTime();
		if(Talents.Tactics.FEROCITY.active()) time /= 2;
		return time;
	}

	@Override
	public String getName() { return "Melee Weapon"; }

	@Override
	public String getTag() { return "meleeWeapon"; }

	@Override
	public String getDescription() { return "Melee Weapon"; }
}
