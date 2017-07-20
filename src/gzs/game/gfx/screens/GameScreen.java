package gzs.game.gfx.screens;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import gzs.entities.Entity;
import gzs.entities.Player;
import gzs.game.gfx.Drawable;
import gzs.game.gfx.Screen;
import gzs.game.info.Globals;
import gzs.game.misc.MouseInfo;
import gzs.game.misc.Pair;
import gzs.game.utils.FileUtilities;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class GameScreen implements Screen {
	private Map<String, Entity> entities;
	
	public GameScreen() {
		entities = new HashMap<String, Entity>();
		try {
			entities.put("player", new Player());
			entities.put("crosshairs", new Drawable(FileUtilities.LoadImage("GZS_Crosshair.png"),
													new Pair<Double>(0.0, 0.0)) {
				@Override
				public void update(long cTime) {
					Pair<Double> m = Globals.mouse.getPosition();
					setPosition(m.x, m.y);
				}
			});
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void update(long cT) {
		// Update all entities.
		Iterator<Entry<String, Entity>> it = entities.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry<String, Entity> pair = (Map.Entry<String, Entity>) it.next();
			pair.getValue().update(cT);
		}
	}

	@Override
	public void render(GraphicsContext gc, long cT) {
		{ // Render all entities.
			Iterator<Entry<String, Entity>> it = entities.entrySet().iterator();
			while(it.hasNext()) {
				Map.Entry<String, Entity> pair = (Map.Entry<String, Entity>) it.next();
				pair.getValue().render(gc, cT);
			}
			
			Player player = (Player) entities.get("player");
			int clip = player.getCurrentWeapon().getClipAmmo();
			int inventory = player.getCurrentWeapon().getInventoryAmmo();
			gc.setFill(Color.BLACK);
			gc.fillText(String.format("Ammo: %d / %d", clip, inventory), 50, 20);
		}
	}

	@Override
	public void dispatchClick(MouseInfo mouse) {
		
	}

	@Override
	public boolean hidesCursor() {
		return entities.containsKey("crosshairs");
	}
}
