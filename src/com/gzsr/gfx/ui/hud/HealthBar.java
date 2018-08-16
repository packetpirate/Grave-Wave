package com.gzsr.gfx.ui.hud;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.util.FontUtils;

import com.gzsr.AssetManager;
import com.gzsr.entities.Entity;
import com.gzsr.entities.Player;
import com.gzsr.gfx.Layers;
import com.gzsr.misc.Pair;
import com.gzsr.status.Status;

public class HealthBar implements Entity {
	private static final Color POISON_COLOR = new Color(0x009900);
	private static final Color ARMOR_COLOR = new Color(0x4286F4);
	
	private Pair<Float> position;
	private Pair<Float> size;
	
	private Rectangle bounds;
	
	public HealthBar(Pair<Float> position_, Pair<Float> size_) {
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
		
		double currentHealth = player.getAttributes().getDouble("health");
		double maxHealth = player.getAttributes().getDouble("maxHealth");
		double percentage = (currentHealth / maxHealth);
		
		changeColor(g, Color.black, touchingPlayer);
		g.fillRect(position.x, position.y, size.x, size.y);
		changeColor(g, Color.lightGray, touchingPlayer);
		g.drawRect(position.x, position.y, size.x, size.y);
		
		changeColor(g, (player.getStatusHandler().hasStatus(Status.POISON) ? POISON_COLOR : Color.red), touchingPlayer);
		g.fillRect((position.x + 3.0f), 
				   (position.y + 3.0f), 
				   ((float)percentage * (size.x - 6.0f)), (size.y - 6.0f));
		
		// Render the armor bar on top of the health bar.
		double currentArmor = player.getAttributes().getDouble("armor");
		double maxArmor = player.getAttributes().getDouble("maxArmor");
		double armorPercentage = currentArmor / maxArmor;
		
		changeColor(g, ARMOR_COLOR, touchingPlayer);
		g.fillRect((position.x + 3.0f), 
				   (position.y + 3.0f), 
				   ((float)armorPercentage * (size.x - 6.0f)), (size.y - 6.0f));
		
		// Render the border around the health / armor bars.
		changeColor(g, Color.lightGray, touchingPlayer);
		g.drawRect((position.x + 3.0f), 
				   (position.y + 3.0f), 
				   ((float)Math.max(percentage, armorPercentage) * (size.x - 6.0f)), (size.y - 6.0f));
		
		UnicodeFont f = AssetManager.getManager().getFont("PressStart2P-Regular_small");
		String healthText = String.format("HP: %d / %d", (int)currentHealth, (int)maxHealth);
		FontUtils.drawCenter(f, healthText, (int)position.x.floatValue(), (int)(position.y.floatValue() + f.getLineHeight()), (int)size.x.floatValue());
		
		float startX = (position.x + size.x);
		float startY = position.y + 5.0f;
		Image life = AssetManager.getManager().getImage("GZS_Life");
		for(int i = 0; i < player.getAttributes().getInt("lives"); i++) {
			g.drawImage(life, (startX + (i * life.getWidth()) + (i * 3.0f) + 5.0f), startY);
		}
	}
	
	private void changeColor(Graphics g, Color c, boolean touchingPlayer) {
		g.setColor(touchingPlayer ? c.multiply(HUD.FADE) : c);
	}
	
	private boolean intersects(Player player) {
		return bounds.intersects(player.getCollider());
	}

	@Override
	public String getName() {
		return "Health Bar";
	}

	@Override
	public String getDescription() {
		return "Health Bar";
	}
	
	@Override
	public int getLayer() {
		return Layers.NONE.val();
	}
}
