package com.gzsr.objects.weapons.melee;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.state.BasicGameState;

import com.gzsr.Globals;
import com.gzsr.entities.Player;
import com.gzsr.entities.enemies.Enemy;
import com.gzsr.misc.Pair;
import com.gzsr.objects.weapons.Weapon;

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
	
	public MeleeWeapon() {
		super();
		
		img = null;
		attackArea = null;
		
		attacking = false;
		multihit = false;
		enemiesHit = new ArrayList<Enemy>();
		
		currentCritical = false;
		
		attackTheta = 0.0f;
		lastAttack = -(getAttackTime() + getCooldown());
	}
	
	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
		if(attacking) {
			long elapsed = (cTime - lastAttack);
			
			// Check to see if we're already attacking, and if so, the attack time has elapsed.
			if(elapsed > getAttackTime()) stopAttack();
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
			
			// Used to show line indicating where melee weapon should be pointing during swing/stab/whatever.
			//g.setColor(Color.red);
			//g.drawLine(player.x.floatValue(), player.y.floatValue(), (player.x + ((float)Math.cos(theta) * offset)), (player.y + ((float)Math.sin(theta) * offset)));
			
			float x = (player.x + ((float)Math.cos(theta) * offset));
			float y = (player.y + ((float)Math.sin(theta) * offset));
			
			g.rotate(x, y, (float)Math.toDegrees(theta + (float)Math.PI));
			img.draw(x, y);
			g.rotate(x, y, (float)Math.toDegrees(-(theta + (float)Math.PI)));
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
		
		if(useSound != null) useSound.play(); 
	}

	@Override
	public boolean canUse(long cTime) {
		long elapsed = (cTime - lastAttack);
		
		// Check to see if we're already attacking, and if so, the attack time has elapsed.
		if(attacking && (elapsed > getAttackTime())) stopAttack();
		boolean cooledDown = (elapsed > (getAttackTime() + getCooldown()));
		
		return (!attacking && cooledDown);
	}
	
	protected void stopAttack() {
		attackArea = null;
		
		attacking = false;
		enemiesHit.clear();
		
		attackTheta = 0.0f;
	}
	
	public boolean hit(Enemy enemy, long cTime) {
		if(multihit) {
			for(Enemy e : enemiesHit) {
				if(enemy.equals(e)) return false;
			}
		}
		
		boolean isHit = enemy.getCollider().intersects(getHitBox(cTime));
		if(isHit) {
			if(!multihit) stopAttack();
			else enemiesHit.add(enemy);
		}
		
		return isHit;
	}
	
	public abstract int rollDamage();
	
	// Gets the width of the collision rectangle that can hit.
	protected float getAttackTimeRatio(long cTime) {
		long elapsed = (cTime - lastAttack);
		return ((float)elapsed / (float)getAttackTime()); 
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
	
	public abstract float getDistance();
	public abstract float getImageDistance();
	public abstract Pair<Float> getHitAreaSize();
	public abstract float getThetaOffset();
	public abstract long getAttackTime();

	@Override
	public String getName() {
		return "Melee Weapon";
	}

	@Override
	public String getDescription() {
		return "Melee Weapon";
	}
}
