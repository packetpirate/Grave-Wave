package com.gzsr.gfx.ui.hud;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.BasicGameState;

import com.gzsr.AssetManager;
import com.gzsr.entities.Entity;
import com.gzsr.entities.Player;
import com.gzsr.gfx.Layers;
import com.gzsr.misc.Pair;

public class Lives implements Entity {
	private static final float GEM_OFFSET = 26.0f;

	private Image gem;
	private Pair<Float> position;
	private Rectangle bounds;

	public Lives(Pair<Float> position_) {
		this.position = position_;

		this.gem = AssetManager.getManager().getImage("GZS_Life_Gem");
		this.bounds = new Rectangle(position.x, position.y, gem.getWidth(), gem.getHeight());
	}

	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
	}

	@Override
	public void render(Graphics g, long cTime) {
		Player player = Player.getPlayer();
		boolean touchingPlayer = intersects(player);

		int lives = player.getAttributes().getInt("lives");
		for(int i = 0; i < lives; i++) {
			float x = (position.x + (i * GEM_OFFSET));
			g.drawImage(gem, x, position.y);
		}
	}

	private boolean intersects(Player player) {
		return bounds.intersects(player.getCollider());
	}

	@Override
	public String getName() {
		return "Lives";
	}

	@Override
	public String getDescription() {
		return "Lives";
	}

	@Override
	public int getLayer() {
		return Layers.NONE.val();
	}
}
