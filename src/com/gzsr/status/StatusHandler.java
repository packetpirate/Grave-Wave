package com.gzsr.status;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.newdawn.slick.Graphics;

import com.gzsr.entities.Entity;
import com.gzsr.states.GameState;

public class StatusHandler {
	// TODO: Add any new harmful status effects to this list.
	private static final List<Status> HARMFUL = new ArrayList<Status>() {{
		add(Status.DEAFENED);
		add(Status.FLASHBANG);
		add(Status.POISON);
		add(Status.BURNING);
		add(Status.PARALYSIS);
		add(Status.DAMAGE);
	}};
	
	private Entity entity;
	
	private List<StatusEffect> statusEffects;
	public List<StatusEffect> getStatusEffects() { return statusEffects; }
	public void clearAll() { statusEffects.clear(); }
	
	public void destroyAll(long cTime) {
		statusEffects.stream().forEach(status -> status.onDestroy(entity, cTime));
		statusEffects.clear();
	}
	
	public StatusEffect getStatus(Status status) { 
		for(StatusEffect se : statusEffects) {
			if(se.getStatus().equals(status)) return se;
		}
		
		return null;
	}
	
	public boolean hasStatus(Status status) {
		for(StatusEffect se : statusEffects) {
			if(se.getStatus().equals(status)) return true;
		}
		
		return false;
	}
	
	public void addStatus(StatusEffect effect, long cTime) {
		// First check to see if the player already has this status.
		for(StatusEffect se : statusEffects) {
			Status s = se.getStatus();
			if(s.equals(effect.getStatus())) {
				// Refresh the effect rather than adding it to the list.
				se.refresh(cTime);
				return;
			}
		}
		
		// The player does not have this effect. Add it.
		effect.onApply(entity, cTime);
		statusEffects.add(effect);
	}
	
	public void clearHarmful() {
		Iterator<StatusEffect> it = statusEffects.iterator();
		while(it.hasNext()) {
			Status s = it.next().getStatus();
			if(HARMFUL.contains(s)) it.remove();
		}
	}
	
	public StatusHandler(Entity entity_) {
		entity = entity_;
		statusEffects = new ArrayList<StatusEffect>();
	}
	
	public void update(GameState gs, long cTime, int delta) {
		Iterator<StatusEffect> it = statusEffects.iterator();
		while(it.hasNext()) {
			StatusEffect status = it.next();
			if(status.isActive(cTime)) {
				status.update(entity, gs, cTime, delta);
			} else {
				status.onDestroy(entity, cTime);
				it.remove();
			}
		}
	}
	
	public void render(Graphics g, long cTime) {
		if(!statusEffects.isEmpty()) statusEffects.stream().filter(status -> status.isActive(cTime)).forEach(status -> status.render(g, cTime));
	}
}
