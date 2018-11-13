package com.gzsr.achievements.state;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AchievementState {
	class Transition {
		protected int id;
		public int getID() { return id; }
		protected long metric;
		protected int occurrences;
		protected int count;
		public int getCount() { return count; }
		
		protected AchievementState destination;
		public AchievementState getDestination() { return destination; }
		
		protected Function<Long, Boolean> condition;
		
		public Transition(int id_, long metric_, AchievementState destination_) {
			this.id = id_;
			this.metric = metric_;
			this.occurrences = 1;
			this.count = 0;
			this.destination = destination_;
			
			this.condition = null;
		}
		
		public Transition(int id_, long metric_, int occurrences_, AchievementState destination_) {
			this.id = id_;
			this.metric = metric_;
			this.occurrences = occurrences_;
			this.count = 0;
			
			this.destination = destination_;
			
			this.condition = null;
		}
		
		public Transition(int id_, AchievementState destination_, Function<Long, Boolean> condition_) {
			this.id = id_;
			this.destination = destination_;
			this.condition = condition_;
			
			this.metric = 0L;
			this.occurrences = 0;
			this.count = 0;
		}
		
		/**
		 * Tells the current state whether to transition to this state based on the given metric.
		 * @return True if the state should transition to this one. False if not.
		 */
		public boolean checkCondition(long m) {
			if(condition != null) return condition.apply(m);
			else if(m == metric) {
				count++;
				if(count >= occurrences) return true;
			}

			return false;
		}
		
		public String format() {
			return String.format("t(%d,%d,%d)", id, metric, count);
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
		
		public BranchTransition(int id_, long metric_, AchievementState destination_, AchievementState other_) {
			super(id_, metric_, destination_);
			this.other = other_;
		}
		
		public BranchTransition(int id_, long metric_, int occurrences_, AchievementState destination_, AchievementState other_) {
			super(id_, metric_, occurrences_, destination_);
			this.other = other_;
		}
		
		@Override
		public boolean checkCondition(long m) {
			if(condition != null) return condition.apply(m);
			else if(m == metric) {
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
	
	public Transition createTransition(int id, long metric, AchievementState state) {
		Transition transition = new Transition(id, metric, state);
		transitions.add(transition);
		return transition;
	}
	
	public Transition createTransition(int id, long metric, int occurrences, AchievementState state) {
		Transition transition = new Transition(id, metric, occurrences, state);
		transitions.add(transition);
		return transition;
	}
	
	public Transition createTransition(int id, AchievementState state, Function<Long, Boolean> condition) {
		Transition transition = new Transition(id, state, condition);
		transitions.add(transition);
		return transition;
	}
	
	public BranchTransition createBranch(int id, long metric, AchievementState dest, AchievementState other) {
		BranchTransition transition = new BranchTransition(id, metric, dest, other);
		transitions.add(transition);
		return transition;
	}
	
	public BranchTransition createBranch(int id, long metric, int occurrences, AchievementState dest, AchievementState other) {
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
	public AchievementState checkTransitions(long metric) {
		for(Transition transition : transitions) {
			if(transition.checkCondition(metric)) {
				return transition.getDestination();
			}
		}
		
		return this;
	}
	
	public String formatTransitions() {
		String format = ",";
		for(int i = 0; i < transitions.size(); i++) {
			Transition t = transitions.get(i);
			format += t.format();
			if(i < (transitions.size() - 1)) format += ",";
		}
		
		return format;
	}
	
	public void parseSaveData(String data) {
		Pattern pattern = Pattern.compile("t\\((\\d+),(\\d+),(\\d+)\\)");
		Matcher matcher = pattern.matcher(data);
		while(matcher.find()) {
			try {
				int id = Integer.parseInt(matcher.group(1));
				int count = Integer.parseInt(matcher.group(3));
				
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
