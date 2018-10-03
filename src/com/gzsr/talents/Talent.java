package com.gzsr.talents;

import com.gzsr.misc.Pair;
import com.gzsr.talents.Talents.TalentType;

public class Talent {
	private TalentType type;
	public TalentType getType() { return type; }
	
	private Pair<Integer> ratio;
	public Pair<Integer> getRatio() { return ratio; }
	public void reset() { ratio.x = 0; }
	public void addPoint() {
		if(ratio.x < ratio.y) ratio.x++;
	}
	public void removePoint() {
		if(ratio.x > 0) ratio.x--;
	}
	
	public Talent(TalentType type_) {
		type = type_;
		ratio = new Pair<Integer>(0, type_.maxRanks());
	}
}
