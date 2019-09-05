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
import com.grave.states.GameState;

public class Armor implements Entity {
	private Pair<Float> position;
	private Pair<Float> size;
	private Rectangle bounds;

	public Armor(Pair<Float> position_, Pair<Float> size_) {
		this.position = position_;
		this.size = size_;

		this.bounds = new Rectangle(position.x, position.y, size.x, size.y);
	}

	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
	}

	@Override
	public void render(GameState gs, Graphics g, long cTime) {
		Player player = Player.getPlayer();
		boolean touchingPlayer = intersects(player);

		double currentArmor = player.getAttributes().getDouble("armor");
		if(currentArmor > 0.0) {
			Image armor = AssetManager.getManager().getImage("GZS_HUD_Armor");

			double maxArmor = player.getAttributes().getDouble("maxArmor");
			float ratio = (float)(currentArmor / maxArmor);
			float h = (ratio * armor.getHeight());
			float y = (position.y + (armor.getHeight() - h));

			Image display = armor.getSubImage(0, (int)(armor.getHeight() - h), armor.getWidth(), (int)h);
			display.draw(position.x, y, getFilterColor(Color.white, touchingPlayer));
		}
	}

	private Color getFilterColor(Color c, boolean touchingPlayer) {
		return (touchingPlayer ? c.multiply(HUD.FADE) : c);
	}

	private boolean intersects(Player player) {
		return (bounds.intersects(player.getCollider()) || bounds.contains(player.getCollider()));
	}

	@Override
	public String getName() { return "Armor"; }

	@Override
	public String getTag() { return "armorDisplay"; }

	@Override
	public String getDescription() { return "Armor Display"; }

	@Override
	public int getLayer() { return Layers.NONE.val(); }
}
