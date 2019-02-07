package com.gzsr.gfx.ui;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.state.BasicGameState;

import com.gzsr.AssetManager;
import com.gzsr.entities.Entity;
import com.gzsr.gfx.Layers;
import com.gzsr.misc.Pair;
import com.gzsr.objects.crafting.Recipe;
import com.gzsr.objects.crafting.Resources;
import com.gzsr.objects.items.Item;
import com.gzsr.objects.weapons.Weapon;

public class CraftWindow implements Entity {
	public static final float WIDTH = 784;
	public static final float HEIGHT = 200;

	private static final float ICON_SIZE = 64.0f;

	private Pair<Float> position;
	public Pair<Float> getPosition() { return position; }
	public void setPosition(float x, float y) {
		position.x = x;
		position.y = y;
	}

	private Recipe recipe;
	private TooltipText tooltip;

	public CraftWindow(Pair<Float> position_, Recipe recipe_) {
		this.position = position_;
		this.recipe = recipe_;

		this.tooltip = new TooltipText(AssetManager.getManager().getFont("PressStart2P-Regular_small"),
									   recipe.getResult().getName(), recipe.getResult().getDescription(),
									   new Pair<Float>((position.x + (ICON_SIZE / 2) + 10.0f), (position.y + (ICON_SIZE / 2) + 10.0f)), ICON_SIZE);
	}

	@Override
	public void update(BasicGameState gs, long cTime, int delta) {

	}

	@Override
	public void render(Graphics g, long cTime) {
		g.setColor(Color.lightGray);
		g.fillRect(position.x, position.y, WIDTH, HEIGHT);
		g.setColor(Color.white);
		g.drawRect(position.x, position.y, WIDTH, HEIGHT);

		drawIcon(g, cTime);
		drawResources(g, cTime);
		drawWeaponCosts(g, cTime);
	}

	private void drawIcon(Graphics g, long cTime) {
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

		tooltip.render(g, cTime);
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

		// TODO: Come back to this once you figure out how to get the icon by name and have recipes using weapons as ingredients.
		// TODO: Maybe instead of storing string names of weapons as costs, create an enum with all weapons and keep meta-info (name, description, image name)
		// in this enumerated value, then store enum values as the recipe requirements.
	}

	@Override
	public String getName() { return "Craft Window"; }

	@Override
	public String getDescription() { return "Craft Window"; }

	@Override
	public int getLayer() { return Layers.NONE.val(); }
}
