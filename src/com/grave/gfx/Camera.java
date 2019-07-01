package com.grave.gfx;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import com.grave.Globals;
import com.grave.entities.Player;
import com.grave.misc.Pair;
import com.grave.tmx.TMap;

public class Camera {
	private static final long VIGNETTE_DURATION = 500L;
	public static final Color VIGNETTE_COLOR = new Color(0x22880000);
	public static final float MAX_OFFSET = 20.0f;

	private enum Direction {
		NONE(0, 0), LEFT(-1, 0), UP(0, -1), RIGHT(1, 0), DOWN(0, 1),
		TOPLEFT(-1, -1), TOPRIGHT(1, -1), BOTTOMRIGHT(1, 1), BOTTOMLEFT(-1, 1);

		private int x, y;
		public int getX() { return x; }
		public int getY() { return y; }

		Direction(int x_, int y_) {
			this.x = x_;
			this.y = y_;
		}
	}

	public static class ShakeEffect {
		protected long duration;
		protected long interval;
		protected float magnitude;

		public ShakeEffect(long duration_, long interval_, float magnitude_) {
			this.duration = duration_;
			this.interval = interval_;
			this.magnitude = magnitude_;
		}
	}

	private static Camera camera;
	public static Camera getCamera() {
		if(camera == null) camera = new Camera();
		return camera;
	}

	private Pair<Float> offset;
	public Pair<Float> getOffset() { return offset; }

	private Pair<Float> shakeOff;
	public Pair<Float> getShakeOffset() { return shakeOff; }

	public Pair<Float> getTotalOffset() { return new Pair<Float>((offset.x + shakeOff.x), (offset.y + shakeOff.y)); }

	private boolean shaking;
	public boolean isShaking() { return shaking; }
	public void setShaking(boolean shaking_) { this.shaking = shaking_; }

	private float magnitude;
	public float getShakeMagnitude() { return magnitude; }
	public void setShakeMagnitude(float magnitude_) { this.magnitude = magnitude_; }

	private boolean vignette;
	public boolean displayVignette() { return vignette; }

	private long shakeStart, shakeDuration, shakeInterval, lastShake;
	private long lastVignette;

	private Direction lastDirection;

	private Camera() {
		offset = new Pair<Float>(0.0f, 0.0f);
		shakeOff = new Pair<Float>(0.0f, 0.0f);

		shaking = false;
		magnitude = 0.0f;

		vignette = false;

		shakeStart = 0L;
		shakeDuration = 0L;
		shakeInterval = 0L;
		lastShake = 0L;
		lastVignette = 0L;
		lastDirection = Direction.NONE;
	}

	public void update(long cTime) {
		if(isShaking()) {
			long elapsed = (cTime - shakeStart);
			if(elapsed >= shakeDuration) {
				stop();

				magnitude = 0.0f;
				shaking = false;
			} else if((cTime - lastShake) >= shakeInterval) {
				// Magnitude reduction.
				float percentageTimeLeft = 1.0f - ((float)elapsed / (float)shakeDuration);

				// Determine which way to shake the camera.
				List<Direction> validMoves = new ArrayList<Direction>();
				for(Direction dir : Direction.values()) {
					if(!dir.equals(lastDirection)) {
						float oX = dir.getX() * magnitude * percentageTimeLeft;
						float oY = dir.getY() * magnitude * percentageTimeLeft;
						if(inBounds((shakeOff.x + oX), (shakeOff.y + oY))) {
							validMoves.add(dir);
						}
					}
				}

				if(!validMoves.isEmpty()) {
					Direction move = validMoves.get(Globals.rand.nextInt(validMoves.size()));

					shakeOff.x = move.getX() * magnitude * percentageTimeLeft;
					shakeOff.y = move.getY() * magnitude * percentageTimeLeft;

					lastDirection = move;
				}

				lastShake = cTime;
			}
		}

		if(vignette) {
			long elapsed = (cTime - lastVignette);
			if(elapsed >= VIGNETTE_DURATION) vignette = false;
		}
	}

	private boolean inBounds(float x, float y) {
		return ((x >= -MAX_OFFSET) && (x <= MAX_OFFSET) &&
				(y >= -MAX_OFFSET) && (y <= MAX_OFFSET));
	}

	public void translate(Graphics g) {
		float x = -offset.x + shakeOff.x;
		float y = -offset.y + shakeOff.y;
		g.translate(x, y);
	}

	public void shake(long cTime, long duration, long interval, float mag) {
		shaking = true;
		magnitude = mag;

		shakeStart = cTime;
		shakeDuration = duration;
		shakeInterval = interval;
		lastShake = 0L;
	}

	public void shake(ShakeEffect effect, long cTime) {
		shaking = true;
		magnitude = effect.magnitude;

		shakeStart = cTime;
		shakeDuration = effect.duration;
		shakeInterval = effect.interval;
		lastShake = 0L;
	}

	public void refreshShake(long cTime) {
		shaking = true;
		shakeStart = cTime;
		lastShake = (cTime - shakeInterval);
	}

	public void stop() {
		shakeOff.x = 0.0f;
		shakeOff.y = 0.0f;

		shaking = false;

		shakeStart = 0L;
		shakeDuration = 0L;
		shakeInterval = 0L;
		lastShake = 0L;
		lastDirection = Direction.NONE;
	}

	public void move(float x, float y) {
		offset.x = x;
		offset.y = y;
	}

	public void addOffset(float x, float y) {
		offset.x += x;
		offset.y += y;
	}

	public void focusOnPlayer(TMap map) {
		Player player = Player.getPlayer();
		offset.x = (player.getPosition().x - (Globals.WIDTH / 2));
		offset.y = (player.getPosition().y - (Globals.HEIGHT / 2));

		// Constrain the camera to the size of the map.
		float w = map.getMapWidthTotal();
		float h = map.getMapHeightTotal();

		if(offset.x < 0.0f) offset.x = 0.0f;
		else if((offset.x + Globals.WIDTH) >= w) offset.x = (w - Globals.WIDTH);

		if(offset.y < 0.0f) offset.y = 0.0f;
		else if((offset.y + Globals.HEIGHT) >= h) offset.y = (h - Globals.HEIGHT);
	}

	public void reset() {
		offset.x = 0.0f;
		offset.y = 0.0f;

		shaking = false;
		vignette = false;

		shakeStart = 0L;
		shakeDuration = 0L;
		shakeInterval = 0L;
		lastShake = 0L;
		lastVignette = 0L;
		lastDirection = Direction.NONE;
	}

	public void damage(long cTime) {
		vignette = true;
		lastVignette = cTime;
	}
}
