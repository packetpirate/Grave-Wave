package com.grave.entities;

import java.util.Comparator;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.BasicGameState;

import com.grave.states.GameState;

public interface Entity {
	public abstract String getName();
	public abstract String getTag();
	public abstract String getDescription();
	public abstract int getLayer();
	public abstract void update(BasicGameState gs, long cTime, int delta);
	public abstract void render(GameState gs, Graphics g, long cTime);

	/**
	 * Compares entities according to their rendering layer.
	 * Entities placed on a lower-numbered layer will be rendered first.
	 * See the Layers enum for rendering order.
	 */
	public static final Comparator<Entity> COMPARE = new Comparator<Entity>() {
		@Override
		public int compare(Entity o1, Entity o2) {
			int left = o1.getLayer();
			int right = o2.getLayer();

			if(left == right) return 0;
			else return ((left < right) ? -1 : 1);
		}
	};
}
