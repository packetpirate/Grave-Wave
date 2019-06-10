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
import com.gzsr.gfx.Camera;
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
		Camera camera = Camera.getCamera();
		Player player = Player.getPlayer();
		boolean touchingPlayer = intersects(player);

		float currentExp = player.getAttributes().getInt("experience");
		float expToLevel = player.getAttributes().getInt("expToLevel");
		float percentage = currentExp / expToLevel;

		Color filter = getFilterColor(Color.white, touchingPlayer);

		Pair<Float> dPos = new Pair<Float>((position.x + camera.getOffset().x), (position.y + camera.getOffset().y));

		Image subImage = bar.getSubImage(0, 0, (int)(bar.getWidth() * percentage), bar.getHeight());
		g.drawImage(subImage, dPos.x, dPos.y, filter);

		UnicodeFont f = AssetManager.getManager().getFont("PressStart2P-Regular_xs");
		FontUtils.drawCenter(f, String.format("%d%%", (int)(percentage * 100)), (int)dPos.x.floatValue(), (int)(dPos.y + (bar.getHeight() / 2) - (f.getLineHeight() / 2)), bar.getWidth(), filter);
	}

	private Color getFilterColor(Color c, boolean touchingPlayer) {
		return (touchingPlayer ? c.multiply(HUD.FADE) : c);
	}

	public boolean intersects(Player player) {
		return (bounds.intersects(player.getCollider()) || bounds.contains(player.getCollider()));
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
