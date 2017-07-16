package gzs.entities;

import javafx.scene.canvas.GraphicsContext;

public interface Entity {
	public abstract void update(long cTime);
	public abstract void render(GraphicsContext gc, long cTime);
}
