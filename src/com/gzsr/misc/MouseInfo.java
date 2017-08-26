package com.gzsr.misc;

public class MouseInfo {
	private Pair<Float> lmp;
	private boolean md;
	
	public Pair<Float> getPosition() { return lmp; }
	public void setPosition(float x, float y) {
		this.lmp.x = x;
		this.lmp.y = y;
	}
	
	public boolean isMouseDown() { return md; }
	public void setMouseDown(boolean md_) { this.md = md_; }
	
	public MouseInfo() {
		lmp = new Pair<Float>(0.0f, 0.0f);
		md = false;
	}
}
