package com.gzsr.states;

import java.awt.Font;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.InputListener;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import com.gzsr.Globals;
import com.gzsr.MusicPlayer;
import com.gzsr.math.Calculate;
import com.gzsr.misc.Pair;
import com.gzsr.objects.weapons.Weapon;

public class ShopState extends BasicGameState implements InputListener {
	public static final int ID = 2;
	
	private static final TrueTypeFont FONT_HEADER = new TrueTypeFont(new Font("Lucida Console", Font.BOLD, 32), true);
	private static final TrueTypeFont FONT_NORMAL = new TrueTypeFont(new Font("Lucida Console", Font.PLAIN, 16), true);
	
	private static final Pair<Float> SELECTION_CONTAINER = new Pair<Float>(10.0f, 64.0f);
	private static final Pair<Float> DETAIL_CONTAINER = new Pair<Float>((Globals.WIDTH - 210.0f), 64.0f);
	//private static float SELECTION_WIDTH = Globals.WIDTH - 230.0f;
	private static float SELECTION_WIDTH = 300.0f;
	//private static float DETAIL_WIDTH = 200.0f;
	private static float DETAIL_WIDTH = 300.0f;
	private static float CONTAINER_HEIGHT = Globals.HEIGHT - 110.0f;
	
	private static final int ROWS = 6; // The number of rows of item boxes that can fit in the selection container.
	private static final int COLS = 6; // The number of item boxes that can fit in one row in the selection container.
	private static final float ITEM_BOX_SIZE = 96.0f;
	private static final float ITEM_BOX_SPACING_H = 20.0f;
	private static final float ITEM_BOX_SPACING_V = 10.0f;
	private static final float SELECTION_CONTAINER_PADDING_H = ((SELECTION_WIDTH - ((COLS * ITEM_BOX_SIZE) + (ITEM_BOX_SPACING_H * (COLS - 1)))) / 2);
	private static final float SELECTION_CONTAINER_PADDING_V = ((CONTAINER_HEIGHT - ((ROWS * ITEM_BOX_SIZE) + (ITEM_BOX_SPACING_V * (ROWS - 1)))) / 2);
	
	private Rectangle [][] itemBoxes;
	private Pair<Integer> selected;
	
	private boolean exit;
	
	@Override
	public void init(GameContainer gc, StateBasedGame game) throws SlickException {
		itemBoxes = new Rectangle[ROWS][COLS]; // 12 rows, 13 columns
		for(int r = 0; r < ROWS; r++) {
			for(int c = 0; c < COLS; c++) {
				float x = (((c * ITEM_BOX_SIZE) + (c * ITEM_BOX_SPACING_H)) + SELECTION_CONTAINER.x + SELECTION_CONTAINER_PADDING_H);
				float y = (((r * ITEM_BOX_SIZE) + (r * ITEM_BOX_SPACING_V)) + SELECTION_CONTAINER.y + SELECTION_CONTAINER_PADDING_V);
				itemBoxes[r][c] = new Rectangle(x, y, ITEM_BOX_SIZE, ITEM_BOX_SIZE);
			}
		}
		
		selected = null;
		exit = false;
	}

	@Override
	public void update(GameContainer gc, StateBasedGame game, int delta) throws SlickException {
		if(exit) game.enterState(GameState.ID, new FadeOutTransition(), new FadeInTransition());
		MusicPlayer.getInstance().update();
	}
	
	/*
	@Override
	public void render(GameContainer gc, StateBasedGame game, Graphics g) throws SlickException {
		g.resetTransform();
		g.clear();
		
		g.setColor(Color.darkGray);
		g.fillRect(0.0f, 0.0f, Globals.WIDTH, Globals.HEIGHT);
		
		// Draw the header and footer.
		g.setColor(Color.white);
		g.drawLine(10.0f, 36.0f, (Globals.WIDTH - 10.0f), 36.0f);
		g.drawLine(10.0f, (Globals.HEIGHT - 36.0f), (Globals.WIDTH - 10.0f), (Globals.HEIGHT - 36.0f));
		g.setFont(ShopState.FONT_HEADER);
		g.drawString("Item Shop", 30.0f, 20.0f);
		
		// Draw the item selection container.
		g.setColor(new Color(0x2e2e2e));
		g.fillRect(SELECTION_CONTAINER.x, SELECTION_CONTAINER.y, SELECTION_WIDTH, CONTAINER_HEIGHT);
		g.setColor(Color.white);
		g.drawRect(SELECTION_CONTAINER.x, SELECTION_CONTAINER.y, SELECTION_WIDTH, CONTAINER_HEIGHT);
		
		// Draw the item detail container.
		g.setColor(new Color(0x2e2e2e));
		g.fillRect(DETAIL_CONTAINER.x, DETAIL_CONTAINER.y, DETAIL_WIDTH, CONTAINER_HEIGHT);
		g.setColor(Color.white);
		g.drawRect(DETAIL_CONTAINER.x, DETAIL_CONTAINER.y, DETAIL_WIDTH, CONTAINER_HEIGHT);
		
		// Draw the item boxes.
		List<Weapon> weapons = Globals.player.getActiveWeapons();
		for(int r = 0; r < ROWS; r++) {
			for(int c = 0; c < COLS; c++) {
				float x = itemBoxes[r][c].getX();
				float y = itemBoxes[r][c].getY();
				
				g.setColor(Color.black);
				g.fillRect(x, y, ITEM_BOX_SIZE, ITEM_BOX_SIZE);
				
				if((r == 0) || ((r == 1) && (c < (weapons.size() - COLS)))) {
					// FIXME: Change so this can load image from any item box.
					Image img = weapons.get((r * COLS) + c).getInventoryIcon();
					img.draw(x, y, 2.0f);
				}
				
				g.setColor(Color.darkGray);
				if((selected != null) && (selected.x == r) && (selected.y == c)) {
					g.setColor(Color.white);
					g.setLineWidth(2.0f);
				}
				g.drawRect(x, y, ITEM_BOX_SIZE, ITEM_BOX_SIZE);
				g.setLineWidth(1.0f);
			}
		}
		
		// Draw the item portrait.
		g.setColor(Color.black);
		g.fillRect((DETAIL_CONTAINER.x + 52.0f), (DETAIL_CONTAINER.y + 40.0f), 96.0f, 96.0f);
		g.setColor(Color.darkGray);
		g.drawRect((DETAIL_CONTAINER.x + 52.0f), (DETAIL_CONTAINER.y + 40.0f), 96.0f, 96.0f);
		
		if((selected != null) && ((selected.x == 0) || ((selected.x == 1) && (selected.y < (weapons.size() - COLS)))) && (selected.y < weapons.size())) {
			// FIXME: Change this so portrait can be loaded for any item box.
			Image portrait = weapons.get((selected.x * COLS) + selected.y).getInventoryIcon();
			portrait.draw((DETAIL_CONTAINER.x + 52.0f), (DETAIL_CONTAINER.y + 40.0f), 2.0f);
		}
		
		// Draw the item's name below its portrait.
		// FIXME: Change this so item name can be taken from any item box.
		String itemName = ((selected != null) && ((selected.x == 0) || ((selected.x == 1) && (selected.y < (weapons.size() - COLS)))) && (selected.y < weapons.size())) ? weapons.get((selected.x * COLS) + selected.y).getName() : "No Item Selected";
		Calculate.TextWrap(g, itemName, FONT_NORMAL, 
						   (DETAIL_CONTAINER.x + 10.0f), (DETAIL_CONTAINER.y + 136.0f + FONT_NORMAL.getHeight()), 
						   180.0f, true, Color.white);
	}
	*/
	
	/**
	 * Alternate render method. Testing a different shop layout.
	 */
	@Override
	public void render(GameContainer gc, StateBasedGame game, Graphics g) throws SlickException {
		g.resetTransform();
		g.clear();
		
		g.setColor(Color.darkGray);
		g.fillRect(0.0f, 0.0f, Globals.WIDTH, Globals.HEIGHT);
		
		// Draw the header and footer.
		g.setColor(Color.white);
		g.drawLine(10.0f, 36.0f, (Globals.WIDTH - 10.0f), 36.0f);
		g.drawLine(10.0f, (Globals.HEIGHT - 36.0f), (Globals.WIDTH - 10.0f), (Globals.HEIGHT - 36.0f));
		g.setFont(ShopState.FONT_HEADER);
		g.drawString("Item Shop", 30.0f, 20.0f);
		
		// Draw the item selection container.
		g.setColor(new Color(0x2e2e2e));
		g.fillRect(SELECTION_CONTAINER.x, SELECTION_CONTAINER.y, SELECTION_WIDTH, CONTAINER_HEIGHT);
		g.setColor(Color.white);
		g.drawRect(SELECTION_CONTAINER.x, SELECTION_CONTAINER.y, SELECTION_WIDTH, CONTAINER_HEIGHT);
	}
	
	@Override
	public void mouseMoved(int oldx, int oldy, int newx, int newy) {
		Globals.mouse.setPosition(newx, newy);
	}
	
	@Override
	public void mousePressed(int button, int x, int y) {
		if(button == 0) Globals.mouse.setMouseDown(true);
		
		// Check to see if we've clicked an item box.
		for(int r = 0; r < ROWS; r++) {
			for(int c = 0; c < COLS; c++) {
				if(itemBoxes[r][c].contains(x, y)) {
					selected = new Pair<Integer>(r, c);
				}
			}
		}
	}
	
	@Override
	public void mouseReleased(int button, int x, int y) {
		if(button == 0) Globals.mouse.setMouseDown(false);
	}
	
	@Override
	public void keyReleased(int key, char c) {
		if(key == Input.KEY_B) exit = true;
	}
	
	@Override
	public void enter(GameContainer gc, StateBasedGame game) {
		exit = false;
	}

	@Override
	public int getID() {
		return ShopState.ID;
	}
}
