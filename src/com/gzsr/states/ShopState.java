package com.gzsr.states;

import java.text.NumberFormat;
import java.util.Locale;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.InputListener;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;
import org.newdawn.slick.util.FontUtils;

import com.gzsr.AssetManager;
import com.gzsr.Controls;
import com.gzsr.Globals;
import com.gzsr.MusicPlayer;
import com.gzsr.entities.Entity;
import com.gzsr.entities.Player;
import com.gzsr.gfx.ui.TransactionButton;
import com.gzsr.math.Calculate;
import com.gzsr.misc.MouseInfo;
import com.gzsr.misc.Pair;
import com.gzsr.objects.Inventory;
import com.gzsr.objects.items.ItemConstants;
import com.gzsr.objects.weapons.AK47;
import com.gzsr.objects.weapons.BigRedButton;
import com.gzsr.objects.weapons.BowAndArrow;
import com.gzsr.objects.weapons.ClaymoreWeapon;
import com.gzsr.objects.weapons.Flamethrower;
import com.gzsr.objects.weapons.GrenadeLauncher;
import com.gzsr.objects.weapons.LaserBarrier;
import com.gzsr.objects.weapons.Mossberg;
import com.gzsr.objects.weapons.SentryWeapon;
import com.gzsr.objects.weapons.Weapon;

public class ShopState extends BasicGameState implements InputListener {
	public static final int ID = 2;
	
	private static final float CONTAINER_WIDTH = 300.0f;
	private static final float CONTAINER_HEIGHT = Globals.HEIGHT - 110.0f;
	private static final int SHOP_SIZE = 30;
	
	private static final Pair<Float> INVENTORY_CONTAINER = new Pair<Float>(10.0f, 64.0f);
	private static final Pair<Float> SHOP_CONTAINER = new Pair<Float>((Globals.WIDTH - CONTAINER_WIDTH - 10.0f), 64.0f);
	private static final Pair<Float> ITEM_DESC = new Pair<Float>((CONTAINER_WIDTH + 10.0f), 64.0f);
	private static final Pair<Float> ITEM_PORTRAIT = new Pair<Float>(((Globals.WIDTH / 2) - 48.0f), 104.0f);
	
	private static final float SCROLL_SPEED = 20.0f;
	
	private static final int SHOP_COLS = 3;
	private static final int SHOP_ROWS = SHOP_SIZE / SHOP_COLS;
	
	private static final float ITEM_BOX_SIZE = 96.0f;
	
	private static final double SELL_BACK_VALUE = 0.6;
	
	private static Inventory SHOP;
	public static Inventory getShop() {
		if(SHOP == null) SHOP = new Inventory(SHOP_SIZE);
		return SHOP;
	}
	
	private Pair<Float> inventoryOrigin;
	private Pair<Float> shopOrigin;
	
	private Rectangle [][] inventoryBoxes;
	private Rectangle [][] shopBoxes;
	private Pair<Integer> selected;
	private boolean selectedInInventory;
	
	private TransactionButton buyButton;
	private TransactionButton sellButton;
	private TransactionButton ammoButton;
	private TransactionButton maxAmmoButton;
	
	private int inventorySize;
	private boolean exit;
	
	@Override
	public void init(GameContainer gc, StateBasedGame game) throws SlickException {
		inventoryOrigin = new Pair<Float>(INVENTORY_CONTAINER.x, INVENTORY_CONTAINER.y);
		shopOrigin = new Pair<Float>(SHOP_CONTAINER.x, SHOP_CONTAINER.y);
		
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
		
		buyButton = new TransactionButton(new Pair<Float>((float)((Globals.WIDTH / 2) - 113.0f), (ITEM_PORTRAIT.y + 64.0f)), TransactionButton.Type.BUY);
		sellButton = new TransactionButton(new Pair<Float>((float)((Globals.WIDTH / 2) + 113.0f), (ITEM_PORTRAIT.y + 64.0f)), TransactionButton.Type.SELL);
		ammoButton = new TransactionButton(new Pair<Float>((float)(Globals.WIDTH / 2), ((Globals.HEIGHT / 2) - 30.0f)), TransactionButton.Type.AMMO);
		{ // Adjust ammo button position.
			float x = ammoButton.getPosition().x;
			float w = ammoButton.getSize().x;
			
			ammoButton.setPosition(new Pair<Float>((x - w), ammoButton.getPosition().y));
		} // Done adjusting ammo button.
		maxAmmoButton = new TransactionButton(new Pair<Float>((float)(Globals.WIDTH / 2), (float)((Globals.HEIGHT / 2) - 30.0f)), TransactionButton.Type.AMMO);
		{ // Adjust max ammo button position.
			float x = maxAmmoButton.getPosition().x;
			float w = maxAmmoButton.getSize().x;
			
			maxAmmoButton.setPosition(new Pair<Float>((x + w), maxAmmoButton.getPosition().y));
		} // Done adjusting max ammo button.
		
		inventorySize = 0;
		exit = false;
	}

	@Override
	public void update(GameContainer gc, StateBasedGame game, int delta) throws SlickException {
		if(exit) game.enterState(GameState.ID, new FadeOutTransition(Color.black, 250), new FadeInTransition(Color.black, 250));
		MusicPlayer.getInstance().update(false);
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
		
		drawInventory(g);
		
		drawShop(g);
		
		drawShopCenter(g);
		
		drawHeaderAndFooter(g);
	}
	
	private void drawHeaderAndFooter(Graphics g) {
		// Draw featureless background boxes to hide item boxes in scroll.
		g.setColor(Color.darkGray);
		g.fillRect(10.0f, 0.0f, CONTAINER_WIDTH, (INVENTORY_CONTAINER.y - 1));
		g.fillRect(10.0f, (INVENTORY_CONTAINER.y + CONTAINER_HEIGHT + 1), CONTAINER_WIDTH, (Globals.HEIGHT - INVENTORY_CONTAINER.y));
		g.fillRect(SHOP_CONTAINER.x, 0.0f, CONTAINER_WIDTH, (SHOP_CONTAINER.y - 1));
		g.fillRect(SHOP_CONTAINER.x, (SHOP_CONTAINER.y + CONTAINER_HEIGHT + 1), CONTAINER_WIDTH, (Globals.HEIGHT - SHOP_CONTAINER.y));
		
		// Draw the header and footer.
		g.setColor(Color.white);
		g.drawLine(10.0f, 36.0f, (Globals.WIDTH - 10.0f), 36.0f);
		g.drawLine(10.0f, (Globals.HEIGHT - 36.0f), (Globals.WIDTH - 10.0f), (Globals.HEIGHT - 36.0f));
		
		// Draw the header text.
		g.setFont(AssetManager.getManager().getFont("PressStart2P-Regular_large"));
		g.drawString("Inventory", 10.0f, 20.0f);
		
		float shopTextWidth = g.getFont().getWidth("Item Shop");
		g.drawString("Item Shop", (Globals.WIDTH - shopTextWidth -  10.0f), 20.0f);
		
		// Draw the shop and inventory borders. This is so item boxes don't overlap border because of draw order.
		g.setColor(Color.white);
		g.drawRect(INVENTORY_CONTAINER.x, INVENTORY_CONTAINER.y, CONTAINER_WIDTH, CONTAINER_HEIGHT);
		g.drawRect(SHOP_CONTAINER.x, SHOP_CONTAINER.y, CONTAINER_WIDTH, CONTAINER_HEIGHT);
	}
	
	private void drawInventory(Graphics g) {
		// Draw the inventory container.
		g.setFont(AssetManager.getManager().getFont("PressStart2P-Regular_small"));
		g.setColor(new Color(0x2e2e2e));
		g.fillRect(INVENTORY_CONTAINER.x, INVENTORY_CONTAINER.y, CONTAINER_WIDTH, CONTAINER_HEIGHT);
		
		// Draw the inventory boxes.
		int cols = SHOP_COLS;
		int rows = (int)(Math.ceil((float)inventorySize / (float)cols));
		for(int r = 0; r < rows; r++) {
			for(int c = 0; c < cols; c++) {
				float x = (inventoryOrigin.x + (c * ITEM_BOX_SIZE) + (c * 2.0f) + 3.0f);
				float y = (inventoryOrigin.y + (r * ITEM_BOX_SIZE) + (r * 2.0f) + 3.0f);
				
				g.setColor(Color.black);
				g.fillRect(x, y, ITEM_BOX_SIZE, ITEM_BOX_SIZE);
				
				// Draw the item image.
				Entity item = Player.getPlayer().getInventory().getItem((r * cols) + c);
				if(item != null) {
					if(item instanceof Weapon) {
						Weapon w = (Weapon)item;
						Image img = w.getInventoryIcon();
						
						float ix = (x + (img.getWidth() / 4));
						float iy = (y + (img.getHeight() / 4) - 5.0f);
						
						img.draw(ix, iy, 1.5f);
						
						// Draw the weapon's current ammo below.
						String ammoText = String.format("%d / %d", w.getClipAmmo(), w.getInventoryAmmo());
						float ty = (y + ITEM_BOX_SIZE - g.getFont().getLineHeight() - 5.0f);
						FontUtils.drawCenter(g.getFont(), ammoText, (int)x, (int)ty, (int)ITEM_BOX_SIZE, Color.white);
					}
					// TODO: Add cases for other item types.
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
	}
	
	private void drawShop(Graphics g) {
		// Draw the shop container.
		g.setColor(new Color(0x2e2e2e));
		g.fillRect(SHOP_CONTAINER.x, SHOP_CONTAINER.y, CONTAINER_WIDTH, CONTAINER_HEIGHT);
		
		// Draw the inventory boxes.
		int cols = SHOP_COLS;
		int rows = (int)(Math.ceil((float)getShop().getCapacity() / (float)cols));
		for(int r = 0; r < rows; r++) {
			for(int c = 0; c < cols; c++) {
				float x = (shopOrigin.x + (c * ITEM_BOX_SIZE) + (c * 2.0f) + 3.0f);
				float y = (shopOrigin.y + (r * ITEM_BOX_SIZE) + (r * 2.0f) + 3.0f);
				
				g.setColor(Color.black);
				g.fillRect(x, y, ITEM_BOX_SIZE, ITEM_BOX_SIZE);
				
				// Draw the item image.
				Entity item = getShop().getItem((r * cols) + c);
				if(item != null) {
					if(item instanceof Weapon) {
						Weapon w = (Weapon)item;
						Image img = w.getInventoryIcon();
						img.draw(x, y, 2.0f);
					}
					// TODO: Add cases for other item types.
				}
				
				if((selected != null) && !selectedInInventory && (r == selected.y) && (c == selected.x)) {
					g.setColor(Color.white);
					g.setLineWidth(2.0f);
				} else {
					g.setColor(Color.darkGray);
				}
				g.drawRect(x, y, ITEM_BOX_SIZE, ITEM_BOX_SIZE);
				g.setLineWidth(1.0f);
			}
		}
	}
	
	private void drawShopCenter(Graphics g) {
		// Draw the item description text.
		String itemName = "No Item Selected";
		Entity item = getSelectedItem();
		if((selected != null) && (item != null)) itemName = item.getName();
		
		g.setFont(AssetManager.getManager().getFont("PressStart2P-Regular"));
		float h = g.getFont().getLineHeight();
		FontUtils.drawCenter(g.getFont(), itemName,
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
				
				UnicodeFont f = AssetManager.getManager().getFont("PressStart2P-Regular_small"); 
				g.setFont(f);
				
				float barWidth = 157.0f;
				float barHeight = 50.0f;
				float barX = ((Globals.WIDTH / 2) - (barWidth / 2));
				float barY = (Globals.HEIGHT - ((barHeight * 3.0f) + (f.getLineHeight() * 3.0f) + 35.0f) - 138.0f);
				
				// Draw damage rating.
				{
					UnicodeFont regular = AssetManager.getManager().getFont("PressStart2P-Regular");
					FontUtils.drawCenter(f, "Damage", (int)barX, (int)(barY - f.getLineHeight() - 5.0f), (int)barWidth, Color.white);
					Pair<Integer> damageRange = w.getDamage();
					
					// Apply the player's current damage bonus to the damage range.
					double damageBonus = (Player.getPlayer().getAttributes().getInt("damageUp") * 0.10);
					damageRange.x += (int)(damageRange.x * damageBonus);
					damageRange.y += (int)(damageRange.y * damageBonus);
					
					g.setColor(Color.black);
					g.fillRect(barX, barY, barWidth, barHeight);
					g.setColor(Color.white);
					g.drawRect(barX, barY, barWidth, barHeight);
					
					String text = String.format("%d - %d", damageRange.x, damageRange.y);
					float textHeight = regular.getHeight(text);
					
					FontUtils.drawCenter(regular, text, (int)barX, (int)((barY + (barHeight / 2)) - (textHeight / 2)), (int)barWidth, Color.white);
				}
				
				// Draw rate of fire rating.
				{
					float y = (barY + f.getLineHeight() + barHeight + 15.0f);
					FontUtils.drawCenter(f, "Rate of Fire", (int)barX, (int)(y - f.getLineHeight() - 5.0f), (int)barWidth, Color.white);
					
					g.setColor(Color.black);
					g.fillRect(barX, y, barWidth, barHeight);
					g.setColor(Color.white);
					g.drawRect(barX, y, barWidth, barHeight);
					
					int pips = ItemConstants.getRateOfFireClass(w.getCooldown()).getPipCount();
					for(int p = 0; p < pips; p++) {
						g.setColor(Color.red);
						g.fillRect((barX + (p * 15.0f) + 5.0f), (y + 5.0f), 10.0f, (barHeight - 10.0f));
						g.setColor(new Color(0x550000));
						g.drawRect((barX + (p * 15.0f) + 5.0f), (y + 5.0f), 10.0f, (barHeight - 10.0f));
					}
				}
				
				// Draw reload time rating.
				{
					float y = (barY + ((f.getLineHeight() + barHeight + 15.0f) * 2.0f));
					FontUtils.drawCenter(f, "Reload Time", (int)barX, (int)(y - f.getLineHeight() - 5.0f), (int)barWidth, Color.white);
					
					g.setColor(Color.black);
					g.fillRect(barX, y, barWidth, barHeight);
					g.setColor(Color.white);
					g.drawRect(barX, y, barWidth, barHeight);
					
					int pips = ItemConstants.getReloadTimeClass(w.getReloadTime()).getPipCount();
					for(int p = 0; p < pips; p++) {
						g.setColor(Color.red);
						g.fillRect((barX + (p * 15.0f) + 5.0f), (y + 5.0f), 10.0f, (barHeight - 10.0f));
						g.setColor(new Color(0x550000));
						g.drawRect((barX + (p * 15.0f) + 5.0f), (y + 5.0f), 10.0f, (barHeight - 10.0f));
					}
				}
			}
			// TODO: Add cases for other kinds of items.
			
			// Draw the transaction buttons.
			if(selectedInInventory) {
				sellButton.render(g, 0L);
				
				String cost = "Error";
				if(item instanceof Weapon) cost = "$" + NumberFormat.getInstance(Locale.US).format((int)(((Weapon)item).getPrice() * SELL_BACK_VALUE));
				
				g.setFont(AssetManager.getManager().getFont("PressStart2P-Regular"));
				FontUtils.drawCenter(g.getFont(), cost, (int)(CONTAINER_WIDTH + 10.0f), 
									 (int)(buyButton.getPosition().y - (g.getFont().getLineHeight() / 2)), 
									 (int)((Globals.WIDTH / 2) - CONTAINER_WIDTH - 58.0f), Color.white);
				
				// Draw the "buy ammo" and "max ammo" buttons and labels.
				if(item instanceof Weapon) {
					{ // Draw ammo button.
						Weapon w = (Weapon) item;
						boolean lessThanOne = ((w.getInventoryAmmo() == ((w.getMaxClips() - 1) * w.getClipSize())) && (w.getClipAmmo() < w.getClipSize()));
						int ammoPrice = (lessThanOne ? ((w.getAmmoPrice() / w.getClipSize()) * (w.getClipSize() - w.getClipAmmo())) : w.getAmmoPrice());
						String text = "$" + NumberFormat.getInstance(Locale.US).format(ammoPrice);
						FontUtils.drawCenter(g.getFont(), "One Clip", (int)(ammoButton.getPosition().x.floatValue() - (ammoButton.getSize().x / 2)), 
											 (int)(ammoButton.getPosition().y - ammoButton.getSize().y - (g.getFont().getLineHeight() - 10.0f)), 
											 (int)ammoButton.getSize().x.floatValue(), Color.white);
						ammoButton.render(g, 0L);
						if(((Weapon)item).clipsMaxedOut() || (Player.getPlayer().getAttributes().getInt("money") < ((Weapon)item).getAmmoPrice())) {
							// If the player has max ammo for this weapon or they can't afford more, show a "disabled" overlay on the button.
							float x = ammoButton.getPosition().x - (ammoButton.getSize().x / 2);
							float y = ammoButton.getPosition().y - (ammoButton.getSize().y / 2);
							
							g.setColor(new Color(0xBB333333));
							g.fillRect(x, y, ammoButton.getSize().x, ammoButton.getSize().y);
						}
						FontUtils.drawCenter(g.getFont(), text, (int)(ammoButton.getPosition().x.floatValue() - (ammoButton.getSize().x.floatValue() / 2)), 
											 (int)(ammoButton.getPosition().y.floatValue() - (g.getFont().getLineHeight() / 2)), 
											 (int)ammoButton.getSize().x.floatValue(), 
											 Color.black);
					} // End draw ammo button.
					{ // Draw max ammo button.
						String ammoPrice = "$" + NumberFormat.getInstance(Locale.US).format((int)(((Weapon)item).getMaxAmmoPrice()));
						FontUtils.drawCenter(g.getFont(), "Max Ammo", (int)(maxAmmoButton.getPosition().x.floatValue() - (maxAmmoButton.getSize().x / 2)), 
											 (int)(maxAmmoButton.getPosition().y - maxAmmoButton.getSize().y - (g.getFont().getLineHeight() - 10.0f)), 
											 (int)maxAmmoButton.getSize().x.floatValue(), Color.white);
						maxAmmoButton.render(g, 0L);
						if(((Weapon)item).clipsMaxedOut() || (Player.getPlayer().getAttributes().getInt("money") < ((Weapon)item).getMaxAmmoPrice())) {
							// If the player has max ammo for this weapon or they can't afford more, show a "disabled" overlay on the button.
							float x = maxAmmoButton.getPosition().x - (maxAmmoButton.getSize().x / 2);
							float y = maxAmmoButton.getPosition().y - (maxAmmoButton.getSize().y / 2);
							
							g.setColor(new Color(0xBB333333));
							g.fillRect(x, y, maxAmmoButton.getSize().x, maxAmmoButton.getSize().y);
						}
						FontUtils.drawCenter(g.getFont(), ammoPrice, (int)(maxAmmoButton.getPosition().x.floatValue() - (maxAmmoButton.getSize().x.floatValue() / 2)), 
											 (int)(maxAmmoButton.getPosition().y.floatValue() - (g.getFont().getLineHeight() / 2)), 
											 (int)maxAmmoButton.getSize().x.floatValue(), 
											 Color.black);
					} // End draw max ammo button.
				}
			} else {
				buyButton.render(g, 0L);
				
				String cost = "Error";
				if(item instanceof Weapon) cost = "$" + NumberFormat.getInstance(Locale.US).format(((Weapon)item).getPrice());
				
				g.setFont(AssetManager.getManager().getFont("PressStart2P-Regular"));
				FontUtils.drawCenter(g.getFont(), cost, (int)((Globals.WIDTH / 2) + 48.0f), 
									 (int)(buyButton.getPosition().y - (g.getFont().getLineHeight() / 2)), 
									 (int)((Globals.WIDTH / 2) - CONTAINER_WIDTH - 58.0f), Color.white);
			}
			
			// Draw the item description text.
			String description = item.getDescription();
			Calculate.TextWrap(g, description, AssetManager.getManager().getFont("PressStart2P-Regular_small"), ((Globals.WIDTH / 2) - 150.0f), (ITEM_PORTRAIT.y + 135.0f), 300.0f, true, Color.white);
		}
		
		g.setColor(Color.white);
		g.drawRect(ITEM_PORTRAIT.x, (ITEM_PORTRAIT.y + h), ITEM_BOX_SIZE, ITEM_BOX_SIZE);
		
		// Draw the player's current cash.
		String myCash = "$" + NumberFormat.getInstance(Locale.US).format(Player.getPlayer().getAttributes().getInt("money"));
		g.setFont(AssetManager.getManager().getFont("PressStart2P-Regular_large"));
		FontUtils.drawCenter(g.getFont(), myCash, (int)((Globals.WIDTH / 2) - 150.0f), (int)(Globals.HEIGHT - g.getFont().getLineHeight() - 70.0f), 300, Color.white);
	}
	
	private Entity getSelectedItem() {
		Entity item = null;
		
		if(selected != null) {
			if(selectedInInventory) {
				return Player.getPlayer().getInventory().getItem((selected.y * SHOP_COLS) + selected.x);
			} else {
				return SHOP.getItem((selected.y * SHOP_COLS) + selected.x);
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
		Controls.getInstance().getMouse().setPosition(newx, newy);
	}
	
	@Override
	public void mousePressed(int button, int x, int y) {
		if(button == 0) Controls.getInstance().getMouse().setMouseDown(true);
		
		// Check to see if the buy/sell buttons were clicked.
		if(selected != null) {
			AssetManager assets = AssetManager.getManager();
			Entity item = getSelectedItem();
			if(buyButton.inBounds(x, y) && !selectedInInventory) {
				if(item instanceof Weapon) {
					Weapon w = (Weapon)item;
					int playerMoney = Player.getPlayer().getAttributes().getInt("money"); 
					if(playerMoney >= w.getPrice()) {
						Player.getPlayer().getAttributes().set("money", (playerMoney - w.getPrice()));
						Player.getPlayer().getInventory().addItem(w);
						Player.getPlayer().resetCurrentWeapon();
						SHOP.dropItem(w.getName());
						assets.getSound("buy_ammo2").play(1.0f, assets.getSoundVolume());
					}
				}
				// TODO: Add cases for other item types.
			} else if(sellButton.inBounds(x, y) && selectedInInventory) {
				if(item instanceof Weapon) {
					Weapon w = (Weapon)item;
					int sellValue = (int)(w.getPrice() * SELL_BACK_VALUE);
					Player.getPlayer().getAttributes().set("money", (Player.getPlayer().getAttributes().getInt("money") + sellValue));
					Player.getPlayer().getInventory().dropItem(w.getName());
					Player.getPlayer().resetCurrentWeapon();
					SHOP.addItem(w);
					assets.getSound("buy_ammo2").play(1.0f, assets.getSoundVolume());
				}
				// TODO: Add cases for other item types.
			} else if(ammoButton.inBounds(x, y) && selectedInInventory) {
				// Buy ammo for the currently selected weapon.
				if(item instanceof Weapon) {
					Weapon w = (Weapon)item;
					
					if(!w.clipsMaxedOut()) {
						// If the player has less than a clip left and max ammo otherwise, only charge for difference.
						boolean lessThanOne = ((w.getInventoryAmmo() == ((w.getMaxClips() - 1) * w.getClipSize())) && (w.getClipAmmo() < w.getClipSize()));
						int cost = (lessThanOne ? ((w.getAmmoPrice() / w.getClipSize()) * (w.getClipSize() - w.getClipAmmo())) : w.getAmmoPrice());
						int moneyAfterPurchase = Player.getPlayer().getAttributes().getInt("money") - cost; 
						if(moneyAfterPurchase >= 0) {
							// Player has enough money. Buy the ammo.
							Player.getPlayer().getAttributes().set("money", moneyAfterPurchase);
							w.addInventoryAmmo(w.getClipSize());
							assets.getSound("buy_ammo2").play(1.0f, assets.getSoundVolume());
						}
					}
				}
			} else if(maxAmmoButton.inBounds(x, y) && selectedInInventory) {
				// Buy max ammo for the currently selected weapon.
				if(item instanceof Weapon) {
					Weapon w = (Weapon)item;
					
					if(!w.clipsMaxedOut()) {
						int cost = w.getMaxAmmoPrice();
						int moneyAfterPurchase = Player.getPlayer().getAttributes().getInt("money") - cost; 
						if(moneyAfterPurchase >= 0) {
							// Player has enough money. Buy the ammo.
							Player.getPlayer().getAttributes().set("money", moneyAfterPurchase);
							w.maxOutAmmo();
							assets.getSound("buy_ammo2").play(1.0f, assets.getSoundVolume());
						}
					}
				}
			}
		}
		
		// Check to see if we've clicked an item.
		findSelectedItem(x, y);
	}
	
	@Override
	public void mouseReleased(int button, int x, int y) {
		if(button == 0) Controls.getInstance().getMouse().setMouseDown(false);
	}
	
	@Override
	public void mouseWheelMoved(int change) {
		MouseInfo mouse = Controls.getInstance().getMouse();
		
		boolean mouseInInventory = ((mouse.getPosition().x >= INVENTORY_CONTAINER.x) && (mouse.getPosition().y >= INVENTORY_CONTAINER.y) && 
									(mouse.getPosition().x <= (INVENTORY_CONTAINER.x + CONTAINER_WIDTH)) && (mouse.getPosition().y <= INVENTORY_CONTAINER.y + CONTAINER_HEIGHT));
		boolean mouseInShop = ((mouse.getPosition().x >= SHOP_CONTAINER.x) && (mouse.getPosition().y >= SHOP_CONTAINER.y) && 
				(mouse.getPosition().x <= (SHOP_CONTAINER.x + CONTAINER_WIDTH)) && (mouse.getPosition().y <= SHOP_CONTAINER.y + CONTAINER_HEIGHT));
		
		float scrollAmount = (change > 0) ? SCROLL_SPEED : -SCROLL_SPEED;
		if(mouseInInventory) {
			if((inventoryOrigin.y + scrollAmount) <= INVENTORY_CONTAINER.y) {
				inventoryOrigin.y += scrollAmount;
			}
		} else if(mouseInShop) {
			if((shopOrigin.y + scrollAmount) <= SHOP_CONTAINER.y) {
				shopOrigin.y += scrollAmount;
			}
		}
	}
	
	@Override
	public void keyReleased(int key, char c) {
		if(key == Controls.Layout.SHOP_SCREEN.getKey()) exit = true;
	}
	
	public static void resetShop() {
		SHOP = new Inventory(SHOP_SIZE);
		
		// Add the default purchasable weapons to the shop inventory.
		SHOP.addItem(new AK47());
		SHOP.addItem(new Mossberg());
		SHOP.addItem(new BowAndArrow());
		SHOP.addItem(new Flamethrower());
		SHOP.addItem(new GrenadeLauncher());
		SHOP.addItem(new ClaymoreWeapon());
		SHOP.addItem(new LaserBarrier());
		SHOP.addItem(new SentryWeapon());
		SHOP.addItem(new BigRedButton());
	}
	
	@Override
	public void enter(GameContainer gc, StateBasedGame game) {
		Controls.getInstance().resetAll();
		exit = false;
		
		if(Player.getPlayer().getInventory().getCapacity() != inventorySize) {
			// Inventory size has changed. Re-build inventory layout.
			inventorySize = Player.getPlayer().getInventory().getCapacity();
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