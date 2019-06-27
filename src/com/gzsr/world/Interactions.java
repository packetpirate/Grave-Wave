package com.gzsr.world;

import com.gzsr.Globals;
import com.gzsr.misc.Pair;
import com.gzsr.objects.crafting.Resources;
import com.gzsr.objects.items.ResourceDrop;
import com.gzsr.objects.weapons.Explosion;
import com.gzsr.states.GameState;

public enum Interactions {
	NONE((gs, pos, cTime) -> {}),

	ONE_RANDOM_RESOURCE((gs, pos, cTime) -> {
		// Create a random resource item and spawn it.
		int resource = Globals.rand.nextInt(6);
		ResourceDrop drop = new ResourceDrop(Resources.getIconName(resource),
											 Resources.getName(resource),
											 resource, 1, pos, cTime);
		gs.getLevel().addEntity("resource", drop);
	}),

	RANDOM_RESOURCES((gs, pos, cTime) -> {
		// Spawn several random resource drops.
		for(int i = 0; i < (Globals.rand.nextInt(5) + 1); i++) {
			int resource = Globals.rand.nextInt(6);
			ResourceDrop drop = new ResourceDrop(Resources.getIconName(resource),
					 							 Resources.getName(resource),
					 							 resource, 1, pos, cTime);
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
