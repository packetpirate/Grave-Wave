package com.gzsr.gfx.ui;

import java.util.function.Consumer;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.state.BasicGameState;

import com.gzsr.AssetManager;
import com.gzsr.entities.Entity;
import com.gzsr.gfx.Layers;
import com.gzsr.misc.Pair;

public class CheckBox implements Entity {
	public static final float SIZE = 40.0f;
	
	private String label;
	private Pair<Float> position;
	
	private boolean defaultVal;
	private boolean checked;
	public boolean isChecked() { return checked; }
	public void setChecked(boolean val_) { checked = val_; }
	public void check() { checked = true; }
	public void uncheck() { checked = false; }
	public void toggle() { 
		checked = !checked;
		operation.accept(checked);
	}
	
	private Consumer<Boolean> operation;
	
	public CheckBox(String label_, Pair<Float> position_, boolean checked_, Consumer<Boolean> operation_) {
		this.label = label_;
		this.position = position_;
		
		this.defaultVal = checked_;
		this.checked = checked_;
		this.operation = operation_;
	}

	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
		// Unused
	}

	@Override
	public void render(Graphics g, long cTime) {
		UnicodeFont f = AssetManager.getManager().getFont("PressStart2P-Regular");
		
		g.setFont(f);
		g.setColor(Color.white);
		g.drawRect(position.x, position.y, SIZE, SIZE);
		if(isChecked()) {
			g.drawLine((position.x + 5.0f), (position.y + 5.0f), (position.x + (SIZE - 5.0f)), (position.y + (SIZE - 5.0f)));
			g.drawLine(((position.x + SIZE) - 5.0f), (position.y + 5.0f), (position.x + 5.0f), (position.y + (SIZE - 5.0f)));
		}
		
		float strWidth = f.getWidth(label);
		float strHeight = f.getHeight(label);
		g.drawString(label, (position.x - (strWidth + 10.0f)), (position.y + ((SIZE - strHeight) / 2)));
	}
	
	public void apply(boolean save) {
		if(save) {
			defaultVal = checked;
		} else {
			checked = defaultVal;
			operation.accept(defaultVal);
		}
	}
	
	public boolean contains(float x, float y) {
		return ((x >= position.x) && (x <= (position.x + SIZE)) && 
				(y >= position.y) && (y <= (position.y + SIZE)));
	}
	
	public boolean contains(Pair<Float> pos_) {
		return contains(pos_.x, pos_.y);
	}

	@Override
	public String getName() {
		return "Check Box";
	}

	@Override
	public String getDescription() {
		return "Used to represent a boolean property.";
	}
	
	@Override
	public int getLayer() {
		return Layers.NONE.val();
	}
}
