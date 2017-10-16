package com.gzsr.gfx.ui;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.util.FontUtils;

import com.gzsr.misc.MouseInfo;

public class MenuButton {
	private static final Color DEFAULT_BUTTON_STROKE = new Color(0.0f, 0.133f, 0.398f, 1.0f);
	private static final Color DEFAULT_BUTTON_FILL = new Color(0.234f, 0.391f, 0.684f, 1.0f);
	private static final Color HOVER_BUTTON_FILL = new Color(0.140f, 0.235f, 0.410f, 1.0f);
	private static final Color DEFAULT_TEXT_FILL = new Color(0.938f, 0.781f, 0.371f, 1.0f);
	
	private float x, y, w, h;
	
	private String text;
	private Image img;
	
	private boolean mouseIn;
	public boolean isMouseOver() { return mouseIn; }
	public void mouseEnter() { this.mouseIn = true; }
	public void mouseExit() { this.mouseIn = false; }
	
	public MenuButton(float x_, float y_, float w_, float h_, String text_) {
		this.img = null;
		this.x = x_;
		this.y = y_;
		this.w = w_;
		this.h = h_;
		this.text = text_;
		this.mouseIn = false;
	}
	
	public MenuButton(float x_, float y_, Image img_) {
		this.img = img_;
		this.text = (img == null)?"Default":null;
		this.x = x_;
		this.y = y_;
		this.w = img.getWidth();
		this.h = img.getHeight();
		this.mouseIn = false;
	}
	
	public void render(Graphics g) {
		if(img == null) {
			// Draw a basic shape to represent the button.
			g.setColor(DEFAULT_BUTTON_STROKE);
			g.drawRect(x, y, w, h);
			g.setColor((mouseIn)?HOVER_BUTTON_FILL:DEFAULT_BUTTON_FILL);
			g.fillRect(x, y, w, h);
			
			// Draw text on it.
			g.setColor(DEFAULT_TEXT_FILL);
			FontUtils.drawCenter(g.getFont(), text, (int)(x + (w / 2)), (int)(y + (h / 4)), 0);
		} else g.drawImage(img, x, y);
	}
	
	public boolean contains(MouseInfo mouse) {
		return (((mouse.getPosition().x >= x) && (mouse.getPosition().y >= y)) && 
				((mouse.getPosition().x <= (x + w)) && (mouse.getPosition().y <= (y + h))));
	}
}
