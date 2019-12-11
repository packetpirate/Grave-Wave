package com.grave.gfx.lighting;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.BasicGameState;

import com.grave.entities.Entity;
import com.grave.gfx.Layers;
import com.grave.misc.Pair;
import com.grave.states.GameState;

public class LightSource implements Entity {
	protected Pair<Float> position;
	public Pair<Float> getPosition() { return position; }

	public void move(float xOff, float yOff) {
		position.x += xOff;
		position.y += yOff;
	}

	public void move(Pair<Float> dest) {
		position = dest;
	}

	private boolean active;
	public boolean isActive() { return active; }
	public void activate() { active = true; }
	public void deactivate() { active = false; }
	public void toggle() { active = !active; }

	public LightSource(Pair<Float> position_) {
		position = position_;
		active = false;
	}

	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
		// To be overridden...
	}

	@Override
	public void render(GameState gs, Graphics g, long cTime) {
		// To be overridden...
	}

	@Override
	public String getName() { return "Light Source"; }

	@Override
	public String getDescription() { return "Light Source"; }

	@Override
	public String getTag() { return "light"; }

	@Override
	public int getLayer() { return Layers.LIGHT.val(); }
}
