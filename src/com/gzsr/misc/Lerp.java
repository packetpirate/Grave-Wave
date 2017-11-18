package com.gzsr.misc;

public class Lerp {
	private boolean complete;
	public boolean isComplete() { return complete; }
	private float current, end, step;
	public float getCurrent() { return current; }
	
	public Lerp(float start_, float end_, float step_) {
		this.complete = false;
		this.current = start_;
		this.end = end_;
		this.step = step_;
	}
	
	public void update(int delta) {
		if(!isComplete()) {
			current += step * delta;
			if((end - current) <= step) {
				current = end;
				complete = true;
			}
		}
	}
}
