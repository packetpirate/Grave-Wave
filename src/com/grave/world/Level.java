package com.grave.world;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;

import com.grave.Globals;
import com.grave.entities.Entity;
import com.grave.entities.Player;
import com.grave.entities.enemies.EnemyController;
import com.grave.gfx.effects.VisualEffect;
import com.grave.gfx.particles.Emitter;
import com.grave.gfx.particles.Particle;
import com.grave.math.Calculate;
import com.grave.misc.Pair;
import com.grave.objects.items.Item;
import com.grave.objects.weapons.Explosion;
import com.grave.states.GameState;
import com.grave.tmx.TLayer;
import com.grave.tmx.TMap;
import com.grave.tmx.TParser;
import com.grave.tmx.TTile;

public class Level {
	private TMap map;
	public TMap getMap() { return map; }

	private ConcurrentHashMap<String, List<Entity>> entities;
	public List<Entity> getEntitiesByTag(String tag) { return entities.get(tag); }
	public void addEntity(String tag, Entity entity) {
		List<Entity> tagged = entities.get(tag);
		if(tagged == null) {
			tagged = new ArrayList<Entity>();
			tagged.add(entity);
			entities.put(tag, tagged);
		} else tagged.add(entity);
	}

	public Level(String mapName_) {
		this.entities = new ConcurrentHashMap<String, List<Entity>>();

		this.map = TParser.load(mapName_);
		this.map.constructMap();
		this.map.placeObjects(this);
	}

	public void update(GameState gs, long cTime, int delta) {
		Player player = Player.getPlayer();

		Iterator<Entry<String, List<Entity>>> it = entities.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry<String, List<Entity>> pair = it.next();
			List<Entity> ents = pair.getValue();

			Iterator<Entity> eit = ents.iterator();
			while(eit.hasNext()) {
				Entity ent = eit.next();

				ent.update(gs, cTime, Globals.STEP_TIME);
				if(ent instanceof EnemyController) {
					EnemyController ec = (EnemyController)ent;
					ec.updateEnemies(gs, player, cTime, Globals.STEP_TIME);
				} else if(ent instanceof Item) {
					Item item = (Item) ent;
					if(item.isActive(cTime)) {
						player.checkItem(item, cTime);
					} else eit.remove();
				} else if(ent instanceof VisualEffect) {
					VisualEffect visual = (VisualEffect) ent;
					if(!visual.isActive(cTime)) eit.remove();
				} else if(ent instanceof Particle) {
					Particle p = (Particle) ent;
					if(!p.isActive(cTime)) {
						p.onDestroy(gs, cTime);
						eit.remove();
					}
				} else if(ent instanceof Emitter) {
					Emitter e = (Emitter) ent;
					if(!e.isAlive(cTime)) eit.remove();
				} else if(ent instanceof Explosion) {
					Explosion exp = (Explosion) ent;
					if(!exp.isActive(cTime)) eit.remove();
				}
			}
		}
	}

	public void render(Graphics g, long cTime) {
		map.render(g, cTime);

		List<Entity> sorted = new ArrayList<Entity>();
		entities.values().stream().forEach(ents -> sorted.addAll(ents));
		sorted.stream().sorted(Entity.COMPARE).forEach(ent -> ent.render(g, cTime));
	}

	public void interact(GameState gs, long cTime) {
		Player player = Player.getPlayer();
		List<Entity> objects = getEntitiesByTag("object");
		if((objects != null) && !objects.isEmpty()) {
			for(int i = 0; i < objects.size(); i++) {
				GameObject obj = ((GameObject) objects.get(i));

				if(!obj.isUsed()) {
					float dist = Calculate.Distance(player.getPosition(), obj.getPosition());

					if(dist <= Player.INTERACT_DIST) {
						obj.use();
						obj.getInteraction().execute(gs, obj.getPosition(), cTime);
						break; // Only allow interaction with one object per button press.
					}
				}
			}
		}
	}

	public boolean obstaclePresent(Pair<Float> pos, Pair<Float> size) {
		Shape collider = new Rectangle((pos.x - (size.x / 2)), (pos.y - (size.y / 2)), size.x, size.y);
		boolean collision = false;

		List<TLayer> layers = map.getLayers();
		for(int i = 0; i < layers.size(); i++) {
			TLayer layer = map.getLayer(i);

			if(layer != null) {
				TTile [][] tiles = layer.getTiles();
				for(int y = 0; y < map.getMapHeight(); y++) {
					for(int x = 0; x < map.getMapWidth(); x++) {
						TTile tile = tiles[y][x];
						if(!tile.isWalkable() && (tile.getCollider().intersects(collider) || tile.getCollider().contains(collider))) collision = true;
					}
				}
			}
		}

		return collision;
	}

	public void reset() {
		// Don't remove game objects. Reset them instead.
		Iterator<Entry<String, List<Entity>>> it = entities.entrySet().iterator();
		while(it.hasNext()) {
			Entry<String, List<Entity>> ent = it.next();
			if(!ent.getKey().equals("object")) it.remove();
			else {
				List<Entity> objects = ent.getValue();
				for(int i = 0; i < objects.size(); i++) {
					GameObject gObj = (GameObject) objects.get(i);
					gObj.reset();
				}
			}
		}
	}
}
