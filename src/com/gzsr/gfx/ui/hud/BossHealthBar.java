package com.gzsr.gfx.ui.hud;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.util.FontUtils;

import com.gzsr.AssetManager;
import com.gzsr.entities.Entity;
import com.gzsr.entities.Player;
import com.gzsr.entities.enemies.Enemy;
import com.gzsr.entities.enemies.EnemyController;
import com.gzsr.gfx.Layers;
import com.gzsr.misc.Pair;

public class BossHealthBar implements Entity {
	private Pair<Float> position;
	private Pair<Float> size;
	
	private Rectangle bounds;
	
	public BossHealthBar(Pair<Float> position_, Pair<Float> size_) {
		this.position = position_;
		this.size = size_;
		
		this.bounds = new Rectangle(position.x, position.y, size.x, size.y);
	}

	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
		
	}

	@Override
	public void render(Graphics g, long cTime) {
		EnemyController ec = EnemyController.getInstance();
		
		g.setColor(Color.black);
		g.fillRect(position.x, position.y, size.x, size.y);
		g.setColor(Color.white);
		g.drawRect(position.x, position.y, size.x, size.y);
		
		float healthLeft = 0.0f;
		for(Enemy e : ec.getUnbornEnemies()) healthLeft += (float)e.getHealth();
		for(Enemy e : ec.getAliveEnemies()) if(e.isAlive(cTime)) healthLeft += (float)e.getHealth();
		
		float percentage = (healthLeft / (float)ec.getBossWaveHealth());
		if(percentage < 0.0f) percentage = 0.0f;
		else if(percentage > 1.0f) percentage = 1.0f;
		
		g.setColor(Color.red);
		g.fillRect((position.x + 2.0f), (position.y + 2.0f), 
				   ((size.x - 4.0f) * percentage), (size.y - 4.0f));
		g.setColor(Color.white);
		g.drawRect((position.x + 2.0f), (position.y + 2.0f), 
				   ((size.x - 4.0f) * percentage), (size.y - 4.0f));
		
		g.setFont(AssetManager.getManager().getFont("PressStart2P-Regular"));
		FontUtils.drawCenter(g.getFont(), ec.getBossTitle(), 
							 (int)position.x.floatValue(), 
							 (int)(position.y + size.y + 10.0f), 
							 (int)size.x.floatValue(), Color.white);
	}
	
	private boolean intersects(Player player) {
		return bounds.intersects(player.getCollider());
	}

	@Override
	public String getName() {
		return "Boss Health Bar";
	}

	@Override
	public String getDescription() {
		return "Displays boss wave health.";
	}

	@Override
	public int getLayer() {
		return Layers.HUD.val();
	}
}
