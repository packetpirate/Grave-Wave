package com.gzsr.entities;

public class Resources {
	private int metal;
	public int getMetal() { return metal; }
	public void addMetal(int amnt) { metal += amnt; }

	private int cloth;
	public int getCloth() { return cloth; }
	public void addCloth(int amnt) { cloth += amnt; }

	private int glass;
	public int getGlass() { return glass; }
	public void addGlass(int amnt) { glass += amnt; }

	private int wood;
	public int getWood() { return wood; }
	public void addWood(int amnt) { wood += amnt; }

	private int bio;
	public int getBio() { return bio; }
	public void addBio(int amnt) { bio += amnt; }

	private int power;
	public int getPower() { return power; }
	public void addPower(int amnt) { power += amnt; }

	public Resources() {
		reset();
	}

	public void reset() {
		this.metal = 0;
		this.cloth = 0;
		this.glass = 0;
		this.wood = 0;
		this.bio = 0;
		this.power = 0;
	}
}
