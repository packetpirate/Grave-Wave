package gzs.game.info;

import gzs.game.state.GameStateManager;

public class Globals {
	// Contains global constants.
	public static final String VERSION = "0.1";
	
	public static final long SLEEP_MS = 20L;
	public static final long NANO_TO_MS = 1_000_000L;
	public static final double UPDATE_TIME = 0.016667;
	
	public static double WIDTH = 800;
	public static double HEIGHT = 640;
	
	private static GameStateManager GSM = new GameStateManager();
	public static GameStateManager getGSM() { return Globals.GSM; };
}
