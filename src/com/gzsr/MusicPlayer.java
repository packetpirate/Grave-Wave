package com.gzsr;

import java.util.HashMap;
import java.util.Map;

import org.newdawn.slick.Music;

public class MusicPlayer {
	private static MusicPlayer instance;
	
	private boolean autoplay;
	private int currentSong; // To keep track of current soundtrack song.
	private Music nowPlaying;
	private Map<String, Music> music;
	
	public static MusicPlayer getInstance() {
		if(instance == null) instance = new MusicPlayer();
		return instance;
	}
	
	private MusicPlayer() {
		autoplay = true;
		currentSong = 1;
		nowPlaying = null;
		music = new HashMap<String, Music>();
	}
	
	public void addSong(String key, Music m) {
		if((music != null) && (key != null) && (!key.equals(""))) {
			music.put(key, m);
			System.out.println(String.format("Music Loaded: %s", key));
		}
	}
	
	public void update() {
		String songName = String.format("soundtrack_%02d", currentSong);
		Music song = music.get(songName);
		if((song != null) && !song.playing() && autoplay) {
			nextSong();
		}
	}
	
	public void pause() {
		if((nowPlaying != null) && !nowPlaying.playing()) {
			nowPlaying.pause();
		}
	}
	
	public void resume() {
		if((nowPlaying != null) && nowPlaying.playing()) {
			nowPlaying.resume();
		}
	}
	
	public void nextSong() {
		String songName = String.format("soundtrack_%02d", currentSong);
		Music song = music.get(songName);
		if(song != null) {
			song.pause();
		}
		currentSong++;
		
		String nextSong = String.format("soundtrack_%02d", currentSong);
		System.out.println("Playing next song: " + nextSong);
		playSong(nextSong);
	}
	
	public void playSong(String key) {
		Music song = music.get(key);
		if(song != null) {
			nowPlaying = song;
			song.play();
		}
	}
}
