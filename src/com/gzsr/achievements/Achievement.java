package com.gzsr.achievements;

import org.newdawn.slick.Image;

import com.gzsr.AssetManager;
import com.gzsr.controllers.AchievementController;
import com.gzsr.states.GameState;

public class Achievement implements IAchievement {
	protected String name;
	protected String description;
	protected String icon;
	
	// Short-circuits isEarned method if already complete so no complicated checks need to be performed.
	protected boolean hidden;
	protected boolean complete;
	
	public Achievement(String name_, String description_, String icon_) {
		this(name_, description_, icon_, false);
	}
	
	public Achievement(String name_, String description_, String icon_, boolean hidden_) {
		this.name = name_;
		this.description = description_;
		this.icon = icon_;
		
		this.hidden = hidden_;
		this.complete = false;
	}

	@Override
	public void update(AchievementController controller, GameState gs, long cTime) {
		// To be overridden.
	}

	@Override
	public boolean isEarned() { return complete; }

	@Override
	public boolean isSecret() { return hidden; }

	@Override
	public void onComplete(AchievementController controller, long cTime) {
		controller.broadcast(this, cTime);
		complete = true;
	}

	@Override
	public String getName() { return name; }

	@Override
	public String getDescription() { return description; }

	@Override
	public Image getIcon() { return AssetManager.getManager().getImage(icon); }
}
