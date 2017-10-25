package com.gzsr.gfx;

import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.util.FontUtils;

import com.gzsr.AssetManager;
import com.gzsr.Globals;
import com.gzsr.entities.Player;
import com.gzsr.misc.Pair;
import com.gzsr.objects.weapons.Weapon;
import com.gzsr.status.Status;
import com.gzsr.status.StatusEffect;

public class HUD {
	private static final long WEAPONS_DISPLAY_TIME = 2000L;
	
	private static final Pair<Float> HEALTH_ORIGIN = new Pair<Float>(10.0f, 10.0f);
	private static final Pair<Float> WEAPONS_ORIGIN = new Pair<Float>(10.0f, (Globals.HEIGHT - 64.0f));
	private static final Pair<Float> EXP_ORIGIN = new Pair<Float>(10.0f, 41.0f);
	private static final Pair<Float> STATUS_ORIGIN = new Pair<Float>(10.0f, 62.0f);
	
	private long lastWeaponSwitch;
	private boolean cycleWeapons;
	public void queueWeaponCycle() { cycleWeapons = true; }
	
	public HUD() throws SlickException {
		lastWeaponSwitch = 0L;
		cycleWeapons = false;
		
		AssetManager assets = AssetManager.getManager();
		assets.addFont("eurostile.oblique", "fonts/eurostile.oblique.ttf", 16, false, false);
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
	
	public void render(Graphics g, Player player, long cTime) {
		AssetManager assets = AssetManager.getManager();
		{ // Render the health bar.
			float currentHealth = (float)player.getDoubleAttribute("health");
			float maxHealth = (float)player.getDoubleAttribute("maxHealth");
			float percentage = currentHealth / maxHealth;
			
			g.setColor(Color.black);
			g.fillRect(HEALTH_ORIGIN.x, HEALTH_ORIGIN.y, 156.0f, 26.0f);
			g.setColor(Color.lightGray);
			g.drawRect(HEALTH_ORIGIN.x, HEALTH_ORIGIN.y, 156.0f, 26.0f);
			
			g.setColor(Color.red);
			g.fillRect((HEALTH_ORIGIN.x + 3.0f), 
					   (HEALTH_ORIGIN.y + 3.0f), 
					   (percentage * 150.0f), 20.0f);
			g.setColor(Color.lightGray);
			g.drawRect((HEALTH_ORIGIN.x + 3.0f), 
					   (HEALTH_ORIGIN.y + 3.0f), 
					   (percentage * 150.0f), 20.0f);
			
			String healthText = String.format("HP: %d / %d", (int)currentHealth, (int)maxHealth);
			FontUtils.drawCenter(g.getFont(), healthText, (int)HEALTH_ORIGIN.x.floatValue(), (int)(HEALTH_ORIGIN.y.floatValue() + 5.0f), 156);
		} // End health bar rendering.
		
		{ // Begin experience bar rendering.
			float currentExp = (float)player.getIntAttribute("experience");
			float expToLevel = (float)player.getIntAttribute("expToLevel");
			float percentage = currentExp / expToLevel;
			
			g.setColor(Color.black);
			g.fillRect(EXP_ORIGIN.x, EXP_ORIGIN.y, 156.0f, 16.0f);
			g.setColor(Color.lightGray);
			g.drawRect(EXP_ORIGIN.x, EXP_ORIGIN.y, 156.0f, 16.0f);
			
			if(percentage != 0.0f) {
				g.setColor(Color.green);
				g.fillRect((EXP_ORIGIN.x + 3.0f), (EXP_ORIGIN.y + 3.0f), 
						   (percentage * 150.0f), 10.0f);
				g.setColor(Color.lightGray);
				g.drawRect((EXP_ORIGIN.x + 3.0f), (EXP_ORIGIN.y + 3.0f), 
						   (percentage * 150.0f), 10.0f);
			}
		} // End experience bar rendering.
		
		if(displayWeapons(cTime)) {
			// Render the three weapons (or the player's active weapons - 1) on top of the current weapon.
			List<Weapon> weapons = player.getWeapons();
			int wi = player.getWeaponIndex() - 1;
			float startY = WEAPONS_ORIGIN.y;
			for(int i = 0; i < Math.min(3, player.activeWeapons()); i++) {
				Weapon w = weapons.get(Math.floorMod((wi + (i + 1)), weapons.size()));
				float cy = (startY - (i * 54.0f));
				
				g.setColor(new Color(0.7f, 0.7f, 0.7f));
				g.fillRect(WEAPONS_ORIGIN.x, cy, 54.0f, 54.0f);
				g.setColor(Color.black);
				g.drawRect(WEAPONS_ORIGIN.x, cy, 54.0f, 54.0f);
				
				g.setColor(Color.gray);
				g.fillRect((WEAPONS_ORIGIN.x + 3.0f), (cy + 3.0f), 48.0f, 48.0f);
				g.setColor(Color.black);
				g.drawRect((WEAPONS_ORIGIN.x + 3.0f), (cy + 3.0f), 48.0f, 48.0f);
				
				g.drawImage(w.getInventoryIcon(), (WEAPONS_ORIGIN.x + 3.0f), (cy + 3.0f));
			}
		}
		
		{ // Render the weapons loadout.
			g.setColor(new Color(0.7f, 0.7f, 0.7f));
			g.fillRect(WEAPONS_ORIGIN.x, WEAPONS_ORIGIN.y, 150.0f, 54.0f);
			g.setColor(Color.black);
			g.drawRect(WEAPONS_ORIGIN.x, WEAPONS_ORIGIN.y, 150.0f, 54.0f);
			
			g.setColor(Color.gray);
			g.fillRect((WEAPONS_ORIGIN.x + 3.0f), (WEAPONS_ORIGIN.y + 3.0f), 48.0f, 48.0f);
			g.setColor(Color.black);
			g.drawRect((WEAPONS_ORIGIN.x + 3.0f), (WEAPONS_ORIGIN.y + 3.0f), 48.0f, 48.0f);
			
			g.drawImage(player.getCurrentWeapon().getInventoryIcon(),
						(WEAPONS_ORIGIN.x + 3.0f), (WEAPONS_ORIGIN.y + 3.0f));
			
			// Render the reloading bar, if the player is reloading.
			if(player.getCurrentWeapon().isReloading(cTime)) {
				float percentage = 1.0f - (float)player.getCurrentWeapon().getReloadTime(cTime);
				float height = percentage * 48.0f;
				float y = (WEAPONS_ORIGIN.y + 3.0f + (48.0f - height));
				
				g.setColor(new Color(1.0f, 1.0f, 1.0f, 0.5f));
				g.fillRect((WEAPONS_ORIGIN.x + 3.0f), y, 48.0f, height);
			}
			
			if(player.hasStatus(Status.UNLIMITED_AMMO)) {
				Image unlimitedAmmo = assets.getImage("GZS_UnlimitedAmmo");
				float x = WEAPONS_ORIGIN.x + 100.0f - (unlimitedAmmo.getWidth() / 2);
				float y = WEAPONS_ORIGIN.y + 27.0f - (unlimitedAmmo.getHeight() / 2);
				g.drawImage(unlimitedAmmo, x, y);
			} else {
				String ammoText = String.format("%d / %d", 
									player.getCurrentWeapon().getClipAmmo(),
									player.getCurrentWeapon().getInventoryAmmo());
				FontUtils.drawCenter(assets.getFont("eurostile.oblique"), ammoText, 
									 (int)(WEAPONS_ORIGIN.x + 54.0f), 
									 (int)(WEAPONS_ORIGIN.y + 18.0f), 93, Color.black);
			}
		} // End weapons loadout rendering.
		
		{ // Begin Status Effects rendering.
			float xPlus = 0.0f;
			List<StatusEffect> statusEffects = player.getStatuses();
			for(StatusEffect status : statusEffects) {
				// Render each individual status underneath the health and experience bars.
				Image img = status.getIcon();
				float percentageTimeLeft = status.getPercentageTimeLeft(cTime);
				g.drawImage(img, (STATUS_ORIGIN.x + xPlus), STATUS_ORIGIN.y, 
							new Color(1.0f, 1.0f, 1.0f, percentageTimeLeft));
				xPlus += img.getWidth() + 5.0f;
			}
		} // End Status Effects rendering.
	}
}
