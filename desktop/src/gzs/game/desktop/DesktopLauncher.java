package gzs.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import gzs.game.GZS_Game;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "GZS Remastered";
		config.useGL30 = true;
		config.width = 800;
		config.height = 640;
		new LwjglApplication(new GZS_Game(), config);
	}
}
