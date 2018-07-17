package com.gzsr.states.settings;

import java.util.function.Consumer;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
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
import com.gzsr.gfx.ui.CheckBox;
import com.gzsr.gfx.ui.MenuButton;
import com.gzsr.misc.MouseInfo;
import com.gzsr.misc.Pair;
import com.gzsr.states.SettingsState;

public class GameSettingsState extends BasicGameState implements InputListener {
	public static final int ID = 9;
	
	private CheckBox fullscreenCheckbox;
	
	private MenuButton applyButton;
	private MenuButton backButton;

	@Override
	public void init(GameContainer gc, StateBasedGame game) throws SlickException {
		fullscreenCheckbox = new CheckBox("fullscreen", "Fullscreen", new Pair<Float>((Globals.WIDTH - (CheckBox.SIZE + 70.0f)), (Globals.HEIGHT - 270.0f)), Globals.app.isFullscreen(), fullscreenOperation);
		
		applyButton = new MenuButton(new Pair<Float>(50.0f, (Globals.HEIGHT - 80.0f)), "Apply");
		backButton = new MenuButton(new Pair<Float>((Globals.WIDTH - 200.0f), (Globals.HEIGHT - 80.0f)), "Back");
	}
	
	@Override
	public void enter(GameContainer gc, StateBasedGame game) throws SlickException {
		Controls.getInstance().resetAll();
		
		fullscreenCheckbox.setChecked(Globals.app.isFullscreen());
	}

	@Override
	public void update(GameContainer gc, StateBasedGame game, int delta) throws SlickException {
		MouseInfo mouse = Controls.getInstance().getMouse();
		
		if(applyButton.inBounds(mouse.getPosition().x, mouse.getPosition().y)) {
			applyButton.mouseEnter();
			if(mouse.isMouseDown()) {
				fullscreenCheckbox.apply(true);
				ConfigManager.getInstance().save();
				game.enterState(SettingsState.ID, new FadeOutTransition(), new FadeInTransition());
			}
		} else applyButton.mouseExit();
		
		if(backButton.inBounds(mouse.getPosition().x, mouse.getPosition().y)) {
			backButton.mouseEnter();
			if(mouse.isMouseDown()) {
				fullscreenCheckbox.apply(false);
				game.enterState(SettingsState.ID, new FadeOutTransition(), new FadeInTransition());
			}
		} else backButton.mouseExit();
		
		MusicPlayer.getInstance().update(true);
	}
	
	@Override
	public void render(GameContainer gc, StateBasedGame game, Graphics g) throws SlickException {
		g.setColor(Color.white);
		g.setFont(AssetManager.getManager().getFont("PressStart2P-Regular_large"));
		FontUtils.drawCenter(g.getFont(), "Game Settings", 0, 40, Globals.WIDTH);
		
		fullscreenCheckbox.render(g, 0L);
		
		applyButton.render(g, 0L);
		backButton.render(g, 0L);
	}
	
	@Override
	public void mouseMoved(int oldx, int oldy, int newx, int newy) {
		Controls.getInstance().getMouse().setPosition(newx, newy);
	}
	
	@Override
	public void mousePressed(int button, int x, int y) {
		if(button == 0) Controls.getInstance().getMouse().setMouseDown(true);
	}
	
	@Override
	public void mouseReleased(int button, int x, int y) {
		if(button == 0) {
			Controls.getInstance().getMouse().setMouseDown(false);
			
			if(fullscreenCheckbox.contains(x, y)) {
				fullscreenCheckbox.toggle();
			}
		}
	}
	
	private Consumer<Boolean> fullscreenOperation = new Consumer<Boolean>() {
		@Override
		public void accept(Boolean val_) {
			try {
				Globals.app.setFullscreen(val_);
			} catch(SlickException sle) {
				System.out.printf("Error attempting to change to %s mode!", (val_ ? "fullscreen" : "windowed"));
				sle.printStackTrace();
			}
		}
	};

	@Override
	public int getID() {
		return GameSettingsState.ID;
	}

}
