package com.grave.gfx.effects;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.BasicGameState;

import com.grave.misc.Pair;

public class Lightning extends VisualEffect {
	public static final Color ELECTRIC_BLUE = new Color(0x47C2F7);

	private static final long DELAY = 50L;
	private static final int MAX_FORKS = 5;

	private Pair<Float> start, end;
	private List<Segment> segments;
	private float maxOffset;
	private long lastBolt;

	public Lightning(Pair<Float> start_, Pair<Float> end_, float maxOffset_, long lifespan_, long cTime_) {
		super(lifespan_, cTime_);

		this.start = start_;
		this.end = end_;

		this.segments = new ArrayList<Segment>();
		this.maxOffset = maxOffset_;

		this.lastBolt = 0L;
	}

	private void createBolt() {
		segments.clear();
		segments.add(new Segment(start, end));
		float offset = maxOffset;
		for(int f = 0; f < MAX_FORKS; f++) {
			List<Segment> tempSegs = new ArrayList<Segment>();
			tempSegs.addAll(segments);
			for(int s = 0; s < tempSegs.size(); s++) {
				Segment seg = tempSegs.get(s);
				segments.remove(seg);

				Pair<Float> perpendicular = seg.getPerpendicular(offset);
				segments.add(new Segment(seg.getStart(), perpendicular));
				segments.add(new Segment(perpendicular, seg.getEnd()));
			}

			offset /= 2;
		}
	}

	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
		long elapsed = (cTime - lastBolt);
		if(elapsed >= DELAY) {
			createBolt();
			lastBolt = cTime;
		}
	}

	@Override
	public void render(Graphics g, long cTime) {
		segments.stream().forEach(segment -> segment.render(g));
	}

	@Override
	public String getName() { return "Lightning"; }

	@Override
	public String getDescription() { return "Lightning Bolt Effect"; }
}
