package com.gzsr.achievements.milestone;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gzsr.achievements.Achievement;
import com.gzsr.controllers.AchievementController;
import com.gzsr.misc.Pair;
import com.gzsr.states.GameState;

public class MilestoneAchievement extends Achievement {
	private Map<Long, Pair<Integer>> milestones;
	public Map<Long, Pair<Integer>> getMilestones() { return milestones; }
	
	private Map<Long, String> descriptors;
	public Map<Long, String> getDescriptors() { return descriptors; }
	
	public MilestoneAchievement(int id_, String name_, String description_, String icon_) {
		this(id_, name_, description_, icon_, false);
	}
	
	public MilestoneAchievement(int id_, String name_, String description_, String icon_, boolean hidden_) {
		super(id_, name_, description_, icon_, hidden_);
		
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
	
	@Override
	public String saveFormat() {
		StringBuilder builder = new StringBuilder();
		
		builder.append(id);
		
		Iterator<Map.Entry<Long, Pair<Integer>>> it = milestones.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry<Long, Pair<Integer>> entry = it.next();
			long metric = entry.getKey();
			Pair<Integer> progress = entry.getValue();
			
			String text = String.format("milestone(%d,%d,%d)", metric, progress.x, progress.y);
			builder.append(" ").append(text);
		}
		
		return builder.toString();
	}
	
	@Override
	public void parseSaveData(String [] tokens) {
		Pattern pattern = Pattern.compile("milestone\\((\\d+),(\\d+),(\\d+)\\)");
		for(String token : tokens) {
			Matcher matcher = pattern.matcher(token);
			if(matcher.matches()) {
				try {
					long metric = Long.parseLong(matcher.group(1));
					int x = Integer.parseInt(matcher.group(2));
					int y = Integer.parseInt(matcher.group(3));
					
					if(milestones.containsKey(metric)) {
						Pair<Integer> progress = milestones.get(metric);
						progress.x = x;
						progress.y = y;
					}
				} catch(NumberFormatException nfe) {
					System.err.println("Malformed milestone achievement data! Aborting...");
					nfe.printStackTrace();
					return;
				}
			}
		}
	}
}
