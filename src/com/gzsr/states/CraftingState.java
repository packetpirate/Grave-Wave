package com.gzsr.states;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
import org.newdawn.slick.util.FontUtils;

import com.gzsr.AssetManager;
import com.gzsr.Controls;
import com.gzsr.Globals;
import com.gzsr.MusicPlayer;
import com.gzsr.entities.Player;
import com.gzsr.gfx.ui.CraftWindow;
import com.gzsr.gfx.ui.MenuButton;
import com.gzsr.misc.MouseInfo;
import com.gzsr.misc.Pair;
import com.gzsr.objects.crafting.Recipe;
import com.gzsr.objects.crafting.RecipeController;
import com.gzsr.objects.crafting.Resources;
import com.gzsr.talents.Talents;

public class CraftingState extends BasicGameState implements InputListener {
	public static final int ID = 15;

	private static final Pair<Float> CRAFT_WINDOW_SIZE = new Pair<Float>((Globals.WIDTH - 200.0f), 500.0f);
	private static final Pair<Float> CRAFT_WINDOW_POS = new Pair<Float>(((Globals.WIDTH / 2) - (CRAFT_WINDOW_SIZE.x / 2)), 150.0f);
	private static final float SCROLL_SPEED = 20.0f;

	private MenuButton back;

	private List<CraftWindow> crafts;

	private float sOff; // Scroll Offset

	private boolean exit;

	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		UnicodeFont large = AssetManager.getManager().getFont("PressStart2P-Regular_large");
		float fh = large.getLineHeight();
		back = new MenuButton(new Pair<Float>((Globals.WIDTH - large.getWidth("Exit") - 100.0f), (Globals.HEIGHT - fh - 40.0f)), "Exit");

		crafts = new ArrayList<CraftWindow>();

		sOff = 0.0f;

		exit = false;
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		g.resetTransform();
		g.clear();

		g.setColor(Color.darkGray);
		g.fillRect(0.0f, 0.0f, Globals.WIDTH, Globals.HEIGHT);

		drawCraftWindow(g);
		drawResources(g);

		back.render(g, 0L);

		UnicodeFont f = AssetManager.getManager().getFont("PressStart2P-Regular_large");
		FontUtils.drawCenter(f, "Crafting", 0, 40, Globals.WIDTH);
	}

	private void drawCraftWindow(Graphics g) {
		g.setColor(Color.gray);
		g.fillRect(CRAFT_WINDOW_POS.x, CRAFT_WINDOW_POS.y, CRAFT_WINDOW_SIZE.x, CRAFT_WINDOW_SIZE.y);

		drawCrafts(g);

		// Draw bars to cover the scrolled craft boxes.
		g.setColor(Color.darkGray);
		g.fillRect(CRAFT_WINDOW_POS.x, 0.0f, CRAFT_WINDOW_SIZE.x, CRAFT_WINDOW_POS.y);
		g.fillRect(CRAFT_WINDOW_POS.x, (CRAFT_WINDOW_POS.y + CRAFT_WINDOW_SIZE.y), CRAFT_WINDOW_SIZE.x, (Globals.HEIGHT - CRAFT_WINDOW_SIZE.y - CRAFT_WINDOW_POS.y));
		// Draw bounding box over crafting window.
		g.setColor(Color.white);
		g.drawRect(CRAFT_WINDOW_POS.x, CRAFT_WINDOW_POS.y, CRAFT_WINDOW_SIZE.x, CRAFT_WINDOW_SIZE.y);
	}

	private void drawCrafts(Graphics g) {
		g.translate(0.0f, -sOff);
		crafts.stream().forEach(craft -> craft.render(g, 0L));
		g.resetTransform();
	}

	private void drawResources(Graphics g) {
		int [] resources = Player.getPlayer().getResources().getAll();
		float w = ((32.0f * resources.length) + (resources.length * 10.0f));

		UnicodeFont f = AssetManager.getManager().getFont("PressStart2P-Regular_small");
		for(int i = 0; i < resources.length; i++) {
			w += (f.getWidth(Integer.toString(resources[i])) + 10.0f);
		}

		float x = ((Globals.WIDTH / 2) - (w / 2));
		float y = 100.0f;
		g.setFont(f);
		for(int i = 0; i < resources.length; i++) {
			Image icon = AssetManager.getManager().getImage(Resources.getIconName(i));

			String resource = Integer.toString(resources[i]);
			float tw = f.getWidth(resource);

			g.setColor(Color.black);
			g.fillRect(x, y, 32.0f, 32.0f);
			g.drawImage(icon, x, y);
			g.setColor(Color.white);
			g.drawRect(x, y, 32.0f, 32.0f);

			g.drawString(resource, (x + 42.0f), (y + (16.0f - (f.getLineHeight() / 2))));

			x += (tw + 52.0f);
		}
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		if(exit) game.enterState(GameState.ID, new FadeOutTransition(Color.black, 250), new FadeInTransition(Color.black, 100));

		MouseInfo mouse = Controls.getInstance().getMouse();
		if(back.inBounds(mouse.getPosition().x, mouse.getPosition().y)) {
			back.mouseEnter();
			if(mouse.isLeftDown()) game.enterState(GameState.ID, new FadeOutTransition(Color.black, 250), new FadeInTransition(Color.black, 100));
		} else back.mouseExit();

		MusicPlayer.getInstance().update(false);
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
	public void mouseWheelMoved(int change) {
		MouseInfo mouse = Controls.getInstance().getMouse();
		float scrollAmount = ((change > 0) ? SCROLL_SPEED : -SCROLL_SPEED);
		sOff += scrollAmount;
	}

	@Override
	public void keyReleased(int key, char c) {
		if((key == Controls.Layout.CRAFT_SCREEN.getKey()) ||
		   (key == Input.KEY_ESCAPE)) exit = true;
	}

	@Override
	public void enter(GameContainer gc, StateBasedGame game) {
		Controls.getInstance().resetAll();

		// Determine which recipes the player has unlocked.
		crafts.clear();

		List<Recipe> recipes = new ArrayList<Recipe>();
		if(Talents.Munitions.INVENTOR.active()) recipes.addAll(RecipeController.getBasicRecipes().stream().filter(recipe -> !recipe.isCrafted()).collect(Collectors.toList()));
		if(Talents.Munitions.ENGINEER.active()) recipes.addAll(RecipeController.getAdvancedRecipes().stream().filter(recipe -> !recipe.isCrafted()).collect(Collectors.toList()));

		if(!recipes.isEmpty()) {
			for(int i = 0; i < recipes.size(); i++) {
				CraftWindow craft = new CraftWindow(new Pair<Float>((CRAFT_WINDOW_POS.x + 20.0f), (CRAFT_WINDOW_POS.y + (i * CraftWindow.HEIGHT) + ((i + 1) * 20.0f))), recipes.get(i));
				crafts.add(craft);
			}
		}
	}

	@Override
	public int getID() {
		return CraftingState.ID;
	}
}
