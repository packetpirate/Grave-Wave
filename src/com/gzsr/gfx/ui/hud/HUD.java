package com.gzsr.gfx.ui.hud;

import java.text.NumberFormat;
import java.util.Locale;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.util.FontUtils;

import com.gzsr.AssetManager;
import com.gzsr.Controls;
import com.gzsr.Globals;
import com.gzsr.entities.Player;
import com.gzsr.entities.enemies.EnemyController;
import com.gzsr.misc.Pair;
import com.gzsr.objects.weapons.ranged.RangedWeapon;
import com.gzsr.states.GameState;

public class HUD {
	public static final Color FADE = new Color(1.0f, 1.0f, 1.0f, 0.3f);

	private static final Pair<Float> HEART_OFFSET = new Pair<Float>(15.0f, 10.0f);
	private static final Pair<Float> ARMOR_OFFSET = new Pair<Float>(4.0f, 4.0f);
	private static final Pair<Float> EXP_OFFSET = new Pair<Float>(66.0f, 6.0f);
	private static final Pair<Float> EKG_OFFSET = new Pair<Float>(65.0f, 19.0f);
	private static final Pair<Float> GEM_OFFSET = new Pair<Float>(98.0f, 46.0f);

	private Image hud;
	private Rectangle hudBounds;

	private Heart heart;
	private Armor armor;
	private Lives lives;
	private EKGBar ekg;
	private ExperienceBar experience;
	private StatusBar statusBar;

	private BossHealthBar bossHealth;

	private WeaponDisplay weaponDisplay;
	public WeaponDisplay getWeaponDisplay() { return weaponDisplay; }

	private AchievementDisplay achievementDisplay;

	public HUD() throws SlickException {
		hud = AssetManager.getManager().getImage("GZS_HUD_01");
		hudBounds = new Rectangle(10.0f, 10.0f, hud.getWidth(), hud.getHeight());

		heart = new Heart(new Pair<Float>((HEART_OFFSET.x + 10.0f), (HEART_OFFSET.y + 10.0f)), new Pair<Float>(34.0f, 44.0f));
		armor = new Armor(new Pair<Float>((ARMOR_OFFSET.x + 10.0f), (ARMOR_OFFSET.y + 10.0f)), new Pair<Float>(56.0f, 56.0f));
		lives = new Lives(new Pair<Float>((GEM_OFFSET.x + 10.0f), (GEM_OFFSET.y + 10.0f)));
		ekg = new EKGBar(new Pair<Float>((EKG_OFFSET.x + 10.0f), (EKG_OFFSET.y + 10.0f)));
		experience = new ExperienceBar(new Pair<Float>((EXP_OFFSET.x + 10.0f), (EXP_OFFSET.y + 10.0f)));
		statusBar = new StatusBar(new Pair<Float>(15.0f, 83.0f));

		bossHealth = new BossHealthBar(new Pair<Float>(((Globals.WIDTH / 2) - 150.0f), 20.0f), new Pair<Float>(300.0f, 26.0f));

		weaponDisplay = new WeaponDisplay(new Pair<Float>(10.0f, (Globals.HEIGHT - 84.0f)));

		achievementDisplay = new AchievementDisplay();
	}

	public void update(Player player, long cTime) {
		weaponDisplay.update(null, cTime, 0);
		ekg.update(null, cTime, 0);
	}

	public void render(Graphics g, GameState gs, long cTime) {
		Player player = Player.getPlayer();
		EnemyController ec = EnemyController.getInstance();
		boolean touchingPlayer = intersects(player);

		g.drawImage(hud, 10.0f, 10.0f, getFilterColor(Color.white, touchingPlayer));

		armor.render(g, cTime);
		heart.render(g, cTime);
		lives.render(g, cTime);
		ekg.render(g, cTime);
		experience.render(g, cTime);
		statusBar.render(g, cTime);

		if(ec.isBossWave() && !ec.isRestarting()) bossHealth.render(g, cTime);

		weaponDisplay.render(g, cTime);
		achievementDisplay.render(g, cTime);

		UnicodeFont fs = AssetManager.getManager().getFont("PressStart2P-Regular_small");
		{ // Begin Wave Counter rendering.
			UnicodeFont f = AssetManager.getManager().getFont("PressStart2P-Regular");

			if(ec.isRestarting()) {
				// Render the countdown to the next wave.
				int time = ec.timeToNextWave(cTime);
				String text = String.format("Wave %d in %ds...", ec.getWave(), time);
				String subtext = "Press N to Skip Countdown";
				int w1 = f.getWidth(text);
				int w2 = fs.getWidth(subtext);

				g.setColor(Color.white);
				FontUtils.drawCenter(f, text, (Globals.WIDTH - w1 - 20), 20, w1);
				FontUtils.drawCenter(fs, subtext, (Globals.WIDTH - w2 - 20), (f.getLineHeight() + 30), w2);
			} else {
				// Render the wave counter.
				String text = String.format("Wave: %d", ec.getWave());
				int w = f.getWidth(text);

				g.setColor(Color.white);
				FontUtils.drawCenter(f, text, (Globals.WIDTH - 20 - w), 20, w);
			}
		} // End Wave Counter rendering.

		{ // Begin Drawing Player Money
			g.setFont(AssetManager.getManager().getFont("PressStart2P-Regular"));
			String money = String.format("$%s", NumberFormat.getInstance(Locale.US).format(player.getAttributes().getInt("money")));
			float w = g.getFont().getWidth(money);
			float h = g.getFont().getLineHeight();
			float x = ((Globals.WIDTH / 2) - (w / 2));
			float y = (Globals.HEIGHT - h - 20.0f);
			FontUtils.drawCenter(g.getFont(), money, (int)x, (int)y, (int)w, Color.white);
		} // End Player Money Drawing

		{ // Draw Shop and Training Screen Icons
			g.setColor(Color.white);
			g.setFont(AssetManager.getManager().getFont("PressStart2P-Regular"));

			float w1 = g.getFont().getWidth(Controls.Layout.TALENTS_SCREEN.getDisplay());
			float w2 = g.getFont().getWidth(Controls.Layout.SHOP_SCREEN.getDisplay());
			float w3 = g.getFont().getWidth(Controls.Layout.CRAFT_SCREEN.getDisplay());
			float h = g.getFont().getLineHeight();

			Image character = AssetManager.getManager().getImage("GZS_Joe-Portrait");
			Image cash = AssetManager.getManager().getImage("GZS_Cash");
			Image crafting = AssetManager.getManager().getImage("GZS_Crafting_Icon");

			float tx = (Globals.WIDTH - (w1 + 20.0f));
			float ty = (Globals.HEIGHT - h - 20.0f);
			float ix = (Globals.WIDTH - (w1 + 20.0f) - ((character.getWidth() / 2) + 10.0f));
			float iy = (Globals.HEIGHT - 20.0f);

			// Draw the character icon to indicate the hotkey for the talents screen.
			g.drawString(Controls.Layout.TALENTS_SCREEN.getDisplay(), tx, ty);
			character.draw(ix, (iy - (character.getHeight() / 2)), 0.5f);

			// Draw the cash icon to indicate the hotkey for the shop screen.
			tx = (ix - w2 - 10.0f);
			ix = (tx - (cash.getWidth() / 2) - 10.0f);
			g.drawString(Controls.Layout.SHOP_SCREEN.getDisplay(), tx, ty);
			cash.draw(ix, (iy - (cash.getHeight() / 2)), 0.5f);

			// Draw the crafting icon to indicate the hotkey for the crafting screen.
			tx = (ix - w3 - 10.0f);
			ix = (tx - (crafting.getWidth() / 2) - 10.0f);
			g.drawString(Controls.Layout.CRAFT_SCREEN.getDisplay(), tx, ty);
			crafting.draw(ix, (iy - (crafting.getHeight() / 2)), 0.5f);
		} // End drawing shop and training icons.

		// Show debug information.
		if(Globals.debug) {
			// Show weapon state info.
			RangedWeapon rw = Player.getPlayer().getCurrentRanged();
			String weaponName = ("Name: " + rw.getName());
			String equipped = ("Equipped: " + Boolean.toString(rw.isEquipped()));
			String reloading = ("Reloading: " + Boolean.toString(rw.isReloading(cTime)));

			float width = (Math.max(fs.getWidth(weaponName), Math.max(fs.getWidth(equipped), fs.getWidth(reloading))) + 10.0f);
			float height = ((fs.getLineHeight() * 3) + 20.0f);

			g.setColor(new Color(0x444444AA));
			g.fillRect(10.0f, 100.0f, width, height);
			g.setColor(Color.white);
			g.drawRect(10.0f, 100.0f, width, height);

			float lh = fs.getLineHeight();
			g.setFont(fs);
			g.drawString(weaponName, 15.0f, 105.0f);
			g.drawString(equipped, 15.0f, (lh + 110.0f));
			g.drawString(reloading, 15.0f, ((lh * 2) + 115.0f));
		}

		// If player is respawning, draw the countdown.
		if(player.isRespawning()) {
			g.setColor(Color.white);
			g.setFont(AssetManager.getManager().getFont("PressStart2P-Regular"));
			long timeToRespawn = player.getTimeToRespawn(cTime);
			String respawnText = "Respawn in " + (timeToRespawn / 1000L) + "...";
			float w = g.getFont().getWidth(respawnText);
			float h = g.getFont().getLineHeight();
			FontUtils.drawCenter(g.getFont(), respawnText, (int)((Globals.WIDTH / 2) - (w / 2)), (int)((Globals.HEIGHT / 2) - (h / 2)), (int)w);
		}
	}

	private Color getFilterColor(Color c, boolean touchingPlayer) {
		return (touchingPlayer ? c.multiply(HUD.FADE) : c);
	}

	private boolean intersects(Player player) {
		return (hudBounds.intersects(player.getCollider()) || hudBounds.contains(player.getCollider()));
	}
}
