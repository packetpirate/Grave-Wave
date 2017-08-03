package gzs.game.gfx;

import gzs.game.misc.MouseInfo;
import javafx.scene.canvas.GraphicsContext;

public interface Screen {
	public boolean hidesCursor();
	public void update(long cT) throws Exception;
	public void render(GraphicsContext gc, long cT) throws Exception;
	public void dispatchClick(MouseInfo mouse) throws Exception;
	public void dispatchScroll(int direction);
}
