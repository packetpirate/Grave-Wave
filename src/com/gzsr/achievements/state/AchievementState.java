package com.gzsr.achievements.state;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class AchievementState {
	class Transition {
		private long metric;
		
		private AchievementState destination;
		public AchievementState getDestination() { return destination; }
		
		private Function<Long, Boolean> condition;
		
		public Transition(long metric_, AchievementState destination_) {
			this.metric = metric_;
			this.destination = destination_;
			
			this.condition = null;
		}
		
		public Transition(AchievementState destination_, Function<Long, Boolean> condition_) {
			this.destination = destination_;
			this.condition = condition_;
			
			this.metric = 0L;
		}
		
		/**
		 * Tells the current state whether to transition to this state based on the given metric.
		 * @return True if the state should transition to this one. False if not.
		 */
		public boolean checkCondition(long m) {
			if(condition != null) return condition.apply(m);
			else return (m == metric);
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
	
	public Transition createTransition(AchievementState state, Function<Long, Boolean> condition) {
		Transition transition = new Transition(state, condition);
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
