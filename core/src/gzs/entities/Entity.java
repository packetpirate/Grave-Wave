package gzs.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public interface Entity {
	public abstract void update(long cTime);
	public abstract void render(SpriteBatch batch, ShapeRenderer sr, long cTime);
}