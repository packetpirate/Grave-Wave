package com.gzsr.achievements.state;

import java.util.List;

import com.gzsr.achievements.Achievement;
import com.gzsr.controllers.AchievementController;
import com.gzsr.states.GameState;

public class StateBasedAchievement extends Achievement {
	private AchievementState currentState;
	
	public StateBasedAchievement(String name_, String description_, String icon_, AchievementState start_) {
		this(name_, description_, icon_, start_, false);
	}
	
	public StateBasedAchievement(String name_, String description_, String icon_, AchievementState start_, boolean hidden_) {
		super(name_, description_, icon_, hidden_);
		
		this.currentState = start_;
	}
	
	@Override
	public void update(AchievementController controller, GameState gs, long cTime) {
		List<Long> metrics = controller.getMetrics();
		for(long metric : metrics) {
			AchievementState next = currentState.checkTransitions(metric);
			if(currentState != next) {
				currentState = next;
				break; // Prevents multiple transitions per update.
			}
		}
		
		if(isEarned()) onComplete(controller, cTime);
	}

	@Override
	public boolean isEarned() { return (complete || currentState.isEndState()); }
}
