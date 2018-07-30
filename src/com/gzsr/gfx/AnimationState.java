package com.gzsr.gfx;

import java.util.HashMap;
import java.util.Map;

public class AnimationState {
	private Map<String, Animation> states;
	public Map<String, Animation> getStates() { return states; }
	public void addState(String key, Animation anim) { states.put(key, anim); }
	
	private String current;
	public String getCurrent() { return current; }
	public void setCurrent(String state) {
		// Will not change the current state if a mapping for that state is not found.
		if(states.containsKey(state)) current = state;
	}
	
	public Animation getCurrentAnimation() { return states.get(current); }
	
	public AnimationState() {
		states = new HashMap<String, Animation>();
		current = null;
	}
}
