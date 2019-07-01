package com.grave.world;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.BasicGameState;

import com.grave.entities.Entity;
import com.grave.gfx.Layers;

public class GameObject implements Entity {
	public enum Type {
		// TODO: Add to this as more game objects are created.
		NONE(Interactions.NONE),
		TRASH_CAN(Interactions.ONE_RANDOM_RESOURCE),
		FIRE_HYDRANT(Interactions.NONE);

		private Interactions interaction;
		public Interactions getInteraction() { return interaction; }

		Type(Interactions interaction_) {
			this.interaction = interaction_;
		}
	}

	private Type type;
	public Interactions getInteraction() { return type.getInteraction(); }

	public GameObject(Type type_) {
		this.type = type_;
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
