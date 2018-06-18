package com.gzsr.gfx.ui;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import com.gzsr.AssetManager;
import com.gzsr.Globals;
import com.gzsr.misc.Pair;

public class SkillButton extends Button {
	private static final float SIZE = 50.0f;
	
	private String skillName;
	
	public SkillButton(String skillName_, Pair<Float> position_) {
		super();
		
		this.skillName = skillName_;
		
		this.image = "GZS_SkillUpButton";
		
		this.position = position_;
		this.size = new Pair<Float>(SIZE, SIZE);
	}
	
	@Override
	public void render(Graphics g, long cTime) {
		Image button = AssetManager.getManager().getImage(image);
		if(button != null) g.drawImage(button, position.x, position.y);
	}
	
	@Override
	public void click() {
		int skillPoints = Globals.player.getIntAttribute("skillPoints");
		int currentSkillLevel = Globals.player.getIntAttribute(skillName);
		
		if((skillPoints > 0) && (currentSkillLevel < 10)) {
			// Add a skill point to the associated skill.
			Globals.player.setAttribute(skillName, (currentSkillLevel + 1));
			Globals.player.setAttribute("skillPoints", (skillPoints - 1));
			
			AssetManager.getManager().getSound("point_buy").play();
		}
	}
	
	@Override
	public boolean inBounds(float x, float y) {
		return ((x > position.x) && (y > position.y) && 
				(x < (position.x + size.x)) && (y < (position.y + size.y)));
	}

	@Override
	public String getName() {
		return "Skill Button";
	}
}
