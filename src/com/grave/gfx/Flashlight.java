package com.grave.gfx;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.state.BasicGameState;

import com.grave.AssetManager;
import com.grave.Globals;
import com.grave.entities.Player;
import com.grave.gfx.lighting.LightSource;
import com.grave.misc.Pair;
import com.grave.states.GameState;

public class Flashlight extends LightSource {
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

	public Flashlight(Pair<Float> origin_) {
		super(origin_);
		this.origin = origin_;
		this.theta = 0.0f;

		this.lightMap = AssetManager.getManager().getImage("GZS_LightAlphaMap3");
		this.flashlight = AssetManager.getManager().getImage("GZS_Flashlight");
		this.oCollider = new Rectangle(18.0f, -192.0f, flashlight.getWidth(), (flashlight.getHeight() * 2));
		this.tCollider = new Rectangle(18.0f, -192.0f, flashlight.getWidth(), (flashlight.getHeight() * 2));
	}

	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
		Player player = Player.getPlayer();

		origin.x = player.getPosition().x;
		origin.y = player.getPosition().y;
		theta = player.getRotation();

		oCollider.setX(origin.x + 18.0f);
		oCollider.setY(origin.y - 192.0f);
		tCollider = oCollider.transform(Transform.createRotateTransform((theta - (float)(Math.PI / 2)), origin.x, origin.y));
	}

	@Override
	public void render(GameState gs, Graphics g, long cTime) {
		if(isActive() && Player.getPlayer().isAlive()) {
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
	}

	public boolean inView(Shape other) { return (tCollider.intersects(other) || tCollider.contains(other)); }

	@Override
	public String getName() { return "Flashlight"; }

	@Override
	public String getDescription() { return "Flashlight"; }

	@Override
	public String getTag() { return "flashlight"; }
}
