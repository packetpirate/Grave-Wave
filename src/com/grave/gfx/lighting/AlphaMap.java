package com.grave.gfx.lighting;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.BasicGameState;

import com.grave.entities.Entity;
import com.grave.gfx.Layers;
import com.grave.states.GameState;

public class AlphaMap implements Entity {
	private static AlphaMap instance = null;
	public static AlphaMap getInstance() {
		if(instance == null) instance = new AlphaMap();
		return instance;
	}

	private List<LightSource> lights;
	public List<LightSource> getLights() { return lights; }
	public void addLight(LightSource source) { lights.add(source); }

	private AlphaMap() {
		lights = new ArrayList<LightSource>();
	}

	@Override
	public void update(BasicGameState gs, long cTime, int delta) {

	}

	@Override
	public void render(GameState gs, Graphics g, long cTime) {

	}

	@Override
	public String getName() { return "Alpha Map"; }

	@Override
	public String getDescription() { return "Alpha Map"; }

	@Override
	public String getTag() { return "alphamap"; }

	@Override
	public int getLayer() { return Layers.LIGHT.val(); }
}
