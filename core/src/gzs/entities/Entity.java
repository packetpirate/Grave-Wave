package gzs.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface Entity {
	public abstract void update(long cTime);
	public abstract void render(SpriteBatch batch, long cTime);
}