package com.gzsr.controllers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.gzsr.achievements.Achievement;
import com.gzsr.achievements.AchievementBroadcast;
import com.gzsr.achievements.IAchievement;
import com.gzsr.achievements.Metrics;
import com.gzsr.achievements.milestone.MilestoneAchievement;
import com.gzsr.achievements.state.AchievementState;
import com.gzsr.achievements.state.StateBasedAchievement;
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
	
	private List<Long> metrics;
	public List<Long> getMetrics() { return metrics; }
	public void postMetric(long metric) { metrics.add(metric); }
	
	private List<IAchievement> achievements;
	public List<IAchievement> getAchievements() { return achievements; }
	public void registerAchievement(Achievement achievement) { achievements.add(achievement); }
	
	private List<AchievementBroadcast> broadcasts;
	public List<AchievementBroadcast> getBroadcasts() { return broadcasts; }
	public void broadcast(Achievement achievement, long cTime) { broadcasts.add(new AchievementBroadcast(achievement, cTime)); }
	
	private AchievementController() {
		metrics = new ArrayList<Long>();
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
		
		{ // Milestone achievements.
			MilestoneAchievement enemies500 = new MilestoneAchievement("500 Kills", "Killed 500 enemies of any kind.", "")
					.addMilestone(Metrics.ENEMY_KILL, 500);
			achievements.add(enemies500);
			
			MilestoneAchievement enemies1000 = new MilestoneAchievement("1,000 Kills", "Killed 1,000 enemies of any kind.", "")
					.addMilestone(Metrics.ENEMY_KILL, 1_000);
			achievements.add(enemies1000);
			
			MilestoneAchievement aberrationKilled = new MilestoneAchievement("Aberration", "Defeated the Aberration for the first time.", "")
					.addMilestone(Metrics.compose(Metrics.ABERRATION, Metrics.ENEMY_KILL), 1);
			achievements.add(aberrationKilled);
			
			MilestoneAchievement stitchesKilled = new MilestoneAchievement("Stitches", "Defeated Stitches for the first time.", "")
					.addMilestone(Metrics.compose(Metrics.STITCHES, Metrics.ENEMY_KILL), 1);
			achievements.add(stitchesKilled);
			
			MilestoneAchievement zombatsKilled = new MilestoneAchievement("Zombats", "Defeated the Zombats for the first time.", "")
					.addMilestone(Metrics.compose(Metrics.ZOMBAT, Metrics.ENEMY_KILL), 3);
			achievements.add(zombatsKilled);
		}
		
		{ // Kill Aberration with just the Beretta M9.
			AchievementState s0 = new AchievementState(false);
			AchievementState s1 = new AchievementState(false);
			AchievementState s2 = new AchievementState(true);
			
			s0.createTransition(Metrics.compose(Metrics.ABERRATION, Metrics.WAVE_START), s1);
			s1.createTransition(Metrics.compose(Metrics.ABERRATION, Metrics.ENEMY_KILL), s2);
			s1.createTransition(s0, (metric -> ((((metric & Metrics.ABERRATION) == 1) && (metric & Metrics.ENEMY_DAMAGE) == 1) && ((metric & Metrics.BERETTA) == 0))));
			
			StateBasedAchievement aberrationWithBeretta = new StateBasedAchievement("Absolute Madman", "Killed the Aberration with just the Beretta M9.", "", s0);
			achievements.add(aberrationWithBeretta);
		}
	}
}
