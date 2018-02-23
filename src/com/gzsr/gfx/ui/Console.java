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
import com.gzsr.entities.enemies.BigMama;
import com.gzsr.entities.enemies.EnemyController;
import com.gzsr.entities.enemies.Gasbag;
import com.gzsr.entities.enemies.Rotdog;
import com.gzsr.entities.enemies.Upchuck;
import com.gzsr.entities.enemies.Zumby;
import com.gzsr.entities.enemies.bosses.Aberration;
import com.gzsr.entities.enemies.bosses.Stitches;
import com.gzsr.entities.enemies.bosses.Zombat;
import com.gzsr.misc.Pair;
import com.gzsr.objects.items.AmmoCrate;
import com.gzsr.objects.items.HealthKit;
import com.gzsr.objects.items.InvulnerableItem;
import com.gzsr.objects.items.SpeedItem;
import com.gzsr.objects.items.UnlimitedAmmoItem;
import com.gzsr.objects.weapons.Explosion;
import com.gzsr.states.GameState;

public class Console implements Entity {
	private static final Color CONSOLE_BACKGROUND = Color.lightGray;
	private static final Color CONSOLE_BORDER = Color.black;
	private static final Color CONSOLE_TEXT = Color.white;
	private static final Color CONSOLE_PASTTEXT = Color.black;
	private static final Color CONSOLE_TEXTBOX = Color.darkGray;
	private static final Color CONSOLE_TEXTBORDER = Color.black;
	private static final TrueTypeFont CONSOLE_FONT = new TrueTypeFont(new Font("Lucida Console", Font.PLAIN, 12), true);
	private static final long DELETE_FREQ = 2L;
	
	private GameState gs;
	
	private String currentCommand;
	private List<String> pastCommands;
	
	private long pauseTime; // What time was the console opened?
	public void setPauseTime(long time) { pauseTime = time; }
	
	private boolean deleting;
	private long lastDelete;
	
	private boolean continuousSpawn;
	private String spawnType;
	
	public Console(GameState gs_, GameContainer gc_) {
		this.gs = gs_;
		this.currentCommand = "";
		this.pastCommands = new ArrayList<String>();
		this.pauseTime = 0L;
		this.deleting = false;
		this.lastDelete = 0L;
		this.continuousSpawn = false;
	}

	@Override
	public void update(GameState gs, long cTime, int delta) {
		if(deleting && (cTime > (lastDelete + Console.DELETE_FREQ)) && (currentCommand.length() > 0)) {
			deleteLastCommandChar();
			lastDelete = cTime;
		}
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
		
		// Draw the cursor next to the last character of the current command.
		g.fillRect((20.0f + CONSOLE_FONT.getWidth(currentCommand)), (Globals.HEIGHT - 30.0f), 2.0f, CONSOLE_FONT.getHeight());
		
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
	
	private void deleteLastCommandChar() {
		currentCommand = currentCommand.substring(0, (currentCommand.length() - 1));
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
					
					EnemyController ec = (EnemyController)gs.getEntity("enemyController");
					
					if(command.equals("help") && (args == 0)) {
						pastCommands.add("  HELP: Here are some commands and example usage.");
						pastCommands.add("    /help - display this help dialog");
						pastCommands.add("    /spawn entityName - (usage: /spawn zumby) will enable Zumby spawning. Left click to spawn. Right click to stop.");
						pastCommands.add("    /spawn entityName x y - (usage: /spawn zumby 300 300) will spawn a Zumby at (300, 300)");
						pastCommands.add("    /item itemName x y - (usage: /item health 300 300) will spawn a Health Kit at (300, 300)");
						pastCommands.add("    /set attribute value - (usage: /set health 300) will set your health to 300");
						pastCommands.add("    /explode x y damage radius - (usage: /explode 300 300 100 50) will create an explosion at (300, 300) with radius 50 and doing 100 damage.");
					} else if(command.equals("spawn")) {
						// Requires entity name and (x, y) coordinates.
						if(args == 1) {
							spawnType = tokens[1];
							continuousSpawn = true;
							pastCommands.add("  INFO: Continuous spawn started for entity \"" + spawnType + "\".");
						} else if(args == 3) {
							String entityName = tokens[1];
							float x = Float.parseFloat(tokens[2]);
							float y = Float.parseFloat(tokens[3]);
							Pair<Float> position = new Pair<Float>(x, y);
							
							spawnEnemy(gs, entityName, position);
						}
					} else if(command.equals("item") && (args == 3)) {
						String itemName = tokens[1];
						float x = Float.parseFloat(tokens[2]);
						float y = Float.parseFloat(tokens[3]);
						long cTime = gs.getTime();
						int id = Globals.generateEntityID();
						
						if(itemName.equals("ammo")) {
							AmmoCrate ac = new AmmoCrate(new Pair<Float>(x, y), cTime);
							gs.addEntity(String.format("ammo%d", id), ac);
						} else if(itemName.equals("health")) {
							HealthKit hk = new HealthKit(new Pair<Float>(x, y), cTime);
							gs.addEntity(String.format("health%d", id), hk);
						} else if(itemName.equals("invulnerability")) {
							InvulnerableItem inv = new InvulnerableItem(new Pair<Float>(x, y), cTime);
							gs.addEntity(String.format("invuln%d", id), inv);
						} else if(itemName.equals("speed")) {
							SpeedItem spd = new SpeedItem(new Pair<Float>(x, y), cTime);
							gs.addEntity(String.format("speed%d", id), spd);
						} else if(itemName.equals("unlimitedammo")) {
							UnlimitedAmmoItem una = new UnlimitedAmmoItem(new Pair<Float>(x, y), cTime);
							gs.addEntity(String.format("unlimAmmo%d", args), una);
						} else {
							pastCommands.add("  ERROR: Invalid item name specified.");
						}
					} else if(command.equals("set") && (args == 2)) {
						String attributeName = tokens[1];
						
						if(attributeName.equals("health")) {
							double health = Double.parseDouble(tokens[2]);
							Globals.player.setAttribute("health", health);
						} else if(attributeName.equals("money")) {
							int money = Integer.parseInt(tokens[2]);
							Globals.player.setAttribute("money", money);
						} else {
							pastCommands.add("  ERROR: Invalid attribute specified.");
						}
					} else if(command.equals("explode") && (args == 4)) {
						try {
							float x = Float.parseFloat(tokens[1]);
							float y = Float.parseFloat(tokens[2]);
							double damage = Double.parseDouble(tokens[3]);
							float radius = Float.parseFloat(tokens[4]);
							int id = Globals.generateEntityID();
							
							Explosion exp = new Explosion("GZS_Explosion", new Pair<Float>(x, y), damage, radius);
							gs.addEntity(String.format("explosion%d", id), exp);
						} catch(NumberFormatException nfe) {
							pastCommands.add("  ERROR: Invalid parameters specified for /explode command.");
						}
					} else if(command.equals("killall") && (args == 0)) {
						ec.getAliveEnemies().clear();
						ec.getUnbornEnemies().clear();
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
	
	private void spawnEnemy(GameState gs, String entityType, Pair<Float> position) {
		EnemyController ec = (EnemyController)gs.getEntity("enemyController");
		
		if(!ec.isRestarting()) {
			if(entityType.equals("zumby")) {
				Zumby z = new Zumby(position);
				ec.addAlive(z);
			} else if(entityType.equals("rotdog")) {
				Rotdog r = new Rotdog(position);
				ec.addAlive(r);
			} else if(entityType.equals("upchuck")) {
				Upchuck u = new Upchuck(position);
				ec.addAlive(u);
			} else if(entityType.equals("gasbag")) {
				Gasbag g = new Gasbag(position);
				ec.addAlive(g);
			} else if(entityType.equals("bigmama")) {
				BigMama bm = new BigMama(position, pauseTime);
				ec.addAlive(bm);
			} else if(entityType.equals("aberration")) {
				Aberration ab = new Aberration(position);
				ec.addAlive(ab);
			} else if(entityType.equals("zombat")) {
				Zombat zb = new Zombat(position);
				ec.addAlive(zb);
			} else if(entityType.equals("stitches")) {
				Stitches st = new Stitches(position);
				ec.addAlive(st);
			} else {
				pastCommands.add("  ERROR: Invalid entity name specified.");
			}
		} else {
			pastCommands.add("  INFO: Cannot spawn while wave is restarting.");
		}
	}
	
	public void mousePressed(GameState gs, int button, int x, int y) {
		if(continuousSpawn && (button == 0)) {
			// Spawn a new enemy of the given type.
			Pair<Float> position = new Pair<Float>((float)x, (float)y);
			spawnEnemy(gs, spawnType, position);
		} else if(continuousSpawn && (button == 1)) {
			pastCommands.add("  INFO: Continuous spawn terminated for entity \"" + spawnType + "\".");
			continuousSpawn = false;
			spawnType = "";
		} else {
			// Append the current x and y position to the end of the current command.
			if(!currentCommand.isEmpty() && currentCommand.charAt(currentCommand.length() - 1) != ' ') currentCommand += " ";
			currentCommand += String.format("%d %d", x, y);
		}
	}

	public void keyPressed(int key, char c) {
		// Handle submitting commands or erasing characters.
		if(key == Input.KEY_ENTER) submitCommand();
		if(key == Input.KEY_BACK) deleting = true;
	}

	public void keyReleased(int key, char c) {
		// Handle key typing.
		if(key == Input.KEY_BACK) deleting = false;
		if(((c == '/') || (c == ' ') || (c == '.') || Character.isLetterOrDigit(c)) && (CONSOLE_FONT.getWidth(currentCommand) < (Globals.WIDTH - 24.0f))) currentCommand += c;
	}

	@Override
	public String getName() {
		return "Console";
	}
	
	@Override
	public String getDescription() {
		return "Console";
	}
}
