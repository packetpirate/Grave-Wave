package com.grave.gfx.lighting;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.BasicGameState;

import com.grave.ConfigManager;
import com.grave.Globals;
import com.grave.entities.Entity;
import com.grave.entities.Player;
import com.grave.gfx.Camera;
import com.grave.gfx.Layers;
import com.grave.states.GameState;
import com.grave.status.Status;

public class AlphaMap implements Entity {
	private static Color SHADOW = new Color(0xBB000009);
	private static final Color NIGHT_VISION = new Color(0x66004409);

	public static float getShadowOpacity() {
		if(!ConfigManager.getInstance().getAttributes().getMap().containsKey("shadowLevel")) return AlphaMap.SHADOW.a;
		else return ConfigManager.getInstance().getAttributes().getFloat("shadowLevel");
	}
	public static void setShadowOpacity(float val_) { AlphaMap.SHADOW.a = val_; }

	private static AlphaMap instance = null;
	public static AlphaMap getInstance() {
		if(instance == null) instance = new AlphaMap();
		return instance;
	}

	private List<LightSource> lights;
	public List<LightSource> getLights() { return lights; }
	public void addLight(LightSource source) { lights.add(source); }
	public void clear() { lights.clear(); }

	public void removeByTag(String tag) {
		lights = lights.stream().filter(light -> !light.getTag().equals(tag)).collect(Collectors.toList());
	}

	private AlphaMap() {
		lights = new ArrayList<LightSource>();
	}

	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
		lights.stream().filter(light -> light.isActive()).forEach(light -> light.update(gs, cTime, delta));
	}

	@Override
	public void render(GameState gs, Graphics g, long cTime) {
		Camera camera = Camera.getCamera();
		Player player = Player.getPlayer();

		g.clearAlphaMap();

		g.setDrawMode(Graphics.MODE_NORMAL);
		g.setColor((player.getStatusHandler().hasStatus(Status.NIGHT_VISION)) ? NIGHT_VISION : SHADOW);
		g.fillRect((camera.getOffset().x - Camera.MAX_OFFSET), (camera.getOffset().y - Camera.MAX_OFFSET),
				   (Globals.WIDTH + (Camera.MAX_OFFSET * 2)), (Globals.HEIGHT + (Camera.MAX_OFFSET * 2)));

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_DST_COLOR, GL11.GL_ONE_MINUS_SRC_ALPHA);

		lights.stream().filter(light -> light.isActive()).forEach(light -> light.render(gs, g, cTime));

		GL11.glDisable(GL11.GL_BLEND);
		g.setDrawMode(Graphics.MODE_NORMAL);
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
