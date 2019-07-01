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
	public double getAccuracy() {
		if((shotsFired == 0) || (shotsHit == 0)) return 0.0;
		return ((double)shotsHit / (double)shotsFired); 
	}
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
		int k = (kills * 10);
		int a = (int)(getAccuracy() * k);
		int w = (wavesCleared * 100);
		
		return (a + moneyCollected + w);
	}
}
