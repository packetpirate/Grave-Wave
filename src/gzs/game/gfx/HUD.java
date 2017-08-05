package gzs.game.gfx;

import java.util.List;

import gzs.entities.Player;
import gzs.game.info.Globals;
import gzs.game.misc.Pair;
import gzs.game.objects.weapons.Weapon;
import gzs.game.utils.FileUtilities;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class HUD {
	private static final Font FONT_EUROSTILE = FileUtilities.LoadFont("eurostile.oblique.ttf", 20);
	private static final long WEAPONS_DISPLAY_TIME = 2000L;
	
	private static final Pair<Double> HEALTH_ORIGIN = new Pair<Double>(10.0, 10.0);
	private static final Pair<Double> WEAPONS_ORIGIN = new Pair<Double>(10.0, (Globals.HEIGHT - 64.0));
	
	private long lastWeaponSwitch;
	private boolean cycleWeapons;
	public void queueWeaponCycle() { cycleWeapons = true; }
	
	public HUD() {
		lastWeaponSwitch = 0L;
		cycleWeapons = false;
	}
	
	private boolean displayWeapons(long cTime) {
		long elapsed = cTime - lastWeaponSwitch;
		return (elapsed <= WEAPONS_DISPLAY_TIME);
	}
	
	public void update(Player player, long cTime) {
		if(cycleWeapons) {
			cycleWeapons = false;
			lastWeaponSwitch = cTime;
		}
	}
	
	public void render(GraphicsContext gc, Player player, long cTime) {
		{ // Render the health bar.
			double currentHealth = player.getDoubleAttribute("health");
			double maxHealth = player.getDoubleAttribute("maxHealth");
			double percentage = currentHealth / maxHealth;
			
			gc.setFill(Color.BLACK);
			gc.setStroke(Color.LIGHTGRAY);
			gc.fillRect(HEALTH_ORIGIN.x, HEALTH_ORIGIN.y, 156.0, 26.0);
			gc.strokeRect(HEALTH_ORIGIN.x, HEALTH_ORIGIN.y, 156.0, 26.0);
			
			gc.setFill(Color.RED);
			gc.setStroke(Color.LIGHTSLATEGRAY);
			gc.fillRect((HEALTH_ORIGIN.x + 3.0), (HEALTH_ORIGIN.y + 3.0), 
						(percentage * 150.0), 20.0);
			gc.strokeRect((HEALTH_ORIGIN.x + 3.0), (HEALTH_ORIGIN.y + 3.0), 
						  (percentage * 150.0), 20.0);
			
			String healthText = String.format("HP: %d / %d", (int)currentHealth, (int)maxHealth);
			gc.save();
			gc.setTextAlign(TextAlignment.CENTER);
			gc.setTextBaseline(VPos.TOP);
			gc.setGlobalBlendMode(BlendMode.ADD);
			gc.setFill(Color.WHITE);
			gc.fillText(healthText, (HEALTH_ORIGIN.x + 78.0), (HEALTH_ORIGIN.y + 5.0));
			gc.restore();
		} // End health bar rendering.
		
		if(displayWeapons(cTime)) {
			// Render the three weapons (or the player's active weapons - 1) on 
			// top of the current weapon.
			List<Weapon> weapons = player.getWeapons();
			int wi = player.getWeaponIndex() - 1;
			double startY = WEAPONS_ORIGIN.y;
			for(int i = 0; i < Math.min(3, player.activeWeapons()); i++) {
				Weapon w = weapons.get(Math.floorMod((wi + (i + 1)), weapons.size()));
				double cy = (startY - (i * 54.0));
				
				gc.setFill(Color.LIGHTGRAY);
				gc.setStroke(Color.LIGHTSLATEGRAY);
				gc.fillRect(WEAPONS_ORIGIN.x, cy, 54.0, 54.0);
				gc.strokeRect(WEAPONS_ORIGIN.x, cy, 54.0, 54.0);
				
				gc.setFill(Color.GRAY);
				gc.setStroke(Color.LIGHTSLATEGRAY);
				gc.fillRect((WEAPONS_ORIGIN.x + 3.0), (cy + 3.0), 48.0, 48.0);
				gc.strokeRect((WEAPONS_ORIGIN.x + 3.0), (cy + 3.0), 48.0, 48.0);
				
				gc.drawImage(w.getInventoryIcon(), (WEAPONS_ORIGIN.x + 3.0), (cy + 3.0));
			}
		}
		
		{ // Render the weapons loadout.
			gc.setFill(Color.LIGHTGRAY);
			gc.setStroke(Color.LIGHTSLATEGRAY);
			gc.fillRect(WEAPONS_ORIGIN.x, WEAPONS_ORIGIN.y, 150.0, 54.0);
			gc.strokeRect(WEAPONS_ORIGIN.x, WEAPONS_ORIGIN.y, 150.0, 54.0);
			
			gc.setFill(Color.GRAY);
			gc.setStroke(Color.LIGHTSLATEGRAY);
			gc.fillRect((WEAPONS_ORIGIN.x + 3.0), (WEAPONS_ORIGIN.y + 3.0), 48.0, 48.0);
			gc.strokeRect((WEAPONS_ORIGIN.x + 3.0), (WEAPONS_ORIGIN.y + 3.0), 48.0, 48.0);
			
			gc.drawImage(player.getCurrentWeapon().getInventoryIcon(), 
						(WEAPONS_ORIGIN.x + 3.0), (WEAPONS_ORIGIN.y + 3.0));
			
			// Render the reloading bar, if the player is reloading.
			if(player.getCurrentWeapon().isReloading(cTime)) {
				double percentage = 1.0 - player.getCurrentWeapon().getReloadTime(cTime);
				double height = percentage * 48.0;
				double y = (WEAPONS_ORIGIN.y + 3.0 + (48.0 - height));
				gc.save();
				gc.setGlobalAlpha(0.5);
				gc.setFill(Color.WHITE);
				gc.fillRect((WEAPONS_ORIGIN.x + 3.0), y, 48.0, height);
				gc.restore();
			}
			
			String ammoText = String.format("%d / %d", 
											player.getCurrentWeapon().getClipAmmo(),
											player.getCurrentWeapon().getInventoryAmmo());
			gc.save();
			gc.setTextAlign(TextAlignment.CENTER);
			gc.setTextBaseline(VPos.TOP);
			gc.setFont(HUD.FONT_EUROSTILE);
			gc.setFill(Color.BLACK);
			gc.fillText(ammoText, (WEAPONS_ORIGIN.x + 99.0), (WEAPONS_ORIGIN.y + 18.0));
			gc.restore();
		} // End weapons loadout rendering.
	}
}