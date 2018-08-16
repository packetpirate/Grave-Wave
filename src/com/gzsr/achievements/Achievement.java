package com.gzsr.achievements;

import org.newdawn.slick.Image;

import com.gzsr.AssetManager;
import com.gzsr.controllers.AchievementController;
import com.gzsr.states.GameState;

public class Achievement implements IAchievement {
	protected String name;
	protected String description;
	protected String icon;
	
	public Achievement(String name_, String description_, String icon_) {
		this.name = name_;
		this.description = description_;
		this.icon = icon_;
	}

	@Override
	public void update(AchievementController controller, GameState gs, long cTime) {
		// To be overridden.
	}

	@Override
	public boolean isEarned() {
		return false;
	}

	@Override
	public boolean isSecret() {
		return false;
	}

	@Override
	public void onComplete(AchievementController controller, long cTime) {
		// To be overridden.
	}

	@Override
	public String getName() { return name; }

	@Override
	public String getDescription() { return description; }

	@Override
	public Image getIcon() { return AssetManager.getManager().getImage(icon); }
}
