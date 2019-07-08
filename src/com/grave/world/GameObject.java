package com.grave.world;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.BasicGameState;

import com.grave.entities.Entity;
import com.grave.gfx.Layers;
import com.grave.misc.Pair;

public class GameObject implements Entity {
	public enum Type {
		// TODO: Add to this as more game objects are created.
		NONE(Interactions.NONE),
		TRASH_CAN(Interactions.ONE_RANDOM_RESOURCE),
		FIRE_HYDRANT(Interactions.NONE),
		DEAD_BODY(Interactions.RANDOM_RESOURCES);

		private Interactions interaction;
		public Interactions getInteraction() { return interaction; }

		Type(Interactions interaction_) {
			this.interaction = interaction_;
		}
	}

	private Type type;
	public Interactions getInteraction() { return type.getInteraction(); }

	private Pair<Float> position;
	public Pair<Float> getPosition() { return position; }

	private boolean used;
	public boolean isUsed() { return used; }
	public void use() { used = true; }
	public void reset() { used = false; }

	public GameObject(Type type_, Pair<Float> position_) {
		this.type = type_;
		this.position = position_;

		this.used = false;
	}

	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
		// Not needed.
	}

	@Override
	public void render(Graphics g, long cTime) {
		// Not needed.
	}

	public static Type getTypeByTID(int tid) {
		// TODO: Add to this as more game objects are created.
		switch(tid) {
			case 37: return Type.FIRE_HYDRANT;
			case 38: return Type.TRASH_CAN;
			case 46: return Type.DEAD_BODY;
			default: return Type.NONE;
		}
	}

	@Override
	public String getName() {
		return "Game Object";
	}

	@Override
	public String getTag() {
		return "object";
	}

	@Override
	public String getDescription() {
		return "Game Object";
	}

	@Override
	public int getLayer() {
		return Layers.NONE.val();
	}
}
