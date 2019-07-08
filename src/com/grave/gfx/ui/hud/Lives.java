package com.grave.gfx.ui.hud;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.BasicGameState;

import com.grave.AssetManager;
import com.grave.entities.Entity;
import com.grave.entities.Player;
import com.grave.gfx.Layers;
import com.grave.misc.Pair;

public class Lives implements Entity {
	private static final float GEM_OFFSET = 26.0f;

	private Image gem;
	private Pair<Float> position;
	private Rectangle bounds;

	public Lives(Pair<Float> position_) {
		this.position = position_;

		this.gem = AssetManager.getManager().getImage("GZS_Life_Gem");
		this.bounds = new Rectangle(position.x, position.y, ((gem.getWidth() * 5) + ((GEM_OFFSET - gem.getWidth()) * 4)), gem.getHeight());
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
			g.drawImage(gem, x, position.y, getFilterColor(Color.white, touchingPlayer));
		}
	}

	private Color getFilterColor(Color c, boolean touchingPlayer) {
		return (touchingPlayer ? c.multiply(HUD.FADE) : c);
	}

	private boolean intersects(Player player) {
		return (bounds.intersects(player.getCollider()) || bounds.contains(player.getCollider()));
	}

	@Override
	public String getName() { return "Lives"; }

	@Override
	public String getTag() { return "lives"; }

	@Override
	public String getDescription() { return "Lives"; }

	@Override
	public int getLayer() { return Layers.NONE.val(); }
}
