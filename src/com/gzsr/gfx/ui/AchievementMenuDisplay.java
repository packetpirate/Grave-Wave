package com.gzsr.gfx.ui;

import java.util.Map;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.state.BasicGameState;

import com.gzsr.AssetManager;
import com.gzsr.achievements.Achievement;
import com.gzsr.achievements.milestone.MilestoneAchievement;
import com.gzsr.entities.Entity;
import com.gzsr.gfx.Layers;
import com.gzsr.misc.Pair;

public class AchievementMenuDisplay implements Entity {
	private static final String HEADER = "PressStart2P-Regular";
	private static final String BODY = "PressStart2P-Regular_small";
	private static final Color HEADER_COLOR = Color.white;
	private static final Color BODY_COLOR = Color.lightGray;
	private static final Color BACKGROUND = new Color(0x2D2D2D);
	private static final Color PROGRESS_COLOR = new Color(0xC9C264);
	
	private static final float ICON_SIZE = 64.0f;
	private static final float PROGRESS_WIDTH = 200.0f;
	private static final float PROGRESS_HEIGHT = 20.0f;
	
	private Achievement achievement;
	
	private Pair<Float> origin;
	public Pair<Float> getPosition() { return origin; }
	public void setPosition(Pair<Float> origin_) { this.origin = origin_; }
	public void scrollPosition(float amnt) { origin.y += amnt; }
	
	private float containerWidth;
	public float getContainerWidth() { return containerWidth; }
	
	private float containerHeight;
	public float getContainerHeight() { return containerHeight; }
	
	public AchievementMenuDisplay(Achievement achievement_, float containerWidth_) {
		this.achievement = achievement_;
		this.origin = Pair.ZERO;
		this.containerWidth = containerWidth_;
		this.containerHeight = getTotalDisplayHeight();
	}
	
	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
	}

	@Override
	public void render(Graphics g, long cTime) {
		AssetManager assets = AssetManager.getManager();
		
		// Background of achievement display.
		g.setColor(BACKGROUND);
		g.fillRect(origin.x, origin.y, containerWidth, containerHeight);
		g.setColor(Color.white);
		g.drawRect(origin.x, origin.y, containerWidth, containerHeight);
		
		// Achievement image.
		g.setColor(Color.black);
		g.fillRect((origin.x + 10.0f), (origin.y + 10.0f), ICON_SIZE, ICON_SIZE);
		g.setColor(Color.white);
		g.drawRect((origin.x + 10.0f), (origin.y + 10.0f), ICON_SIZE, ICON_SIZE);
		
		Image img = achievement.getIcon();
		if(img != null) {
			float scale = (ICON_SIZE / img.getWidth());
			img.draw((origin.x + 10.0f), (origin.y + 10.0f), scale);
		}
		
		// Achievement name.
		UnicodeFont header = assets.getFont(HEADER);
		float textX = (origin.x + ICON_SIZE + 30.0f);
		g.setColor(HEADER_COLOR);
		g.setFont(header);
		g.drawString(achievement.getName(), textX, (origin.y + 10.0f));
		
		// Achievement description.
		UnicodeFont body = assets.getFont(BODY);
		float descY = (origin.y + header.getLineHeight() + 20.0f);
		g.setColor(BODY_COLOR);
		g.setFont(body);
		g.drawString(achievement.getDescription(), textX, descY);
		
		// Milestones, if any.
		if(achievement instanceof MilestoneAchievement) {
			Map<Long, Pair<Integer>> milestones = ((MilestoneAchievement)achievement).getMilestones();
			Map<Long, String> descriptors = ((MilestoneAchievement)achievement).getDescriptors();
			
			float y = (descY + body.getLineHeight() + 20.0f);
			for(Map.Entry<Long, Pair<Integer>> entry : milestones.entrySet()) {
				long metric = entry.getKey();
				Pair<Integer> progress = entry.getValue();
				String name = descriptors.get(metric);
				
				// Draw milestone name.
				g.setColor(BODY_COLOR);
				g.drawString(name, textX, y);
				
				// Draw milestone progress bar.
				g.setColor(Color.black);
				g.fillRect(textX, (y + body.getLineHeight() + 5.0f), PROGRESS_WIDTH, PROGRESS_HEIGHT);
				
				if(progress.x > 0) {
					float percentage = ((float)progress.x / (float)progress.y);
					g.setColor(PROGRESS_COLOR);
					g.fillRect(textX, (y + body.getLineHeight() + 5.0f), (percentage * PROGRESS_WIDTH), PROGRESS_HEIGHT);
				}
				
				g.setColor(Color.white);
				g.drawRect(textX, (y + body.getLineHeight() + 5.0f), PROGRESS_WIDTH, PROGRESS_HEIGHT);
				
				// Progress text.
				g.setColor(BODY_COLOR);
				g.drawString(String.format("%d / %d", progress.x, progress.y), (textX + PROGRESS_WIDTH + 10.0f), (y + body.getLineHeight() + 5.0f));
				
				y += (body.getLineHeight() + PROGRESS_HEIGHT + 15.0f);
			}
		}
	}
	
	public float getTotalDisplayHeight() {
		AssetManager assets = AssetManager.getManager();
		float min = ICON_SIZE + 20.0f;
		float height = 10.0f;
		
		// Add height of header text.
		height += assets.getFont(HEADER).getLineHeight();
		
		// Add height of body text.
		height += assets.getFont(BODY).getLineHeight() + 30.0f;
		
		// Add height of milestones, if any.
		if(achievement instanceof MilestoneAchievement) {
			int numOfMilestones = ((MilestoneAchievement)achievement).getMilestones().size();
			height += (numOfMilestones * (assets.getFont(BODY).getLineHeight() + PROGRESS_HEIGHT + 15.0f));
		}
		
		// Add a bit of padding.
		height += 10.0f;
		
		return Math.max(height, min);
	}

	@Override
	public String getName() {
		return "Achievement Display";
	}

	@Override
	public String getDescription() {
		return "Achievement Display";
	}

	@Override
	public int getLayer() {
		return Layers.NONE.val();
	}
}
