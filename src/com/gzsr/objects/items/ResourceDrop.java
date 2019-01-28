package com.gzsr.objects.items;

import com.gzsr.AssetManager;
import com.gzsr.entities.Player;
import com.gzsr.gfx.ui.StatusMessages;
import com.gzsr.misc.Pair;
import com.gzsr.objects.crafting.Resources;

public class ResourceDrop extends Item {
	private static final long DURATION = 10_000L;

	private String name;
	private int resource;
	private int amount;

	public ResourceDrop(String iconName_, String name_, int resource_, int amount_, Pair<Float> pos, long cTime) {
		super(pos, cTime);

		this.iconName = iconName_;

		this.duration = ResourceDrop.DURATION;
		this.pickup = AssetManager.getManager().getSound("powerup2");

		this.name = name_;
		this.resource = resource_;
		this.amount = amount_;
	}

	@Override
	public void apply(Player player, long cTime) {
		Resources resources = player.getResources();
		resources.add(resource, amount);

		duration = 0L;
		pickup.play(1.0f, AssetManager.getManager().getSoundVolume());

		String message = String.format("+%d %s!", amount, name);
		StatusMessages.getInstance().addMessage(message, player, Player.ABOVE_1, cTime, 2_000L);
	}

	@Override
	public String getName() { return "Resource Drop"; }

	@Override
	public String getDescription() { return "A resource used for crafting."; }

	@Override
	public int getCost() { return 0; }
}
