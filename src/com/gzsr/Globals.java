package com.gzsr;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.newdawn.slick.Input;

import com.gzsr.entities.Player;
import com.gzsr.misc.MouseInfo;

public class Globals {
	// Contains global constants.
	public static final String VERSION = "0.15";
	
	public static final long SLEEP_MS = 20L;
	public static final long NANO_TO_MS = 1_000_000L;
	public static final long UPDATE_TIME = 20;
	
	public static int WIDTH = 1024;
	public static int HEIGHT = 768;
	
	public static Set<Integer> inputs = new HashSet<Integer>();
	public static Set<Integer> released = new HashSet<Integer>();
	public static MouseInfo mouse = new MouseInfo();
	
	public static int [] keyCodes = new int[] { Input.KEY_Q, Input.KEY_W, Input.KEY_E, Input.KEY_R, Input.KEY_T, Input.KEY_Y, Input.KEY_U, Input.KEY_I, Input.KEY_O, Input.KEY_P,
			   								 	Input.KEY_A, Input.KEY_S, Input.KEY_D, Input.KEY_F, Input.KEY_G, Input.KEY_H, Input.KEY_J, Input.KEY_K, Input.KEY_L,
			   								 	Input.KEY_Z, Input.KEY_X, Input.KEY_C, Input.KEY_V, Input.KEY_B, Input.KEY_N, Input.KEY_M, Input.KEY_SPACE, Input.KEY_ENTER,
			   								 	Input.KEY_0, Input.KEY_1, Input.KEY_2, Input.KEY_3, Input.KEY_4, Input.KEY_5, Input.KEY_6, Input.KEY_7, Input.KEY_8, Input.KEY_9,
			   								 	Input.KEY_ESCAPE, Input.KEY_GRAVE };
	public static String [] keyNames = new String[] { "q", "w", "e", "r", "t", "y", "u", "i", "o", "p",
													  "a", "s", "d", "f", "g", "h", "j", "k", "l",
													  "z", "x", "c", "v", "b", "n", "m", "space", "enter",
													  "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
													  "escape", "tilde" };
	
	public static Random rand = new Random();
	
	public static Player player = null;
}
