package com.gzsr.gfx.ui;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.TrueTypeFont;

import com.gzsr.Globals;
import com.gzsr.entities.Entity;
import com.gzsr.entities.enemies.EnemyController;
import com.gzsr.entities.enemies.Rotdog;
import com.gzsr.entities.enemies.Zumby;
import com.gzsr.misc.Pair;
import com.gzsr.states.GameState;

public class Console implements Entity {
	private static final Color CONSOLE_BACKGROUND = Color.lightGray;
	private static final Color CONSOLE_BORDER = Color.black;
	private static final Color CONSOLE_TEXT = Color.white;
	private static final Color CONSOLE_PASTTEXT = Color.black;
	private static final Color CONSOLE_TEXTBOX = Color.darkGray;
	private static final Color CONSOLE_TEXTBORDER = Color.black;
	private static final TrueTypeFont CONSOLE_FONT = new TrueTypeFont(new Font("Lucida Console", Font.PLAIN, 12), true);
	
	private GameState gs;
	private GameContainer gc;
	
	private String currentCommand;
	private List<String> pastCommands;
	
	public Console(GameState gs_, GameContainer gc_) {
		this.gs = gs_;
		this.gc = gc_;
		this.currentCommand = "";
		this.pastCommands = new ArrayList<String>();
		
		System.out.printf("Font Size: %d\n", CONSOLE_FONT.getHeight());
	}

	@Override
	public void update(long cTime) {
		
	}

	@Override
	public void render(Graphics g, long cTime) {
		// Draw the container window.
		g.setColor(CONSOLE_BACKGROUND);
		g.fillRect(10.0f, (Globals.HEIGHT - 227.0f), (Globals.WIDTH - 20.0f), 217.0f);
		g.setColor(CONSOLE_BORDER);
		g.drawRect(10.0f, (Globals.HEIGHT - 227.0f), (Globals.WIDTH - 20.0f), 217.0f);
		
		// Draw the input box.
		g.setColor(CONSOLE_TEXTBOX);
		g.fillRect(15.0f, (Globals.HEIGHT - 34.0f), (Globals.WIDTH - 30.0f), 19.0f);
		g.setColor(CONSOLE_TEXTBORDER);
		g.drawRect(15.0f, (Globals.HEIGHT - 34.0f), (Globals.WIDTH - 30.0f), 19.0f);
		
		// Draw the current command.
		g.setColor(CONSOLE_TEXT);
		g.setFont(CONSOLE_FONT);
		g.drawString(currentCommand, 20.0f, (Globals.HEIGHT - 30.0f));
	}
	
	private void submitCommand() {
		currentCommand = currentCommand.trim(); // Get rid of leading and trailing whitespace.
		
		if(currentCommand.length() > 0) {
			if(currentCommand.charAt(0) == '/') {
				// Command detected.
				String [] tokens = currentCommand.substring(1).split(" ");
				if(tokens.length > 0) {
					// Parse the command and determine what to do.
					if(tokens[0].equals("spawn") && (tokens.length == 4)) {
						// requires entity name and x,y coordinates
						EnemyController ec = (EnemyController)gs.getEntities().get("enemyController");
						
						String entityName = tokens[1];
						float x = Float.parseFloat(tokens[2]);
						float y = Float.parseFloat(tokens[3]);
						
						if(entityName.equals("zumby")) {
							Zumby z = new Zumby(new Pair<Float>(x, y));
							ec.addAlive(z);
						} else if(entityName.equals("rotdog")) {
							Rotdog r = new Rotdog(new Pair<Float>(x, y));
							ec.addAlive(r);
						}
					}
					
					pastCommands.add(currentCommand);
				} else {
					System.out.printf("Invalid console command!: %s\n", currentCommand);
				}
			} else {
				// Garbage text. Just add it to the previous commands list.
				pastCommands.add(currentCommand);
			}
		}
		
		currentCommand = "";
	}

	public void keyPressed(int key, char c) {
		// Handle submitting commands or erasing characters.
		if(key == Input.KEY_ENTER) submitCommand();
		if(key == Input.KEY_BACK) currentCommand = currentCommand.substring(0, (currentCommand.length() - 1));
	}

	public void keyReleased(int key, char c) {
		// Handle key typing.
		if(((c == '/') || (c == ' ') || Character.isLetterOrDigit(c)) && (CONSOLE_FONT.getWidth(currentCommand) < (Globals.WIDTH - 24.0f))) currentCommand += c;
	}
}
