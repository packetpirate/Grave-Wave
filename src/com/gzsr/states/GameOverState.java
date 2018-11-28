package com.gzsr.states;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.InputListener;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import com.gzsr.AssetManager;
import com.gzsr.Controls;
import com.gzsr.Globals;
import com.gzsr.MusicPlayer;
import com.gzsr.controllers.Scorekeeper;
import com.gzsr.gfx.ui.MenuButton;
import com.gzsr.gfx.ui.SlidingText;
import com.gzsr.misc.MouseInfo;
import com.gzsr.misc.Pair;

public class GameOverState extends BasicGameState implements InputListener {
	public static final int ID = 4;
	private static final double SCORE_COUNT_SPEED = 0.005;
	
	private AssetManager assets;
	
	private MenuButton menuButton;
	
	private SlidingText killCount;
	private SlidingText accuracy;
	private SlidingText moneyCollected;
	private SlidingText wavesCleared;
	private SlidingText scoreCount;
	
	private int scCurr, scEnd;
	private boolean counting;
	
	private long time;
	
	@Override
	public void init(GameContainer gc, StateBasedGame game) throws SlickException {
		assets = AssetManager.getManager();
		
		UnicodeFont uni = AssetManager.getManager().getFont("PressStart2P-Regular_large");
		menuButton = new MenuButton(new Pair<Float>((float)((Globals.WIDTH / 2) - (uni.getWidth("Back to Main Menu") / 2)), (Globals.HEIGHT - 150.0f)), "Back to Main Menu");
		
		killCount = new SlidingText(String.format("Kills: %d", Scorekeeper.getInstance().getKillCount()), -100.0f, 100.0f, (Globals.HEIGHT / 2), 1.0f, 0.0f, 1.0f);
		accuracy = new SlidingText(String.format("Accuracy: %.1f", (Scorekeeper.getInstance().getAccuracy() * 100)), -100.0f, 100.0f, ((Globals.HEIGHT / 2) + 32.0f), 1.0f, 0.0f, 1.0f);
		moneyCollected = new SlidingText(String.format("Money: %d", Scorekeeper.getInstance().getMoneyCollected()), -100.0f, 100.0f, ((Globals.HEIGHT / 2) + 64.0f), 1.0f, 0.0f, 1.0f);
		wavesCleared = new SlidingText(String.format("Waves: %d", Scorekeeper.getInstance().getWavesCleared()), -100.0f, 100.0f, ((Globals.HEIGHT / 2) + 96.0f), 1.0f, 0.0f, 1.0f);
		scoreCount = new SlidingText("Score: 0", -100.0f, 100.0f, ((Globals.HEIGHT / 2) + 160.0f), 1.0f, 0.0f, 1.0f);
		
		scCurr = 0;
		scEnd = 0;
		counting = false;
		
		time = 0L;
	}

	@Override
	public void update(GameContainer gc, StateBasedGame game, int delta) throws SlickException {
		time += (long)delta;
		
		MouseInfo mouse = Controls.getInstance().getMouse();
		if(menuButton.inBounds(mouse.getPosition().x, mouse.getPosition().y)) {
			menuButton.mouseEnter();
			if(mouse.isLeftDown()) {
				Controls.getInstance().resetAll();
				game.enterState(MenuState.ID);
			}
		} else menuButton.mouseExit();
		
		if(!killCount.isDone()) killCount.update(null, time, delta);
		else if(!accuracy.started()) accuracy.start();
		
		if(!accuracy.isDone()) accuracy.update(null, time, delta);
		else if(!moneyCollected.started()) moneyCollected.start();
		
		if(!moneyCollected.isDone()) moneyCollected.update(null, time, delta);
		else if(!wavesCleared.started()) wavesCleared.start();
		
		if(!wavesCleared.isDone()) wavesCleared.update(null, time, delta);
		else if(!scoreCount.started()) scoreCount.start();
		
		if(!scoreCount.isDone()) scoreCount.update(null, time, delta);
		else if(counting) {
			if(scCurr < scEnd) {
				scCurr += (int)(SCORE_COUNT_SPEED * scEnd);
				if(scCurr >= scEnd) {
					scCurr = scEnd;
					counting = false;
				}
				
				scoreCount.setText(String.format("Score: %d", scCurr));
			} else counting = false;
		} else counting = true;
	}
	
	@Override
	public void render(GameContainer gc, StateBasedGame game, Graphics g) throws SlickException {
		Image deathScreen = assets.getImage("GZS_DeathScreen");
		if(deathScreen != null) {
			g.drawImage(deathScreen, 
						0.0f, 0.0f, Globals.WIDTH, Globals.HEIGHT, 
						0.0f, 0.0f, deathScreen.getWidth(), deathScreen.getHeight());
		}
		
		menuButton.render(g, 0L);
		
		if(killCount.started()) killCount.render(g, time);
		if(accuracy.started()) accuracy.render(g, time);
		if(moneyCollected.started()) moneyCollected.render(g, time);
		if(wavesCleared.started()) wavesCleared.render(g, time);
		if(scoreCount.started()) scoreCount.render(g, time);
	}
	
	@Override
	public void enter(GameContainer gc, StateBasedGame game) throws SlickException {
		Scorekeeper score = Scorekeeper.getInstance();
		
		killCount.reset();
		killCount.setText(String.format("Kills: %d", score.getKillCount()));
		
		accuracy.reset();
		accuracy.setText(String.format("Accuracy: %.1f%%", (score.getAccuracy() * 100)));
		
		moneyCollected.reset();
		moneyCollected.setText(String.format("Money: %d", score.getMoneyCollected()));
		
		wavesCleared.reset();
		wavesCleared.setText(String.format("Waves: %d", score.getWavesCleared()));
		
		scoreCount.reset();
		scoreCount.setText("Score: 0");
		
		killCount.start();
		
		scCurr = 0;
		scEnd = score.calculateFinalScore();
		counting = false;
	}
	
	@Override
	public void leave(GameContainer gc, StateBasedGame game) throws SlickException {
		MusicPlayer.getInstance().reset();
		MusicPlayer.getInstance().nextSong();
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
		return GameOverState.ID;
	}
}
