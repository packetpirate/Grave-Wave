package com.grave.world;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.BasicGameState;

import com.grave.entities.Entity;
import com.grave.gfx.Layers;
import com.grave.misc.Pair;
import com.grave.states.GameState;

public class GameObject implements Entity {
	public enum Type {
		// TODO: Add to this as more game objects are created.
		NONE(Interactions.NONE),
		TRASH_CAN(Interactions.ONE_RANDOM_RESOURCE),
		FIRE_HYDRANT(Interactions.NONE),
		DEAD_BODY(Interactions.RANDOM_RESOURCES),
		EXPLOSIVE_BARREL(Interactions.NONE),
		VENDING_MACHINE(Interactions.SKILL_POINT);

		private Interactions interaction;
		public Interactions getInteraction() { return interaction; }

		Type(Interactions interaction_) {
			this.interaction = interaction_;
		}
	}

	protected Type type;
	public Interactions getInteraction() { return type.getInteraction(); }

	protected Pair<Float> position;
	public Pair<Float> getPosition() { return position; }

	protected boolean used;
	public boolean isUsed() { return used; }
	public void use(GameState gs, Pair<Float> objPos, long cTime) {
		used = true;
		getInteraction().execute(gs, objPos, cTime);
	}
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
	public void render(GameState gs, Graphics g, long cTime) {
		// Not needed.
	}

	public static Type getTypeByTID(int tid) {
		// TODO: Add to this as more game objects are created.
		switch(tid) {
			case 37: return Type.FIRE_HYDRANT;
			case 38: return Type.TRASH_CAN;
			case 39: return Type.EXPLOSIVE_BARREL;
			case 40: return Type.VENDING_MACHINE;
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
