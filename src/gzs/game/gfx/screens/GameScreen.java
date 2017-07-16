package gzs.game.gfx.screens;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import gzs.entities.Entity;
import gzs.entities.Player;
import gzs.game.gfx.Screen;
import gzs.game.misc.MouseInfo;
import javafx.scene.canvas.GraphicsContext;

public class GameScreen implements Screen {
	private Map<String, Entity> entities;
	
	public GameScreen() {
		entities = new HashMap<String, Entity>();
		entities.put("player", new Player());
	}

	@Override
	public void update(long cT, MouseInfo mouse) {
		// Update all entities.
		Iterator<Entry<String, Entity>> it = entities.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry<String, Entity> pair = (Map.Entry<String, Entity>) it.next();
			pair.getValue().update(cT);
		}
	}

	@Override
	public void render(GraphicsContext gc, long cT) {
		// Render all entities.
		Iterator<Entry<String, Entity>> it = entities.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry<String, Entity> pair = (Map.Entry<String, Entity>) it.next();
			pair.getValue().render(gc, cT);
		}
	}

	@Override
	public void dispatchClick(MouseInfo mouse) {
		
	}
}
