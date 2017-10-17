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
import com.gzsr.objects.items.AmmoCrate;
import com.gzsr.objects.items.HealthKit;
import com.gzsr.objects.items.InvulnerableItem;
import com.gzsr.objects.items.SpeedItem;
import com.gzsr.objects.items.UnlimitedAmmoItem;
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
		g.fillRect(10.0f, (Globals.HEIGHT - 232.0f), (Globals.WIDTH - 20.0f), 222.0f);
		g.setColor(CONSOLE_BORDER);
		g.drawRect(10.0f, (Globals.HEIGHT - 232.0f), (Globals.WIDTH - 20.0f), 222.0f);
		
		// Draw the input box.
		g.setColor(CONSOLE_TEXTBOX);
		g.fillRect(15.0f, (Globals.HEIGHT - 34.0f), (Globals.WIDTH - 30.0f), 19.0f);
		g.setColor(CONSOLE_TEXTBORDER);
		g.drawRect(15.0f, (Globals.HEIGHT - 34.0f), (Globals.WIDTH - 30.0f), 19.0f);
		
		// Draw the current command.
		g.setColor(CONSOLE_TEXT);
		g.setFont(CONSOLE_FONT);
		g.drawString(currentCommand, 20.0f, (Globals.HEIGHT - 30.0f));
		
		// Display the previous commands.
		if(!pastCommands.isEmpty()) {
			int command = (pastCommands.size() > 10) ? (pastCommands.size() - 10) : 0;
			
			g.setColor(CONSOLE_PASTTEXT);
			for(int i = 0; i < Math.min(pastCommands.size(), 10); i++) {
				float x = 20.0f;
				float y = (Globals.HEIGHT - 39.0f - (19.0f * Math.min(pastCommands.size(), 10))) + (i * 19.0f);
				g.drawString(pastCommands.get(command), x, (y + 4.0f));
				command++;
			}
		}
	}
	
	private void submitCommand() {
		currentCommand = currentCommand.trim(); // Get rid of leading and trailing whitespace.
		
		if(currentCommand.length() > 0) {
			if(currentCommand.charAt(0) == '/') {
				// Command detected.
				String [] tokens = currentCommand.substring(1).split(" ");
				if(tokens.length > 0) {
					// Parse the command and determine what to do.
					String command = tokens[0];
					int args = tokens.length - 1;
					pastCommands.add(String.format("> %s", currentCommand));
					
					if(command.equals("help") && (args == 0)) {
						pastCommands.add("  HELP: Here are some commands and example usage.");
						pastCommands.add("    /help - display this help dialog");
						pastCommands.add("    /spawn - (usage: /spawn zumby 300 300) will spawn a Zumby at (300, 300)");
						pastCommands.add("    /item - (usage: /item health 300 300) will spawn a Health Kit at (300, 300)");
					} else if(command.equals("spawn") && (args == 3)) {
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
						} else {
							pastCommands.add("  ERROR: Invalid entity name specified.");
						}
					} else if(command.equals("item") && (args == 3)) {
						String itemName = tokens[1];
						float x = Float.parseFloat(tokens[2]);
						float y = Float.parseFloat(tokens[3]);
						long cTime = gs.getTime();
						int id = Globals.generateEntityID();
						
						if(itemName.equals("ammo")) {
							AmmoCrate ac = new AmmoCrate(new Pair<Float>(x, y), cTime);
							gs.getEntities().put(String.format("ammo%d", id), ac);
						} else if(itemName.equals("health")) {
							HealthKit hk = new HealthKit(new Pair<Float>(x, y), cTime);
							gs.getEntities().put(String.format("health%d", id), hk);
						} else if(itemName.equals("invulnerability")) {
							InvulnerableItem inv = new InvulnerableItem(new Pair<Float>(x, y), cTime);
							gs.getEntities().put(String.format("invuln%d", id), inv);
						} else if(itemName.equals("speed")) {
							SpeedItem spd = new SpeedItem(new Pair<Float>(x, y), cTime);
							gs.getEntities().put(String.format("speed%d", id), spd);
						} else if(itemName.equals("unlimitedammo")) {
							UnlimitedAmmoItem una = new UnlimitedAmmoItem(new Pair<Float>(x, y), cTime);
							gs.getEntities().put(String.format("unlimAmmo%d", args), una);
						} else {
							pastCommands.add("  ERROR: Invalid item name specified.");
						}
					} else {
						pastCommands.add(String.format("  ERROR: Unrecognized command name: \"%s\"", command));
					}
				} else {
					pastCommands.add("  ERROR: Invalid command format!");
				}
			} else {
				// Garbage text. Just add it to the previous commands list.
				pastCommands.add(currentCommand);
			}
		}
		
		currentCommand = "";
	}
	
	public void mousePressed(int button, int x, int y) {
		// Append the current x and y position to the end of the current command.
		if(currentCommand.charAt(currentCommand.length() - 1) != ' ') currentCommand += " ";
		currentCommand += String.format("%d %d", x, y);
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