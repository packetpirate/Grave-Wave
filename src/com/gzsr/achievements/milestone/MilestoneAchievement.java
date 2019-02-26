package com.gzsr.achievements.milestone;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gzsr.achievements.Achievement;
import com.gzsr.achievements.Metric;
import com.gzsr.controllers.AchievementController;
import com.gzsr.misc.Pair;
import com.gzsr.states.GameState;

public class MilestoneAchievement extends Achievement {
	private Map<Metric, Pair<Integer>> milestones;
	public Map<Metric, Pair<Integer>> getMilestones() { return milestones; }

	private Map<Metric, String> descriptors;
	public Map<Metric, String> getDescriptors() { return descriptors; }

	public MilestoneAchievement(int id_, String name_, String description_, String icon_) {
		this(id_, name_, description_, icon_, false);
	}

	public MilestoneAchievement(int id_, String name_, String description_, String icon_, boolean hidden_) {
		super(id_, name_, description_, icon_, hidden_);

		milestones = new HashMap<Metric, Pair<Integer>>();
		descriptors = new HashMap<Metric, String>();
	}

	@Override
	public void update(AchievementController controller, GameState gs, long cTime) {
		List<Metric> metrics = controller.getMetrics();

		if(!metrics.isEmpty()) {
			for(Map.Entry<Metric, Pair<Integer>> entry : milestones.entrySet()) {
				Metric metric = entry.getKey();
				Pair<Integer> milestone = entry.getValue();

				for(Metric m : metrics) {
					if(m.equals(metric)) milestone.x += 1;
				}
			}
		}

		if(isEarned()) onComplete(controller, cTime);
	}

	public MilestoneAchievement addMilestone(String name, Metric metric, int target) {
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

		Iterator<Map.Entry<Metric, Pair<Integer>>> it = milestones.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry<Metric, Pair<Integer>> entry = it.next();
			Metric metric = entry.getKey();
			Pair<Integer> progress = entry.getValue();

			// TODO: Correct formatting of Metric object so it's readable by MilestoneAchievement.parseSaveData().
			String text = String.format("milestone(%d,%d,%d)", metric, progress.x, progress.y);
			builder.append(" ").append(text);
		}

		return builder.toString();
	}

	@Override
	public void parseSaveData(String [] tokens) {
		// TODO: Correct parsing of Metric data.
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
