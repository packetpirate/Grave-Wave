package com.grave.achievements.state;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.grave.achievements.Metric;

public class AchievementState {
	class Transition {
		protected int id;
		public int getID() { return id; }
		protected Metric metric;
		protected int occurrences;
		protected int count;
		public int getCount() { return count; }

		protected AchievementState destination;
		public AchievementState getDestination() { return destination; }

		protected Function<Metric, Boolean> condition;

		public Transition(int id_, Metric metric_, AchievementState destination_) {
			this.id = id_;
			this.metric = metric_;
			this.occurrences = 1;
			this.count = 0;
			this.destination = destination_;

			this.condition = null;
		}

		public Transition(int id_, Metric metric_, int occurrences_, AchievementState destination_) {
			this.id = id_;
			this.metric = metric_;
			this.occurrences = occurrences_;
			this.count = 0;

			this.destination = destination_;

			this.condition = null;
		}

		public Transition(int id_, AchievementState destination_, Function<Metric, Boolean> condition_) {
			this.id = id_;
			this.destination = destination_;
			this.condition = condition_;

			this.metric = new Metric();
			this.occurrences = 0;
			this.count = 0;
		}

		/**
		 * Tells the current state whether to transition to this state based on the given metric.
		 * @return True if the state should transition to this one. False if not.
		 */
		public boolean checkCondition(Metric m) {
			if(condition != null) return condition.apply(m);
			else if(m.equals(metric)) {
				count++;
				if(count >= occurrences) return true;
			}

			return false;
		}

		public String format() {
			return String.format("t(%d,%d)", id, count);
		}

		public void parseSaveData(int count_) {
			this.count = count_;
		}
	}

	class BranchTransition extends Transition {
		private AchievementState other;

		@Override
		public AchievementState getDestination() {
			if(occurrences >= count) return destination;
			else return other;
		}

		public BranchTransition(int id_, Metric metric_, AchievementState destination_, AchievementState other_) {
			super(id_, metric_, destination_);
			this.other = other_;
		}

		public BranchTransition(int id_, Metric metric_, int occurrences_, AchievementState destination_, AchievementState other_) {
			super(id_, metric_, occurrences_, destination_);
			this.other = other_;
		}

		@Override
		public boolean checkCondition(Metric m) {
			if(condition != null) return condition.apply(m);
			else if(m.equals(metric)) {
				count++;

				// If we've reached the count, transition. Otherwise, don't because the count was increased.
				if(count >= occurrences) return true;
				else return false;
			}

			// Metric was not seen this update, so go back to the other state.
			return true;
		}
	}

	private int id;
	public int getID() { return id; }

	private List<Transition> transitions;

	private boolean endState;
	public boolean isEndState() { return endState; }

	public AchievementState(int id_, boolean endState_) {
		this.id = id_;
		this.transitions = new ArrayList<Transition>();
		this.endState = endState_;
	}

	public Transition createTransition(int id, Metric metric, AchievementState state) {
		Transition transition = new Transition(id, metric, state);
		transitions.add(transition);
		return transition;
	}

	public Transition createTransition(int id, Metric metric, int occurrences, AchievementState state) {
		Transition transition = new Transition(id, metric, occurrences, state);
		transitions.add(transition);
		return transition;
	}

	public Transition createTransition(int id, AchievementState state, Function<Metric, Boolean> condition) {
		Transition transition = new Transition(id, state, condition);
		transitions.add(transition);
		return transition;
	}

	public BranchTransition createBranch(int id, Metric metric, AchievementState dest, AchievementState other) {
		BranchTransition transition = new BranchTransition(id, metric, dest, other);
		transitions.add(transition);
		return transition;
	}

	public BranchTransition createBranch(int id, Metric metric, int occurrences, AchievementState dest, AchievementState other) {
		BranchTransition transition = new BranchTransition(id, metric, occurrences, dest, other);
		transitions.add(transition);
		return transition;
	}

	/**
	 * Returns the Achievement State to transition to based on the given metric.
	 * Checks each transition to see if any of the conditions match the metric. If so, that
	 * state is returned. If the metric doesn't match any transition states, returns the original state.
	 * @param metric The metric to check against all of this state's transitions.
	 * @return The state to transition to based on the metric, or this state if none match.
	 */
	public AchievementState checkTransitions(Metric metric) {
		for(Transition transition : transitions) {
			if(transition.checkCondition(metric)) {
				return transition.getDestination();
			}
		}

		return this;
	}

	public String formatTransitions() {
		if(!transitions.isEmpty()) {
			String format = ",";
			for(int i = 0; i < transitions.size(); i++) {
				Transition t = transitions.get(i);
				format += t.format();
				if(i < (transitions.size() - 1)) format += ",";
			}

			return format;
		}

		return "";
	}

	public void parseSaveData(String data) {
		Pattern pattern = Pattern.compile("t\\((\\d+),(\\d+)\\)");
		Matcher matcher = pattern.matcher(data);
		while(matcher.find()) {
			try {
				int id = Integer.parseInt(matcher.group(1));
				int count = Integer.parseInt(matcher.group(2));

				for(Transition t : transitions) {
					if(id == t.getID()) {
						t.parseSaveData(count);
						break;
					}
				}
			} catch(NumberFormatException nfe) {
				System.err.println("Malformed transition format! Aborting...");
				nfe.printStackTrace();
				return;
			}
		}
	}
}
