package com.gzsr.gfx.ui.hud;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.BasicGameState;

import com.gzsr.entities.Entity;
import com.gzsr.entities.Player;
import com.gzsr.gfx.Layers;
import com.gzsr.misc.Pair;

public class StaminaBar implements Entity {
	private static final Color STAMINA_COLOR = new Color(0xFFF793);
	
	private Pair<Float> position;
	private Pair<Float> size;
	
	private Rectangle bounds;
	
	public StaminaBar(Pair<Float> position_, Pair<Float> size_) {
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
		
		double currentStamina = player.getAttributes().getDouble("stamina");
		double maxStamina = player.getAttributes().getDouble("maxStamina");
		double percentage = (currentStamina / maxStamina);
		
		changeColor(g, Color.black, touchingPlayer);
		g.fillRect(position.x, position.y, size.x, size.y);
		changeColor(g, Color.lightGray, touchingPlayer);
		g.drawRect(position.x, position.y, size.x, size.y);
		
		changeColor(g, STAMINA_COLOR, touchingPlayer);
		g.fillRect((position.x + 3.0f), (position.y + 3.0f), 
				   (float)(percentage * (size.x - 6.0f)),
				   (size.y - 6.0f));
		
		changeColor(g, Color.lightGray, touchingPlayer);
		g.drawRect((position.x + 3.0f), (position.y + 3.0f), 
				   (float)(percentage * (size.x - 6.0f)), (size.y - 6.0f));
	}
	
	public boolean intersects(Player player) {
		return bounds.intersects(player.getCollider());
	}
	
	private void changeColor(Graphics g, Color c, boolean touchingPlayer) {
		g.setColor(touchingPlayer ? c.multiply(HUD.FADE) : c);
	}
	
	@Override
	public String getName() {
		return "Stamina Bar";
	}

	@Override
	public String getDescription() {
		return "Stamina Bar";
	}

	@Override
	public int getLayer() {
		return Layers.NONE.val();
	}
}
