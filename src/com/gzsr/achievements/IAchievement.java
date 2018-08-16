package com.gzsr.achievements;

import org.newdawn.slick.Image;

import com.gzsr.controllers.AchievementController;
import com.gzsr.states.GameState;

public interface IAchievement {
	public String getName();
	public String getDescription();
	public Image getIcon();
	
	public void update(AchievementController controller, GameState gs, long cTime);
	
	public boolean isEarned();
	public boolean isSecret();
	
	public void onComplete(AchievementController controller, long cTime);
}
