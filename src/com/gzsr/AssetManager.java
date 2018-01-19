package com.gzsr;

import java.awt.Color;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.font.effects.Effect;

import com.gzsr.gfx.Animation;

public class AssetManager {
	private static int ASSETS_TO_LOAD = 0;
	private static int ASSETS_LOADED = 0;
	
	private static AssetManager instance = null;
	
	private Map<String, Image> images = null;
	private Map<String, Animation> animations = null;
	private Map<String, Sound> sounds = null;
	private Map<String, UnicodeFont> fonts = null;
	
	private AssetManager() {
		images = new HashMap<String, Image>();
		animations = new HashMap<String, Animation>();
		sounds = new HashMap<String, Sound>();
		fonts = new HashMap<String, UnicodeFont>();
	}
	
	public static int assetsToLoad() {
		return ASSETS_TO_LOAD;
	}
	
	public static int assetsLoaded() {
		return ASSETS_LOADED;
	}
	
	public static AssetManager getManager() {
		if(instance == null) instance = new AssetManager();
		return instance;
	}
	
	public void addImage(String key, String img) throws SlickException {
		if(images != null) {
			AssetManager.ASSETS_TO_LOAD++;
			
			Image image = new Image(img);
			images.put(key, image);
			
			AssetManager.ASSETS_LOADED++;
			System.out.println(String.format("Image Loaded: %s", key));
		}
	}
	
	public Image getImage(String key) {
		if((images != null) && (key != null) && (!key.equals(""))) {
			return images.get(key);
		}
		
		return null;
	}
	
	public void addAnimation(String key, Animation anim) throws SlickException {
		if(animations != null) {
			AssetManager.ASSETS_TO_LOAD++;
			
			animations.put(key, anim);
			
			AssetManager.ASSETS_LOADED++;
			System.out.println(String.format("Animation Loaded: %s", key));
		}
	}
	
	public Animation getAnimation(String key) {
		if((animations != null) && (key != null) && (!key.equals(""))) {
			return new Animation(animations.get(key));
		}
		
		return null;
	}
	
	public void addSound(String key, String snd) throws SlickException {
		if(sounds != null) {
			AssetManager.ASSETS_TO_LOAD++;
			
			Sound sound = new Sound(snd);
			sounds.put(key, sound);
			
			AssetManager.ASSETS_LOADED++;
			System.out.println(String.format("Sound Loaded: %s", key));
		}
	}
	
	public Sound getSound(String key) {
		if((sounds != null) && (key != null) && (!key.equals(""))) {
			return sounds.get(key);
		}
		
		return null;
	}
	
	public void addFont(String key, String file, int size, boolean bold, boolean italic) throws SlickException {
		addFont(key, file, size, bold, italic, new Effect[] { new ColorEffect(Color.WHITE) });
	}
	
	@SuppressWarnings("unchecked")
	public void addFont(String key, String file, int size, boolean bold, boolean italic, Effect [] effects) throws SlickException {
		try {
			AssetManager.ASSETS_TO_LOAD++;
			
			UnicodeFont uni = new UnicodeFont(file, size, bold, italic);
			uni.addAsciiGlyphs();
			uni.addGlyphs(400, 600);
			uni.getEffects().add(new ColorEffect(Color.WHITE));
			uni.getEffects().addAll(Arrays.asList(effects));
			uni.loadGlyphs();
			if((fonts != null) && (uni != null)) {
				fonts.put(key, uni);
				
				AssetManager.ASSETS_LOADED++;
				System.out.println(String.format("Font Loaded: %s", key));
			}
		} catch(Exception ex) {
			ex.printStackTrace();
			System.out.printf("ERROR: Font \"%s\" could not be loaded!\n", file);
		}
	}
	
	public UnicodeFont getFont(String key) {
		if((fonts != null) && (key != null) && (!key.equals(""))) {
			return fonts.get(key);
		}
		
		return null;
	}
}
