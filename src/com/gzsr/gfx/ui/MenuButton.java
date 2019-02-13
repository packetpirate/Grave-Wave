package com.gzsr.gfx.ui;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.UnicodeFont;

import com.gzsr.AssetManager;
import com.gzsr.misc.Pair;

public class MenuButton extends Button {
	private static final Color DEFAULT_TEXT = new Color(0xFFFFFF);
	private static final Color DEFAULT_HOVER = new Color(0xABCDEF);
	private static final String FONT_NAME = "PressStart2P-Regular_large";

	public MenuButton(Pair<Float> position_, String text_) {
		this(position_, text_, null);
	}

	public MenuButton(Pair<Float> position_, String text_, String image_) {
		super();

		this.image = image_;
		this.position = position_;
		this.text = text_;
	}

	@Override
	public void render(Graphics g, long cTime) {
		Image img = AssetManager.getManager().getImage(image);
		if(img == null) {
			// Draw the text representing the button.
			UnicodeFont fnt = AssetManager.getManager().getFont(FONT_NAME);
			Color color = mouseOver() ? DEFAULT_HOVER : DEFAULT_TEXT;

			g.setColor(color);
			g.setFont(fnt);
			g.drawString(text, position.x, position.y);
		} else g.drawImage(img, position.x, position.y);
	}

	@Override
	public void click() {
		click(true);
	}

	@Override
	public void click(boolean left) {

	}

	public float getWidth() {
		UnicodeFont large = AssetManager.getManager().getFont(FONT_NAME);
		float textWidth = large.getWidth(text);
		if(image == null) return textWidth;

		Image img = AssetManager.getManager().getImage(image);
		return Math.max(img.getWidth(), textWidth);
	}

	@Override
	public boolean inBounds(float x, float y) {
		UnicodeFont fnt = AssetManager.getManager().getFont(FONT_NAME);

		float w = fnt.getWidth(text);
		float h = fnt.getHeight(text);

		return ((x > position.x) && (y > position.y) &&
				(x < (position.x + w)) && (y < (position.y + h)));
	}
}
