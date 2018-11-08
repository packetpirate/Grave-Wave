package com.gzsr.achievements.state;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class AchievementState {
	class Transition {
		protected long metric;
		protected int occurrences;
		protected int count;
		
		protected AchievementState destination;
		public AchievementState getDestination() { return destination; }
		
		protected Function<Long, Boolean> condition;
		
		public Transition(long metric_, AchievementState destination_) {
			this.metric = metric_;
			this.occurrences = 1;
			this.count = 0;
			this.destination = destination_;
			
			this.condition = null;
		}
		
		public Transition(long metric_, int occurrences_, AchievementState destination_) {
			this.metric = metric_;
			this.occurrences = occurrences_;
			this.count = 0;
			
			this.destination = destination_;
			
			this.condition = null;
		}
		
		public Transition(AchievementState destination_, Function<Long, Boolean> condition_) {
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
	}
	
	class BranchTransition extends Transition {
		private AchievementState other;
		
		@Override
		public AchievementState getDestination() {
			if(occurrences >= count) return destination;
			else return other;
		}
		
		public BranchTransition(long metric_, AchievementState destination_, AchievementState other_) {
			super(metric_, destination_);
			this.other = other_;
		}
		
		public BranchTransition(long metric_, int occurrences_, AchievementState destination_, AchievementState other_) {
			super(metric_, occurrences_, destination_);
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
	
	private List<Transition> transitions;
	
	private boolean endState;
	public boolean isEndState() { return endState; }
	
	public AchievementState(boolean endState_) {
		this.transitions = new ArrayList<Transition>();
		this.endState = endState_;
	}
	
	public Transition createTransition(long metric, AchievementState state) {
		Transition transition = new Transition(metric, state);
		transitions.add(transition);
		return transition;
	}
	
	public Transition createTransition(long metric, int occurrences, AchievementState state) {
		Transition transition = new Transition(metric, occurrences, state);
		transitions.add(transition);
		return transition;
	}
	
	public Transition createTransition(AchievementState state, Function<Long, Boolean> condition) {
		Transition transition = new Transition(state, condition);
		transitions.add(transition);
		return transition;
	}
	
	public BranchTransition createBranch(long metric, AchievementState dest, AchievementState other) {
		BranchTransition transition = new BranchTransition(metric, dest, other);
		transitions.add(transition);
		return transition;
	}
	
	public BranchTransition createBranch(long metric, int occurrences, AchievementState dest, AchievementState other) {
		BranchTransition transition = new BranchTransition(metric, occurrences, dest, other);
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
}
