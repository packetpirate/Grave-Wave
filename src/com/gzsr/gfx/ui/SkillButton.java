package com.gzsr.gfx.ui;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import com.gzsr.AssetManager;
import com.gzsr.entities.Entity;
import com.gzsr.entities.Player;
import com.gzsr.misc.Pair;

public class SkillButton implements Entity {
	private static final float SIZE = 50.0f;
	
	private String skillName;
	private boolean increase;
	
	private Pair<Float> pos;
	
	public SkillButton(String skillName_, boolean increase_, Pair<Float> pos_) {
		this.skillName = skillName_;
		this.increase = increase_;
		this.pos = pos_;
	}
	
	public void click(Player player) {
		int skillPoints = player.getIntAttribute("skillPoints");
		int currentSkillLevel = player.getIntAttribute(skillName);
		
		if(increase) {
			if((skillPoints > 0) && (currentSkillLevel < 10)) {
				// Add a skill point to the associated skill.
				player.setIntAttribute(skillName, (currentSkillLevel + 1));
				player.setIntAttribute("skillPoints", (skillPoints - 1));
			}
		} else {
			if(currentSkillLevel > 0) {
				// Remove a skill point from the associated skill.
				player.setIntAttribute(skillName, (currentSkillLevel - 1));
				player.setIntAttribute("skillPoints", (skillPoints + 1));
			}
		}
		
		AssetManager.getManager().getSound("point_buy").play();
	}

	@Override
	public void update(long cTime) {
		// Shouldn't need to do anything here...............
	}

	@Override
	public void render(Graphics g, long cTime) {
		Image button = AssetManager.getManager().getImage(increase ? "GZS_SkillUpButton" : "GZS_SkillDownButton" );
		if(button != null) g.drawImage(button, pos.x, pos.y);
	}
	
	public boolean inBounds(float x, float y) {
		return ((x > pos.x) && (y > pos.y) && 
				(x < (pos.x + SIZE)) && (y < (pos.y + SIZE)));
	}
}
