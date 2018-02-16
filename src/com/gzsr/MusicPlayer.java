package com.gzsr;

import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;

public class MusicPlayer {
	private static final int SOUNDTRACK_LENGTH = 15;
	
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
		Music song = new Music(String.format("music/%s.ogg", songName));
		if(song != null) {
			nowPlaying = song;
			nowPlaying.play();
		}
		
		System.out.println("Playing next song: " + songName);
	}
}
