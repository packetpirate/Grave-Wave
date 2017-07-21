package gzs.game.utils;

import java.io.File;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class SoundManager {
	public static Media LoadSound(String filename) {
		if((filename == null) || (filename.isEmpty())) return null;
		
		Media media = new Media(new File("res/sounds/" + filename).toURI().toString());
		
		return media;
	}
	
	public static void PlaySound(Media sound) {
		MediaPlayer player = new MediaPlayer(sound);
		player.play();
	}
}
