package gzs.game.state;

public class Transition {
	private GameState start, end;
	private String condition;
	
	public Transition(GameState st_, String cond_, GameState ed_) {
		this.start = st_;
		this.condition = cond_;
		this.end = ed_;
	}
	
	/**
	 * Returns the GameState resulting from a transition from the provided condition.
	 * If the condition provided matches the required condition for the transition,
	 * this method returns the end state of this transition. If they do not match,
	 * the start state is returned (essentially, nothing happens).
	 * @param cond_ The condition to check against the required transition condition.
	 * @return The resulting GameState.
	 */
	public GameState checkTransition(String cond_) {
		return (condition.equals(cond_))?end:start;
	}
}
