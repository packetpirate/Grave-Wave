package com.grave.gfx.ui.hud;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.state.BasicGameState;

import com.grave.Globals;
import com.grave.MusicPlayer;
import com.grave.entities.Entity;
import com.grave.entities.Player;
import com.grave.entities.enemies.BigMama;
import com.grave.entities.enemies.ElSalvo;
import com.grave.entities.enemies.Enemy;
import com.grave.entities.enemies.EnemyController;
import com.grave.entities.enemies.Gasbag;
import com.grave.entities.enemies.Glorp;
import com.grave.entities.enemies.Prowler;
import com.grave.entities.enemies.Rotdog;
import com.grave.entities.enemies.Starfright;
import com.grave.entities.enemies.Upchuck;
import com.grave.entities.enemies.Zumby;
import com.grave.entities.enemies.bosses.Aberration;
import com.grave.entities.enemies.bosses.Stitches;
import com.grave.entities.enemies.bosses.Zombat;
import com.grave.gfx.Camera;
import com.grave.gfx.Layers;
import com.grave.misc.Pair;
import com.grave.objects.crafting.Resources;
import com.grave.objects.items.AmmoCrate;
import com.grave.objects.items.Armor;
import com.grave.objects.items.CritChanceItem;
import com.grave.objects.items.ExpMultiplierItem;
import com.grave.objects.items.ExtraLife;
import com.grave.objects.items.HealthKit;
import com.grave.objects.items.InvulnerableItem;
import com.grave.objects.items.NightVisionItem;
import com.grave.objects.items.SpeedItem;
import com.grave.objects.items.UnlimitedAmmoItem;
import com.grave.objects.weapons.Explosion;
import com.grave.states.GameState;

public class Console implements Entity {
	private static final Color CONSOLE_BACKGROUND = Color.lightGray;
	private static final Color CONSOLE_BORDER = Color.black;
	private static final Color CONSOLE_TEXT = Color.white;
	private static final Color CONSOLE_PASTTEXT = Color.black;
	private static final Color CONSOLE_TEXTBOX = Color.darkGray;
	private static final Color CONSOLE_TEXTBORDER = Color.black;
	private static final TrueTypeFont CONSOLE_FONT = new TrueTypeFont(new Font("Lucida Console", Font.PLAIN, 12), true);
	private static final long DELETE_FREQ = 4L;

	private GameState gs;

	private int commandIndex;
	private String currentCommand;
	private List<String> pastCommands;
	private List<String> consoleLines;

	private long pauseTime; // What time was the console opened?
	public void setPauseTime(long time) { pauseTime = time; }

	private boolean deleting;
	private long lastDelete;

	private boolean continuousSpawn;
	private String spawnType;

	public Console(GameState gs_, GameContainer gc_) {
		this.gs = gs_;

		this.commandIndex = 0;
		this.currentCommand = "";
		this.pastCommands = new ArrayList<String>();
		this.consoleLines = new ArrayList<String>();

		this.pauseTime = 0L;
		this.deleting = false;
		this.lastDelete = 0L;
		this.continuousSpawn = false;
	}

	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
		if(deleting && (cTime > (lastDelete + Console.DELETE_FREQ)) && (currentCommand.length() > 0)) {
			deleteLastCommandChar();
			lastDelete = cTime;
		}
	}

	@Override
	public void render(GameState gs, Graphics g, long cTime) {
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
		if(!consoleLines.isEmpty()) {
			int command = (consoleLines.size() > 10) ? (consoleLines.size() - 10) : 0;

			g.setColor(CONSOLE_PASTTEXT);
			for(int i = 0; i < Math.min(consoleLines.size(), 10); i++) {
				float y = (Globals.HEIGHT - 39.0f - (19.0f * Math.min(consoleLines.size(), 10))) + (i * 19.0f);
				g.drawString(consoleLines.get(command), 20.0f, (y + 4.0f));
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

					pastCommands.add(currentCommand);
					consoleLines.add(String.format("> %s", currentCommand));
					commandIndex = pastCommands.size();

					EnemyController ec = EnemyController.getInstance();

					if(command.equals("help") && ((args == 0) || (args == 1))) {
						if(args == 0) {
							consoleLines.add("  HELP: Use this command to get information about other console commands.");
							consoleLines.add("    Usage: /help command");
							consoleLines.add("    Example: /help spawn");
							consoleLines.add("    Commands: spawn item resource set levelup explode killall music ec");
							consoleLines.add("    Prints the help information for the given command.");
						} else if(args == 1) {
							String cmd = tokens[1];
							help(cmd);
						}
					} else if(command.equals("spawn")) {
						// Requires entity name and (x, y) coordinates.
						if(args == 1) {
							spawnType = tokens[1];
							continuousSpawn = true;
							consoleLines.add("  INFO: Continuous spawn started for entity \"" + spawnType + "\".");
						} else if(args == 3) {
							String entityName = tokens[1];
							float x = Float.parseFloat(tokens[2]);
							float y = Float.parseFloat(tokens[3]);
							Pair<Float> position = new Pair<Float>(x, y);

							spawnEnemy(gs, entityName, position);
						}
					} else if(command.equals("item") && (args == 3)) {
						String itemName = tokens[1];
						Pair<Float> pos = new Pair<Float>(Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3]));
						long cTime = gs.getTime();
						//int id = Globals.generateEntityID();

						if(itemName.equals("ammo")) {
							AmmoCrate ac = new AmmoCrate(pos, cTime);
							gs.getLevel().addEntity("ammo", ac);
						} else if(itemName.equals("health")) {
							HealthKit hk = new HealthKit(pos, cTime);
							gs.getLevel().addEntity("health", hk);
						} else if(itemName.equals("armor")) {
							Armor ar = new Armor(Armor.Type.REINFORCED, pos, cTime);
							gs.getLevel().addEntity("armor", ar);
						} else if(itemName.equals("life")) {
							ExtraLife el = new ExtraLife(pos, cTime);
							gs.getLevel().addEntity("life", el);
						} else if(itemName.equals("critchance")) {
							CritChanceItem crit = new CritChanceItem(pos, cTime);
							gs.getLevel().addEntity("crit", crit);
						} else if(itemName.equals("expmult")) {
							ExpMultiplierItem exp = new ExpMultiplierItem(pos, cTime);
							gs.getLevel().addEntity("exp", exp);
						} else if(itemName.equals("invulnerability")) {
							InvulnerableItem inv = new InvulnerableItem(pos, cTime);
							gs.getLevel().addEntity("invulnerability", inv);
						} else if(itemName.equals("nightvision")) {
							NightVisionItem night = new NightVisionItem(pos, cTime);
							gs.getLevel().addEntity("nightvision", night);
						} else if(itemName.equals("speed")) {
							SpeedItem spd = new SpeedItem(pos, cTime);
							gs.getLevel().addEntity("speed", spd);
						} else if(itemName.equals("unlimitedammo")) {
							UnlimitedAmmoItem una = new UnlimitedAmmoItem(pos, cTime);
							gs.getLevel().addEntity("unlimAmmo", una);
						} else {
							consoleLines.add("  ERROR: Invalid item name specified.");
						}
					} else if(command.equals("resource") && (args == 2)) {
						String resource = tokens[1];
						int amount = Integer.parseInt(tokens[2]);

						if(resource.equals("metal")) Player.getPlayer().getResources().add(Resources.METAL, amount);
						else if(resource.equals("cloth")) Player.getPlayer().getResources().add(Resources.CLOTH, amount);
						else if(resource.equals("glass")) Player.getPlayer().getResources().add(Resources.GLASS, amount);
						else if(resource.equals("wood")) Player.getPlayer().getResources().add(Resources.WOOD, amount);
						else if(resource.equals("electronics")) Player.getPlayer().getResources().add(Resources.ELECTRONICS, amount);
						else if(resource.equals("power")) Player.getPlayer().getResources().add(Resources.POWER, amount);
						else consoleLines.add("  ERROR: Invalid resource specified.");
					} else if(command.equals("set") && (args == 2)) {
						String attributeName = tokens[1];

						if(attributeName.equals("health")) {
							double health = Double.parseDouble(tokens[2]);
							Player.getPlayer().getAttributes().set("health", health);
						} else if(attributeName.equals("money")) {
							int money = Integer.parseInt(tokens[2]);
							Player.getPlayer().getAttributes().set("money", money);
						} else if(attributeName.equals("wave")) {
							int wave = Integer.parseInt(tokens[2]);
							EnemyController.getInstance().setWave(wave, pauseTime);
						} else {
							consoleLines.add("  ERROR: Invalid attribute specified.");
						}
					} else if(command.equals("levelup") && (args == 1)) {
						int levels = Integer.parseInt(tokens[1]);
						for(int i = 0; i < levels; i++) {
							int toLevel = Player.getPlayer().getAttributes().getInt("expToLevel");
							Player.getPlayer().addExperience(gs, toLevel, pauseTime, false);
						}
					} else if(command.equals("explode") && (args == 4)) {
						try {
							float x = Float.parseFloat(tokens[1]);
							float y = Float.parseFloat(tokens[2]);
							double damage = Double.parseDouble(tokens[3]);
							float radius = Float.parseFloat(tokens[4]);
							//int id = Globals.generateEntityID();

							Explosion exp = new Explosion(Explosion.Type.NORMAL, "GZS_Explosion",
														  new Pair<Float>(x, y), damage, false,
														  10.0f, radius, pauseTime);
							gs.getLevel().addEntity(exp.getTag(), exp);
						} catch(NumberFormatException nfe) {
							consoleLines.add("  ERROR: Invalid parameters specified for /explode command.");
						}
					} else if(command.equals("killall") && (args == 0)) {
						ec.getAliveEnemies().clear();
					} else if(command.equals("music") && (args == 1)) {
						String action = tokens[1];

						if(action.equals("pause")) {
							MusicPlayer.getInstance().pause();
						} else if(action.equals("resume")) {
							MusicPlayer.getInstance().resume();
						} else if(action.equals("reset")) {
							MusicPlayer.getInstance().reset();

							try {
								MusicPlayer.getInstance().nextSong();
							} catch(SlickException se) {
								se.printStackTrace();
								System.err.println("Error with console command: /" + command);
							}
						} else if(action.equals("next")) {
							try {
								MusicPlayer.getInstance().nextSong();
							} catch(SlickException se) {
								se.printStackTrace();
								System.err.println("Error with console command: /" + command);
							}
						}
					} else if(command.equals("trashman")) {
						Player player = Player.getPlayer();

						// Level Up 29 Times
						for(int i = 0; i < 29; i++) {
							int toLevel = player.getAttributes().getInt("expToLevel");
							player.addExperience(gs, toLevel, pauseTime, false);
						}

						// Give $1,000,000
						player.getAttributes().set("money", 1_000_000);

						// Give 200 of each resource.
						player.getResources().add(Resources.METAL, 200);
						player.getResources().add(Resources.CLOTH, 200);
						player.getResources().add(Resources.GLASS, 200);
						player.getResources().add(Resources.WOOD, 200);
						player.getResources().add(Resources.ELECTRONICS, 200);
						player.getResources().add(Resources.POWER, 200);
					} else if(command.equals("debug") && (args == 0)) {
						// Toggle Debug Mode
						Globals.debug = !Globals.debug;

						// Print message telling the user if debug mode is now on or off.
						consoleLines.add(String.format("DEBUG Mode: %s", (Globals.debug ? "ON" : "OFF")));
					} else if(command.equals("ec") && (args == 0)) {
						// Print information about remaining enemies in each enemy controller list.
						List<Enemy> alive = ec.getAliveEnemies();
						int immediate = ec.getImmediateEnemies().size();

						String str = String.format("Alive: %d, Immediate: %d", alive.size(), immediate);
						consoleLines.add(str);

						// Print 5 of the remaining alive enemies, if any.
						if(alive.size() > 0) {
							for(int i = 0; i < Math.min(alive.size(), 5); i++) {
								String pr = alive.get(i).print();
								consoleLines.add(pr);
							}
						}
					} else {
						consoleLines.add(String.format("  ERROR: Unrecognized command name: \"%s\"", command));
					}
				} else {
					consoleLines.add("  ERROR: Invalid command format!");
				}
			} else {
				// Garbage text. Just add it to the previous commands list.
				consoleLines.add(currentCommand);
			}
		}

		currentCommand = "";
	}

	private void help(String command) {
		consoleLines.add(String.format("  HELP: /%s", command));
		if(command.equals("spawn")) {
			consoleLines.add("    Usage: /spawn entityName x y");
			consoleLines.add("    Example: /spawn zumby 200 200");
			consoleLines.add("    The entityName argument should be all one word in lowercase.");
			consoleLines.add("    You can also specify just the entityName and submit the command to enter continuous spawn mode.");
			consoleLines.add("    In continuous spawn mode, just click on the screen where you want to spawn an enemy.");
		} else if(command.equals("item")) {
			consoleLines.add("    Usage: /item name x y");
			consoleLines.add("    Example: /item health 512 384");
			consoleLines.add("    Items: health ammo armor life critchance expmult invulnerability nightvision speed unlimitedammo");
		} else if(command.equals("resource")) {
			consoleLines.add("    Usage: /resource name amount");
			consoleLines.add("    Example: /resource metal 20");
			consoleLines.add("    Resources: metal cloth glass wood electronics power");
		} else if(command.equals("set")) {
			consoleLines.add("    Usage: /set attribute value");
			consoleLines.add("    Example: /set health 100");
			consoleLines.add("    Attributes: health money wave");
		} else if(command.equals("levelup")) {
			consoleLines.add("    Usage: /levelup times");
			consoleLines.add("    Example: /levelup 5");
			consoleLines.add("    Levels you up a number of times equal to the argument given.");
		} else if(command.equals("explode")) {
			consoleLines.add("    Usage: /explode x y damage radius");
			consoleLines.add("    Example: /explode 200 300 20 150");
			consoleLines.add("    Creates an explosion at the (x, y) coordinates doing the given damage to all enemies in the given radius.");
		} else if(command.equals("killall")) {
			consoleLines.add("    Kills all enemies on the screen, effectively ending the wave.");
		} else if(command.equals("music")) {
			consoleLines.add("    Usage: /music [pause|resume|reset|next]");
			consoleLines.add("    Example: /music pause");
			consoleLines.add("    Manipulates the music player. All commands affect the current song except reset.");
			consoleLines.add("    The reset command will restart the entire soundtrack.");
		} else if(command.equals("ec")) {
			consoleLines.add("    Prints information about currently living enemies. Used to debug waves not ending properly.");
		} else {
			consoleLines.add("    ERROR: Invalid command specified.");
		}
	}

	private void spawnEnemy(GameState gs, String entityType, Pair<Float> position) {
		EnemyController ec = EnemyController.getInstance();

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
				BigMama bm = new BigMama(position);
				ec.addAlive(bm);
			} else if(entityType.equals("starfright")) {
				Starfright sf = new Starfright(position);
				ec.addAlive(sf);
			} else if(entityType.equals("elsalvo")) {
				ElSalvo es = new ElSalvo(position);
				ec.addAlive(es);
			} else if(entityType.equals("prowler")) {
				Prowler pr = new Prowler(position);
				ec.addAlive(pr);
			} else if(entityType.equals("glorp")) {
				Glorp gl = new Glorp(position);
				ec.addAlive(gl);
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
				consoleLines.add("  ERROR: Invalid entity name specified.");
			}
		} else {
			consoleLines.add("  INFO: Cannot spawn while wave is restarting.");
		}
	}

	public void mousePressed(GameState gs, int button, int x, int y) {
		Camera camera = Camera.getCamera();
		float offX = camera.getOffset().x;
		float offY = camera.getOffset().y;

		if(continuousSpawn && (button == 0)) {
			// Spawn a new enemy of the given type.
			Pair<Float> position = new Pair<Float>(offX + x, offY + y);
			spawnEnemy(gs, spawnType, position);
		} else if(continuousSpawn && (button == 1)) {
			consoleLines.add("  INFO: Continuous spawn terminated for entity \"" + spawnType + "\".");
			continuousSpawn = false;
			spawnType = "";
		} else {
			// Append the current x and y position to the end of the current command.
			if(!currentCommand.isEmpty() && currentCommand.charAt(currentCommand.length() - 1) != ' ') currentCommand += " ";
			currentCommand += String.format("%d %d", (int)(offX + x), (int)(offY + y));
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

		if(key == Input.KEY_UP) {
			if(commandIndex > 0) commandIndex--;
			currentCommand = pastCommands.get(commandIndex);
		}

		if(key == Input.KEY_DOWN) {
			if(commandIndex < (pastCommands.size() - 1)) commandIndex++;
			currentCommand = pastCommands.get(commandIndex);
		}
	}

	@Override
	public String getName() { return "Console"; }

	@Override
	public String getTag() { return "console"; }

	@Override
	public String getDescription() { return "Console"; }

	@Override
	public int getLayer() { return Layers.HUD.val(); }
}
