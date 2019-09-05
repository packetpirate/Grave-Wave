package com.grave.gfx.ui.hud;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.util.FontUtils;

import com.grave.AssetManager;
import com.grave.entities.Entity;
import com.grave.entities.Player;
import com.grave.gfx.Layers;
import com.grave.misc.Pair;
import com.grave.states.GameState;
import com.grave.status.Status;

public class Heart implements Entity {
	private static final Color POISON_COLOR = new Color(0x5AAD5B);

	private Pair<Float> position;
	private Pair<Float> size;
	private Rectangle bounds;

	public Heart(Pair<Float> position_, Pair<Float> size_) {
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

		Pair<Float> dPos = new Pair<Float>(position.x, position.y);

		// Draw the Heart
		double currentHealth = player.getAttributes().getDouble("health");
		Image heart = AssetManager.getManager().getImage("GZS_Heart");

		Color filter = changeColor(g, (player.getStatusHandler().hasStatus(Status.POISON) ? POISON_COLOR : Color.white), touchingPlayer);
		g.drawImage(heart, dPos.x, dPos.y, filter);

		Color textColor = player.getHeartMonitor().getState().getColor();
		UnicodeFont f = AssetManager.getManager().getFont("PressStart2P-Regular_xs");
		FontUtils.drawCenter(f, String.format("%d", (int)currentHealth), (int)dPos.x.floatValue(), (int)(dPos.y + (size.y / 2) - (f.getLineHeight() / 2)), (int)size.x.floatValue(), textColor);
	}

	private Color changeColor(Graphics g, Color c, boolean touchingPlayer) {
		return (touchingPlayer ? c.multiply(HUD.FADE) : c);
	}

	private boolean intersects(Player player) {
		return (bounds.intersects(player.getCollider()) || bounds.contains(player.getCollider()));
	}

	@Override
	public String getName() { return "Heart"; }

	@Override
	public String getTag() { return "heartDisplay"; }

	@Override
	public String getDescription() { return "Heart"; }

	@Override
	public int getLayer() { return Layers.NONE.val(); }
}
