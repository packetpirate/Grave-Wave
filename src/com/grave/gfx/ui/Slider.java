package com.grave.gfx.ui;

import java.util.function.Consumer;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.BasicGameState;

import com.grave.AssetManager;
import com.grave.ConfigManager;
import com.grave.Controls;
import com.grave.entities.Entity;
import com.grave.gfx.Layers;
import com.grave.misc.Pair;
import com.grave.states.GameState;

public class Slider implements Entity {
	private static final float HEIGHT = 20.0f;

	private String property;
	public String getProperty() { return property; }

	private String label;
	public String getLabel() { return label; }

	private Pair<Float> position;
	public Pair<Float> getPosition() { return position; }
	private float width;
	public float getWidth() { return width; }

	private Pair<Float> sliderBounds;
	public Pair<Float> getSliderBounds() { return sliderBounds; }
	public void setSliderBounds(Pair<Float> bounds_) { this.sliderBounds = bounds_; }

	private float sliderVal, defaultVal;
	public float getSliderVal() { return sliderVal; }
	public void setSliderVal(float val_) { this.sliderVal = val_; }
	public void setDefaultVal(float val_) { this.defaultVal = val_; }

	private Consumer<Float> operation;

	public Slider(String property_, String label_, Pair<Float> position_, float width_, Consumer<Float> operation_) {
		this.property = property_;
		this.label = label_;

		this.position = position_;
		this.width = width_;

		this.sliderBounds = new Pair<Float>(0.0f, 100.0f);
		this.sliderVal = 0.0f;
		this.defaultVal = 0.0f;

		this.operation = operation_;
	}

	@Override
	public void update(BasicGameState gs, long cTime, int delta) {

	}

	@Override
	public void render(GameState gs, Graphics g, long cTime) {
		// Draw the label just above the slider.
		g.setColor(Color.white);
		g.setFont(AssetManager.getManager().getFont("PressStart2P-Regular"));
		g.drawString(label, position.x, (position.y - g.getFont().getLineHeight() - 5.0f));

		// Draw the slider bar in the center of the bounds of the slider.
		g.setLineWidth(2.0f);
		g.drawLine(position.x, (position.y + (HEIGHT / 2)), (position.x + width), (position.y + (HEIGHT / 2)));
		g.setLineWidth(1.0f);

		// Draw the bars at the edge of the slider.
		g.drawLine(position.x, position.y, position.x, (position.y + HEIGHT));
		g.drawLine((position.x + width), position.y, (position.x + width), (position.y + HEIGHT));

		// Draw the slider bar.
		// Determine the bar position by figuring out what percentage the slider value is between the bound values.
		float barPos = ((sliderVal - sliderBounds.x) / (sliderBounds.y - sliderBounds.x)) * width;
		g.setLineWidth(5.0f);
		g.drawLine((position.x + barPos), (position.y + 2.0f), (position.x + barPos), (position.y + HEIGHT - 2.0f));
		g.setLineWidth(1.0f);
	}

	public void apply(boolean save) {
		if(save) {
			defaultVal = sliderVal;
		} else {
			sliderVal = defaultVal;
			operation.accept(defaultVal); // Revert to old value.
		}

		ConfigManager.getInstance().getAttributes().set(property, sliderVal);
	}

	public boolean contains(float x, float y) {
		return ((x >= position.x) && (x <= (position.x + width)) &&
				(y >= position.y) && (y <= (position.y + HEIGHT)));
	}

	public boolean contains(Pair<Float> pos_) {
		return contains(pos_.x, pos_.y);
	}

	public void move() {
		// Calculate ratio of mouse position to slider value.
		float mx = Controls.getInstance().getMouse().getPosition().x;
		float pos = mx;

		if(mx < position.x) pos = position.x;
		else if(mx > (position.x + width)) pos = (position.x + width);

		float val = (pos - position.x) / width; // Value between 0.0 and 1.0 representing percentage of full slider width.

		// Figure out the percentage of the difference between the slider bounds and add the lower bound.
		setSliderVal((val * (sliderBounds.y - sliderBounds.x)) + sliderBounds.x); // This gets us the distance between bounds equivalent to the slider position.

		// Save the value on the slider by accepting the operation attached to it.
		operation.accept(getSliderVal());
	}

	@Override
	public String getName() { return "Slider"; }

	@Override
	public String getTag() { return "slider"; }

	@Override
	public String getDescription() { return "A sliding value that allows the user to adjust a property."; }

	@Override
	public int getLayer() { return Layers.NONE.val(); }
}
