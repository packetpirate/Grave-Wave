package com.gzsr.gfx.ui.hud;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.BasicGameState;

import com.gzsr.entities.Entity;
import com.gzsr.entities.Player;
import com.gzsr.entities.components.HeartMonitor;
import com.gzsr.gfx.Animation;
import com.gzsr.gfx.Layers;
import com.gzsr.misc.Pair;

public class EKGBar implements Entity {
	private static final Pair<Float> SIZE = new Pair<Float>(181.0f, 22.0f);

	private Pair<Float> position;
	private Rectangle bounds;

	public EKGBar(Pair<Float> position_) {
		this.position = new Pair<Float>((position_.x + (SIZE.x / 2)), (position_.y + (SIZE.y / 2)));
		this.bounds = new Rectangle(position.x, position.y, SIZE.x, SIZE.y);
	}

	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
		Player player = Player.getPlayer();
		HeartMonitor monitor = player.getHeartMonitor();
		Animation animation = monitor.getState().getAnimation();
		animation.update(cTime);

		// TODO: Add logic to play EKG beeps at appropriate intervals.

	}

	@Override
	public void render(Graphics g, long cTime) {
		Player player = Player.getPlayer();
		HeartMonitor monitor = player.getHeartMonitor();
		Animation animation = monitor.getState().getAnimation();

		animation.render(g, position, SIZE);
	}

	@Override
	public String getName() { return "EKG Bar"; }

	@Override
	public String getDescription() { return "EKG Bar"; }

	@Override
	public int getLayer() { return Layers.NONE.val(); }
}
