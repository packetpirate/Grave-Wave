package com.gzsr.objects.weapons;

import org.newdawn.slick.Image;
import org.newdawn.slick.Sound;

import com.gzsr.Globals;
import com.gzsr.entities.Entity;
import com.gzsr.entities.Player;
import com.gzsr.gfx.Layers;
import com.gzsr.math.Dice;
import com.gzsr.misc.Pair;

public abstract class Weapon implements Entity {
	protected Sound useSound;
	
	protected Dice damage;
	
	protected boolean equipped;
	protected long lastUsed;
	
	public Weapon() {
		this.useSound = null;
		
		this.damage = null; // Must be set by each individual weapon.
		
		this.equipped = false;
		this.lastUsed = -getCooldown();
	}
	
	public abstract void use(Player player, Pair<Float> position, float theta, long cTime);
	public abstract boolean canUse(long cTime);
	
	public boolean isCritical() {
		float roll = Globals.rand.nextFloat();
		float chance = Player.getPlayer().getAttributes().getFloat("critChance");
		return (roll <= chance); 
	}

	public boolean isEquipped() { return equipped; }
	public void equip() { equipped = true; }
	public void unequip() { equipped = false; }
	public boolean isChargedWeapon() { return false; };
	public boolean isCharging() { return false; }
	
	public abstract int getPrice();
	public abstract Pair<Integer> getDamage();
	
	public abstract float getKnockback();
	public abstract Image getInventoryIcon();
	public abstract long getCooldown();	
	
	@Override
	public int getLayer() {
		return Layers.NONE.val();
	}
}