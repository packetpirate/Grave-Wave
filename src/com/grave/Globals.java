package com.grave;

import java.util.Random;

import org.newdawn.slick.AppGameContainer;

public class Globals {
	// Contains global constants.
	public static final String VERSION = "v1.5";

	public static final long SLEEP_MS = 20L;
	public static final long NANO_TO_MS = 1_000_000L;

	public static final int TARGET_FPS = 60;
	public static final int STEP_TIME = 1000 / TARGET_FPS;
	public static final int MAX_STEPS = 4;

	public static final boolean SHOW_COLLIDERS = true; // Disabled in player releases.
	public static final boolean ENABLE_CONSOLE = true; // TODO: Disable for player releases.

	public static int WIDTH = 1024;
	public static int HEIGHT = 768;

	public static AppGameContainer app = null;

	public static Random rand = new Random();

	public static boolean gameOver = false;
	public static boolean inGame = false; // Used to return to a different place from certain screens depending on whether the screen was accessed during gameplay.

	public static boolean debug = false;
	public static boolean firstTimeGamma = true; // If true, show the gamma settings screen at launch and then set this to false. Save in config file.

	private static int entityNum = 0;
	public static int generateEntityID() { return entityNum++; }
	public static void resetEntityNum() { entityNum = 0; }
}
