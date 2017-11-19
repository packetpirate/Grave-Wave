package com.gzsr.misc;

public class Lerp {
	private boolean complete;
	public boolean isComplete() { return complete; }
	private float current, end, step;
	public float getCurrent() { return current; }
	public float getEnd() { return end; }
	
	public Lerp(float start_, float end_, float step_) {
		this.complete = false;
		this.current = start_;
		this.end = end_;
		this.step = step_;
		
		// Determine direction to move in according to arc length from both directions from start to end.
		float circumference = 2 * (float)Math.PI * 50; // 50 is arbitrary
		float inverse = (float)(Math.PI * 2) - end_;
		float arcLength1 = Math.abs(circumference * ((end_ - start_) / (float)(2 * Math.PI)));
		float arcLength2 = Math.abs(circumference * ((inverse - start_) / (float)(2 * Math.PI)));
		this.step = (arcLength1 < arcLength2) ? step : -step;
	}
	
	public void update(int delta) {
		if(!isComplete()) {
			current += step * delta;
			
			if(nearEnd(delta)) {
				current = end;
				complete = true;
			}
		}
	}
	
	private boolean nearEnd(int delta) {
		float arcLength = Math.abs((2 * (float)Math.PI * 50) * ((end - current) / (float)(2 * Math.PI)));
		return (arcLength <= Math.abs(2 * (step * delta)));
	}
}
