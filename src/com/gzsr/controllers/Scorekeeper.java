package com.gzsr.controllers;

public class Scorekeeper {
	private static Scorekeeper instance = null;
	public static Scorekeeper getInstance() {
		if(instance == null) instance = new Scorekeeper();
		return instance;
	}
	
	private int kills;
	public int getKillCount() { return kills; }
	public void addKill() { kills++; }
	
	private int shotsFired, shotsHit;
	public double getAccuracy() { return ((double)shotsFired / (double)shotsHit); }
	public void addShotFired() { shotsFired++; }
	public void addShotsFired(int amnt) { shotsFired += amnt; }
	public void addShotHit() { shotsHit++; }
	
	private int moneyCollected;
	public int getMoneyCollected() { return moneyCollected; }
	public void addMoney(int amnt) { moneyCollected += amnt; }
	
	private int wavesCleared;
	public int getWavesCleared() { return wavesCleared; }
	public void waveCleared() { wavesCleared++; }
	
	private Scorekeeper() {
		reset();
	}
	
	public void reset() {
		this.kills = 0;
		this.shotsFired = 0;
		this.shotsHit = 0;
		this.moneyCollected = 0;
		this.wavesCleared = 0;
	}
	
	public int calculateFinalScore() {
		int score = 0;
		
		// TODO: Calculate the final score based on variables.
		
		return score;
	}
}
