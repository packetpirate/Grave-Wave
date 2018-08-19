package com.gzsr.states.settings;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.InputListener;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import com.gzsr.AssetManager;
import com.gzsr.ConfigManager;
import com.gzsr.Controls;
import com.gzsr.Globals;
import com.gzsr.MusicPlayer;
import com.gzsr.gfx.Flashlight;
import com.gzsr.gfx.ui.MenuButton;
import com.gzsr.gfx.ui.Slider;
import com.gzsr.math.Calculate;
import com.gzsr.misc.MouseInfo;
import com.gzsr.misc.Pair;
import com.gzsr.states.GameState;

public class GammaSettingsState extends BasicGameState implements InputListener {
	public static final int ID = 13;
	
	private static final String EXPLANATION = "Adjust the slider until you can just barely see the zombies outside the flashlight area.";
	
	private static final Pair<Float> playerPos = new Pair<Float>(150.0f, 150.0f);
	private static final List<Pair<Float>> positions = new ArrayList<Pair<Float>>() {{
		add(new Pair<Float>(300.0f, 150.0f));
		add(new Pair<Float>(450.0f, 150.0f));
		add(new Pair<Float>(600.0f, 150.0f));
		add(new Pair<Float>(750.0f, 150.0f));
		add(new Pair<Float>(900.0f, 150.0f));
	}};

	private Flashlight flashlight;
	
	private Slider opacitySlider;
	
	private MenuButton applyButton;
	private MenuButton backButton;
	
	@Override
	public void init(GameContainer gc, StateBasedGame game) throws SlickException {
		flashlight = new Flashlight();
		flashlight.setOrigin(playerPos.x, playerPos.y);
		flashlight.setTheta((float)(Math.PI / 2));
		
		opacitySlider = new Slider("shadowLevel", "Gamma Level", new Pair<Float>(((Globals.WIDTH / 2) - 256.0f), (Globals.HEIGHT - 400.0f)), 512.0f, opacityOperation);
		opacitySlider.setSliderBounds(new Pair<Float>(0.60f, 0.90f));
		opacitySlider.setDefaultVal(Flashlight.getShadowOpacity());
		opacitySlider.setSliderVal(Flashlight.getShadowOpacity());
		
		applyButton = new MenuButton(new Pair<Float>(50.0f, (Globals.HEIGHT - 80.0f)), "Apply");
		backButton = new MenuButton(new Pair<Float>((Globals.WIDTH - 200.0f), (Globals.HEIGHT - 80.0f)), "Back");
	}

	@Override
	public void enter(GameContainer gc, StateBasedGame game) throws SlickException {
		Controls.getInstance().resetAll();
		
		opacitySlider.setSliderVal(Flashlight.getShadowOpacity());
	}
	
	@Override
	public void update(GameContainer gc, StateBasedGame game, int delta) throws SlickException {
		MouseInfo mouse = Controls.getInstance().getMouse();
		
		if(applyButton.inBounds(mouse.getPosition().x, mouse.getPosition().y)) {
			applyButton.mouseEnter();
			if(mouse.isLeftDown()) {
				opacitySlider.apply(true);
				ConfigManager.getInstance().save();
				int state = Globals.inGame ? GameState.ID : DisplaySettingsState.ID;
				game.enterState(state, new FadeOutTransition(), new FadeInTransition());
			}
		} else applyButton.mouseExit();
		
		if(backButton.inBounds(mouse.getPosition().x, mouse.getPosition().y)) {
			backButton.mouseEnter();
			if(mouse.isLeftDown()) {
				opacitySlider.apply(false);
				int state = Globals.inGame ? GameState.ID : DisplaySettingsState.ID;
				game.enterState(state, new FadeOutTransition(), new FadeInTransition());
			}
		} else backButton.mouseExit();
		
		MusicPlayer.getInstance().update(true);
	}
	
	@Override
	public void render(GameContainer gc, StateBasedGame game, Graphics g) throws SlickException {
		// Render the "scene" to simulate how the flashlight looks in-game.
		AssetManager assets = AssetManager.getManager();
		Image background = assets.getImage("GZS_Background6");
		
		g.resetTransform();
		g.clear();
		
		if(background != null) g.drawImage(background, 0.0f, 0.0f, Globals.WIDTH, Globals.HEIGHT, 0.0f, 0.0f, background.getWidth(), background.getHeight());
		
		// Draw the player.
		Image player = assets.getImage("GZS_Player");
		if(player != null) {
			g.rotate(playerPos.x, playerPos.y, (float)Math.toDegrees(Math.PI / 2));
			g.drawImage(player, (playerPos.x - (player.getWidth() / 2)), (playerPos.y - (player.getHeight() / 2)));
			g.resetTransform();
		}
		
		// Draw a couple enemies both in and outside the flashlight area.
		Image zombie = assets.getImage("GZS_Zumby3").getSubImage(0, 0, 48, 48);
		if(zombie != null) {
			for(Pair<Float> pos : positions) {
				float theta = Calculate.Hypotenuse(playerPos, pos) - (float)(Math.PI / 2);
				g.rotate(pos.x, pos.y, (float)Math.toDegrees(theta));
				g.drawImage(zombie, (pos.x - (zombie.getWidth() / 2)), (pos.y - (zombie.getHeight() / 2)));
				g.resetTransform();
			}
		}
		
		// Draw the flashlight.
		flashlight.render(g, 0L);
		
		// Draw the UI for adjusting shadow opacity.
		g.setColor(Color.black);
		g.fillRect(0.0f, 300.0f, Globals.WIDTH, (Globals.HEIGHT - 300.0f));
		
		opacitySlider.render(g, 0L);
		
		// Draw the text explaining what the slider is for.
		g.setFont(assets.getFont("PressStart2P-Regular"));
		Calculate.TextWrap(g, EXPLANATION, g.getFont(), ((Globals.WIDTH / 2) - 250.0f), (Globals.HEIGHT - 250.0f), 500.0f, true);
		
		applyButton.render(g, 0L);
		backButton.render(g, 0L);
	}
	
	@Override
	public void mouseClicked(int button, int x, int y, int count) {
		if(opacitySlider.contains(x, y)) opacitySlider.move();
	}

	@Override
	public void mouseMoved(int oldx, int oldy, int newx, int newy) {
		Controls.getInstance().getMouse().setPosition(newx, newy);
	}
	
	@Override
	public void mouseDragged(int oldx, int oldy, int newx, int newy) {
		Controls.getInstance().getMouse().setPosition(newx, newy);
		if(opacitySlider.contains(newx, newy)) opacitySlider.move();
	}
	
	@Override
	public void mousePressed(int button, int x, int y) {
		if(button == Input.MOUSE_LEFT_BUTTON) Controls.getInstance().getMouse().setLeftDown(true);
	}
	
	@Override
	public void mouseReleased(int button, int x, int y) {
		if(button == Input.MOUSE_LEFT_BUTTON) Controls.getInstance().getMouse().setLeftDown(false);
	}
	
	private Consumer<Float> opacityOperation = new Consumer<Float>() {
		@Override
		public void accept(Float val_) {
			Flashlight.setShadowOpacity(val_);
		}
	};
	
	@Override
	public int getID() {
		return GammaSettingsState.ID;
	}

}
