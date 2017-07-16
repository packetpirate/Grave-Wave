package gzs.game.gfx.screens.components;

import gzs.game.misc.MouseInfo;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

public class MenuButton {
	private static final Color DEFAULT_BUTTON_STROKE = new Color(0.0, 0.133, 0.398, 1.0);
	private static final Color DEFAULT_BUTTON_FILL = new Color(0.234, 0.391, 0.684, 1.0);
	private static final Color HOVER_BUTTON_FILL = new Color(0.140, 0.235, 0.410, 1.0);
	private static final Color DEFAULT_TEXT_STROKE = new Color(0.781, 0.645, 0.059, 1.0);
	private static final Color DEFAULT_TEXT_FILL = new Color(0.938, 0.781, 0.371, 1.0);
	
	private double x, y;
	private double w, h;
	
	private String text;
	private Image img;
	
	private boolean mouseIn;
	public void mouseEnter() { this.mouseIn = true; }
	public void mouseExit() { this.mouseIn = false; }
	
	public MenuButton(double x_, double y_, double w_, double h_, String text_) {
		this.img = null;
		this.x = x_;
		this.y = y_;
		this.w = w_;
		this.h = h_;
		this.text = text_;
		this.mouseIn = false;
	}
	
	public MenuButton(double x_, double y_, Image img_) {
		this.img = img_;
		this.text = (img == null)?"Default":null;
		this.x = x_;
		this.y = y_;
		this.w = img.getWidth();
		this.h = img.getHeight();
		this.mouseIn = false;
	}
	
	public void render(GraphicsContext gc, long cT) {
		if(img == null) {
			// Draw a basic shape to represent the button.
			gc.setStroke(DEFAULT_BUTTON_STROKE);
			if(mouseIn) gc.setFill(HOVER_BUTTON_FILL);
			else 		gc.setFill(DEFAULT_BUTTON_FILL);
			gc.strokeRect(x, y, w, h);
			gc.fillRect(x, y, w, h);
			
			// Draw text on it.
			gc.setStroke(DEFAULT_TEXT_STROKE);
			gc.setFill(DEFAULT_TEXT_FILL);
			gc.setTextAlign(TextAlignment.CENTER);
			gc.setTextBaseline(VPos.CENTER);
			gc.strokeText(text, (x + (w / 2)), (y + (h / 2)));
			gc.fillText(text, (x + (w / 2)), (y + (h / 2)));
		} else gc.drawImage(img, x, y);
	}
	
	public boolean contains(MouseInfo mouse) {
		return (((mouse.getPosition().x >= x) && (mouse.getPosition().y >= y)) && 
				((mouse.getPosition().x <= (x + w)) && (mouse.getPosition().y <= (y + h))));
	}
}
