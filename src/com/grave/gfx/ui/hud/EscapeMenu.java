package com.grave.gfx.ui.hud;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import com.grave.AssetManager;
import com.grave.Controls;
import com.grave.Globals;
import com.grave.gfx.Camera;
import com.grave.gfx.ui.MenuButton;
import com.grave.misc.MouseInfo;
import com.grave.misc.Pair;
import com.grave.states.AchievementMenuState;
import com.grave.states.GameState;
import com.grave.states.MenuState;
import com.grave.states.settings.AudioSettingsState;
import com.grave.states.settings.ControlSettingsState;
import com.grave.states.settings.GameSettingsState;
import com.grave.states.settings.GammaSettingsState;

public class EscapeMenu {
	private enum EscapeMenuState { MAIN, SETTINGS, DISPLAY, EXIT; }

	private static final String BUTTON_FONT = "PressStart2P-Regular_large";

	// Main Escape Menu Components
	private MenuButton resume;
	private MenuButton settings;
	private MenuButton achievements;
	private MenuButton exit;

	// Exit Prompt Components
	private MenuButton yes;
	private MenuButton no;

	// Settings Sub-Menu Components
	private MenuButton game;
	private MenuButton audio;
	private MenuButton display;
	private MenuButton controls;
	private MenuButton settingsBack;

	// Display Sub-Menu Components
	private MenuButton gamma;
	private MenuButton displayBack;

	private EscapeMenuState state;

	private boolean open;
	public boolean isOpen() { return open; }
	public void openMenu() { open = true; }

	public EscapeMenu() {
		UnicodeFont font = AssetManager.getManager().getFont(BUTTON_FONT);
		float halfHeight = (Globals.HEIGHT / 2);
		float fontHeight = font.getLineHeight();

		// Initialize menu buttons for main escape menu.
		resume = new MenuButton(new Pair<Float>(getTextOffset(font, "Resume"), (halfHeight - ((fontHeight * 2) + 15.0f))), "Resume", null, true);
		settings = new MenuButton(new Pair<Float>(getTextOffset(font, "Settings"), (halfHeight - (fontHeight + 5.0f))), "Settings", null, true);
		achievements = new MenuButton(new Pair<Float>(getTextOffset(font, "Achievements"), (halfHeight + 5.0f)), "Achievements", null, true);
		exit = new MenuButton(new Pair<Float>(getTextOffset(font, "Exit"), (halfHeight + (fontHeight + 15.0f))), "Exit", null, true);

		// Initialize menu buttons for exit prompt.
		yes = new MenuButton(new Pair<Float>((Globals.WIDTH / 2) - font.getWidth("Yes") - 20.0f, (halfHeight + 5.0f)), "Yes", null, true);
		no = new MenuButton(new Pair<Float>((Globals.WIDTH / 2) + 40.0f, (halfHeight + 5.0f)), "No", null, true);

		// Initialize menu buttons for settings sub-menu.
		game = new MenuButton(new Pair<Float>(getTextOffset(font, "Game"), (halfHeight - (fontHeight * 2.5f) - 20.0f)), "Game", null, true);
		audio = new MenuButton(new Pair<Float>(getTextOffset(font, "Audio"), (halfHeight - (fontHeight * 1.5f) - 10.0f)), "Audio", null, true);
		display = new MenuButton(new Pair<Float>(getTextOffset(font, "Display"), (halfHeight - (fontHeight / 2))), "Display", null, true);
		controls = new MenuButton(new Pair<Float>(getTextOffset(font, "Controls"), (halfHeight + (fontHeight / 2) + 10.0f)), "Controls", null, true);
		settingsBack = new MenuButton(new Pair<Float>(getTextOffset(font, "Back"), (halfHeight + (fontHeight * 1.5f) + 20.0f)), "Back", null, true);

		// Initialize menu buttons for display sub-menu.
		gamma = new MenuButton(new Pair<Float>(getTextOffset(font, "Gamma"), (halfHeight - fontHeight - 5.0f)), "Gamma");
		displayBack = new MenuButton(new Pair<Float>(getTextOffset(font, "Back"), (halfHeight + 5.0f)), "Back");

		reset();
	}

	public void update(GameContainer gc, StateBasedGame sbg, GameState gs) throws SlickException {
		Camera camera = Camera.getCamera();
		MouseInfo mouse = Controls.getInstance().getMouse();
		Pair<Float> mPos = new Pair<Float>(0.0f, 0.0f);
		mPos.x = (mouse.getPosition().x + camera.getOffset().x);
		mPos.y = (mouse.getPosition().y + camera.getOffset().y);

		if(state == EscapeMenuState.MAIN) {
			if(resume.inBounds(mPos.x, mPos.y)) {
				resume.mouseEnter();
				if(mouse.isLeftDown()) open = false;
			} else resume.mouseExit();

			if(settings.inBounds(mPos.x, mPos.y)) {
				settings.mouseEnter();
				if(mouse.isLeftDown()) {
					state = EscapeMenuState.SETTINGS;
					Controls.getInstance().resetAll();
				}
			} else settings.mouseExit();

			if(achievements.inBounds(mPos.x, mPos.y)) {
				achievements.mouseEnter();
				if(mouse.isLeftDown()) sbg.enterState(AchievementMenuState.ID, new FadeOutTransition(Color.black, 100), new FadeInTransition(Color.black, 100));
			} else achievements.mouseExit();

			if(exit.inBounds(mPos.x, mPos.y)) {
				exit.mouseEnter();
				if(mouse.isLeftDown()) {
					state = EscapeMenuState.EXIT;
					Controls.getInstance().resetAll();
				}
			} else exit.mouseExit();
		} else if(state == EscapeMenuState.SETTINGS) {
			if(game.inBounds(mPos.x, mPos.y)) {
				game.mouseEnter();
				if(mouse.isLeftDown()) sbg.enterState(GameSettingsState.ID, new FadeOutTransition(Color.black, 100), new FadeInTransition(Color.black, 100));
			} else game.mouseExit();

			if(audio.inBounds(mPos.x, mPos.y)) {
				audio.mouseEnter();
				if(mouse.isLeftDown()) sbg.enterState(AudioSettingsState.ID, new FadeOutTransition(Color.black, 100), new FadeInTransition(Color.black, 100));
			} else audio.mouseExit();

			if(display.inBounds(mPos.x, mPos.y)) {
				display.mouseEnter();
				if(mouse.isLeftDown()) {
					state = EscapeMenuState.DISPLAY;
					Controls.getInstance().resetAll();
				}
			} else display.mouseExit();

			if(controls.inBounds(mPos.x, mPos.y)) {
				controls.mouseEnter();
				if(mouse.isLeftDown()) sbg.enterState(ControlSettingsState.ID, new FadeOutTransition(Color.black, 100), new FadeInTransition(Color.black, 100));
			} else controls.mouseExit();

			if(settingsBack.inBounds(mPos.x, mPos.y)) {
				settingsBack.mouseEnter();
				if(mouse.isLeftDown()) {
					state = EscapeMenuState.MAIN;
					Controls.getInstance().resetAll();
				}
			} else settingsBack.mouseExit();
		} else if(state == EscapeMenuState.DISPLAY) {
			if(gamma.inBounds(mPos.x, mPos.y)) {
				gamma.mouseEnter();
				if(mouse.isLeftDown()) sbg.enterState(GammaSettingsState.ID, new FadeOutTransition(Color.black, 100), new FadeInTransition(Color.black, 100));
			} else gamma.mouseExit();

			if(displayBack.inBounds(mPos.x, mPos.y)) {
				displayBack.mouseEnter();
				if(mouse.isLeftDown()) {
					state = EscapeMenuState.SETTINGS;
					Controls.getInstance().resetAll();
				}
			} else displayBack.mouseExit();
		} else if(state == EscapeMenuState.EXIT) {
			if(yes.inBounds(mPos.x, mPos.y)) {
				yes.mouseEnter();
				if(mouse.isLeftDown()) {
					gs.reset(gc);
					Globals.inGame = false;
					Camera.getCamera().reset();
					sbg.enterState(MenuState.ID, new FadeOutTransition(Color.black, 100), new FadeInTransition(Color.black, 100));
				}
			} else yes.mouseExit();

			if(no.inBounds(mPos.x, mPos.y)) {
				no.mouseEnter();
				if(mouse.isLeftDown()) {
					state = EscapeMenuState.MAIN;
					Controls.getInstance().resetAll();
				}
			} else no.mouseExit();
		}
	}

	public void render(Graphics g, long cTime) {
		UnicodeFont font = AssetManager.getManager().getFont(BUTTON_FONT);
		float fontHeight = font.getLineHeight();

		if(state == EscapeMenuState.MAIN) {
			drawContainer(g, fontHeight, 4);

			resume.render(g, 0L);
			settings.render(g, 0L);
			achievements.render(g, 0L);
			exit.render(g, 0L);
		} else if(state == EscapeMenuState.SETTINGS) {
			drawContainer(g, fontHeight, 5);

			game.render(g, 0L);
			audio.render(g, 0L);
			display.render(g, 0L);
			controls.render(g, 0L);
			settingsBack.render(g, 0L);
		} else if(state == EscapeMenuState.DISPLAY) {
			drawContainer(g, fontHeight, 2);

			gamma.render(g, 0L);
			displayBack.render(g, 0L);
		} else if(state == EscapeMenuState.EXIT) {
			drawContainer(g, fontHeight, 2);

			UnicodeFont small = AssetManager.getManager().getFont("PressStart2P-Regular");
			float textW = small.getWidth("Are you sure?");
			g.setFont(small);
			g.setColor(Color.white);
			g.drawString("Are you sure?", ((Globals.WIDTH / 2) - (textW / 2)), ((Globals.HEIGHT / 2) - (fontHeight + 5.0f)));

			yes.render(g, 0L);
			no.render(g, 0L);
		}
	}

	private void drawContainer(Graphics g, float fontHeight, int numOfButtons) {
		float totalHeight = ((fontHeight * numOfButtons) + ((numOfButtons - 1) * 10.0f) + 40.0f);

		g.setColor(Color.gray);
		g.fillRect(((Globals.WIDTH / 2) - 200.0f), ((Globals.HEIGHT / 2) - (totalHeight / 2)), 400.0f, totalHeight);
		g.setColor(Color.white);
		g.drawRect(((Globals.WIDTH / 2) - 200.0f), ((Globals.HEIGHT / 2) - (totalHeight / 2)), 400.0f, totalHeight);
	}

	private float getTextOffset(UnicodeFont f, String text) { return (Globals.WIDTH / 2) - (f.getWidth(text) / 2); }

	public void escape() {
		if(state == EscapeMenuState.MAIN) {
			open = false;
		} else if(state == EscapeMenuState.SETTINGS) {
			state = EscapeMenuState.MAIN;
		} else if(state == EscapeMenuState.DISPLAY) {
			state = EscapeMenuState.SETTINGS;
		} else if(state == EscapeMenuState.EXIT) {
			state = EscapeMenuState.MAIN;
		}

		Controls.getInstance().resetAll();
	}

	public void reset() {
		state = EscapeMenuState.MAIN;
		open = false;
	}
}
