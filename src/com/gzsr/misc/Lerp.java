package com.gzsr.misc;

public class Lerp {
	private static final float TWO_PI = (float)(Math.PI * 2);
	
	private boolean complete;
	public boolean isComplete() { return complete; }
	private float current, end, step;
	public float getCurrent() { return current; }
	public float getEnd() { return end; }
	private Vector2f targetDir;
	
	public Lerp(Pair<Float> pos, Pair<Float> target, float start_, float end_, float step_) {
		this.complete = false;
		this.current = start_;
		this.end = end_;
		this.step = step_;
		
		// Calculate vector between entity position and target position.
		this.targetDir = Vector2f.normalize(Vector2f.sub(new Vector2f(target.x, target.y), new Vector2f(pos.x, pos.y)));
	}
	
	public void update(Pair<Float> pos, Pair<Float> target, int delta) {
		if(!isComplete()) {
			// Calculate vector between entity position and target position.
			this.targetDir = Vector2f.normalize(Vector2f.sub(new Vector2f(target.x, target.y), new Vector2f(pos.x, pos.y)));
			
			Vector2f aimDir = new Vector2f(Math.cos(current), Math.sin(current));
			Vector2f perp = new Vector2f(aimDir.y, -aimDir.x); // perpendicular to the current direction
			
			// Should the turret be moving left or right?
			boolean moveRight = Vector2f.dot(targetDir, perp) > 0;
			
			// Get the difference between current and end theta.
			float angleDiff = (float) Math.acos(Vector2f.dot(targetDir, aimDir));
			
			// Are we at our destination?
			if(angleDiff < (step * delta)) {
				aimDir.set(targetDir);
				current = ((float) Math.atan2(targetDir.y, targetDir.x) + TWO_PI) % TWO_PI;
				complete = true;
			} else current += moveRight ? (-step * delta) : (step * delta);
		}
	}
}
