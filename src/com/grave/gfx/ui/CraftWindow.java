package com.grave.gfx.ui;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.state.BasicGameState;

import com.grave.AssetManager;
import com.grave.Controls;
import com.grave.entities.Entity;
import com.grave.gfx.Layers;
import com.grave.misc.MouseInfo;
import com.grave.misc.Pair;
import com.grave.objects.crafting.Recipe;
import com.grave.objects.crafting.Resources;
import com.grave.objects.items.Item;
import com.grave.objects.weapons.WType;
import com.grave.objects.weapons.Weapon;
import com.grave.states.GameState;

public class CraftWindow implements Entity {
	public static final float WIDTH = 784;
	public static final float HEIGHT = 200;

	private static final Color ACTIVE_COLOR = new Color(0x278C38);

	private static final float ICON_SIZE = 64.0f;

	private Pair<Float> position;
	public Pair<Float> getPosition() { return position; }
	public void setPosition(float x, float y) {
		position.x = x;
		position.y = y;
	}

	private Recipe recipe;
	private TooltipText tooltip;
	private MenuButton craft;

	public CraftWindow(Pair<Float> position_, Recipe recipe_) {
		this.position = position_;
		this.recipe = recipe_;

		this.tooltip = new TooltipText(AssetManager.getManager().getFont("PressStart2P-Regular_small"),
									   recipe.getResult().getName(), recipe.getResult().getDescription(),
									   new Pair<Float>((position.x + (ICON_SIZE / 2) + 10.0f), (position.y + (ICON_SIZE / 2) + 10.0f)), ICON_SIZE);

		UnicodeFont large = AssetManager.getManager().getFont("PressStart2P-Regular_large");
		float w = large.getWidth("Craft");
		craft = new MenuButton(new Pair<Float>(((position.x + WIDTH) - (w + 15.0f)), ((position.y + HEIGHT) - (large.getLineHeight() + 15.0f))), "Craft");
	}

	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
		MouseInfo mouse = Controls.getInstance().getMouse();
		if(craft.inBounds(mouse.getPosition().x, mouse.getPosition().y)) {
			craft.mouseEnter();
		} else craft.mouseExit();
	}

	@Override
	public void render(GameState gs, Graphics g, long cTime) {
		g.setColor(Color.lightGray);
		g.fillRect(position.x, position.y, WIDTH, HEIGHT);
		g.setColor(Color.white);
		g.drawRect(position.x, position.y, WIDTH, HEIGHT);

		drawResources(g, cTime);
		drawWeaponCosts(g, cTime);
		drawIcon(gs, g, cTime);

		boolean haveIngredients = recipe.hasIngredients();
		g.setColor(haveIngredients ? ACTIVE_COLOR : Color.gray);
		UnicodeFont large = AssetManager.getManager().getFont("PressStart2P-Regular_large");
		float craftWidth = craft.getWidth();
		g.fillRect((position.x + WIDTH - craftWidth - 20.0f), ((position.y + HEIGHT) - (large.getLineHeight() + 20.0f)), (craftWidth + 10.0f), (large.getLineHeight() + 10.0f));
		craft.render(gs, g, cTime);
		g.setColor(Color.white);
		g.drawRect((position.x + WIDTH - craftWidth - 20.0f), ((position.y + HEIGHT) - (large.getLineHeight() + 20.0f)), (craftWidth + 10.0f), (large.getLineHeight() + 10.0f));
	}

	private void drawIcon(GameState gs, Graphics g, long cTime) {
		g.setColor(Color.black);
		g.fillRect((position.x + 10.0f), (position.y + 10.0f), ICON_SIZE, ICON_SIZE);
		g.setColor(Color.white);
		g.drawRect((position.x + 10.0f), (position.y + 10.0f), ICON_SIZE, ICON_SIZE);

		Entity result = recipe.getResult();
		UnicodeFont regular = AssetManager.getManager().getFont("PressStart2P-Regular");
		if(result instanceof Item) {
			Item item = (Item) result;
			Image icon = item.getIcon();

			float scale = ICON_SIZE / icon.getWidth();
			icon.draw((position.x + 10.0f), (position.y + 10.0f), scale);
		} else if(result instanceof Weapon) {
			Weapon weapon = (Weapon) result;
			Image icon = weapon.getInventoryIcon();

			float scale = ICON_SIZE / icon.getWidth();
			icon.draw((position.x + 10.0f), (position.y + 10.0f), scale);
		}

		g.setColor(Color.white);
		g.setFont(regular);
		g.drawString(result.getName(), (position.x + ICON_SIZE + 30.0f), (position.y + ((ICON_SIZE / 2) - (regular.getLineHeight() / 2)) + 10.0f));

		tooltip.render(gs, g, cTime);
	}

	private void drawResources(Graphics g, long cTime) {
		float y = (position.y + ICON_SIZE + 20.0f);

		UnicodeFont small = AssetManager.getManager().getFont("PressStart2P-Regular_small");

		int [] resources = recipe.getResources().getAll();
		float x = (position.x + 10.0f);

		g.setFont(small);
		for(int i = 0; i < resources.length; i++) {
			if(resources[i] > 0) {
				g.setColor(Color.black);
				g.fillRect(x, y, 32.0f, 32.0f);
				g.setColor(Color.white);
				g.drawRect(x, y, 32.0f, 32.0f);

				Image icon = AssetManager.getManager().getImage(Resources.getIconName(i));
				float scale = (32.0f / icon.getWidth());
				icon.draw(x, y, scale);

				g.drawString(Integer.toString(resources[i]), (x + 42.0f), (y + (16.0f - (small.getLineHeight() / 2))));

				x += (small.getWidth(Integer.toString(resources[i])) + 52.0f);
			}
		}
	}

	private void drawWeaponCosts(Graphics g, long cTime) {
		float x = (position.x + 10.0f);
		float y = (position.y + ICON_SIZE + 62.0f);

		WType [] weapons = recipe.getWeapons();
		for(int i = 0; i < weapons.length; i++) {
			WType w = weapons[i];
			Image img = w.getImage();
			float scale = 32.0f / img.getWidth();

			g.setColor(Color.black);
			g.fillRect((x + (i * 42.0f)), y, 32.0f, 32.0f);
			img.draw((x + (i * 42.0f)), y, scale);
			g.setColor(Color.white);
			g.drawRect((x + (i * 42.0f)), y, 32.0f, 32.0f);
		}
	}

	public boolean click(float x, float y) {
		if(craft.inBounds(x, y) && recipe.hasIngredients()) {
			recipe.craft();
			return true;
		}

		return false;
	}

	public boolean inBounds(float x, float y) {
		return ((x >= position.x) && (x <= (position.x + WIDTH)) &&
				(y >= position.y) && (y <= (position.y + HEIGHT)));
	}

	@Override
	public String getName() { return "Craft Window"; }

	@Override
	public String getTag() { return "craftWindow"; }

	@Override
	public String getDescription() { return "Craft Window"; }

	@Override
	public int getLayer() { return Layers.NONE.val(); }
}
