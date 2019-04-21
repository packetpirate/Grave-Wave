package com.gzsr.controllers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.gzsr.achievements.Achievement;
import com.gzsr.achievements.AchievementBroadcast;
import com.gzsr.achievements.Metric;
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

	private List<Metric> metrics;
	public List<Metric> getMetrics() { return metrics; }
	public void postMetric(Metrics metric) { metrics.add(new Metric(metric)); }
	public void postMetric(Metric metric) { metrics.add(metric); }

	private List<Achievement> achievements;
	public List<Achievement> getAchievements() { return achievements; }
	public void registerAchievement(Achievement achievement) { achievements.add(achievement); }

	private List<AchievementBroadcast> broadcasts;
	public List<AchievementBroadcast> getBroadcasts() { return broadcasts; }
	public void broadcast(Achievement achievement, long cTime) { broadcasts.add(new AchievementBroadcast(achievement, cTime)); }

	private AchievementController() {
		metrics = new ArrayList<Metric>();
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
			MilestoneAchievement enemies100 = new MilestoneAchievement(1, "100 Kills", "Killed 100 enemies of any kind.", "")
					.addMilestone("Enemies Killed", Metrics.compose(Metrics.ENEMY, Metrics.KILL), 100);
			achievements.add(enemies100);

			MilestoneAchievement enemies500 = new MilestoneAchievement(2, "500 Kills", "Killed 500 enemies of any kind.", "")
					.addMilestone("Enemies Killed", Metrics.compose(Metrics.ENEMY, Metrics.KILL), 500);
			achievements.add(enemies500);

			MilestoneAchievement enemies1000 = new MilestoneAchievement(3, "1,000 Kills", "Killed 1,000 enemies of any kind.", "")
					.addMilestone("Enemies Killed", Metrics.compose(Metrics.ENEMY, Metrics.KILL), 1_000);
			achievements.add(enemies1000);

			MilestoneAchievement aberrationKilled = new MilestoneAchievement(4, "Aberration", "Defeated the Aberration for the first time.", "")
					.addMilestone("Aberration Killed", Metrics.compose(Metrics.ABERRATION, Metrics.ENEMY, Metrics.KILL), 1);
			achievements.add(aberrationKilled);

			MilestoneAchievement stitchesKilled = new MilestoneAchievement(5, "Stitches", "Defeated Stitches for the first time.", "")
					.addMilestone("Stitches Killed", Metrics.compose(Metrics.STITCHES, Metrics.ENEMY, Metrics.KILL), 1);
			achievements.add(stitchesKilled);

			MilestoneAchievement zombatsKilled = new MilestoneAchievement(6, "Zombats", "Defeated the Zombats for the first time.", "")
					.addMilestone("Zombats Killed", Metrics.compose(Metrics.ZOMBAT, Metrics.ENEMY, Metrics.KILL), 3);
			achievements.add(zombatsKilled);

			MilestoneAchievement dontTazeMe = new MilestoneAchievement(7, "Don't Taze Me, Bro!", "Paralyze 100 enemies with the Taser.", "", true)
					.addMilestone("Enemies Tased", Metrics.compose(Metrics.TASER, Metrics.ENEMY, Metrics.DAMAGE), 100);
			achievements.add(dontTazeMe);
		}

		{ // Kill an enemy with the Lollipop.
			AchievementState s0 = new AchievementState(0, false);
			AchievementState s1 = new AchievementState(1, false);
			AchievementState s2 = new AchievementState(2, true);

			s0.createTransition(1, s1, (metric -> metric.has(Metrics.LOLLIPOP, Metrics.ENEMY, Metrics.DAMAGE)));
			s1.createBranch(2, Metrics.compose(Metrics.ENEMY, Metrics.KILL), s2, s0);

			StateBasedAchievement timeToParty = new StateBasedAchievement(8, "It's Time To Party!", "Kill an enemy with the Lollipop.", "", s0, true, true);
			timeToParty.saveStates(s0, s1, s2);
			achievements.add(timeToParty);
		}

		{ // Kill Aberration with just the Nail Gun.
			AchievementState s0 = new AchievementState(0, false);
			AchievementState s1 = new AchievementState(1, false);
			AchievementState s2 = new AchievementState(2, true);

			s0.createTransition(1, Metrics.compose(Metrics.ABERRATION, Metrics.WAVE_START), s1);
			s1.createTransition(2, Metrics.compose(Metrics.ABERRATION, Metrics.ENEMY, Metrics.KILL), s2);
			s1.createTransition(3, s0, (metric -> (metric.has(Metrics.ABERRATION, Metrics.ENEMY, Metrics.DAMAGE)) && !metric.has(Metrics.NAIL_GUN)));

			StateBasedAchievement absoluteMadman = new StateBasedAchievement(9, "Absolute Madman", "Killed the Aberration using only the Nail Gun.", "", s0, false, true);
			absoluteMadman.saveStates(s0, s1, s2);
			achievements.add(absoluteMadman);
		}

		{ // Take no damage during Aberration fight.
			AchievementState s0 = new AchievementState(0, false);
			AchievementState s1 = new AchievementState(1, false);
			AchievementState s2 = new AchievementState(2, true);

			s0.createTransition(1, Metrics.compose(Metrics.ABERRATION, Metrics.WAVE_START), s1);
			s1.createTransition(2, Metrics.compose(Metrics.PLAYER, Metrics.DAMAGE), s0);
			s1.createTransition(3, Metrics.compose(Metrics.ABERRATION, Metrics.ENEMY, Metrics.KILL), s2);

			StateBasedAchievement tentacleWrangler = new StateBasedAchievement(10, "Tentacle Wrangler", "Killed the Aberration without taking damage.", "", s0, false, true);
			tentacleWrangler.saveStates(s0, s1, s2);
			achievements.add(tentacleWrangler);
		}

		{ // Take no damage during Stitches fight.
			AchievementState s0 = new AchievementState(0, false);
			AchievementState s1 = new AchievementState(1, false);
			AchievementState s2 = new AchievementState(2, true);

			s0.createTransition(1, Metrics.compose(Metrics.STITCHES, Metrics.WAVE_START), s1);
			s1.createTransition(2, Metrics.compose(Metrics.PLAYER, Metrics.DAMAGE), s0);
			s1.createTransition(3, Metrics.compose(Metrics.STITCHES, Metrics.ENEMY, Metrics.KILL), s2);

			StateBasedAchievement offTheHook = new StateBasedAchievement(11, "Off The Hook", "Killed Stitches without taking damage.", "", s0, false, true);
			offTheHook.saveStates(s0, s1, s2);
			achievements.add(offTheHook);
		}

		{ // Take no damage during Zombat fight.
			AchievementState s0 = new AchievementState(0, false);
			AchievementState s1 = new AchievementState(1, false);
			AchievementState s2 = new AchievementState(2, false);
			AchievementState s3 = new AchievementState(3, false);
			AchievementState s4 = new AchievementState(4, true);

			s0.createTransition(1, Metrics.compose(Metrics.ZOMBAT, Metrics.WAVE_START), s1);

			s1.createTransition(2, Metrics.compose(Metrics.PLAYER, Metrics.DAMAGE), s0);
			s1.createTransition(3, Metrics.compose(Metrics.ZOMBAT, Metrics.ENEMY, Metrics.KILL), s2);

			s2.createTransition(4, Metrics.compose(Metrics.PLAYER, Metrics.DAMAGE), s0);
			s2.createTransition(5, Metrics.compose(Metrics.ZOMBAT, Metrics.ENEMY, Metrics.KILL), s3);

			s3.createTransition(6, Metrics.compose(Metrics.PLAYER, Metrics.DAMAGE), s0);
			s3.createTransition(7, Metrics.compose(Metrics.ZOMBAT, Metrics.ENEMY, Metrics.KILL), s4);

			StateBasedAchievement blindAsABat = new StateBasedAchievement(12, "Blind As A Bat", "Killed the Zombats without taking damage.", "", s0, false, true);
			blindAsABat.saveStates(s0, s1, s2, s3, s4);
			achievements.add(blindAsABat);
		}

		{ // Stop Breaking Things! - Reach wave 30 using only the Pistol.

		}
	}
}
