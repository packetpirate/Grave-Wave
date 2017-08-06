package gzs.game.info;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import gzs.game.misc.MouseInfo;
import gzs.game.state.GameStateManager;

public class Globals {
	// Contains global constants.
	public static final String VERSION = "0.15";
	
	public static final long SLEEP_MS = 20L;
	public static final long NANO_TO_MS = 1_000_000L;
	public static final long UPDATE_TIME = 20;
	
	public static double WIDTH = 800;
	public static double HEIGHT = 640;
	
	private static GameStateManager GSM = new GameStateManager();
	public static GameStateManager getGSM() { return Globals.GSM; };
	
	public static Set<String> inputs = new HashSet<String>();
	public static MouseInfo mouse = new MouseInfo();
	
	public static Random rand = new Random();
}
