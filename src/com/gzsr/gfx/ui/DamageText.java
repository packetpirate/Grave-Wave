package com.gzsr.gfx.ui;

import org.newdawn.slick.Color;
import org.newdawn.slick.state.BasicGameState;

import com.gzsr.misc.Pair;

public class DamageText extends VanishingText {
	private static final String FONT_NAME = "PressStart2P-Regular_small";
	private static final float SPEED = 0.1f;

	private float theta;

	public DamageText(String text_, Pair<Float> position_, float theta_, long cTime_, long duration_, boolean critical_) {
		super(text_, FONT_NAME, position_, new Color(critical_ ? Color.red : Color.white), cTime_, duration_);
		this.theta = theta_;
	}

	public DamageText(String text_, Pair<Float> position_, float theta_, long cTime_, long duration_, Color color_) {
		super(text_, FONT_NAME, position_, color_, cTime_, duration_);
		this.theta = theta_;
	}

	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
		super.update(gs, cTime, delta);
		if(active) {
			position.x += (float)(Math.cos(theta) * SPEED * delta);
			position.y += (float)(Math.sin(theta) * SPEED * delta);
		}
	}

	@Override
	public String getName() {
		return "Damage Text";
	}

	@Override
	public String getDescription() {
		return "Displays damage taken by enemies.";
	}
}
