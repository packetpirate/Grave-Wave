package com.gzsr.gfx.ui;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.UnicodeFont;

import com.gzsr.AssetManager;
import com.gzsr.misc.MouseInfo;

public class MenuButton {
	private static final Color DEFAULT_TEXT = new Color(0xFFFFFF);
	private static final Color DEFAULT_HOVER = new Color(0xABCDEF);
	private static final String FONT_NAME = "PressStart2P-Regular_large";
	
	private float x, y;
	
	private String text;
	private Image img;
	
	private boolean mouseIn;
	public boolean isMouseOver() { return mouseIn; }
	public void mouseEnter() { this.mouseIn = true; }
	public void mouseExit() { this.mouseIn = false; }
	
	public MenuButton(float x_, float y_, String text_) {
		this.img = null;
		this.x = x_;
		this.y = y_;
		this.text = text_;
		this.mouseIn = false;
	}
	
	public MenuButton(float x_, float y_, Image img_) {
		this.img = img_;
		this.text = (img == null) ? "Default" : null;
		this.x = x_;
		this.y = y_;
		this.mouseIn = false;
	}
	
	public void render(Graphics g) {
		if(img == null) {
			// Draw the text representing the button.
			UnicodeFont fnt = AssetManager.getManager().getFont(FONT_NAME); 
			Color color = mouseIn ? DEFAULT_HOVER : DEFAULT_TEXT;
			//fnt.drawString(x, y, text, color);
			g.setColor(color);
			g.setFont(fnt);
			g.drawString(text, x, y);
		} else g.drawImage(img, x, y);
	}
	
	public boolean contains(MouseInfo mouse) {
		UnicodeFont fnt = AssetManager.getManager().getFont(FONT_NAME);
		float w = fnt.getWidth(text);
		float h = fnt.getHeight(text);
		return ((mouse.getPosition().x > x) && (mouse.getPosition().y > y) && 
				(mouse.getPosition().x < (x + w)) && (mouse.getPosition().y < (y + h)));
	}
}
