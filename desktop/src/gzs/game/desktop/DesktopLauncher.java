package gzs.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import gzs.game.GZS_Game;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "GZS Remastered";
		config.useGL30 = true;
		
		// Corrects the issue of the missing 'u_projModelView' from the default shader.
		ShaderProgram.prependVertexCode = "#version 140\n#define varying out\n#define attribute in\n";
		ShaderProgram.prependFragmentCode = "#version 140\n#define varying in\n#define texture2D texture\n#define gl_FragColor fragColor\nout vec4 fragColor;\n";
		
		config.width = 800;
		config.height = 640;
		new LwjglApplication(new GZS_Game(), config);
	}
}
