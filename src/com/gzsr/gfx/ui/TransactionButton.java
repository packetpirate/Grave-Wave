package com.gzsr.gfx.ui;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import com.gzsr.AssetManager;
import com.gzsr.misc.Pair;

public class TransactionButton extends Button {
	public static enum Type {
		BUY, SELL
	}
	
	private Type type;
	public Type getType() { return type; }
	
	public TransactionButton(Pair<Float> position_, Type type_) {
		super();
		
		this.type = type_;
		switch(type_) {
			case BUY:
				image = "GZS_BuyButton2";
				break;
			case SELL:
				image = "GZS_SellButton2";
				break;
			default:
				image = null;
				break;
		}
		
		this.position = position_;
		this.size = null;
	}
	
	@Override
	public void render(Graphics g, long cTime) {
		// Draw using position as center.
		Image button = AssetManager.getManager().getImage(image);
		if(button != null) {
			g.drawImage(button, (position.x - (button.getWidth() / 2)), (position.y - (button.getHeight() / 2)));
		}
	}
	
	@Override
	public void click() {
		// Not needed.
	}

	@Override
	public boolean inBounds(float x, float y) {
		float w = AssetManager.getManager().getImage(image).getWidth();
		float h = AssetManager.getManager().getImage(image).getHeight();
		return ((x > (position.x - (w / 2))) && (y > (position.y - (h / 2))) && 
				(x < (position.x + (w / 2))) && (y < (position.y + (h / 2))));
	}
	
	@Override
	public String getName() {
		return "Transaction Button";
	}
}
