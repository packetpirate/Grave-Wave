package com.grave.gfx.effects;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import com.grave.Globals;
import com.grave.misc.Pair;

public class Segment {
	private Pair<Float> start;
	public Pair<Float> getStart() { return start; }
	private Pair<Float> end;
	public Pair<Float> getEnd() { return end; }

	public Segment(Pair<Float> start_, Pair<Float> end_) {
		this.start = new Pair<Float>(start_);
		this.end = new Pair<Float>(end_);
	}

	public Pair<Float> getMidpoint() {
		return new Pair<Float>(((start.x + end.x) / 2), ((start.y + end.y) / 2));
	}

	public Pair<Float> getPerpendicular(float offset) {
		Pair<Float> midpoint = getMidpoint();
		Pair<Float> perp = new Pair<Float>((start.x - midpoint.x), (start.y + midpoint.y));
		Pair<Float> norm = new Pair<Float>(-perp.y, perp.x);

		float length = (float) Math.sqrt((norm.x * norm.x) + (norm.y * norm.y));
		norm.x /= length;
		norm.y /= length;

		float dist = (Globals.rand.nextFloat() * offset * (Globals.rand.nextInt(3) - 1));
		return new Pair<Float>((midpoint.x + (dist * norm.x)), (midpoint.y + (dist * norm.y)));
	}

	public void render(Graphics g) {
		g.setColor(Lightning.ELECTRIC_BLUE);
		g.setLineWidth(3.0f);
		g.drawLine(start.x, start.y, end.x, end.y);
		g.setColor(Color.white);
		g.setLineWidth(1.0f);
		g.drawLine(start.x, start.y, end.x, end.y);
	}
}
