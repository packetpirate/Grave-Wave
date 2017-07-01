package gzs.game.utils;

import javafx.scene.image.Image;
import javafx.scene.text.Font;

public class FileUtilities {
	/**
	 * Retrieves the image in the given filename and creates an Image object from it.
	 * @param filename The name of the file, located in the res/images folder.
	 * @return An image object representing the image at the location given.
	 */
	public static Image LoadImage(String filename) throws Exception {
		if((filename == null) || filename.isEmpty()) return null;
		
		Image image =  new Image("file:res/images/" + filename);
		
		return image;
	}
	
	public static Font LoadFont(String filename, double size) throws Exception {
		if((filename == null) || filename.isEmpty()) return null;
		
		Font font = Font.loadFont(("file:res/fonts/" + filename), size);
		
		return font;
	}
}
