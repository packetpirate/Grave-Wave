package com.grave.world;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.newdawn.slick.Graphics;

import com.grave.Globals;
import com.grave.entities.Entity;
import com.grave.entities.Player;
import com.grave.entities.enemies.EnemyController;
import com.grave.gfx.effects.VisualEffect;
import com.grave.gfx.particles.Emitter;
import com.grave.gfx.particles.Particle;
import com.grave.objects.items.Item;
import com.grave.objects.weapons.Explosion;
import com.grave.states.GameState;
import com.grave.tmx.TMap;
import com.grave.tmx.TParser;

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
		this.map = TParser.load(mapName_);
		this.map.constructMap();
		this.map.placeObjects(this);

		this.entities = new ConcurrentHashMap<String, List<Entity>>();
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

	public void reset() {
		entities.clear();
	}
}
