package com.grave.gfx;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Transform;

import com.grave.AssetManager;
import com.grave.ConfigManager;
import com.grave.Globals;
import com.grave.entities.Player;
import com.grave.misc.Pair;
import com.grave.status.Status;

public class Flashlight {
	private static Color SHADOW = new Color(0xBB000009);
	private static final Color NIGHT_VISION = new Color(0x66004409);

	public static float getShadowOpacity() {
		if(!ConfigManager.getInstance().getAttributes().getMap().containsKey("shadowLevel")) return Flashlight.SHADOW.a;
		else return ConfigManager.getInstance().getAttributes().getFloat("shadowLevel");
	}
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

	private Rectangle oCollider; // Original collider.
	private Shape tCollider; // Transformed collider.

	private boolean enabled;
	public boolean isEnabled() { return enabled; }
	public void toggle() { enabled = !enabled; }

	public Flashlight() {
		this.origin = new Pair<Float>(0.0f, 0.0f);
		this.theta = 0.0f;

		this.lightMap = AssetManager.getManager().getImage("GZS_LightAlphaMap3");
		this.flashlight = AssetManager.getManager().getImage("GZS_Flashlight");
		this.oCollider = new Rectangle(18.0f, -192.0f, flashlight.getWidth(), (flashlight.getHeight() * 2));
		this.tCollider = new Rectangle(18.0f, -192.0f, flashlight.getWidth(), (flashlight.getHeight() * 2));

		this.enabled = true;
	}

	public void update(Player player, long cTime) {
		origin.x = player.getPosition().x;
		origin.y = player.getPosition().y;
		theta = player.getRotation();

		oCollider.setX(origin.x + 18.0f);
		oCollider.setY(origin.y - 192.0f);
		tCollider = oCollider.transform(Transform.createRotateTransform((theta - (float)(Math.PI / 2)), origin.x, origin.y));
	}

	public void render(Graphics g, long cTime) {
		Camera camera = Camera.getCamera();
		Player player = Player.getPlayer();

		g.clearAlphaMap();

		g.setDrawMode(Graphics.MODE_NORMAL);
		g.setColor((player.getStatusHandler().hasStatus(Status.NIGHT_VISION)) ? NIGHT_VISION : SHADOW);
		g.fillRect((camera.getOffset().x - Camera.MAX_OFFSET), (camera.getOffset().y - Camera.MAX_OFFSET),
				   (Globals.WIDTH + (Camera.MAX_OFFSET * 2)), (Globals.HEIGHT + (Camera.MAX_OFFSET * 2)));

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_DST_COLOR, GL11.GL_ONE_MINUS_SRC_ALPHA);

		if(isEnabled() && player.isAlive()) {
			if(lightMap != null) g.drawImage(lightMap, (origin.x - (lightMap.getWidth() / 2)), (origin.y - (lightMap.getHeight() / 2)));
			if(flashlight != null) {
				float a = (float)Math.toDegrees(theta - (Math.PI / 2));
				g.rotate(origin.x, origin.y, a);
				g.drawImage(flashlight, (origin.x + 22.0f), (origin.y - 88.0f));
				g.rotate(origin.x, origin.y, -a);

				if(Globals.SHOW_COLLIDERS) {
					g.setColor(Color.red);
					g.draw(tCollider);
				}
			}
		}

		GL11.glDisable(GL11.GL_BLEND);
		g.setDrawMode(Graphics.MODE_NORMAL);
	}

	public boolean inView(Shape other) { return (tCollider.intersects(other) || tCollider.contains(other)); }
}
