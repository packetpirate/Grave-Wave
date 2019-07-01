package com.gzsr.achievements;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import com.gzsr.math.Calculate;
import com.gzsr.misc.Pair;

public class AchievementBroadcast {
	public static final Pair<Float> SIZE = new Pair<Float>(300.0f, 72.0f);
	private static final float ICON_SIZE = 32.0f;
	private static final long DURATION = 3_000L;
	
	private Achievement achievement;
	private long created;
	
	public AchievementBroadcast(Achievement achievement_, long cTime) {
		this.achievement = achievement_;
		this.created = cTime;
	}

	public void render(Graphics g, Pair<Float> position, long cTime) {
		if(isActive(cTime)) {
			g.setColor(Color.darkGray);
			g.fillRect(position.x, position.y, (SIZE.x - (SIZE.y + 5.0f)), SIZE.y);
			g.fillRect(((position.x + SIZE.x) - SIZE.y), position.y, SIZE.y, SIZE.y);
			g.setColor(Color.white);
			g.drawRect(position.x, position.y, (SIZE.x - (SIZE.y + 5.0f)), SIZE.y);
			g.drawRect(((position.x + SIZE.x) - SIZE.y), position.y, SIZE.y, SIZE.y);
			
			String name = achievement.getName();
			String desc = achievement.getDescription();
			Image icon = achievement.getIcon();
			
			g.drawString(name, (position.x + 4.0f), (position.y + 4.0f));
			Calculate.TextWrap(g, desc, g.getFont(), (position.x + 4.0f), (position.y + g.getFont().getLineHeight() + 9.0f), (SIZE.x - (SIZE.y + 9.0f)), false, Color.lightGray);
			
			if(icon != null) {
				float scale = (AchievementBroadcast.ICON_SIZE / icon.getWidth());
				icon.draw(((position.x + SIZE.x) - (SIZE.y - 4.0f)), (position.y + 4.0f), scale);
			}
		}
	}
	
	public boolean isActive(long cTime) {
		long elapsed = (cTime - created);
		return (elapsed < AchievementBroadcast.DURATION);
	}
}
