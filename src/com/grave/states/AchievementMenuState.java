package com.grave.states;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import com.grave.AssetManager;
import com.grave.Controls;
import com.grave.Globals;
import com.grave.achievements.Achievement;
import com.grave.controllers.AchievementController;
import com.grave.gfx.ui.AchievementMenuDisplay;
import com.grave.gfx.ui.MenuButton;
import com.grave.misc.MouseInfo;
import com.grave.misc.Pair;

public class AchievementMenuState extends BasicGameState {
	public static final int ID = 14;
	private static final Color BACKGROUND = new Color(0x6C6C6C);
	private static final Color ACHIEVEMENTS_BACKGROUND = new Color(0x474747);
	
	private static final Pair<Float> DISPLAY_ORIGIN = new Pair<Float>(50.0f, 150.0f);
	private static final float DISPLAY_WIDTH = (Globals.WIDTH - 100.0f);
	private static final float DEFAULT_HEIGHT = (Globals.HEIGHT - 300.0f);
	
	private static final float SCROLL_SPEED = 20.0f;
	
	private MenuButton exit;

	private Pair<Float> cOrigin;
	private float displayHeight;
	
	private List<AchievementMenuDisplay> achievements;
	
	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		exit = new MenuButton(new Pair<Float>(30.0f, (Globals.HEIGHT - 66.0f)), "Exit");
		
		cOrigin = new Pair<Float>(DISPLAY_ORIGIN);
		displayHeight = DEFAULT_HEIGHT;
		
		achievements = new ArrayList<AchievementMenuDisplay>();
	}
	
	@Override
	public void enter(GameContainer gc, StateBasedGame game) throws SlickException {
		Controls.getInstance().resetAll();
		achievements.clear();
		
		List<Achievement> ach = AchievementController.getInstance().getAchievements();
		ach.stream().forEach(ac -> achievements.add(new AchievementMenuDisplay(ac, (DISPLAY_WIDTH - 40.0f))));
		
		float h = 20.0f;
		float y = (DISPLAY_ORIGIN.y + 20.0f);
		for(AchievementMenuDisplay display : achievements) {
			float dH = display.getTotalDisplayHeight();
			display.setPosition(new Pair<Float>((DISPLAY_ORIGIN.x + 20.0f), y));
			h += (dH + 10.0f);
			y += (dH + 10.0f);
		}
		
		h += 10.0f;
		
		displayHeight = Math.max(h, DEFAULT_HEIGHT);
	}
	
	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		MouseInfo mouse = Controls.getInstance().getMouse();
		
		if(exit.inBounds(mouse.getPosition().x, mouse.getPosition().y)) {
			exit.mouseEnter();
			if(mouse.isLeftDown()) {
				Controls.getInstance().resetAll();
				int state = Globals.inGame ? GameState.ID : MenuState.ID;
				game.enterState(state, new FadeOutTransition(), new FadeInTransition());
			}
		} else exit.mouseExit();
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		AssetManager assets = AssetManager.getManager();
		
		g.clear();
		
		g.setColor(AchievementMenuState.BACKGROUND);
		g.fillRect(0.0f, 0.0f, Globals.WIDTH, Globals.HEIGHT);
		
		g.setColor(AchievementMenuState.ACHIEVEMENTS_BACKGROUND);
		g.fillRect(50.0f, 150.0f, (Globals.WIDTH - 100.0f), DEFAULT_HEIGHT);
		
		// Draw achievements here in scrollable fashion.
		achievements.stream().forEach(achievement -> achievement.render(g, 0L));
		
		// Draw rectangles over the header and footer sections to hide scrolled items in the achievement display.
		g.setColor(AchievementMenuState.BACKGROUND);
		g.fillRect(0.0f, 0.0f, Globals.WIDTH, 150.0f);
		g.fillRect(0.0f, (DEFAULT_HEIGHT + 150.0f), Globals.WIDTH, 150.0f);
		g.setColor(Color.white);
		g.drawRect(50.0f, 150.0f, (Globals.WIDTH - 100.0f), DEFAULT_HEIGHT);
		
		// Draw the header and footer.
		g.drawLine(10.0f, 36.0f, (Globals.WIDTH - 10.0f), 36.0f);
		g.drawLine(10.0f, (Globals.HEIGHT - 96.0f), (Globals.WIDTH - 10.0f), (Globals.HEIGHT - 96.0f));
		g.setFont(assets.getFont("PressStart2P-Regular_large"));
		g.drawString("Achievements", 30.0f, 20.0f);
		g.setFont(assets.getFont("PressStart2P-Regular"));
		g.drawString("Track your achievement progress here.", 30.0f, 100.0f);
		
		exit.render(g, 0L);
	}

	@Override
	public void mouseMoved(int oldx, int oldy, int newx, int newy) {
		Controls.getInstance().getMouse().setPosition(newx, newy);
	}
	
	@Override
	public void mouseWheelMoved(int change) {
		MouseInfo mouse = Controls.getInstance().getMouse();
		Pair<Float> mPos = mouse.getPosition();
		
		float scrollAmount = ((change > 0) ? SCROLL_SPEED : -SCROLL_SPEED);
		boolean inDisplay = ((mPos.x >= DISPLAY_ORIGIN.x) && (mPos.x <= (DISPLAY_ORIGIN.x + DISPLAY_WIDTH)) && 
							 (mPos.y >= DISPLAY_ORIGIN.y) && (mPos.y <= (DISPLAY_ORIGIN.y + DEFAULT_HEIGHT)));
		
		if(inDisplay) {
			boolean tooLow = ((cOrigin.y + scrollAmount) > DISPLAY_ORIGIN.y);
			boolean tooHigh = ((cOrigin.y + scrollAmount) < (DISPLAY_ORIGIN.y - (displayHeight - DEFAULT_HEIGHT)));
			if(!tooLow && !tooHigh) {
				cOrigin.y += scrollAmount;
				achievements.stream().forEach(achievement -> achievement.scrollPosition(scrollAmount));
			}
		}
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
		return AchievementMenuState.ID;
	}
}
