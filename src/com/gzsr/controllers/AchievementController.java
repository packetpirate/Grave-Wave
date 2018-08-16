package com.gzsr.controllers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.gzsr.achievements.Achievement;
import com.gzsr.achievements.AchievementBroadcast;
import com.gzsr.achievements.IAchievement;
import com.gzsr.achievements.milestone.MilestoneAchievement;
import com.gzsr.states.GameState;

/**
 * Tracks metrics involved with achievement progress so that managed achievements can query
 * this information and determine when an achievement has been earned.
 * @author packetpirate
 *
 */
public class AchievementController {
	private static AchievementController instance;
	public static AchievementController getInstance() {
		if(instance == null) instance = new AchievementController();
		return instance;
	}
	
	private List<String> metrics;
	public List<String> getMetrics() { return metrics; }
	public void postMetric(String name) { metrics.add(name); }
	
	private List<IAchievement> achievements;
	public List<IAchievement> getAchievements() { return achievements; }
	public void registerAchievement(Achievement achievement) { achievements.add(achievement); }
	
	private List<AchievementBroadcast> broadcasts;
	public List<AchievementBroadcast> getBroadcasts() { return broadcasts; }
	public void broadcast(Achievement achievement, long cTime) { broadcasts.add(new AchievementBroadcast(achievement, cTime)); }
	
	private AchievementController() {
		metrics = new ArrayList<String>();
		achievements = new ArrayList<IAchievement>();
		broadcasts = new ArrayList<AchievementBroadcast>();
	}
	
	public void update(GameState gs, long cTime, int delta) {
		achievements.stream().filter(ac -> !ac.isEarned()).forEach(ac -> ac.update(this, gs, cTime));
		metrics.clear();
		
		// Remove inactive broadcasts.
		Iterator<AchievementBroadcast> it = broadcasts.iterator();
		while(it.hasNext()) {
			AchievementBroadcast broadcast = it.next();
			if(!broadcast.isActive(cTime)) it.remove();
		}
	}
	
	// TODO: Perhaps in the future, achievements could be loaded from XML. For now, this will do.
	public void init() {
		// Initializes all achievements.
		MilestoneAchievement enemies500 = new MilestoneAchievement("500 Kills", "Killed 500 enemies of any kind.", "")
				.addMilestone("enemyKilled", 500);
		achievements.add(enemies500);
		
		MilestoneAchievement enemies1000 = new MilestoneAchievement("1,000 Kills", "Killed 1,000 enemies of any kind.", "")
				.addMilestone("enemyKilled", 1_000);
		achievements.add(enemies1000);
		
		MilestoneAchievement aberrationKilled = new MilestoneAchievement("Aberration", "Defeated the Aberration for the first time.", "")
				.addMilestone("aberrationKilled", 1);
		achievements.add(aberrationKilled);
		
		MilestoneAchievement stitchesKilled = new MilestoneAchievement("Stitches", "Defeated Stitches for the first time.", "")
				.addMilestone("stitchesKilled", 1);
		achievements.add(stitchesKilled);
		
		MilestoneAchievement zombatsKilled = new MilestoneAchievement("Zombats", "Defeated the Zombats for the first time.", "")
				.addMilestone("zombatsKilled", 3);
		achievements.add(zombatsKilled);
	}
}
