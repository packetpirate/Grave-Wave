package com.grave.achievements;

import org.newdawn.slick.Image;

import com.grave.controllers.AchievementController;
import com.grave.states.GameState;

public interface IAchievement {
	public int getID();
	public String getName();
	public String getDescription();
	public Image getIcon();
	
	public void update(AchievementController controller, GameState gs, long cTime);
	
	public boolean resets();
	public boolean isEarned();
	public boolean isSecret();
	
	public void onComplete(AchievementController controller, long cTime);
}
