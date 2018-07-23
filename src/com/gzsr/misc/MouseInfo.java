package com.gzsr.misc;

public class MouseInfo {
	private Pair<Float> lmp;
	private boolean lmd, rmd;
	
	public Pair<Float> getPosition() { return lmp; }
	public void setPosition(float x, float y) {
		this.lmp.x = x;
		this.lmp.y = y;
	}
	
	public boolean isLeftDown() { return lmd; }
	public void setLeftDown(boolean lmd_) { this.lmd = lmd_; }
	
	public boolean isRightDown() { return rmd; }
	public void setRightDown(boolean rmd_) { this.rmd = rmd_; }
	
	public MouseInfo() {
		lmp = new Pair<Float>(0.0f, 0.0f);
		lmd = false;
		rmd = false;
	}
}
