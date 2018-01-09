package com.gzsr.states;

import java.awt.Font;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import com.gzsr.Globals;

public class ShopState extends BasicGameState {
	public static final int ID = 2;
	private static final TrueTypeFont FONT_HEADER = new TrueTypeFont(new Font("Lucida Console", Font.BOLD, 32), true);
	private static final TrueTypeFont FONT_NORMAL = new TrueTypeFont(new Font("Lucida Console", Font.PLAIN, 16), true);
	
	@Override
	public void init(GameContainer gc, StateBasedGame game) throws SlickException {
		
	}

	@Override
	public void update(GameContainer gc, StateBasedGame game, int delta) throws SlickException {
		
	}
	
	@Override
	public void render(GameContainer gc, StateBasedGame game, Graphics g) throws SlickException {
		g.resetTransform();
		g.clear();
		
		g.setColor(Color.darkGray);
		g.fillRect(0.0f, 0.0f, Globals.WIDTH, Globals.HEIGHT);
		
		// Draw the header and footer.
		g.setColor(Color.white);
		g.drawLine(10.0f, 36.0f, (Globals.WIDTH - 10.0f), 36.0f);
		g.drawLine(10.0f, (Globals.HEIGHT - 36.0f), (Globals.WIDTH - 10.0f), (Globals.HEIGHT - 36.0f));
		g.setFont(ShopState.FONT_HEADER);
		g.drawString("Item Shop", 30.0f, 20.0f);
		
		// Draw the item selection container.
		g.setColor(new Color(0x2e2e2e));
		g.fillRect(10.0f, 64.0f, (Globals.WIDTH - 230.0f), (Globals.HEIGHT - 110.0f));
		g.setColor(Color.white);
		g.drawRect(10.0f, 64.0f, (Globals.WIDTH - 230.0f), (Globals.HEIGHT - 110.0f));
		
		// Draw the item detail container.
		g.setColor(new Color(0x2e2e2e));
		g.fillRect((Globals.WIDTH - 210.0f), 64.0f, 200.0f, (Globals.HEIGHT - 110.0f));
		g.setColor(Color.white);
		g.drawRect((Globals.WIDTH - 210.0f), 64.0f, 200.0f, (Globals.HEIGHT - 110.0f));
		
		// Draw the item boxes.
		for(int r = 0; r < 12; r++) {
			for(int c = 0; c < 13; c++) {
				float x = (((c * 48.0f) + (c * 12.0f)) + 23.0f);
				float y = (((r * 48.0f) + (r * 5.0f)) + 77.0f);
				
				g.setColor(Color.black);
				g.fillRect(x, y, 48.0f, 48.0f);
				g.setColor(Color.darkGray);
				g.drawRect(x, y, 48.0f, 48.0f);
			}
		}
	}

	@Override
	public int getID() {
		return ShopState.ID;
	}
}
