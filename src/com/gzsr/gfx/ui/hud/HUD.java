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
import com.gzsr.states.GameState;

public class HUD {
	public static final Color FADE = new Color(1.0f, 1.0f, 1.0f, 0.3f);

	private static final Pair<Float> HEART_OFFSET = new Pair<Float>(15.0f, 10.0f);
	private static final Pair<Float> EXP_OFFSET = new Pair<Float>(66.0f, 6.0f);
	private static final Pair<Float> EKG_OFFSET = new Pair<Float>(65.0f, 19.0f);
	private static final Pair<Float> GEM_OFFSET = new Pair<Float>(98.0f, 46.0f);

	//private HealthBar health;
	private Image hud;
	private Rectangle hudBounds;

	private Heart heart;
	private Lives lives;
	private StaminaBar stamina;
	private ExperienceBar experience;
	private StatusBar statusBar;

	private BossHealthBar bossHealth;

	private WeaponDisplay weaponDisplay;
	public WeaponDisplay getWeaponDisplay() { return weaponDisplay; }

	private AchievementDisplay achievementDisplay;

	public HUD() throws SlickException {
		//health = new HealthBar(new Pair<Float>(10.0f, 10.0f), new Pair<Float>(156.0f, 26.0f));
		hud = AssetManager.getManager().getImage("GZS_HUD_01");
		hudBounds = new Rectangle(10.0f, 10.0f, hud.getWidth(), hud.getHeight());

		heart = new Heart(new Pair<Float>((HEART_OFFSET.x + 10.0f), (HEART_OFFSET.y + 10.0f)), new Pair<Float>(34.0f, 44.0f));
		lives = new Lives(new Pair<Float>((GEM_OFFSET.x + 10.0f), (GEM_OFFSET.y + 10.0f)));
		stamina = new StaminaBar(new Pair<Float>(10.0f, 41.0f), new Pair<Float>(156.0f, 16.0f));
		//experience = new ExperienceBar(new Pair<Float>(10.0f, 62.0f), new Pair<Float>(156.0f, 16.0f));
		experience = new ExperienceBar(new Pair<Float>((EXP_OFFSET.x + 10.0f), (EXP_OFFSET.y + 10.0f)));
		statusBar = new StatusBar(new Pair<Float>(10.0f, 83.0f));

		bossHealth = new BossHealthBar(new Pair<Float>(((Globals.WIDTH / 2) - 150.0f), 20.0f), new Pair<Float>(300.0f, 26.0f));

		weaponDisplay = new WeaponDisplay(new Pair<Float>(10.0f, (Globals.HEIGHT - 64.0f)));

		achievementDisplay = new AchievementDisplay();
	}

	public void update(Player player, long cTime) {
		weaponDisplay.update(null, cTime, 0);
	}

	public void render(Graphics g, GameState gs, long cTime) {
		Player player = Player.getPlayer();
		EnemyController ec = EnemyController.getInstance();

		g.drawImage(hud, 10.0f, 10.0f);

		heart.render(g, cTime);
		lives.render(g, cTime);
		//health.render(g, cTime);
		//stamina.render(g, cTime);
		experience.render(g, cTime);
		//statusBar.render(g, cTime);

		if(ec.isBossWave() && !ec.isRestarting()) bossHealth.render(g, cTime);

		weaponDisplay.render(g, cTime);
		achievementDisplay.render(g, cTime);

		{ // Begin Wave Counter rendering.
			UnicodeFont f = AssetManager.getManager().getFont("PressStart2P-Regular");
			UnicodeFont fs = AssetManager.getManager().getFont("PressStart2P-Regular_small");

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

			// Draw the character portrait on the far right.
			float w1 = g.getFont().getWidth(Controls.Layout.TALENTS_SCREEN.getDisplay());
			float h = g.getFont().getLineHeight();
			g.drawString(Controls.Layout.TALENTS_SCREEN.getDisplay(), (Globals.WIDTH - w1 - 20.0f), (Globals.HEIGHT - h - 20.0f));

			Image character = AssetManager.getManager().getImage("GZS_Joe-Portrait");
			character.draw((Globals.WIDTH - (w1 + 20.0f) - ((character.getWidth() / 2) + 10.0f)), (Globals.HEIGHT - ((character.getHeight() / 2) + 20.0f)), 0.5f);

			// Draw the backpack to the left of that.
			float w2 = g.getFont().getWidth(Controls.Layout.SHOP_SCREEN.getDisplay());
			g.drawString(Controls.Layout.SHOP_SCREEN.getDisplay(), (Globals.WIDTH - (w1 + w2 + (character.getWidth() / 2) + 40.0f)), (Globals.HEIGHT - h - 20.0f));

			Image cash = AssetManager.getManager().getImage("GZS_Cash");
			cash.draw((Globals.WIDTH - (w1 + w2 + (character.getWidth() / 2) + 35.0f) - ((cash.getWidth() / 2) + 20.0f)), (Globals.HEIGHT - ((cash.getHeight() / 2) + 15.0f)), 0.5f);
		} // End drawing shop and training icons.

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

	private boolean intersects(Player player) {
		return (hudBounds.intersects(player.getCollider()) || hudBounds.contains(player.getCollider()));
	}
}
