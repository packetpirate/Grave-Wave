package com.gzsr.achievements.state;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gzsr.achievements.Achievement;
import com.gzsr.controllers.AchievementController;
import com.gzsr.states.GameState;

public class StateBasedAchievement extends Achievement {
	private AchievementState currentState;
	
	private Map<Integer, AchievementState> allStates;
	public void saveState(AchievementState state) { allStates.put(state.getID(), state); }
	public void saveStates(AchievementState... states) {
		for(AchievementState state : states) {
			saveState(state);
		}
	}
	
	public StateBasedAchievement(int id_, String name_, String description_, String icon_, AchievementState start_) {
		this(id_, name_, description_, icon_, start_, false);
	}
	
	public StateBasedAchievement(int id_, String name_, String description_, String icon_, AchievementState start_, boolean hidden_) {
		this(id_, name_, description_, icon_, start_, hidden_, false);
	}
	
	public StateBasedAchievement(int id_, String name_, String description_, String icon_, AchievementState start_, boolean hidden_, boolean resetting_) {
		super(id_, name_, description_, icon_, hidden_, resetting_);
		
		this.currentState = start_;
		this.allStates = new HashMap<Integer, AchievementState>();
	}
	
	@Override
	public void update(AchievementController controller, GameState gs, long cTime) {
		List<Long> metrics = controller.getMetrics();
		for(long metric : metrics) {
			AchievementState next = currentState.checkTransitions(metric);
			if(currentState != next) currentState = next;
		}
		
		if(isEarned()) onComplete(controller, cTime);
	}

	@Override
	public boolean isEarned() { return (complete || currentState.isEndState()); }
	
	@Override
	public String saveFormat() {
		StringBuilder builder = new StringBuilder();
		builder.append(id);
		Iterator<Map.Entry<Integer, AchievementState>> it = allStates.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry<Integer, AchievementState> entry = it.next();
			int id = entry.getKey();
			AchievementState state = entry.getValue();
			
			builder.append(" ").append("state(")
							   .append(id).append(",")
							   .append(currentState.getID())
							   .append(state.formatTransitions())
							   .append(")");
		}
		
		return builder.toString();
	}
	
	@Override
	public void parseSaveData(String [] tokens) {
		Pattern pattern = Pattern.compile("state\\((\\d+),(\\d+),?([a-zA-Z0-9\\(\\),]+)?\\)");
		for(String token : tokens) {
			Matcher matcher = pattern.matcher(token);
			try {
				int id = Integer.parseInt(matcher.group(1));
				int cState = Integer.parseInt(matcher.group(2));
				String body = matcher.group(3);
				
				currentState = allStates.get(cState);
				if((body != null) && !body.isEmpty()) {
					AchievementState state = allStates.get(id);
					if(state != null) {
						state.parseSaveData(body);
					} else {
						System.err.println("Malformed achievement state format! Aborting...");
						return;
					}
				}
			} catch(NumberFormatException nfe) {
				System.err.println("Malformed achievement state! Aborting...");
				nfe.printStackTrace();
				return;
			}
		}
	}
}
