package com.grave.world;

import com.grave.Globals;
import com.grave.misc.Pair;
import com.grave.objects.crafting.Resources;
import com.grave.objects.items.ResourceDrop;
import com.grave.objects.weapons.Explosion;
import com.grave.states.GameState;

public enum Interactions {
	NONE((gs, pos, cTime) -> {}),

	ONE_RANDOM_RESOURCE((gs, pos, cTime) -> {
		// Create a random resource item and spawn it.
		int resource = Globals.rand.nextInt(6);

		ResourceDrop drop = new ResourceDrop(Resources.getIconName(resource),
											 Resources.getName(resource),
											 resource, 1, new Pair<Float>(pos), cTime);

		gs.getLevel().addEntity("resource", drop);
	}),

	RANDOM_RESOURCES((gs, pos, cTime) -> {
		// Spawn several random resource drops.
		for(int i = 0; i < (Globals.rand.nextInt(5) + 1); i++) {
			int resource = Globals.rand.nextInt(6);
			Pair<Float> spawn = new Pair<Float>(pos.x, pos.y);
			pos.x += ((Globals.rand.nextFloat() * 16.0f) * (Globals.rand.nextBoolean() ? 1 : -1));
			pos.y += ((Globals.rand.nextFloat() * 16.0f) * (Globals.rand.nextBoolean() ? 1 : -1));

			ResourceDrop drop = new ResourceDrop(Resources.getIconName(resource),
					 							 Resources.getName(resource),
					 							 resource, 1, spawn, cTime);

			gs.getLevel().addEntity("resource", drop);
		}
	}),

	EXPLOSION((gs, pos, cTime) -> {
		// Spawn an explosion at this position.
		double dmg = (Globals.rand.nextInt(50) + 51);

		Explosion exp = new Explosion(Explosion.Type.NORMAL, "GZS_Explosion",
				  					  new Pair<Float>(pos), dmg,
				  					  false, 10.0f, 128.0f, cTime);

		gs.getLevel().addEntity("explosion", exp);
	});

	private Interaction action;

	Interactions(Interaction action_) {
		this.action = action_;
	}

	public void execute(GameState gs, Pair<Float> pos, long cTime) {
		action.execute(gs, pos, cTime);
	}
}
