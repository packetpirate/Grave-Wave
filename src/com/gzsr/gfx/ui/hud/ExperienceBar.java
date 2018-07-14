package com.gzsr.gfx.ui.hud;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.BasicGameState;

import com.gzsr.entities.Entity;
import com.gzsr.entities.Player;
import com.gzsr.misc.Pair;

public class ExperienceBar implements Entity {
	private Pair<Float> position;
	private Pair<Float> size;
	
	private Rectangle bounds;
	
	public ExperienceBar(Pair<Float> position_, Pair<Float> size_) {
		this.position = position_;
		this.size = size_;
		
		this.bounds = new Rectangle(position.x, position.y, size.x, size.y);
	}
	
	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
	}

	@Override
	public void render(Graphics g, long cTime) {
		Player player = Player.getPlayer();
		boolean touchingPlayer = intersects(player);
		
		float currentExp = (float)player.getAttributes().getInt("experience");
		float expToLevel = (float)player.getAttributes().getInt("expToLevel");
		float percentage = currentExp / expToLevel;
		
		changeColor(g, Color.black, touchingPlayer);
		g.fillRect(position.x, position.y, size.x, size.y);
		changeColor(g, Color.lightGray, touchingPlayer);
		g.drawRect(position.x, position.y, size.x, size.y);
		
		if(percentage != 0.0f) {
			changeColor(g, Color.green, touchingPlayer);
			g.fillRect((position.x + 3.0f), (position.y + 3.0f), 
					   (percentage * (size.x - 6.0f)), (size.y - 6.0f));
			changeColor(g, Color.lightGray, touchingPlayer);
			g.drawRect((position.x + 3.0f), (position.y + 3.0f), 
					   (percentage * (size.x - 6.0f)), (size.y - 6.0f));
		}
	}
	
	public boolean intersects(Player player) {
		return bounds.intersects(player.getCollider());
	}
	
	private void changeColor(Graphics g, Color c, boolean touchingPlayer) {
		g.setColor(touchingPlayer ? c.multiply(HUD.FADE) : c);
	}

	@Override
	public String getName() {
		return "Experience Bar";
	}

	@Override
	public String getDescription() {
		return "Experience Bar";
	}
}
