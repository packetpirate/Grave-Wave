package gzs.game.gfx;

import gzs.entities.Player;
import gzs.game.info.Globals;
import gzs.game.utils.FileUtilities;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class HUD {
	private static Font FONT_EUROSTILE = FileUtilities.LoadFont("eurostile.oblique.ttf", 20);
	
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
			
			// Render the reloading bar, if the player is reloading.
			if(player.getCurrentWeapon().isReloading(cTime)) {
				double percentage = 1.0 - player.getCurrentWeapon().getReloadTime(cTime);
				double height = percentage * 48.0;
				double y = (topLeftY + 3.0 + (48.0 - height));
				gc.save();
				gc.setGlobalAlpha(0.5);
				gc.setFill(Color.WHITE);
				gc.fillRect(13.0, y, 48.0, height);
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
			gc.fillText(ammoText, 109.0, (topLeftY + 18.0));
			gc.restore();
		} // End weapons loadout rendering.
	}
	
	public void rotateWeapon(Player player, int direction, long cTime) {
		
	}
}