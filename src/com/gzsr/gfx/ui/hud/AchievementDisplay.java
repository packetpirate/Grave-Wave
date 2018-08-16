package com.gzsr.gfx.ui.hud;

import java.util.List;

import org.newdawn.slick.Graphics;

import com.gzsr.AssetManager;
import com.gzsr.Globals;
import com.gzsr.achievements.AchievementBroadcast;
import com.gzsr.controllers.AchievementController;
import com.gzsr.misc.Pair;

public class AchievementDisplay {
	public AchievementDisplay() {
		
	}

	public void render(Graphics g, long cTime) {
		g.setFont(AssetManager.getManager().getFont("PressStart2P-Regular_small"));
		
		float x = (Globals.WIDTH - AchievementBroadcast.SIZE.x - 10.0f);
		float y = (Globals.HEIGHT - AchievementBroadcast.SIZE.y - 72.0f);
		
		List<AchievementBroadcast> broadcasts = AchievementController.getInstance().getBroadcasts();
		for(int i = (broadcasts.size() - 1); i >= 0; i--) {
			AchievementBroadcast broadcast = broadcasts.get(i);
			if(broadcast.isActive(cTime)) {
				broadcast.render(g, new Pair<Float>(x, y), cTime);
				y -= (AchievementBroadcast.SIZE.y + 10.0f);
			}
		}
	}
}
