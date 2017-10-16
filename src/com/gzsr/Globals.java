package com.gzsr;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

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
	
	public static Set<String> inputs = new HashSet<String>();
	public static MouseInfo mouse = new MouseInfo();
	
	public static Random rand = new Random();
	
	public static Player player = null;
}
