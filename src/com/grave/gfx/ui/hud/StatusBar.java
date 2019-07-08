package com.grave.gfx.ui.hud;

import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.BasicGameState;

import com.grave.entities.Entity;
import com.grave.entities.Player;
import com.grave.gfx.Layers;
import com.grave.misc.Pair;
import com.grave.status.StatusEffect;

public class StatusBar implements Entity {
	private Pair<Float> position;

	private Rectangle bounds;

	public StatusBar(Pair<Float> position_) {
		this.position = position_;

		this.bounds = new Rectangle(position.x, position.y, 200.0f, 54.0f);
	}

	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
	}

	@Override
	public void render(Graphics g, long cTime) {
		Player player = Player.getPlayer();
		boolean touchingPlayer = intersects(player);

		float xPlus = 0.0f;
		List<StatusEffect> statusEffects = player.getStatusHandler().getStatusEffects();
		for(StatusEffect status : statusEffects) {
			// Render each individual status underneath the health and experience bars.
			if(status.isDrawn()) {
				Image img = status.getIcon();
				float percentageTimeLeft = status.getPercentageTimeLeft(cTime);
				if(touchingPlayer) percentageTimeLeft *= HUD.FADE.a;

				g.drawImage(img, (position.x + xPlus), position.y, new Color(1.0f, 1.0f, 1.0f, percentageTimeLeft));
				xPlus += img.getWidth() + 5.0f;
			}
		}
	}

	public boolean intersects(Player player) {
		return (bounds.intersects(player.getCollider()) || bounds.contains(player.getCollider()));
	}

	@Override
	public String getName() { return "Status Bar"; }

	@Override
	public String getTag() { return "statusBar"; }

	@Override
	public String getDescription() { return "Status Bar"; }

	@Override
	public int getLayer() { return Layers.NONE.val(); }
}
