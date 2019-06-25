package com.gzsr.world;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.newdawn.slick.Graphics;

import com.gzsr.entities.Entity;
import com.gzsr.states.GameState;
import com.gzsr.tmx.TMap;

public class Level {
	private TMap map;
	public TMap getMap() { return map; }

	private ConcurrentHashMap<String, List<Entity>> entities;
	public List<Entity> getEntitiesByTag(String tag) { return entities.get(tag); }
	public void addEntity(String tag, Entity entity) {
		List<Entity> tagged = entities.get(tag);
		if(tagged == null) tagged = new ArrayList<Entity>();
		tagged.add(entity);
	}

	public Level(TMap map_) {
		this.map = map_;
		this.entities = new ConcurrentHashMap<String, List<Entity>>();
	}

	public void update(GameState gs, long cTime, int delta) {

	}

	public void render(Graphics g, long cTime) {

	}
}
