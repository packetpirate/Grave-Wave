package com.grave.states;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.InputListener;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import com.grave.AchievementManager;
import com.grave.AssetManager;
import com.grave.ConfigManager;
import com.grave.Controls;
import com.grave.Globals;
import com.grave.MusicPlayer;
import com.grave.gfx.ui.MenuButton;
import com.grave.misc.MouseInfo;
import com.grave.misc.Pair;

public class MenuState extends BasicGameState implements InputListener {
	public static final int ID = 0;

	private AssetManager assets = null;

	private MenuButton gameStart;
	private MenuButton achievements;
	private MenuButton settings;
	private MenuButton credits;
	private MenuButton exit;

	@Override
	public void init(GameContainer gc, StateBasedGame game) throws SlickException {
		assets = AssetManager.getManager();

		UnicodeFont large = assets.getFont("PressStart2P-Regular_large");

		gameStart = new MenuButton(new Pair<Float>((Globals.WIDTH - large.getWidth("Start Game") - 20.0f), (Globals.HEIGHT - 400.0f)), "Start Game");
		achievements = new MenuButton(new Pair<Float>((Globals.WIDTH - large.getWidth("Achievements") - 20.0f), (Globals.HEIGHT - 340.0f)), "Achievements");
		settings = new MenuButton(new Pair<Float>((Globals.WIDTH - large.getWidth("Settings") - 20.0f), (Globals.HEIGHT - 280.0f)), "Settings");
		credits = new MenuButton(new Pair<Float>((Globals.WIDTH - large.getWidth("Credits") - 20.0f), (Globals.HEIGHT - 220.0f)), "Credits");
		exit = new MenuButton(new Pair<Float>((Globals.WIDTH - large.getWidth("Exit") - 20.0f), (Globals.HEIGHT - 80.0f)), "Exit");
	}

	@Override
	public void enter(GameContainer gc, StateBasedGame game) throws SlickException {
		if(Globals.firstTimeGamma) Globals.firstTimeGamma = false;
		Controls.getInstance().resetAll();
	}

	@Override
	public void update(GameContainer gc, StateBasedGame game, int delta) throws SlickException {
		MouseInfo mouse = Controls.getInstance().getMouse();

		if(gameStart.inBounds(mouse.getPosition().x, mouse.getPosition().y)) {
			gameStart.mouseEnter();
			if(mouse.isLeftDown()) {
				Globals.resetEntityNum();
				Controls.getInstance().resetAll();
				Globals.inGame = true;
				game.enterState(GameState.ID, new FadeOutTransition(), new FadeInTransition());
			}
		} else gameStart.mouseExit();

		if(achievements.inBounds(mouse.getPosition().x, mouse.getPosition().y)) {
			achievements.mouseEnter();
			if(mouse.isLeftDown()) game.enterState(AchievementMenuState.ID, new FadeOutTransition(), new FadeInTransition());
		} else achievements.mouseExit();

		if(settings.inBounds(mouse.getPosition().x, mouse.getPosition().y)) {
			settings.mouseEnter();
			if(mouse.isLeftDown()) game.enterState(SettingsState.ID, new FadeOutTransition(), new FadeInTransition());
		} else settings.mouseExit();

		if(credits.inBounds(mouse.getPosition().x, mouse.getPosition().y)) credits.mouseEnter();
		else credits.mouseExit();

		if(exit.inBounds(mouse.getPosition().x, mouse.getPosition().y)) {
			exit.mouseEnter();
			if(mouse.isLeftDown()) {
				ConfigManager.getInstance().save();
				AchievementManager.save();
				gc.exit();
			}
		} else exit.mouseExit();

		MusicPlayer.getInstance().update(true);
	}

	@Override
	public void render(GameContainer gc, StateBasedGame game, Graphics g) throws SlickException {
		//Image background = assets.getImage("GZS_Background02");
		Image background = assets.getImage("GW_Background_01");

		g.resetTransform();
		g.clear();

		if(background != null) g.drawImage(background, 0.0f, 0.0f, Globals.WIDTH, Globals.HEIGHT, 0.0f, 0.0f, background.getWidth(), background.getHeight());

		gameStart.render(null, g, 0L);
		achievements.render(null, g, 0L);
		settings.render(null, g, 0L);
		credits.render(null, g, 0L);
		exit.render(null, g, 0L);

		g.setColor(Color.white);
		g.setFont(AssetManager.getManager().getFont("PressStart2P-Regular_small"));
		float vWidth = g.getFont().getWidth(Globals.VERSION);
		g.drawString(Globals.VERSION, (Globals.WIDTH - vWidth - 20.0f), 20.0f);
	}

	@Override
	public void mouseMoved(int oldx, int oldy, int newx, int newy) {
		Controls.getInstance().getMouse().setPosition(newx, newy);
	}

	@Override
	public void mousePressed(int button, int x, int y) {
		if(button == Input.MOUSE_LEFT_BUTTON) Controls.getInstance().getMouse().setLeftDown(true);
	}

	@Override
	public void mouseReleased(int button, int x, int y) {
		if(button == Input.MOUSE_LEFT_BUTTON) Controls.getInstance().getMouse().setLeftDown(false);
	}

	@Override
	public int getID() {
		return MenuState.ID;
	}
}
