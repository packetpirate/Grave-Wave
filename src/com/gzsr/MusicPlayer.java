package com.gzsr;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;

public class MusicPlayer {
	private static int SONGS_TO_LOAD = 0;
	private static int SONGS_LOADED = 0;
	
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
		music = Collections.synchronizedMap(new HashMap<String, Music>());
	}
	
	public synchronized void addSong(String key) throws SlickException {
		if((music != null) && (key != null) && (!key.equals(""))) {
			MusicPlayer.SONGS_TO_LOAD++;
			final AtomicReference<SlickException> ref = new AtomicReference<>(); // for passing SlickException to the outside of the runnable
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Music m = new Music("music/" + key + ".ogg");
						music.put(key, m);
						MusicPlayer.SONGS_LOADED++;
						System.out.println(String.format("Music Loaded: %s", key));
					} catch(SlickException se) {
						ref.set(se); // pass this exception outside
					}
				}
			}).start();
			
			// If an exception occurred in the runnable, throw it.
			if(ref.get() != null) {
				throw ref.get();
			}
		}
	}
	
	public synchronized void update() {
		String songName = String.format("soundtrack_%02d", currentSong);
		Music song = music.get(songName);
		if((song != null) && !song.playing() && autoplay) {
			nextSong();
		}
	}
	
	public synchronized void pause() {
		if((nowPlaying != null) && !nowPlaying.playing()) {
			nowPlaying.pause();
		}
	}
	
	public synchronized void resume() {
		if((nowPlaying != null) && nowPlaying.playing()) {
			nowPlaying.resume();
		}
	}
	
	public synchronized void nextSong() {
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
	
	public synchronized void playSong(String key) {
		Music song = music.get(key);
		if(song != null) {
			nowPlaying = song;
			song.play();
		}
	}
}
