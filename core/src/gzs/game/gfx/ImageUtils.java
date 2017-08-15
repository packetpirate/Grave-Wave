package gzs.game.gfx;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ImageUtils {
	public static void Draw(SpriteBatch batch, Texture img, int x, int y) {
		batch.draw(img, x, y, img.getWidth(), img.getHeight(), 0, 0, img.getWidth(), img.getHeight(), false, true);
	}
}
