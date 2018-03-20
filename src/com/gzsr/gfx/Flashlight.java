package com.gzsr.gfx;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.fills.GradientFill;
import org.newdawn.slick.geom.Polygon;

import com.gzsr.AssetManager;
import com.gzsr.Globals;
import com.gzsr.entities.Player;
import com.gzsr.misc.Pair;

public class Flashlight {
	private static final Color TRANSPARENT = new Color(0x01000000);
	private static final Color SHADOW = new Color(0x99000010);
	private static final Color BEAM = new Color(0x99FFFFF0);
	private static final float INTENSITY = 400.0f;
	private static final float BEAM_WIDTH = 40.0f;
	
	private float [] xPoints;
	private float [] yPoints;
	private Pair<Float> origin;
	private float theta;
	
	private Image lightMap;
	private Polygon flashlight;
	private GradientFill flashlightFill;
	
	public Flashlight() {
		this.xPoints = new float[] { 0.0f, 0.0f, 0.0f };
		this.yPoints = new float[] { 0.0f, 0.0f, 0.0f };
		this.origin = new Pair<Float>(0.0f, 0.0f);
		this.theta = 0.0f;
		
		this.lightMap = AssetManager.getManager().getImage("GZS_LightAlphaMap3");
		this.flashlight = null;
		this.flashlightFill = new GradientFill(0.0f, 0.0f, TRANSPARENT, 0.0f, -INTENSITY, SHADOW, true);
	}
	
	public void update(Player player, long cTime) {
		origin.x = player.getPosition().x;
		origin.y = player.getPosition().y;
		theta = player.getRotation();
		
		xPoints[0] = origin.x;
		yPoints[0] = origin.y;
		
		xPoints[1] = (origin.x + (Flashlight.INTENSITY * (float)Math.cos((theta - (Math.PI / 2)) - Math.toRadians((Flashlight.BEAM_WIDTH / 2)))));
		yPoints[1] = (origin.y + (Flashlight.INTENSITY * (float)Math.sin((theta - (Math.PI / 2)) - Math.toRadians((Flashlight.BEAM_WIDTH / 2)))));
		
		xPoints[2] = (origin.x + (Flashlight.INTENSITY * (float)Math.cos((theta - (Math.PI / 2)) + Math.toRadians((Flashlight.BEAM_WIDTH / 2)))));
		yPoints[2] = (origin.y + (Flashlight.INTENSITY * (float)Math.sin((theta - (Math.PI / 2)) + Math.toRadians((Flashlight.BEAM_WIDTH / 2)))));
		
		float [] points = new float[] {
			xPoints[0], yPoints[0],
			xPoints[1], yPoints[1],
			xPoints[2], yPoints[2]
		};
		
		flashlight = new Polygon(points);
	}

	public void render(Graphics g, Player player, long cTime) {
		g.clearAlphaMap();
		
		g.setDrawMode(Graphics.MODE_NORMAL);
		g.setColor(SHADOW);
		g.fillRect(0.0f, 0.0f, Globals.WIDTH, Globals.HEIGHT);
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_DST_COLOR, GL11.GL_ONE_MINUS_SRC_ALPHA);
		if(lightMap != null) g.drawImage(lightMap, (origin.x - (lightMap.getWidth() / 2)), (origin.y - (lightMap.getHeight() / 2)));
		if(flashlight != null) {
			//g.fill(flashlight, flashlightFill);
		}
		GL11.glDisable(GL11.GL_BLEND);
		g.setDrawMode(Graphics.MODE_NORMAL);
	}
}
