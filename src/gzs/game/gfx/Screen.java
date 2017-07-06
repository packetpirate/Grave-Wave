package gzs.game.gfx;

import gzs.game.misc.MouseInfo;
import javafx.scene.canvas.GraphicsContext;

public interface Screen {
	public void update(long cT, MouseInfo mouse);
	public void render(GraphicsContext gc, long cT);
	public void dispatchClick(MouseInfo mouse);
}
