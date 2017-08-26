package com.gzsr;

import java.util.HashMap;
import java.util.Map;

import org.newdawn.slick.Image;

public class AssetManager {
	private static AssetManager instance = null;
	
	private Map<String, Image> images = null;
	
	private AssetManager() {
		images = new HashMap<String, Image>();
	}
	
	public static AssetManager getManager() {
		return (instance == null) ? new AssetManager() : instance;
	}
	
	public void addImage(String key, Image img) {
		if(images != null) {
			images.put(key, img);
			System.out.println(String.format("Asset Loaded: %s", key));
		}
	}
	
	public Image getImage(String key) {
		if((images != null) && (key != null) &&
		   (!key.equals(""))) {
			return images.get(key);
		}
		
		return null;
	}
}
