package com.gzsr;

import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;

public class MusicPlayer {
	private static final int SOUNDTRACK_LENGTH = 9;
	
	private float MUSIC_VOLUME = 0.5f;
	
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
	
	public void update() throws SlickException {
		if((nowPlaying != null) && !nowPlaying.playing() && autoplay) {
			nextSong();
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
			nowPlaying.play(1.0f, MUSIC_VOLUME);
		}
	}
	
	public float getMusicVolume() {
		return MUSIC_VOLUME;
	}
	
	public void setMusicVolume(float val_) {
		if(val_ < 0.0f) MUSIC_VOLUME = 0.0f;
		else if(val_ > 1.0f) MUSIC_VOLUME = 1.0f;
		else MUSIC_VOLUME = val_;
		
		if(nowPlaying != null) nowPlaying.setVolume(MUSIC_VOLUME);
	}
}
