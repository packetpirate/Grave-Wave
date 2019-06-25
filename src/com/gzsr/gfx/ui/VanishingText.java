package com.gzsr.gfx.ui;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.util.FontUtils;

import com.gzsr.AssetManager;
import com.gzsr.entities.Entity;
import com.gzsr.entities.Player;
import com.gzsr.entities.enemies.Enemy;
import com.gzsr.gfx.Layers;
import com.gzsr.misc.Pair;

public class VanishingText implements Entity {
	protected String text;
	protected String font;
	protected Pair<Float> position;
	public Pair<Float> getPosition() { return position; }
	protected Pair<Float> offset;
	public Pair<Float> getOffset() { return offset; }
	protected Color color;

	protected long creationTime;
	protected long duration;

	protected boolean active;
	public boolean isActive() { return active; }

	private Entity tether;
	public Entity getTether() { return tether; }

	/**
	 * Draws a floating piece of text on the screen.
	 * @param text_ The text to draw on the screen.
	 * @param font_ The font to use when drawing the text.
	 * @param position_ The center of the text to draw.
	 * @param color_ The color of the text to draw.
	 * @param creationTime_ The time when this text was created.
	 * @param duration_ How long the text should be displayed on the screen.
	 */
	public VanishingText(String text_, String font_, Pair<Float> position_, Color color_, long creationTime_, long duration_) {
		this.text = text_;
		this.font = font_;

		this.position = position_;
		this.offset = null;

		this.color = new Color(color_);

		this.creationTime = creationTime_;
		this.duration = duration_;
		this.active = true;
		this.tether = null;
	}

	/**
	 * Draws a floating piece of text on the screen that follows the player.
	 * @param text_ The text to draw on the screen.
	 * @param font_ The font to use when drawing the text.
	 * @param offset_ Offset from the player's position that determines where to draw the text.
	 * @param color_ The color of the text to draw.
	 * @param creationTime_ The time when this text was created.
	 * @param duration_ How long the text should be displayed on the screen.
	 * @param followPlayer_ Whether or not to follow the player. If not, text still created relative to player position, but does not follow.
	 */
	public VanishingText(String text_, String font_, Entity tether_, Pair<Float> offset_, Color color_, long creationTime_, long duration_) {
		this.text = text_;
		this.font = font_;

		this.position = new Pair<Float>(offset_.x, offset_.y);
		if(tether_ instanceof Player) {
			Player player = (Player) tether_;
			this.position.x += player.getPosition().x;
			this.position.y += player.getPosition().y;
		} else if(tether_ instanceof Enemy) {
			Enemy enemy = (Enemy) tether_;
			this.position.x += enemy.getPosition().x;
			this.position.y += enemy.getPosition().y;
		}

		this.offset = offset_;

		this.color = new Color(color_);

		this.creationTime = creationTime_;
		this.duration = duration_;
		this.active = true;
		this.tether = tether_;
	}

	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
		long elapsed = (cTime - creationTime);
		if(elapsed > duration) active = false;

		if(isActive() && (tether != null) && (offset != null)) {
			position.x = offset.x;
			position.y = offset.y;

			if(tether instanceof Player) {
				Player player = (Player) tether;
				position.x += player.getPosition().x;
				position.y += player.getPosition().y;
			} else if(tether instanceof Enemy) {
				Enemy enemy = (Enemy) tether;
				position.x += enemy.getPosition().x;
				position.y += enemy.getPosition().y;
			}
		}
	}

	@Override
	public void render(Graphics g, long cTime) {
		if(isActive()) {
			g.setFont(AssetManager.getManager().getFont(font));
			g.setColor(color);

			float w = g.getFont().getWidth(text);
			float h = g.getFont().getLineHeight();
			float x = (position.x - (w / 2));
			float y = (position.y - (h / 2));

			long elapsed = (cTime - creationTime);
			float percentageTimeLeft = ((float)elapsed / (float)duration);
			color.a = (1.0f - percentageTimeLeft);

			FontUtils.drawCenter(g.getFont(), text, (int)x, (int)y, (int)w, color);
		}
	}

	@Override
	public String getName() { return "Floating Text"; }

	@Override
	public String getTag() { return "vanishText"; }

	@Override
	public String getDescription() { return "Text displayed on the screen."; }

	@Override
	public int getLayer() { return Layers.TEXT.val(); }
}
