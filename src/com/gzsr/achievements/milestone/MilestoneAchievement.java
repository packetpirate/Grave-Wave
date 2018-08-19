package com.gzsr.achievements.milestone;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gzsr.achievements.Achievement;
import com.gzsr.controllers.AchievementController;
import com.gzsr.misc.Pair;
import com.gzsr.states.GameState;

public class MilestoneAchievement extends Achievement {
	private Map<Long, Pair<Integer>> milestones;
	public Map<Long, Pair<Integer>> getMilestones() { return milestones; }
	
	private Map<Long, String> descriptors;
	public Map<Long, String> getDescriptors() { return descriptors; }
	
	public MilestoneAchievement(String name_, String description_, String icon_) {
		this(name_, description_, icon_, false);
	}
	
	public MilestoneAchievement(String name_, String description_, String icon_, boolean hidden_) {
		super(name_, description_, icon_, hidden_);
		
		milestones = new HashMap<Long, Pair<Integer>>();
		descriptors = new HashMap<Long, String>();
	}
	
	@Override
	public void update(AchievementController controller, GameState gs, long cTime) {
		List<Long> metrics = controller.getMetrics();
		
		if(!metrics.isEmpty()) {
			for(Map.Entry<Long, Pair<Integer>> entry : milestones.entrySet()) {
				long metric = entry.getKey();
				Pair<Integer> milestone = entry.getValue();
				
				for(long m : metrics) {
					if((metric & m) == metric) milestone.x += 1;
				}
			}
		}
		
		if(isEarned()) onComplete(controller, cTime);
	}
	
	public MilestoneAchievement addMilestone(String name, long metric, int target) {
		milestones.put(metric, new Pair<Integer>(0, target));
		descriptors.put(metric, name);
		return this;
	}
	
	@Override
	public boolean isEarned() {
		if(complete) return true;
		if(milestones.isEmpty()) return false;
		
		boolean earned = true;
		
		for(Pair<Integer> pair : milestones.values()) {
			earned = (earned && (pair.x >= pair.y));
		}
		
		return earned;
	}
}
