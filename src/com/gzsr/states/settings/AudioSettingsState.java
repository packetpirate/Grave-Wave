package com.gzsr.states.settings;

import java.util.function.Consumer;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.InputListener;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;
import org.newdawn.slick.util.FontUtils;

import com.gzsr.AssetManager;
import com.gzsr.ConfigManager;
import com.gzsr.Controls;
import com.gzsr.Globals;
import com.gzsr.MusicPlayer;
import com.gzsr.gfx.ui.MenuButton;
import com.gzsr.gfx.ui.Slider;
import com.gzsr.misc.MouseInfo;
import com.gzsr.misc.Pair;
import com.gzsr.states.SettingsState;

public class AudioSettingsState extends BasicGameState implements InputListener {
	public static final int ID = 10;

	private Slider musicVolumeSlider;
	private Slider soundVolumeSlider;
	
	private MenuButton applyButton;
	private MenuButton backButton;
	
	@Override
	public void init(GameContainer gc, StateBasedGame game) throws SlickException {
		musicVolumeSlider = new Slider("musicVolume", "Music Volume", new Pair<Float>((float)((Globals.WIDTH / 2) - 150.0f), 300.0f), 300.0f, musicVolumeOperation);
		musicVolumeSlider.setSliderBounds(new Pair<Float>(0.0f, 1.0f));
		musicVolumeSlider.setDefaultVal(MusicPlayer.getInstance().getMusicVolume());
		musicVolumeSlider.setSliderVal(MusicPlayer.getInstance().getMusicVolume());
		
		soundVolumeSlider = new Slider("soundVolume", "Sound FX Volume", new Pair<Float>((float)((Globals.WIDTH / 2) - 150.0f), 400.0f), 300.0f, soundVolumeOperation);
		soundVolumeSlider.setSliderBounds(new Pair<Float>(0.0f, 1.0f));
		soundVolumeSlider.setDefaultVal(AssetManager.getManager().getSoundVolume());
		soundVolumeSlider.setSliderVal(AssetManager.getManager().getSoundVolume());
		
		applyButton = new MenuButton(new Pair<Float>(50.0f, (Globals.HEIGHT - 80.0f)), "Apply");
		backButton = new MenuButton(new Pair<Float>((Globals.WIDTH - 200.0f), (Globals.HEIGHT - 80.0f)), "Back");
	}
	
	@Override
	public void enter(GameContainer gc, StateBasedGame game) throws SlickException {
		Controls.getInstance().resetAll();
		
		musicVolumeSlider.setSliderVal(MusicPlayer.getInstance().getMusicVolume());
	}

	@Override
	public void update(GameContainer gc, StateBasedGame game, int delta) throws SlickException {
		MouseInfo mouse = Controls.getInstance().getMouse();
		
		if(applyButton.inBounds(mouse.getPosition().x, mouse.getPosition().y)) {
			applyButton.mouseEnter();
			if(mouse.isLeftDown()) {
				musicVolumeSlider.apply(true);
				ConfigManager.getInstance().save();
				game.enterState(SettingsState.ID, new FadeOutTransition(), new FadeInTransition());
			}
		} else applyButton.mouseExit();
		
		if(backButton.inBounds(mouse.getPosition().x, mouse.getPosition().y)) {
			backButton.mouseEnter();
			if(mouse.isLeftDown()) {
				musicVolumeSlider.apply(false);
				game.enterState(SettingsState.ID, new FadeOutTransition(), new FadeInTransition());
			}
		} else backButton.mouseExit();
		
		MusicPlayer.getInstance().update(true);
	}
	
	@Override
	public void render(GameContainer gc, StateBasedGame game, Graphics g) throws SlickException {
		g.setFont(AssetManager.getManager().getFont("PressStart2P-Regular_large"));
		FontUtils.drawCenter(g.getFont(), "Audio Settings", 0, 40, Globals.WIDTH, Color.white);
		
		musicVolumeSlider.render(g, 0L);
		soundVolumeSlider.render(g, 0L);
		
		applyButton.render(g, 0L);
		backButton.render(g, 0L);
	}
	
	@Override
	public void mouseClicked(int button, int x, int y, int count) {
		if(musicVolumeSlider.contains(x, y)) musicVolumeSlider.move();
		else if(soundVolumeSlider.contains(x, y)) soundVolumeSlider.move();
	}
	
	@Override
	public void mouseMoved(int oldx, int oldy, int newx, int newy) {
		Controls.getInstance().getMouse().setPosition(newx, newy);
	}
	
	@Override
	public void mouseDragged(int oldx, int oldy, int newx, int newy) {
		Controls.getInstance().getMouse().setPosition(newx, newy);
		if(musicVolumeSlider.contains(newx, newy)) musicVolumeSlider.move();
		else if(soundVolumeSlider.contains(newx, newy)) soundVolumeSlider.move();
	}
	
	@Override
	public void mousePressed(int button, int x, int y) {
		if(button == Input.MOUSE_LEFT_BUTTON) Controls.getInstance().getMouse().setLeftDown(true);
	}
	
	@Override
	public void mouseReleased(int button, int x, int y) {
		if(button == Input.MOUSE_LEFT_BUTTON) Controls.getInstance().getMouse().setLeftDown(false);
	}
	
	private static Consumer<Float> musicVolumeOperation = new Consumer<Float>() {
		@Override
		public void accept(Float val_) {
			MusicPlayer.getInstance().setMusicVolume(val_);
		}
	};
	
	private static Consumer<Float> soundVolumeOperation = new Consumer<Float>() {
		@Override
		public void accept(Float val_) {
			AssetManager.getManager().setSoundVolume(val_);
		}
	};

	@Override
	public int getID() {
		return AudioSettingsState.ID;
	}

}
