package com.grave.gfx.lighting;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.state.BasicGameState;

import com.grave.AssetManager;
import com.grave.Globals;
import com.grave.gfx.Camera;
import com.grave.misc.Pair;
import com.grave.states.GameState;

public class RadialLight extends LightSource {
	private static final String IMAGE_NAME = "GZS_LightAlphaMap3";

	private float radius;
	public float getRadius() { return radius; }

	public RadialLight(Pair<Float> position_, float radius_) {
		super(position_);
		radius = radius_;
	}

	@Override
	public void update(BasicGameState gs, long cTime, int delta) {

	}

	@Override
	public void render(GameState gs, Graphics g, long cTime) {
		if(isActive() && inCameraBounds()) {
			Image lightMap = AssetManager.getManager().getImage(IMAGE_NAME);
			float scale = ((radius * 2) / lightMap.getWidth());
			lightMap.draw((position.x - radius), (position.y - radius), scale);
		}
	}

	public boolean inCameraBounds() {
		Pair<Float> camera = Camera.getCamera().getOffset();
		return ((position.x >= (camera.x - radius)) && (position.x < (camera.x + Globals.WIDTH + radius)) &&
				(position.y >= (camera.y - radius)) && (position.y < (camera.y + Globals.HEIGHT + radius)));
	}

	@Override
	public String getName() { return "Radial Light"; }

	@Override
	public String getDescription() { return "Radial Light"; }
}
