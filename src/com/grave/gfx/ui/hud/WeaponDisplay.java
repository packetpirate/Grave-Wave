package com.gzsr.gfx.ui.hud;

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
import com.gzsr.gfx.Camera;
import com.gzsr.gfx.Layers;
import com.gzsr.misc.Pair;
import com.gzsr.objects.weapons.ranged.RangedWeapon;
import com.gzsr.status.Status;

public class WeaponDisplay implements Entity {
	private static final long WEAPONS_DISPLAY_TIME = 2000L;
	private static final Color GRAYISH = new Color(0.7f, 0.7f, 0.7f);
	private static final Color OUT_OF_AMMO_FILTER = new Color(0x8B0000);

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
		Camera camera = Camera.getCamera();
		Player player = Player.getPlayer();
		RangedWeapon rw = player.getCurrentRanged();
		boolean touchingPlayer = intersects(player);
		boolean outOfAmmo = (rw.getClipAmmo() == 0);

		Pair<Float> dPos = new Pair<Float>((position.x + camera.getOffset().x), (position.y + camera.getOffset().y));

		Image img = AssetManager.getManager().getImage("GZS_HUD_Weapon");
		Color filter = (outOfAmmo ? WeaponDisplay.OUT_OF_AMMO_FILTER : Color.white);
		if(touchingPlayer) filter = filter.multiply(HUD.FADE);
		if(img != null) img.draw(dPos.x, dPos.y, filter);

		if(!outOfAmmo) {
			float ammoPercentage = ((float)rw.getClipAmmo() / (float)rw.getClipCapacity());
			Image ammoBar = AssetManager.getManager().getImage("GZS_HUD_Weapon_Ammo");
			if(ammoBar != null) {
				Image subImg = ammoBar.getSubImage(0, 0, (int)(ammoPercentage * ammoBar.getWidth()), ammoBar.getHeight());
				subImg.draw((dPos.x + 4.0f), (dPos.y + 4.0f), (touchingPlayer ? HUD.FADE : Color.white));
			}
		}

		if(rw.isReloading(cTime)) {
			double reloadPercentage = rw.getReloadTime(cTime);
			Image subImg = img.getSubImage(0, 0, (int)(reloadPercentage * img.getWidth()), img.getHeight());
			subImg.draw(dPos.x, dPos.y, (touchingPlayer ? HUD.FADE : Color.white));
		}

		if(rw != null) {
			Image icon = rw.getInventoryIcon();
			float scale = (48.0f / icon.getWidth());
			icon.draw((dPos.x + 24.0f), (dPos.y + 18.0f), scale, (touchingPlayer ? HUD.FADE : Color.white));
		}

		if(player.getStatusHandler().hasStatus(Status.UNLIMITED_AMMO)) {
			Image unlimitedAmmo = AssetManager.getManager().getImage("GZS_UnlimitedAmmo");
			float x = (dPos.x + 48.0f - (unlimitedAmmo.getWidth() / 2));
			float y = (dPos.y - unlimitedAmmo.getHeight() - 5.0f);
			g.drawImage(unlimitedAmmo, x, y);
		} else {
			UnicodeFont f = AssetManager.getManager().getFont("PressStart2P-Regular_small");
			String ammoText = String.format("%d / %d",
					((rw != null) ? rw.getClipAmmo() : 0),
					((rw != null) ? rw.getInventoryAmmo() : 0));
			FontUtils.drawCenter(f, ammoText, (int)dPos.x.floatValue(), (int)(dPos.y - (f.getLineHeight() + 5.0f)), 93, (touchingPlayer ? HUD.FADE : Color.white));
		}
	}

	public boolean displayWeapons(long cTime) {
		long elapsed = cTime - lastWeaponSwitch;
		return (elapsed <= WEAPONS_DISPLAY_TIME);
	}

	public boolean intersects(Player player) {
		return (bounds.intersects(player.getCollider()) || bounds.contains(player.getCollider()));
	}

	private void changeColor(Graphics g, Color c, boolean touchingPlayer) {
		g.setColor(touchingPlayer ? c.multiply(HUD.FADE) : c);
	}

	@Override
	public String getName() { return "Weapon Display"; }

	@Override
	public String getTag() { return "weaponDisplay"; }

	@Override
	public String getDescription() { return "Weapon Display"; }

	@Override
	public int getLayer() { return Layers.NONE.val(); }
}
