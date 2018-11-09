package com.gzsr.gfx.ui;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.UnicodeFont;

import com.gzsr.AssetManager;
import com.gzsr.entities.Player;
import com.gzsr.misc.Pair;
import com.gzsr.talents.Talents;
import com.gzsr.talents.Talents.TalentType;

public class TalentButton extends Button {
	// Use row as index to find level requirement for a particular talent.
	private static final int [] TIER_LEVEL_REQUIREMENTS = new int[] {1, 5, 10, 15, 20, 25, 30};
	private static final Color GRAY_OUT = new Color(150, 150, 150);
	
	public static final float SIZE = 32.0f;
	
	private TalentType talent;
	public TalentType getTalent() { return talent; }
	
	private int pointsToAdd;
	public int getPointsToAdd() { return pointsToAdd; }
	
	public TalentButton(TalentType talent_, Pair<Float> position_) {
		this.talent = talent_;
		this.position = position_;
		
		this.pointsToAdd = 0;
	}
	
	public void confirm() {
		int total = (talent.ranks() + pointsToAdd);
		talent.ranks(total);
		Talents.applyRanks(talent);
		
		pointsToAdd = 0;
	}
	
	public void revert() {
		Player player = Player.getPlayer();
		int sk = player.getAttributes().getInt("skillPoints");
		player.getAttributes().set("skillPoints", (sk + pointsToAdd));
		
		pointsToAdd = 0;
	}
	
	@Override
	public void render(Graphics g, long cTime) {
		boolean correctLevel = meetsLevelRequirement();
		
		Image img = talent.getIcon();
		if(img != null) {
			float w = img.getWidth();
			float h = img.getHeight();
			
			g.setColor(Color.black);
			g.fillRect((position.x - (w / 2)), (position.y - (h / 2)), w, h);
			
			img.draw((position.x - (w / 2)), (position.y - (h / 2)), (correctLevel ? Color.white : GRAY_OUT));
			
			g.setColor(Color.white);
			g.drawRect((position.x - (w / 2)), (position.y - (h / 2)), w, h);
		} else {
			g.setColor(Color.black);
			g.fillRect((position.x - (SIZE / 2)), (position.y - (SIZE / 2)), SIZE, SIZE);
			g.setColor(Color.white);
			g.drawRect((position.x - (SIZE / 2)), (position.y - (SIZE / 2)), SIZE, SIZE);
		}
		
		UnicodeFont f = AssetManager.getManager().getFont("PressStart2P-Regular_small");
		String progress = String.format("%d / %d", (talent.ranks() + pointsToAdd), talent.maxRanks());
		float tw = f.getWidth(progress);
		g.setFont(f);
		g.setColor(Color.white);
		g.drawString(progress, (position.x - (tw / 2)), (position.y + (SIZE / 2) + 10.0f));
	}
	
	private boolean meetsLevelRequirement() {
		Player player = Player.getPlayer();
		int level = player.getAttributes().getInt("level");
		return (level >= TIER_LEVEL_REQUIREMENTS[talent.row()]);
	}
	
	@Override
	public void click() {
		click(true);
	}
	
	@Override
	public void click(boolean left) {
		if(left) {
			if(meetsLevelRequirement()) {
				if((talent.ranks() + pointsToAdd) < talent.maxRanks()) {
					Player player = Player.getPlayer();
					int sk = player.getAttributes().getInt("skillPoints");
					
					if(sk > 0) {
						pointsToAdd++;
						player.getAttributes().set("skillPoints", (sk - 1));
					}
				}
			}
		} else {
			if(pointsToAdd > 0) {
				Player player = Player.getPlayer();
				int sk = player.getAttributes().getInt("skillPoints");
				pointsToAdd--;
				player.getAttributes().set("skillPoints", (sk + 1));
			}
		}
	}

	@Override
	public boolean inBounds(float x, float y) {
		Image img = AssetManager.getManager().getImage(image);
		
		float w = (img != null) ? img.getWidth() : SIZE;
		float h = (img != null) ? img.getHeight() : SIZE;
		
		return ((x > (position.x - (w / 2))) && (x < (position.x + (w / 2))) && 
				(y > (position.y - (h / 2))) && (y < (position.y + (h / 2))));
	}
}
