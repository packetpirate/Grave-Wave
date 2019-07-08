package com.grave.gfx.ui.hud;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.BasicGameState;

import com.grave.entities.Entity;
import com.grave.entities.Player;
import com.grave.entities.components.HeartMonitor;
import com.grave.gfx.Animation;
import com.grave.gfx.Layers;
import com.grave.misc.Pair;

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
	}

	@Override
	public void render(Graphics g, long cTime) {
		Player player = Player.getPlayer();
		HeartMonitor monitor = player.getHeartMonitor();
		Animation animation = monitor.getState().getAnimation();

		Pair<Float> dPos = new Pair<Float>(position.x, position.y);

		animation.render(g, dPos, SIZE);
	}

	@Override
	public String getName() { return "EKG Bar"; }

	@Override
	public String getTag() { return "ekg"; }

	@Override
	public String getDescription() { return "EKG Bar"; }

	@Override
	public int getLayer() { return Layers.NONE.val(); }
}
