package com.gzsr.states;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.InputListener;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;
import org.newdawn.slick.util.FontUtils;

import com.gzsr.AssetManager;
import com.gzsr.Globals;
import com.gzsr.MusicPlayer;
import com.gzsr.entities.Entity;
import com.gzsr.gfx.ui.TransactionButton;
import com.gzsr.misc.Pair;
import com.gzsr.objects.weapons.Weapon;

public class ShopState extends BasicGameState implements InputListener {
	public static final int ID = 2;
	
	private static final float CONTAINER_WIDTH = 300.0f;
	private static final float CONTAINER_HEIGHT = Globals.HEIGHT - 110.0f;
	
	private static final Pair<Float> INVENTORY_CONTAINER = new Pair<Float>(10.0f, 64.0f);
	private static final Pair<Float> SHOP_CONTAINER = new Pair<Float>((Globals.WIDTH - CONTAINER_WIDTH - 10.0f), 64.0f);
	private static final Pair<Float> ITEM_DESC = new Pair<Float>((CONTAINER_WIDTH + 10.0f), 64.0f);
	private static final Pair<Float> ITEM_PORTRAIT = new Pair<Float>(((Globals.WIDTH / 2) - 48.0f), 104.0f);
	
	private static final int SHOP_ROWS = 6;
	private static final int SHOP_COLS = 3;
	
	private static final float ITEM_BOX_SIZE = 96.0f;
	
	private Rectangle [][] inventoryBoxes;
	private Rectangle [][] shopBoxes;
	private Pair<Integer> selected;
	private boolean selectedInInventory;
	
	private TransactionButton buyButton;
	private TransactionButton sellButton;
	
	private int inventorySize;
	private boolean exit;
	
	@Override
	public void init(GameContainer gc, StateBasedGame game) throws SlickException {
		shopBoxes = new Rectangle[SHOP_ROWS][SHOP_COLS];
		for(int r = 0; r < SHOP_ROWS; r++) {
			for(int c = 0; c < SHOP_COLS; c++) {
				float x = SHOP_CONTAINER.x + 3.0f + ((c * ITEM_BOX_SIZE) + (c * 2.0f));
				float y = SHOP_CONTAINER.y + 3.0f + ((r * ITEM_BOX_SIZE) + (c * 2.0f));
				shopBoxes[r][c] = new Rectangle(x, y, ITEM_BOX_SIZE, ITEM_BOX_SIZE);
			}
		}
		
		inventoryBoxes = null;
		selected = null;
		selectedInInventory = false;
		
		buyButton = new TransactionButton(new Pair<Float>((float)((Globals.WIDTH / 2) - 58.0f), (Globals.HEIGHT - 70.0f)), TransactionButton.Type.BUY);
		sellButton = new TransactionButton(new Pair<Float>((float)((Globals.WIDTH / 2) + 58.0f), (Globals.HEIGHT - 70.0f)), TransactionButton.Type.SELL);
		
		inventorySize = 0;
		exit = false;
	}

	@Override
	public void update(GameContainer gc, StateBasedGame game, int delta) throws SlickException {
		if(exit) game.enterState(GameState.ID, new FadeOutTransition(), new FadeInTransition());
		MusicPlayer.getInstance().update();
	}
	
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
		
		// Draw the header text.
		g.setFont(AssetManager.getManager().getFont("PressStart2P-Regular_large"));
		g.drawString("Inventory", 10.0f, 20.0f);
		
		float shopTextWidth = g.getFont().getWidth("Item Shop");
		g.drawString("Item Shop", (Globals.WIDTH - shopTextWidth -  10.0f), 20.0f);
		
		// Draw the inventory container.
		g.setColor(new Color(0x2e2e2e));
		g.fillRect(INVENTORY_CONTAINER.x, INVENTORY_CONTAINER.y, CONTAINER_WIDTH, CONTAINER_HEIGHT);
		g.setColor(Color.white);
		g.drawRect(INVENTORY_CONTAINER.x, INVENTORY_CONTAINER.y, CONTAINER_WIDTH, CONTAINER_HEIGHT);
		
		// Draw the inventory boxes.
		int cols = SHOP_COLS;
		int rows = (int)(Math.ceil((float)inventorySize / (float)cols));
		for(int r = 0; r < rows; r++) {
			for(int c = 0; c < cols; c++) {
				float x = (INVENTORY_CONTAINER.x + (c * ITEM_BOX_SIZE) + (c * 2.0f) + 3.0f);
				float y = (INVENTORY_CONTAINER.y + (r * ITEM_BOX_SIZE) + (r * 2.0f) + 3.0f);
				
				g.setColor(Color.black);
				g.fillRect(x, y, ITEM_BOX_SIZE, ITEM_BOX_SIZE);
				
				// Draw the item image.
				Entity item = Globals.player.getInventory().getItem((r * cols) + c);
				if(item != null) {
					if(item instanceof Weapon) {
						Weapon w = (Weapon)item;
						Image img = w.getInventoryIcon();
						img.draw(x, y, 2.0f);
					}
				}
				
				if((selected != null) && selectedInInventory && (r == selected.y) && (c == selected.x)) {
					g.setColor(Color.white);
					g.setLineWidth(2.0f);
				} else {
					g.setColor(Color.darkGray);
				}
				g.drawRect(x, y, ITEM_BOX_SIZE, ITEM_BOX_SIZE);
				g.setLineWidth(1.0f);
			}
		}
		
		// Draw the shop container.
		g.setColor(new Color(0x2e2e2e));
		g.fillRect(SHOP_CONTAINER.x, SHOP_CONTAINER.y, CONTAINER_WIDTH, CONTAINER_HEIGHT);
		g.setColor(Color.white);
		g.drawRect(SHOP_CONTAINER.x, SHOP_CONTAINER.y, CONTAINER_WIDTH, CONTAINER_HEIGHT);
		
		// Draw the item description text.
		String description = "No Item Selected";
		Entity item = getSelectedItem();
		if((selected != null) && (item != null)) description = item.getName();
		
		g.setFont(AssetManager.getManager().getFont("PressStart2P-Regular"));
		float h = g.getFont().getLineHeight();
		FontUtils.drawCenter(g.getFont(), description,
							 (int)ITEM_DESC.x.floatValue(), 
							 (int)(ITEM_DESC.y.floatValue() + (h / 2)), 
							 (int)(Globals.WIDTH - ITEM_DESC.x - CONTAINER_WIDTH - 10.0f));
		
		// Draw the item portrait.
		g.setColor(Color.black);
		g.fillRect(ITEM_PORTRAIT.x, (ITEM_PORTRAIT.y + h), ITEM_BOX_SIZE, ITEM_BOX_SIZE);
		
		if((selected != null) && (item != null)) {
			if(item instanceof Weapon) {
				Weapon w = (Weapon)item;
				Image img = w.getInventoryIcon();
				img.draw(ITEM_PORTRAIT.x, (ITEM_PORTRAIT.y + h), 2.0f);
			}
			// TODO: Add cases for other kinds of items.
		}
		
		g.setColor(Color.white);
		g.drawRect(ITEM_PORTRAIT.x, (ITEM_PORTRAIT.y + h), ITEM_BOX_SIZE, ITEM_BOX_SIZE);
		
		// Draw the transaction buttons.
		buyButton.render(g, 0L);
		sellButton.render(g, 0L);
	}
	
	private Entity getSelectedItem() {
		Entity item = null;
		
		if(selected != null) {
			if(selectedInInventory) {
				return Globals.player.getInventory().getItem((selected.y * SHOP_COLS) + selected.x);
			} else {
				// FIXME: When shop inventory exists, return item from shop inventory instead.
				return null;
			}
		}
		
		return item;
	}
	
	private boolean findSelectedItem(int x, int y) {
		// Check the inventory boxes.
		int cols = SHOP_COLS;
		int rows = (int)(Math.ceil((float)inventorySize / (float)cols));
		for(int r = 0; r < rows; r++) {
			for(int c = 0; c < cols; c++) {
				if(inventoryBoxes[r][c].contains(x, y)) {
					selected = new Pair<Integer>(c, r);
					selectedInInventory = true;
					return true;
				}
			}
		}
		
		// Check the shop boxes.
		for(int r = 0; r < SHOP_ROWS; r++) {
			for(int c = 0; c < SHOP_COLS; c++) {
				if(shopBoxes[r][c].contains(x, y)) {
					selected = new Pair<Integer>(c, r);
					selectedInInventory = false;
					return true;
				}
			}
		}
		
		return false;
	}
	
	@Override
	public void mouseMoved(int oldx, int oldy, int newx, int newy) {
		Globals.mouse.setPosition(newx, newy);
	}
	
	@Override
	public void mousePressed(int button, int x, int y) {
		if(button == 0) Globals.mouse.setMouseDown(true);
		
		// Check to see if we've clicked an item.
		boolean itemClicked = findSelectedItem(x, y);
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
		
		if(Globals.player.getInventory().getCapacity() != inventorySize) {
			// Inventory size has changed. Re-build inventory layout.
			inventorySize = Globals.player.getInventory().getCapacity();
			int cols = SHOP_COLS;
			int rows = (int)(Math.ceil((float)inventorySize / (float)cols));
			if(inventoryBoxes == null) {
				inventoryBoxes = new Rectangle[rows][cols];
			}
			
			for(int r = 0; r < rows; r++) {
				for(int c = 0; c < cols; c++) {
					float x = INVENTORY_CONTAINER.x + 3.0f + ((c * ITEM_BOX_SIZE) + (c * 2.0f));
					float y = INVENTORY_CONTAINER.y + 3.0f + ((r * ITEM_BOX_SIZE) + (c * 2.0f));
					inventoryBoxes[r][c] = new Rectangle(x, y, ITEM_BOX_SIZE, ITEM_BOX_SIZE);
				}
			}
		}
	}

	@Override
	public int getID() {
		return ShopState.ID;
	}
}
