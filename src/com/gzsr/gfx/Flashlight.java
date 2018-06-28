package com.gzsr.gfx;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import com.gzsr.AssetManager;
import com.gzsr.Globals;
import com.gzsr.entities.Player;
import com.gzsr.misc.Pair;
import com.gzsr.status.Status;

public class Flashlight {
	private static Color SHADOW = new Color(0xCC000009);
	private static final Color NIGHT_VISION = new Color(0x66004409);
	
	public static float getShadowOpacity() { return Flashlight.SHADOW.a; }
	public static void setShadowOpacity(float val_) { Flashlight.SHADOW.a = val_; }
	
	private Pair<Float> origin;
	public Pair<Float> getOrigin() { return origin; }
	public void setOrigin(float x_, float y_) {
		origin.x = x_;
		origin.y = y_;
	}
	
	private float theta;
	public float getTheta() { return theta; }
	public void setTheta(float theta_) { this.theta = theta_; }
	
	private Image lightMap;
	private Image flashlight;
	
	public Flashlight() {
		this.origin = new Pair<Float>(0.0f, 0.0f);
		this.theta = 0.0f;
		
		this.lightMap = AssetManager.getManager().getImage("GZS_LightAlphaMap3");
		this.flashlight = AssetManager.getManager().getImage("GZS_Flashlight3");
	}
	
	public void update(Player player, long cTime) {
		origin.x = player.getPosition().x;
		origin.y = player.getPosition().y;
		theta = player.getRotation();
	}

	public void render(Graphics g, long cTime) {
		g.clearAlphaMap();
		
		g.setDrawMode(Graphics.MODE_NORMAL);
		g.setColor((Player.getPlayer().hasStatus(Status.NIGHT_VISION)) ? NIGHT_VISION : SHADOW);
		g.fillRect(0.0f, 0.0f, Globals.WIDTH, Globals.HEIGHT);
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_DST_COLOR, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		if(Player.getPlayer().isAlive()) {
			if(lightMap != null) g.drawImage(lightMap, (origin.x - (lightMap.getWidth() / 2)), (origin.y - (lightMap.getHeight() / 2)));
			if(flashlight != null) {
				g.rotate(origin.x, origin.y, (float)Math.toDegrees(theta - (Math.PI / 2)));
				g.drawImage(flashlight, (origin.x + 18.0f), (origin.y - 96.0f));
				g.resetTransform();
			}
		}
		
		GL11.glDisable(GL11.GL_BLEND);
		g.setDrawMode(Graphics.MODE_NORMAL);
	}
}
