package gzs.game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;

public class FileUtilities {
	public static Texture LoadTexture(String filename) {
		return new Texture("images/" + filename);
	}
	
	public static Sound LoadSound(String filename) {
		return Gdx.audio.newSound(Gdx.files.internal("sounds/" + filename));
	}
}
