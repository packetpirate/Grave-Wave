package com.grave.status;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.newdawn.slick.Graphics;

import com.grave.Globals;
import com.grave.entities.Entity;
import com.grave.entities.Player;
import com.grave.gfx.ui.StatusMessages;
import com.grave.states.GameState;
import com.grave.talents.Talents;

public class StatusHandler {
	// TODO: Add any new harmful status effects to this list.
	private static final List<Status> HARMFUL = new ArrayList<Status>() {{
		add(Status.DEAFENED);
		add(Status.FLASHBANG);
		add(Status.POISON);
		add(Status.ACID);
		add(Status.BURNING);
		add(Status.PARALYSIS);
		add(Status.DAMAGE);
	}};
	
	private Entity entity;
	
	private List<StatusEffect> statusEffects;
	public List<StatusEffect> getStatusEffects() { return statusEffects; }
	public void clearAll() { statusEffects.clear(); }
	
	private List<Status> immunities;
	public boolean isImmuneTo(Status st) { return immunities.contains(st); }
	public void addImmunity(Status st) { immunities.add(st); }
	
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
		if(entity instanceof Player) {
			boolean isResistable = (effect.getStatus().equals(Status.POISON) ||
									effect.getStatus().equals(Status.PARALYSIS));
			boolean canResist = Talents.Fortification.VIGOR.active();
			if(isResistable && canResist) {
				int ranks = Talents.Fortification.VIGOR.ranks();
				float roll = Globals.rand.nextFloat();
				if(roll <= (ranks * 0.05f)) {
					StatusMessages.getInstance().addMessage("Resisted!", entity, Player.ABOVE_1, cTime, 1_000L);
					return;
				}
			}
		}
		
		if(isImmuneTo(effect.getStatus())) {
			effect.noEffect(entity, cTime);
			return;
		}
		
		for(StatusEffect se : statusEffects) {
			Status s = se.getStatus();
			if(s.equals(effect.getStatus())) {
				se.refresh(cTime);
				return;
			}
		}
		
		effect.onApply(entity, effect.getCreateTime());
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
		immunities = new ArrayList<Status>();
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
