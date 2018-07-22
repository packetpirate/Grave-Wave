package com.gzsr.objects.weapons.melee;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.BasicGameState;

import com.gzsr.objects.weapons.Weapon;

public abstract class MeleeWeapon extends Weapon {
	public MeleeWeapon() {
		
	}
	
	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
		
	}

	@Override
	public void render(Graphics g, long cTime) {
		
	}

	@Override
	public boolean canUse(long cTime) {
		// TODO: Generalize melee weapon canUse method.
		return false;
	}

	@Override
	public String getName() {
		return "Melee Weapon";
	}

	@Override
	public String getDescription() {
		return "Melee Weapon";
	}
}
