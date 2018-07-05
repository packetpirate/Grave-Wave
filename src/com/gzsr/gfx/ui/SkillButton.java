package com.gzsr.gfx.ui;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import com.gzsr.AssetManager;
import com.gzsr.entities.Player;
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
		int skillPoints = Player.getPlayer().getAttributes().getInt("skillPoints");
		int currentSkillLevel = Player.getPlayer().getAttributes().getInt(skillName);
		
		if((skillPoints > 0) && (currentSkillLevel < 10)) {
			// Add a skill point to the associated skill.
			Player.getPlayer().getAttributes().set(skillName, (currentSkillLevel + 1));
			Player.getPlayer().getAttributes().set("skillPoints", (skillPoints - 1));
			
			AssetManager.getManager().getSound("point_buy").play(1.0f, AssetManager.getManager().getSoundVolume());
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
