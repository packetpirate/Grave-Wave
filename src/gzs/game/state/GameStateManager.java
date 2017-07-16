package gzs.game.state;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameStateManager {
	private GameState state;
	public GameState getState() { return state; }
	public void setState(GameState nextState) { state = nextState; }
	
	/**
	 * Checks to see if there is a valid transition from the current state given the condition.
	 * @param condition The transition condition.
	 * @throws GameStateException Thrown if there are no valid transitions or if the current state does not exist in the transitions table.
	 */
	public void transition(String condition) throws GameStateException {
		if(condition.equals("exception")) {
			state = GameState.ERROR;
			return;
		}
		
		if(TRANSITIONS.containsKey(state)) {
			List<Transition> transitions = TRANSITIONS.get(state);
			for(Transition t : transitions) {
				GameState end = t.checkTransition(condition);
				if(end != state) {
					state = end;
					return;
				}
			}
			
			throw new GameStateException("No valid transition found!");
		}
		
		throw new GameStateException("Current state not in transitions table!");
	}
	
	public GameStateManager() {
		state = GameState.INITIALIZE;
	}
	
	private static Map<GameState, List<Transition>> TRANSITIONS = new HashMap<>();
	static {
		List<Transition> init = new ArrayList<Transition>() {{
			add(new Transition(GameState.INITIALIZE, "initialized", GameState.MENU));
		}};
		TRANSITIONS.put(GameState.INITIALIZE, init);
		
		List<Transition> menu = new ArrayList<Transition>() {{
			add(new Transition(GameState.MENU, "credits", GameState.CREDITS));
			add(new Transition(GameState.MENU, "start game", GameState.GAME));
			add(new Transition(GameState.MENU, "quit", GameState.QUIT));
		}};
		TRANSITIONS.put(GameState.MENU, menu);
		
		List<Transition> game = new ArrayList<Transition>() {{
			add(new Transition(GameState.GAME, "pause", GameState.PAUSE));
			add(new Transition(GameState.GAME, "shop", GameState.SHOP));
			add(new Transition(GameState.GAME, "training", GameState.TRAIN));
			add(new Transition(GameState.GAME, "die", GameState.DEATH));
		}};
		TRANSITIONS.put(GameState.GAME, game);
		
		List<Transition> pause = new ArrayList<Transition>() {{
			add(new Transition(GameState.PAUSE, "pause", GameState.GAME));
		}};
		TRANSITIONS.put(GameState.PAUSE, pause);
		
		List<Transition> shop = new ArrayList<Transition>() {{
			add(new Transition(GameState.SHOP, "shop", GameState.GAME));
		}};
		TRANSITIONS.put(GameState.SHOP, shop);
		
		List<Transition> train = new ArrayList<Transition>() {{
			add(new Transition(GameState.TRAIN, "training", GameState.GAME));
		}};
		TRANSITIONS.put(GameState.TRAIN, train);
		
		List<Transition> death = new ArrayList<Transition>() {{
			add(new Transition(GameState.DEATH, "game over", GameState.GAMEOVER));
		}};
		TRANSITIONS.put(GameState.DEATH, death);
		
		List<Transition> gameover = new ArrayList<Transition>() {{
			add(new Transition(GameState.GAMEOVER, "end game", GameState.MENU));
		}};
		TRANSITIONS.put(GameState.GAMEOVER, gameover);
	}
}
