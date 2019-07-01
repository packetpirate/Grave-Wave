package com.grave.gfx.ui;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.state.BasicGameState;

import com.grave.AssetManager;
import com.grave.entities.Entity;
import com.grave.gfx.Layers;
import com.grave.misc.Pair;

public class SlidingText implements Entity {
	private static final String FONT_NAME = "PressStart2P-Regular";

	private String text;
	public void setText(String text_) { this.text = text_; }

	private float sX, eX;
	private Pair<Float> position;
	private float speed;

	private float sO, eO, cO;

	private boolean fromRight, moving, done;
	public void start() { moving = true; }
	public boolean started() { return (moving ^ done); }
	public boolean isDone() { return done; }

	public SlidingText(String text_, float sX_, float eX_, float y_, float speed_) {
		this(text_, sX_, eX_, y_, speed_, 1.0f, 1.0f, false);
	}

	public SlidingText(String text_, float sX_, float eX_, float y_, float speed_, boolean fromRight_) {
		this(text_, sX_, eX_, y_, speed_, 1.0f, 1.0f, fromRight_);
	}

	public SlidingText(String text_, float sX_, float eX_, float y_, float speed_, float sO_, float eO_) {
		this(text_, sX_, eX_, y_, speed_, 1.0f, 1.0f, false);
	}

	public SlidingText(String text_, float sX_, float eX_, float y_, float speed_, float sO_, float eO_, boolean fromRight_) {
		this.text = text_;

		this.sX = sX_;
		this.eX = eX_;
		this.position = new Pair<Float>(sX_, y_);
		this.speed = speed_;

		this.sO = sO_;
		this.eO = eO_;
		this.cO = sO_;

		this.fromRight = fromRight_;
		this.moving = false;
		this.done = false;
	}

	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
		if(moving) {
			if(fromRight) {
				if(position.x > eX) {
					position.x -= speed;
				} else {
					moving = false;
					done = true;
				}
			} else {
				if(position.x < eX) {
					position.x += speed;
				} else {
					moving = false;
					done = true;
				}
			}

			cO = (sO + ((Math.abs(position.x - sX) / Math.abs(eX - sX)) * (eO - sO)));
		}
	}

	@Override
	public void render(Graphics g, long cTime) {
		UnicodeFont font = AssetManager.getManager().getFont(FONT_NAME);
		Color color = new Color(1.0f, 1.0f, 1.0f, cO);

		g.setFont(font);
		g.setColor(color);

		float lineHeight = font.getLineHeight();
		g.drawString(text, position.x, (position.y - (lineHeight / 2)));
	}

	public void reset() {
		position.x = sX;

		cO = sO;

		moving = false;
		done = false;
	}

	@Override
	public String getName() { return "Sliding Text"; }

	@Override
	public String getTag() { return "slidingText"; }

	@Override
	public String getDescription() { return "Sliding Text"; }

	@Override
	public int getLayer() { return Layers.NONE.val(); }
}
