package com.gzsr;

import java.util.HashMap;
import java.util.Map;

import org.newdawn.slick.Font;
import org.newdawn.slick.Image;
import org.newdawn.slick.Sound;

import com.gzsr.gfx.Animation;

public class AssetManager {
	private static AssetManager instance = null;
	
	private Map<String, Image> images = null;
	private Map<String, Animation> animations = null;
	private Map<String, Sound> sounds = null;
	private Map<String, Font> fonts = null;
	
	private AssetManager() {
		images = new HashMap<String, Image>();
		animations = new HashMap<String, Animation>();
		sounds = new HashMap<String, Sound>();
		fonts = new HashMap<String, Font>();
	}
	
	public static AssetManager getManager() {
		if(instance == null) instance = new AssetManager();
		return instance;
	}
	
	public void addImage(String key, Image img) {
		if(images != null) {
			images.put(key, img);
			System.out.println(String.format("Image Loaded: %s", key));
		}
	}
	
	public Image getImage(String key) {
		if((images != null) && (key != null) && (!key.equals(""))) {
			return images.get(key);
		}
		
		return null;
	}
	
	public void addAnimation(String key, Animation anim) {
		if(animations != null) {
			animations.put(key, anim);
			System.out.println(String.format("Animation Loaded: %s", key));
		}
	}
	
	public Animation getAnimation(String key) {
		if((animations != null) && (key != null) && (!key.equals(""))) {
			return new Animation(animations.get(key));
		}
		
		return null;
	}
	
	public void addSound(String key, Sound snd) {
		if(sounds != null) {
			sounds.put(key, snd);
			System.out.println(String.format("Sound Loaded: %s", key));
		}
	}
	
	public Sound getSound(String key) {
		if((sounds != null) && (key != null) && (!key.equals(""))) {
			return sounds.get(key);
		}
		
		return null;
	}
	
	public void addFont(String key, Font fnt) {
		if(fonts != null) {
			fonts.put(key, fnt);
			System.out.println(String.format("Font Loaded: %s", key));
		}
	}
	
	public Font getFont(String key) {
		if((fonts != null) && (key != null) && (!key.equals(""))) {
			return fonts.get(key);
		}
		
		return null;
	}
}
