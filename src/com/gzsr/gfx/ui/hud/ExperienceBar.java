package com.gzsr.gfx.ui.hud;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.util.FontUtils;

import com.gzsr.AssetManager;
import com.gzsr.entities.Entity;
import com.gzsr.entities.Player;
import com.gzsr.gfx.Layers;
import com.gzsr.misc.Pair;

public class ExperienceBar implements Entity {
	private Pair<Float> position;

	private Image bar;
	private Rectangle bounds;

	public ExperienceBar(Pair<Float> position_) {
		this.position = position_;

		this.bar = AssetManager.getManager().getImage("GZS_Experience_Bar");
		this.bounds = new Rectangle(position.x, position.y, bar.getWidth(), bar.getHeight());
	}

	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
	}

	@Override
	public void render(Graphics g, long cTime) {
		Player player = Player.getPlayer();
		boolean touchingPlayer = intersects(player);

		float currentExp = player.getAttributes().getInt("experience");
		float expToLevel = player.getAttributes().getInt("expToLevel");
		float percentage = currentExp / expToLevel;

		Image subImage = bar.getSubImage(0, 0, (int)(bar.getWidth() * percentage), bar.getHeight());
		g.drawImage(subImage, position.x, position.y);

		UnicodeFont f = AssetManager.getManager().getFont("PressStart2P-Regular_xs");
		FontUtils.drawCenter(f, String.format("%d%%", (int)(percentage * 100)), (int)position.x.floatValue(), (int)(position.y + (bar.getHeight() / 2) - (f.getLineHeight() / 2)), bar.getWidth(), Color.white);
	}

	/**
	@Override
	public void render(Graphics g, long cTime) {
		Player player = Player.getPlayer();
		boolean touchingPlayer = intersects(player);

		float currentExp = player.getAttributes().getInt("experience");
		float expToLevel = player.getAttributes().getInt("expToLevel");
		float percentage = currentExp / expToLevel;

		changeColor(g, Color.black, touchingPlayer);
		g.fillRect(position.x, position.y, size.x, size.y);
		changeColor(g, Color.lightGray, touchingPlayer);
		g.drawRect(position.x, position.y, size.x, size.y);

		if(percentage != 0.0f) {
			changeColor(g, Color.green, touchingPlayer);
			g.fillRect((position.x + 3.0f), (position.y + 3.0f),
					   (percentage * (size.x - 6.0f)), (size.y - 6.0f));
			changeColor(g, Color.lightGray, touchingPlayer);
			g.drawRect((position.x + 3.0f), (position.y + 3.0f),
					   (percentage * (size.x - 6.0f)), (size.y - 6.0f));
		}
	}**/

	public boolean intersects(Player player) {
		return bounds.intersects(player.getCollider());
	}

	@Override
	public String getName() {
		return "Experience Bar";
	}

	@Override
	public String getDescription() {
		return "Experience Bar";
	}

	@Override
	public int getLayer() {
		return Layers.NONE.val();
	}
}
