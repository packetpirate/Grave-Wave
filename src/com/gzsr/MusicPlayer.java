package com.gzsr;

import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;

public class MusicPlayer {
	private static final int SOUNDTRACK_LENGTH = 9;
	private static final float DEFAULT_MUSIC_VOLUME = 0.15f;
	
	private static MusicPlayer instance;
	
	private boolean autoplay;
	private int currentSong; // To keep track of current soundtrack song.
	private Music nowPlaying;
	
	public static MusicPlayer getInstance() {
		if(instance == null) instance = new MusicPlayer();
		return instance;
	}
	
	private MusicPlayer() {
		autoplay = true;
		currentSong = 0;
		nowPlaying = null;
	}
	
	public void update(boolean menu) throws SlickException {
		if((nowPlaying != null) && !nowPlaying.playing() && autoplay) {
			if(!menu) nextSong();
			else nowPlaying.play(1.0f, getMusicVolume()); // If we're on the main menu, we effectively want to "loop" the song.
		}
	}
	
	public void pause() {
		if((nowPlaying != null) && nowPlaying.playing()) {
			nowPlaying.pause();
		}
	}
	
	public void resume() {
		if((nowPlaying != null) && !nowPlaying.playing()) {
			nowPlaying.resume();
		}
	}
	
	public void reset() {
		currentSong = 0;
		
		if(nowPlaying != null) {
			nowPlaying.pause();
			nowPlaying = null;
		}
	}
	
	public void nextSong() throws SlickException {
		currentSong++;
		if(currentSong > SOUNDTRACK_LENGTH) currentSong = 1; 
		
		String songName = String.format("soundtrack_%02d", currentSong);
		Music song = new Music(String.format("music/%s.ogg", songName), true);
		if(song != null) {
			nowPlaying = song;
			nowPlaying.play(1.0f, getMusicVolume());
		}
	}
	
	public float getMusicVolume() {
		if(!ConfigManager.getInstance().getAttributes().getMap().containsKey("musicVolume")) return DEFAULT_MUSIC_VOLUME;
		else return ConfigManager.getInstance().getAttributes().getFloat("musicVolume");
	}
	
	public void setMusicVolume(float val_) {
		if(val_ < 0.0f) val_ = 0.0f;
		else if(val_ > 1.0f) val_ = 1.0f;
		
		ConfigManager.getInstance().getAttributes().set("musicVolume", val_);
		if(nowPlaying != null) nowPlaying.setVolume(val_);
	}
}
