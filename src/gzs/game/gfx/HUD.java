package gzs.game.gfx;

import gzs.entities.Player;
import gzs.game.info.Globals;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

public class HUD {
	public HUD() {
		
	}
	
	public void update(Player player, long cTime) {
		
	}
	
	public void render(GraphicsContext gc, Player player, long cTime) {
		{ // Render the health bar.
			double currentHealth = player.getDoubleAttribute("health");
			double maxHealth = player.getDoubleAttribute("maxHealth");
			double percentage = currentHealth / maxHealth;
			
			gc.setFill(Color.BLACK);
			gc.setStroke(Color.LIGHTGRAY);
			gc.fillRect(10.0, 10.0, 156.0, 26.0);
			gc.strokeRect(10.0, 10.0, 156.0, 26.0);
			
			gc.setFill(Color.RED);
			gc.setStroke(Color.LIGHTSLATEGRAY);
			gc.fillRect(13.0, 13.0, (percentage * 150.0), 20.0);
			gc.strokeRect(13.0, 13.0, (percentage * 150.0), 20.0);
			
			String healthText = String.format("HP: %d / %d", (int)currentHealth, (int)maxHealth);
			gc.save();
			gc.setTextAlign(TextAlignment.CENTER);
			gc.setTextBaseline(VPos.TOP);
			gc.setGlobalBlendMode(BlendMode.ADD);
			gc.setFill(Color.WHITE);
			gc.fillText(healthText, 88.0, 15.0);
			gc.restore();
		} // End health bar rendering.
		
		// Render the reloading bar, if the player is reloading.
		// TODO: Change this to render a shadow mask above the image of the weapon.
		if(player.getCurrentWeapon().isReloading(cTime)) {
//			double topLeftX = player.getPosition().x - (player.getImage().getWidth() / 4);
//			double topLeftY = player.getPosition().y + player.getImage().getHeight() + 5;
//			double width = player.getCurrentWeapon().getReloadTime(cTime) * (player.getImage().getWidth() / 2);
//			gc.setFill(Color.WHITE);
//			gc.setStroke(Color.LIGHTSLATEGRAY);
//			gc.fillRect(topLeftX, topLeftY, width, 5);
//			gc.strokeRect(topLeftX, topLeftY, width, 5);
		}
		
		{ // Render the weapons loadout.
			double topLeftY = Globals.HEIGHT - 64.0;
			gc.setFill(Color.LIGHTGRAY);
			gc.setStroke(Color.LIGHTSLATEGRAY);
			gc.fillRect(10.0, topLeftY, 150.0, 54.0);
			gc.strokeRect(10.0, topLeftY, 150.0, 54.0);
			
			gc.setFill(Color.GRAY);
			gc.setStroke(Color.LIGHTSLATEGRAY);
			gc.fillRect(13.0, (topLeftY + 3.0), 48.0, 48.0);
			gc.strokeRect(13.0, (topLeftY + 3.0), 48.0, 48.0);
			
			gc.drawImage(player.getCurrentWeapon().getInventoryIcon(), 13.0, (topLeftY + 3.0));
			
			String ammoText = String.format("Ammo: %d / %d", 
											player.getCurrentWeapon().getClipAmmo(),
											player.getCurrentWeapon().getInventoryAmmo());
			gc.save();
			gc.setTextAlign(TextAlignment.CENTER);
			gc.setTextBaseline(VPos.TOP);
			gc.setFill(Color.BLACK);
			gc.fillText(ammoText, 109.0, (topLeftY + 3.0));
			gc.restore();
		} // End weapons loadout rendering.
		
		
	}
}