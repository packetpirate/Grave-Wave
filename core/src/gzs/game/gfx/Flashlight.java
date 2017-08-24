package gzs.game.gfx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import gzs.entities.Player;
import gzs.game.misc.Pair;

public class Flashlight {
	private static final Color COLOR = Color.WHITE;
	private static final float INTENSITY = 400.0f;
	private static final float BEAM_WIDTH = 40.0f;
	
	private float [] xPoints;
	private float [] yPoints;
	private Pair<Float> origin;
	private float theta;
	
	public Flashlight() {
		this.xPoints = new float[] { 0.0f, 0.0f, 0.0f };
		this.yPoints = new float[] { 0.0f, 0.0f, 0.0f };
		this.origin = new Pair<Float>(0.0f, 0.0f);
		this.theta = 0.0f;
	}
	
	public void update(Player player, long cTime) {
		origin.x = player.getPosition().x;
		origin.y = player.getPosition().y;
		theta = player.getRotation();
		
		xPoints[0] = origin.x;
		yPoints[0] = origin.y;
		
		xPoints[1] = (origin.x + (Flashlight.INTENSITY * (float)Math.cos(theta - Math.toRadians((Flashlight.BEAM_WIDTH / 2)))));
		yPoints[1] = (origin.y + (Flashlight.INTENSITY * (float)Math.sin(theta - Math.toRadians((Flashlight.BEAM_WIDTH / 2)))));
		
		xPoints[2] = (origin.x + (Flashlight.INTENSITY * (float)Math.cos(theta + Math.toRadians((Flashlight.BEAM_WIDTH / 2)))));
		yPoints[2] = (origin.y + (Flashlight.INTENSITY * (float)Math.sin(theta + Math.toRadians((Flashlight.BEAM_WIDTH / 2)))));
	}

	public void render(SpriteBatch batch, Player player, long cTime) {
		// TODO: Replace with LibGDX polygon drawing method.
	}
}