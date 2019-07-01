package com.grave.achievements;

import org.newdawn.slick.Image;

import com.grave.AchievementManager;
import com.grave.AssetManager;
import com.grave.controllers.AchievementController;
import com.grave.states.GameState;

public abstract class Achievement implements IAchievement {
	protected int id;
	protected String name;
	protected String description;
	protected String icon;
	
	// Short-circuits isEarned method if already complete so no complicated checks need to be performed.
	protected boolean resetting;
	protected boolean hidden;
	protected boolean complete;
	
	public Achievement(int id_, String name_, String description_, String icon_) {
		this(id_, name_, description_, icon_, false);
	}
	
	public Achievement(int id_, String name_, String description_, String icon_, boolean hidden_) {
		this(id_, name_, description_, icon_, hidden_, false);
	}
	
	public Achievement(int id_, String name_, String description_, String icon_, boolean hidden_, boolean resetting_) {
		this.id = id_;
		this.name = name_;
		this.description = description_;
		this.icon = icon_;
		
		this.resetting = resetting_;
		this.hidden = hidden_;
		this.complete = false;
	}

	@Override
	public void update(AchievementController controller, GameState gs, long cTime) {
		// To be overridden.
	}
	
	@Override
	public boolean resets() { return resetting; }

	@Override
	public boolean isEarned() { return complete; }

	@Override
	public boolean isSecret() { return hidden; }

	@Override
	public void onComplete(AchievementController controller, long cTime) {
		AchievementManager.save();
		controller.broadcast(this, cTime);
		complete = true;
	}
	
	public abstract String saveFormat();
	public abstract void parseSaveData(String [] tokens);

	@Override
	public int getID() { return id; }
	
	@Override
	public String getName() { return name; }

	@Override
	public String getDescription() { return description; }

	@Override
	public Image getIcon() { return AssetManager.getManager().getImage(icon); }
}
