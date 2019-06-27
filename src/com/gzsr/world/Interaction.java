package com.gzsr.world;

import com.gzsr.misc.Pair;
import com.gzsr.states.GameState;

@FunctionalInterface
public interface Interaction {
	void execute(GameState gs, Pair<Float> pos, long cTime);
}
