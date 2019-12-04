package com.grave.gfx.lighting;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.BasicGameState;

import com.grave.entities.Entity;
import com.grave.gfx.Layers;
import com.grave.states.GameState;

public class LightSource implements Entity {
	public LightSource() {

	}

	@Override
	public void update(BasicGameState gs, long cTime, int delta) {

	}

	@Override
	public void render(GameState gs, Graphics g, long cTime) {

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
