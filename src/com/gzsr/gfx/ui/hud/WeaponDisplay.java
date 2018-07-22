package com.gzsr.gfx.ui.hud;

import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.util.FontUtils;

import com.gzsr.AssetManager;
import com.gzsr.entities.Entity;
import com.gzsr.entities.Player;
import com.gzsr.gfx.Layers;
import com.gzsr.misc.Pair;
import com.gzsr.objects.weapons.Weapon;
import com.gzsr.objects.weapons.ranged.RangedWeapon;
import com.gzsr.status.Status;

public class WeaponDisplay implements Entity {
	private static final long WEAPONS_DISPLAY_TIME = 2000L;
	private static final Color GRAYISH = new Color(0.7f, 0.7f, 0.7f);
	
	private Pair<Float> position;
	
	private Rectangle bounds;
	
	private boolean cycleWeapons;
	private long lastWeaponSwitch;
	public void queueWeaponCycle() { cycleWeapons = true; }
	
	public WeaponDisplay(Pair<Float> position_) {
		this.position = position_;
		
		this.bounds = new Rectangle(position.x, position.y, 150.0f, 54.0f);
		
		this.lastWeaponSwitch = 0L;
		this.cycleWeapons = false;
	}

	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
		if(cycleWeapons) {
			cycleWeapons = false;
			lastWeaponSwitch = cTime;
		}
	}

	@Override
	public void render(Graphics g, long cTime) {
		Player player = Player.getPlayer();
		Weapon w = player.getCurrentWeapon();
		boolean touchingPlayer = intersects(player);
		
		if(displayWeapons(cTime)) {
			// Render the three weapons (or the Player.getPlayer()'s active weapons - 1) on top of the current weapon.	
			List<Weapon> weapons = player.getWeapons();
			int wi = player.getWeaponIndex() - 1;
			float startY = position.y;
			
			for(int i = 0; i < Math.min(3, player.getWeapons().size()); i++) {
				Weapon cWeapon = weapons.get(Math.floorMod((wi + (i + 1)), weapons.size()));
				Image icon = cWeapon.getInventoryIcon();
				float cy = (startY - (i * 54.0f));
				float scale = (48.0f / (float)icon.getWidth());
				
				changeColor(g, GRAYISH, touchingPlayer);
				g.fillRect(position.x, cy, 54.0f, 54.0f);
				changeColor(g, Color.black, touchingPlayer);
				g.drawRect(position.x, cy, 54.0f, 54.0f);
				
				changeColor(g, Color.gray, touchingPlayer);
				g.fillRect((position.x + 3.0f), (cy + 3.0f), 48.0f, 48.0f);
				changeColor(g, Color.black, touchingPlayer);
				g.drawRect((position.x + 3.0f), (cy + 3.0f), 48.0f, 48.0f);
				
				icon.draw((position.x + 3.0f), (cy + 3.0f), scale);
			}
		}
		
		changeColor(g, GRAYISH, touchingPlayer);
		g.fillRect(position.x, position.y, 150.0f, 54.0f);
		changeColor(g, Color.black, touchingPlayer);
		g.drawRect(position.x, position.y, 150.0f, 54.0f);
		
		changeColor(g, Color.gray, touchingPlayer);
		g.fillRect((position.x + 3.0f), (position.y + 3.0f), 48.0f, 48.0f);
		changeColor(g, Color.black, touchingPlayer);
		g.drawRect((position.x + 3.0f), (position.y + 3.0f), 48.0f, 48.0f);
		
		if(!player.getWeapons().isEmpty() && (w instanceof RangedWeapon)) {
			RangedWeapon rw = (RangedWeapon) w;
			
			Image icon = w.getInventoryIcon();
			float scale = (48.0f / (float)icon.getWidth());
			//g.drawImage(icon, (position.x + 3.0f), (position.y + 3.0f));
			icon.draw((position.x + 3.0f), (position.y + 3.0f), scale);
			
			// Render the reloading bar, if the Player.getPlayer() is reloading.
			if(rw.isReloading(cTime)) {
				float percentage = 1.0f - (float)rw.getReloadTime(cTime);
				float height = percentage * 48.0f;
				float y = (position.y + 3.0f + (48.0f - height));
				
				changeColor(g, HUD.FADE, touchingPlayer);
				g.fillRect((position.x + 3.0f), y, 48.0f, height);
			}
		}
		
		if(w instanceof RangedWeapon) {
			RangedWeapon rw = (RangedWeapon) w;
			if(player.hasStatus(Status.UNLIMITED_AMMO)) {
				Image unlimitedAmmo = AssetManager.getManager().getImage("GZS_UnlimitedAmmo");
				float x = position.x + 100.0f - (unlimitedAmmo.getWidth() / 2);
				float y = position.y + 27.0f - (unlimitedAmmo.getHeight() / 2);
				g.drawImage(unlimitedAmmo, x, y);
			} else {
				UnicodeFont f = AssetManager.getManager().getFont("PressStart2P-Regular_small");
				String ammoText = String.format("%d / %d",
						((rw != null) ? rw.getClipAmmo() : 0),
						((rw != null) ? rw.getInventoryAmmo() : 0));
				FontUtils.drawCenter(f, ammoText, (int)(position.x + 54.0f), (int)(position.y + ((54.0f - f.getLineHeight()) / 2)), 93, Color.black);
			}
		}
	}

	public boolean displayWeapons(long cTime) {
		long elapsed = cTime - lastWeaponSwitch;
		return (elapsed <= WEAPONS_DISPLAY_TIME);
	}
	
	public boolean intersects(Player player) {
		return bounds.intersects(player.getCollider());
	}
	
	private void changeColor(Graphics g, Color c, boolean touchingPlayer) {
		g.setColor(touchingPlayer ? c.multiply(HUD.FADE) : c);
	}
	
	@Override
	public String getName() {
		return "Weapon Display";
	}

	@Override
	public String getDescription() {
		return "Weapon Display";
	}
	
	@Override
	public int getLayer() {
		return Layers.NONE.val();
	}
}
