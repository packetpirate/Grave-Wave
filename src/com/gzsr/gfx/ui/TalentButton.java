package com.gzsr.gfx.ui;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import com.gzsr.AssetManager;
import com.gzsr.entities.Player;
import com.gzsr.talents.Talents.TalentType;

public class TalentButton extends Button {
	private TalentType talent;
	public TalentType getTalent() { return talent; }
	
	private int pointsToAdd;
	public int getPointsToAdd() { return pointsToAdd; }
	
	public TalentButton(TalentType talent_) {
		this.talent = talent_;
		this.pointsToAdd = 0;
	}
	
	public void confirm() {
		int total = (talent.ranks() + pointsToAdd);
		talent.ranks(total);
		
		pointsToAdd = 0;
	}
	
	public void revert() {
		Player player = Player.getPlayer();
		int sk = player.getAttributes().getInt("skillPoints");
		player.getAttributes().set("skillPoints", (sk - pointsToAdd));
		
		pointsToAdd = 0;
	}
	
	@Override
	public void render(Graphics g, long cTime) {
		Image img = talent.getIcon();
		if(img != null) {
			float w = img.getWidth();
			float h = img.getHeight();
			
			g.setColor(Color.black);
			g.fillRect((position.x - (w / 2)), (position.y - (h / 2)), w, h);
			g.setColor(Color.white);
			g.drawRect((position.x - (w / 2)), (position.y - (h / 2)), w, h);
			
			g.drawImage(img, (position.x - (w / 2)), (position.y - (h / 2)));
		}
	}
	
	@Override
	public void click() {
		if((talent.ranks() + pointsToAdd) < talent.maxRanks()) {
			Player player = Player.getPlayer();
			int sk = player.getAttributes().getInt("skillPoints");
			
			if(sk > 0) {
				pointsToAdd++;
				player.getAttributes().set("skillPoints", (sk - 1));
			}
		}
	}

	@Override
	public boolean inBounds(float x, float y) {
		Image img = AssetManager.getManager().getImage(image);
		float w = img.getWidth();
		float h = img.getHeight();
		
		return ((x > (position.x - (w / 2))) && (x < (position.x + (w / 2))) && 
				(y > (position.y - (h / 2))) && (y < (position.y + (h / 2))));
	}
}
