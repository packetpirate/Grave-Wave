package com.gzsr.states.settings;

import java.util.ArrayList;
import java.util.List;

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
import com.gzsr.Controls;
import com.gzsr.Globals;
import com.gzsr.MusicPlayer;
import com.gzsr.gfx.ui.ControlConfigButton;
import com.gzsr.gfx.ui.MenuButton;
import com.gzsr.misc.MouseInfo;
import com.gzsr.misc.Pair;
import com.gzsr.states.SettingsState;

public class ControlSettingsState extends BasicGameState implements InputListener {
	public static final int ID = 12;

	private int selected;
	private List<ControlConfigButton> configs;
	
	private MenuButton applyButton;
	private MenuButton backButton;
	
	@Override
	public void init(GameContainer gc, StateBasedGame game) throws SlickException {
		selected = -1;
		configs = new ArrayList<ControlConfigButton>();
		
		{ // Begin defining control config buttons and text.
			for(int i = 0; i < Controls.Layout.values().length; i++) { 
				Controls.Layout input = Controls.Layout.values()[i];
				
				if((input == Controls.Layout.OPEN_CONSOLE) && !Globals.ENABLE_CONSOLE) continue; // dead code warning if constant set to true, will be false in releases
				
				float x = ((i / 10) >= 1) ? ((Globals.WIDTH / 2) + 172.0f) : ((Globals.WIDTH / 2) - 200.0f);
				float y = (((i % 10) * ControlConfigButton.HEIGHT) + ((i % 10) * 5.0f) + 150.0f);
				Pair<Float> position = new Pair<Float>(x, y);
				ControlConfigButton button = new ControlConfigButton(input, position);
				
				button.setLabel(input.getName());
				
				configs.add(button);
			}
		} // End declaration of control config buttons.
		
		applyButton = new MenuButton(new Pair<Float>(50.0f, (Globals.HEIGHT - 80.0f)), "Apply");
		backButton = new MenuButton(new Pair<Float>((Globals.WIDTH - 200.0f), (Globals.HEIGHT - 80.0f)), "Back");
	}
	
	@Override
	public void enter(GameContainer gc, StateBasedGame game) throws SlickException {
		Controls.getInstance().resetAll();
	}

	@Override
	public void update(GameContainer gc, StateBasedGame game, int delta) throws SlickException {
		for(ControlConfigButton config : configs) {
			config.update(this, 0L, delta);
		}
		
		MouseInfo mouse = Controls.getInstance().getMouse();
		
		if(applyButton.inBounds(mouse.getPosition().x, mouse.getPosition().y)) {
			applyButton.mouseEnter();
			if(mouse.isMouseDown()) {
				configs.stream().forEach(button -> button.apply(true));
				game.enterState(SettingsState.ID, new FadeOutTransition(), new FadeInTransition());
			}
		} else applyButton.mouseExit();
		
		if(backButton.inBounds(mouse.getPosition().x, mouse.getPosition().y)) {
			backButton.mouseEnter();
			if(mouse.isMouseDown()) {
				configs.stream().forEach(button -> button.apply(false));
				game.enterState(SettingsState.ID, new FadeOutTransition(), new FadeInTransition());
			}
		} else backButton.mouseExit();
		
		MusicPlayer.getInstance().update(true);
	}
	
	@Override
	public void render(GameContainer gc, StateBasedGame game, Graphics g) throws SlickException {
		g.setFont(AssetManager.getManager().getFont("PressStart2P-Regular_large"));
		g.setColor(Color.white);
		FontUtils.drawCenter(g.getFont(), "Controls", 0, 40, Globals.WIDTH);
		
		for(ControlConfigButton config : configs) {
			config.render(g, 0L);
		}
		
		applyButton.render(g, 0L);
		backButton.render(g, 0L);
	}
	
	@Override
	public void keyPressed(int key, char c) {
		if((selected != -1) && configs.get(selected).isAwaitingInput()) {
			ControlConfigButton config = configs.get(selected);
			config.acceptKeyMapping(key, Character.toUpperCase(c));
			selected = -1;
		}
	}
	
	@Override
	public void mouseClicked(int button, int x, int y, int count) {
		if((button == 0) && (selected == -1)) {
			for(int i = 0; i < configs.size(); i++) {
				ControlConfigButton config = configs.get(i);
				if(config.inBounds(x, y)) {
					config.click();
					selected = i;
					break;
				}
			}
		}
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
		if(button == 0) Controls.getInstance().getMouse().setMouseDown(false);
	}

	@Override
	public int getID() {
		return ControlSettingsState.ID;
	}

}
