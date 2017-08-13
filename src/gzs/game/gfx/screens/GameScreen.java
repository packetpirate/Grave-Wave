package gzs.game.gfx.screens;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import gzs.entities.Entity;
import gzs.entities.Player;
import gzs.entities.enemies.Enemy;
import gzs.entities.enemies.Zumby;
import gzs.game.gfx.Drawable;
import gzs.game.gfx.HUD;
import gzs.game.gfx.Screen;
import gzs.game.info.Globals;
import gzs.game.misc.MouseInfo;
import gzs.game.misc.Pair;
import gzs.game.objects.items.AmmoCrate;
import gzs.game.objects.items.HealthKit;
import gzs.game.objects.items.Item;
import gzs.game.objects.items.SpeedItem;
import gzs.game.state.GameState;
import gzs.game.utils.FileUtilities;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class GameScreen implements Screen {
	private static final Image BACKGROUND = FileUtilities.LoadImage("GZS_Background6.png");
	private static final Image DEATH_OVERLAY = FileUtilities.LoadImage("GZS_DeathScreen.png");
	
	private HUD hud;
	private Map<String, Entity> entities;
	
	private long lastZumby;
	private int zumbyCount;
	
	private long lastHealth;
	private int healthCount;
	
	private long lastAmmo;
	private int ammoCount;
	
	private long lastSpeed;
	private int speedCount;
	
	public GameScreen() {
		hud = new HUD();
		
		entities = new HashMap<String, Entity>();
		entities.put("player", new Player());
		entities.put("crosshairs", new Drawable(FileUtilities.LoadImage("GZS_Crosshair.png"),
												new Pair<Double>(0.0, 0.0)) {
			@Override
			public void update(long cTime) {
				Pair<Double> m = Globals.mouse.getPosition();
				setPosition(m.x, m.y);
			}
		});
		
		zumbyCount = 0;
		lastZumby = 0L;
		
		healthCount = 0;
		lastHealth = 0L;
		
		ammoCount = 0;
		lastAmmo = 0L;
		
		speedCount = 0;
		lastSpeed = 0L;
	}

	@Override
	public void update(long cT) throws Exception {
		// Update all entities.
		Player player = (Player) entities.get("player");
		
		if(Globals.getGSM().getState() != GameState.DEATH) {
			Iterator<Entry<String, Entity>> it = entities.entrySet().iterator();
			while(it.hasNext()) {
				Map.Entry<String, Entity> pair = (Map.Entry<String, Entity>) it.next();
				pair.getValue().update(cT);
				if(pair.getValue() instanceof Enemy) {
					Enemy enemy = (Enemy) pair.getValue();
					enemy.move(player);
					
					// Check if this enemy is touching any of the player's projectiles.
					if(player.checkCollisions(enemy, cT) &&
					   !enemy.isAlive(cT)) it.remove();
					
					// Check if the player is touching the enemy.
					if(enemy.isAlive(cT) && 
					   player.touchingEnemy(enemy)) {
						double damage = enemy.getDamage() / (1_000L / Globals.UPDATE_TIME);
						player.takeDamage(damage);
					}
					
					// If the player has died, transition state.
					if(!player.isAlive()) {
						Globals.getGSM().transition("die");
						break;
					}
				} else if(pair.getValue() instanceof Item) {
					Item item = (Item) pair.getValue();
					if(item.isActive(cT)) {
						player.checkItem(item, cT);
					} else it.remove();
				}
			}
			
			hud.update(player, cT);
			
			{ // Begin Zombie Spawning
				boolean spawn = true;
				long elapsed = cT - lastZumby;
				if(spawn && (elapsed >= 1_000L)) {
					// Spawn a new zumby.
					zumbyCount++;
					lastZumby = cT;
					entities.put(String.format("zumby%3d", zumbyCount), new Zumby(new Pair<Double>(0.0, 0.0)));
				}
			} // End Zombie Spawning
			
			{ // Begin Health Spawning
				boolean spawn = true;
				long elapsed = cT - lastHealth;
				if(spawn && (elapsed >= 20_000L)) {
					// Spawn a new health pack.
					double spawnX = ((Globals.WIDTH * 2) / 3);
					double spawnY = (Globals.HEIGHT / 4);
					healthCount++;
					lastHealth = cT;
					entities.put(String.format("health%3d", healthCount),
								 new HealthKit(new Pair<Double>(spawnX, spawnY), cT));
				}
			} // End Health Spawning
			
			{ // Begin Ammo Spawning
				boolean spawn = true;
				long elapsed = cT - lastAmmo;
				if(spawn && (elapsed >= 20_000L)) {
					// Spawn a new health pack.
					double spawnX = ((Globals.WIDTH * 2) / 3);
					double spawnY = ((Globals.HEIGHT * 3) / 4);
					ammoCount++;
					lastAmmo = cT;
					entities.put(String.format("ammo%3d", ammoCount),
								 new AmmoCrate(new Pair<Double>(spawnX, spawnY), cT));
				}
			} // End Ammo Spawning
			
			{ // Begin Speed Spawning
				boolean spawn = true;
				long elapsed = cT - lastSpeed;
				if(spawn && (elapsed >= 20_000L)) {
					// Spawn a new speed power up.
					double spawnX = (Globals.WIDTH / 4);
					double spawnY = ((Globals.HEIGHT * 3) / 4);
					speedCount++;
					lastSpeed = cT;
					entities.put(String.format("speed%3d", speedCount),
								 new SpeedItem(new Pair<Double>(spawnX, spawnY), cT));
				}
			} // End Speed Spawning
		}
	}

	@Override
	public void render(GraphicsContext gc, long cT) throws Exception {
		Player player = (Player)entities.get("player");
		
		gc.drawImage(BACKGROUND, 0, 0);
		
		{ // Render all entities.
			Iterator<Entry<String, Entity>> it = entities.entrySet().iterator();
			while(it.hasNext()) {
				Map.Entry<String, Entity> pair = (Map.Entry<String, Entity>) it.next();
				pair.getValue().render(gc, cT);
			}
			
			int clip = player.getCurrentWeapon().getClipAmmo();
			int inventory = player.getCurrentWeapon().getInventoryAmmo();
			gc.setFill(Color.WHITE);
			gc.fillText(String.format("Ammo: %d / %d", clip, inventory), 50, 20);
		}
		
		gc.save();
		gc.setGlobalAlpha(0.75);
		gc.setFill(Color.BLACK);
		gc.fillRect(0, 0, Globals.WIDTH, Globals.HEIGHT);
		gc.setGlobalAlpha(0.15);
		gc.setGlobalBlendMode(BlendMode.SRC_OVER);
		player.getFlashlight().render(gc, player, cT);
		gc.restore();
		
		hud.render(gc, player, cT);
		
		if(Globals.getGSM().getState() == GameState.DEATH) {
			gc.save();
			gc.setGlobalAlpha(0.6);
			gc.drawImage(DEATH_OVERLAY, 0, 0);
			gc.restore();
		}
	}

	@Override
	public void dispatchClick(MouseInfo mouse) {
		
	}

	@Override
	public boolean hidesCursor() {
		return (entities.containsKey("crosshairs") && 
			   (entities.get("crosshairs") != null));
	}

	@Override
	public void dispatchScroll(int direction) {
		Player player = (Player) entities.get("player");
		if(player.activeWeapons() > 1) {
			player.weaponRotate(direction);
			hud.queueWeaponCycle();
		}
	}
}
