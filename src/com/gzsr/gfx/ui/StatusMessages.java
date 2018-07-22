package com.gzsr.gfx.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.BasicGameState;

import com.gzsr.entities.Entity;
import com.gzsr.gfx.Layers;
import com.gzsr.misc.Pair;

public class StatusMessages implements Entity {
	private static StatusMessages instance;
	public static StatusMessages getInstance() {
		if(instance == null) instance = new StatusMessages();
		return instance;
	}
	
	private List<VanishingText> messages;
	public List<VanishingText> getMessages() { return messages; }
	public void clear() { messages.clear(); }
	
	private StatusMessages() {
		messages = new ArrayList<VanishingText>();
	}
	
	public void addMessage(String message, Pair<Float> position, long cTime, long duration) {
		VanishingText vt = new VanishingText(message, "PressStart2P-Regular_small",
											 position, Color.white, cTime, duration);
		messages.add(vt);
	}
	
	public void addMessage(String message, Entity tether, Pair<Float> offset, long cTime, long duration) {
		clearOtherByEntity(tether, offset);
		VanishingText vt = new VanishingText(message, "PressStart2P-Regular_small", 
											 tether, offset, Color.white, cTime, duration);
		messages.add(vt);
	}
	
	public void addMessage(VanishingText vt) {
		if(vt.getTether() != null) clearOtherByEntity(vt.getTether(), vt.getOffset());
		messages.add(vt);
	}

	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
		Iterator<VanishingText> it = messages.iterator();
		while(it.hasNext()) {
			VanishingText vt = it.next();
			vt.update(gs, cTime, delta);
			if(!vt.isActive()) it.remove();
		}
	}

	@Override
	public void render(Graphics g, long cTime) {
		messages.stream().forEach(message -> { 
			if(message.isActive()) message.render(g, cTime); 
		});
	}
	
	/**
	 * Clears other vanishing texts tether to this entity. Does not clear damage text.
	 * @param tether The entity to clear vanishing texts from.
	 */
	private void clearOtherByEntity(Entity tether, Pair<Float> offset) {
		Iterator<VanishingText> it = messages.iterator();
		while(it.hasNext()) {
			VanishingText vt = it.next();
			boolean samePosition = (vt.getOffset() != null) && vt.getOffset().equals(offset);
			if(!(vt instanceof DamageText) && (vt.getTether() == tether) && samePosition) it.remove();
		}
	}
	
	@Override
	public String getName() {
		return "Status Text";
	}

	@Override
	public String getDescription() {
		return "Status Text";
	}

	@Override
	public int getLayer() {
		return Layers.TEXT.val();
	}
}
