package com.gzsr.gfx.ui;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.util.FontUtils;

import com.gzsr.AssetManager;
import com.gzsr.ConfigManager;
import com.gzsr.Controls;
import com.gzsr.gfx.Layers;
import com.gzsr.misc.Pair;

public class ControlConfigButton extends Button {
	public static final float WIDTH = 150.0f;
	public static final float HEIGHT = 40.0f;
	
	private Controls.Layout keyMapping;
	private int currentKey;
	private String currentDisplay;
	
	private String property;
	public String getProperty() { return property; }
	private String label;
	public String getLabel() { return label; }
	public void setLabel(String label_) { this.label = label_; }
	
	private boolean awaitingInput;
	public boolean isAwaitingInput() { return awaitingInput; }
	public void cancel() { awaitingInput = false; }
	
	public ControlConfigButton(String property_, Controls.Layout keyMapping_, Pair<Float> position_) {
		super();
		
		this.property = property_;
		this.text = keyMapping_.getDisplay();
		
		setPosition(position_);
		setSize(new Pair<Float>(WIDTH, HEIGHT));
		
		this.keyMapping = keyMapping_;
		this.currentKey = keyMapping_.getKey();
		this.currentDisplay = keyMapping_.getDisplay();
		this.awaitingInput = false;
	}
	
	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
		text = currentDisplay;
	}
	
	@Override
	public void render(Graphics g, long cTime) {
		g.setColor(Color.white);
		g.drawRect(position.x, position.y, size.x, size.y);
		g.drawRect((position.x + 2.0f), (position.y + 2.0f), (size.x - 4.0f), (size.y - 4.0f));
		
		g.setFont(AssetManager.getManager().getFont("PressStart2P-Regular_small"));
		float yOff = (HEIGHT - g.getFont().getLineHeight() - 4.0f) / 2.0f;
		Color color = awaitingInput ? Color.red : Color.white;
		FontUtils.drawCenter(g.getFont(), text, (int)(position.x + 2.0f), (int)(position.y + yOff + 2.0f), (int)(size.x - 4.0f), color);
		
		g.setColor(Color.white);
		g.setFont(AssetManager.getManager().getFont("PressStart2P-Regular"));
		yOff = (HEIGHT - g.getFont().getLineHeight()) / 2.0f;
		int sWidth = g.getFont().getWidth(label);
		g.drawString(label, (position.x - sWidth - 10.0f), (position.y + yOff));
	}
	
	public void apply(boolean accept) {
		if(accept) {
			// Apply the changes to the key.
			keyMapping.setKey(currentKey);
			keyMapping.setDisplay(currentDisplay);			
		} else {
			// Change the displayed key back to the default.
			currentKey = keyMapping.getKey();
			currentDisplay = keyMapping.getDisplay();
		}
		
		text = currentDisplay;
		ConfigManager.getInstance().getAttributes().set(property, currentKey);
	}
	
	public void acceptKeyMapping(int key_, char c_) {
		if(key_ != Input.KEY_ESCAPE) {
			currentKey = key_;
			currentDisplay = Controls.Layout.findDisplay(key_, c_);
			
			cancel();
		}
	}
	
	@Override
	public void click() {
		click(true);
	}
	
	@Override
	public void click(boolean left) {
		awaitingInput = true;
	}

	@Override
	public boolean inBounds(float x, float y) {
		return ((x >= (position.x + 2.0f)) && (x <= (position.x + (size.x - 4.0f))) && 
				(y >= (position.y + 2.0f)) && (y <= (position.y + (size.y - 4.0f))));
	}

	@Override
	public String getName() {
		return "Control Config Button";
	}
	
	@Override
	public String getDescription() {
		return "Control Config Button";
	}
	
	@Override
	public int getLayer() {
		return Layers.NONE.val();
	}
}
