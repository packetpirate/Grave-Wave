package com.gzsr.controllers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.gzsr.achievements.Achievement;
import com.gzsr.achievements.AchievementBroadcast;
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
	
	private List<Achievement> achievements;
	public List<Achievement> getAchievements() { return achievements; }
	public void registerAchievement(Achievement achievement) { achievements.add(achievement); }
	
	private List<AchievementBroadcast> broadcasts;
	public List<AchievementBroadcast> getBroadcasts() { return broadcasts; }
	public void broadcast(Achievement achievement, long cTime) { broadcasts.add(new AchievementBroadcast(achievement, cTime)); }
	
	private AchievementController() {
		metrics = new ArrayList<Long>();
		achievements = new ArrayList<Achievement>();
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
			MilestoneAchievement enemies100 = new MilestoneAchievement("100 Kills", "Killed 100 enemies of any kind.", "")
					.addMilestone("Enemies Killed", Metrics.compose(Metrics.ENEMY, Metrics.KILL), 100);
			achievements.add(enemies100);
			
			MilestoneAchievement enemies500 = new MilestoneAchievement("500 Kills", "Killed 500 enemies of any kind.", "")
					.addMilestone("Enemies Killed", Metrics.compose(Metrics.ENEMY, Metrics.KILL), 500);
			achievements.add(enemies500);
			
			MilestoneAchievement enemies1000 = new MilestoneAchievement("1,000 Kills", "Killed 1,000 enemies of any kind.", "")
					.addMilestone("Enemies Killed", Metrics.compose(Metrics.ENEMY, Metrics.KILL), 1_000);
			achievements.add(enemies1000);
			
			MilestoneAchievement aberrationKilled = new MilestoneAchievement("Aberration", "Defeated the Aberration for the first time.", "")
					.addMilestone("Aberration Killed", Metrics.compose(Metrics.ABERRATION, Metrics.ENEMY, Metrics.KILL), 1);
			achievements.add(aberrationKilled);
			
			MilestoneAchievement stitchesKilled = new MilestoneAchievement("Stitches", "Defeated Stitches for the first time.", "")
					.addMilestone("Stitches Killed", Metrics.compose(Metrics.STITCHES, Metrics.ENEMY, Metrics.KILL), 1);
			achievements.add(stitchesKilled);
			
			MilestoneAchievement zombatsKilled = new MilestoneAchievement("Zombats", "Defeated the Zombats for the first time.", "")
					.addMilestone("Zombats Killed", Metrics.compose(Metrics.ZOMBAT, Metrics.ENEMY, Metrics.KILL), 3);
			achievements.add(zombatsKilled);
			
			MilestoneAchievement dontTazeMe = new MilestoneAchievement("Don't Taze Me, Bro!", "Paralyze 100 enemies with the Taser.", "", true)
					.addMilestone("Enemies Tased", Metrics.compose(Metrics.TASER, Metrics.ENEMY, Metrics.DAMAGE), 100);
			achievements.add(dontTazeMe);
		}
		
		{ // Kill Aberration with just the Nail Gun.
			AchievementState s0 = new AchievementState(false);
			AchievementState s1 = new AchievementState(false);
			AchievementState s2 = new AchievementState(true);
			
			s0.createTransition(Metrics.compose(Metrics.ABERRATION, Metrics.WAVE_START), s1);
			s1.createTransition(Metrics.compose(Metrics.ABERRATION, Metrics.ENEMY, Metrics.KILL), s2);
			s1.createTransition(s0, (metric -> (((Metrics.has(metric, Metrics.ABERRATION) && Metrics.has(metric, Metrics.compose(Metrics.ENEMY, Metrics.DAMAGE)) && !Metrics.has(metric, Metrics.NAIL_GUN))))));
			
			StateBasedAchievement absoluteMadman = new StateBasedAchievement("Absolute Madman", "Killed the Aberration using only the Nail Gun.", "", s0);
			achievements.add(absoluteMadman);
		}
		
		{ // Take no damage during Aberration fight.
			AchievementState s0 = new AchievementState(false);
			AchievementState s1 = new AchievementState(false);
			AchievementState s2 = new AchievementState(true);
			
			s0.createTransition(Metrics.compose(Metrics.ABERRATION, Metrics.WAVE_START), s1);
			s1.createTransition(Metrics.compose(Metrics.PLAYER, Metrics.DAMAGE), s0);
			s1.createTransition(Metrics.compose(Metrics.ABERRATION, Metrics.ENEMY, Metrics.KILL), s2);
			
			StateBasedAchievement tentacleWrangler = new StateBasedAchievement("Tentacle Wrangler", "Killed the Aberration without taking damage.", "", s0);
			achievements.add(tentacleWrangler);
		}
		
		{ // Take no damage during Stitches fight.
			AchievementState s0 = new AchievementState(false);
			AchievementState s1 = new AchievementState(false);
			AchievementState s2 = new AchievementState(true);
			
			s0.createTransition(Metrics.compose(Metrics.STITCHES, Metrics.WAVE_START), s1);
			s1.createTransition(Metrics.compose(Metrics.PLAYER, Metrics.DAMAGE), s0);
			s1.createTransition(Metrics.compose(Metrics.STITCHES, Metrics.ENEMY, Metrics.KILL), s2);
			
			StateBasedAchievement offTheHook = new StateBasedAchievement("Off The Hook", "Killed Stitches without taking damage.", "", s0);
			achievements.add(offTheHook);
		}
		
		{ // Take no damage during Zombat fight.
			AchievementState s0 = new AchievementState(false);
			AchievementState s1 = new AchievementState(false);
			AchievementState s2 = new AchievementState(false);
			AchievementState s3 = new AchievementState(false);
			AchievementState s4 = new AchievementState(true);
			
			s0.createTransition(Metrics.compose(Metrics.ZOMBAT, Metrics.WAVE_START), s1);
			
			s1.createTransition(Metrics.compose(Metrics.PLAYER, Metrics.DAMAGE), s0);
			s1.createTransition(Metrics.compose(Metrics.ZOMBAT, Metrics.ENEMY, Metrics.KILL), s2);
			
			s2.createTransition(Metrics.compose(Metrics.PLAYER, Metrics.DAMAGE), s0);
			s2.createTransition(Metrics.compose(Metrics.ZOMBAT, Metrics.ENEMY, Metrics.KILL), s3);
			
			s3.createTransition(Metrics.compose(Metrics.PLAYER, Metrics.DAMAGE), s0);
			s3.createTransition(Metrics.compose(Metrics.ZOMBAT, Metrics.ENEMY, Metrics.KILL), s4);
			
			StateBasedAchievement blindAsABat = new StateBasedAchievement("Blind As A Bat", "Killed the Zombats without taking damage.", "", s0);
			achievements.add(blindAsABat);
		}
	}
}
