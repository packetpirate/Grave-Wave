package com.grave.entities.enemies.bosses;

import com.grave.entities.enemies.Enemy;
import com.grave.entities.enemies.EnemyType;
import com.grave.misc.Pair;

public abstract class Boss extends Enemy {
	public Boss(EnemyType type_, Pair<Float> position_) {
		super(type_, position_);
	}

	@Override
	public String getName() { return "Boss"; }

	@Override
	public String getTag() { return "boss"; }

	@Override
	public String getDescription() { return "Stronger than the average enemy. This thing means business."; }
}
