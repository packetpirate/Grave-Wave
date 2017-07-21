package gzs.game.utils;

import javafx.scene.image.Image;
import javafx.scene.text.Font;

public class FileUtilities {
	/**
	 * Retrieves the image in the given filename and creates an Image object from it.
	 * @param filename The name of the file, located in the res/images folder.
	 * @return An image object representing the image at the location given.
	 */
	public static Image LoadImage(String filename) {
		if((filename == null) || filename.isEmpty()) return null;
		
		try {
			Image image =  new Image("file:res/images/" + filename);
			return image;
		} catch(Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	public static Font LoadFont(String filename, double size) {
		if((filename == null) || filename.isEmpty()) return null;
		
		try {
			Font font = Font.loadFont(("file:res/fonts/" + filename), size);
			return font;
		} catch(Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
}
