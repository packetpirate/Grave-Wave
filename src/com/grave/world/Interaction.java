package com.grave.world;

import com.grave.misc.Pair;
import com.grave.states.GameState;

@FunctionalInterface
public interface Interaction {
	void execute(GameState gs, Pair<Float> pos, long cTime);
}
