package com.gzsr.states;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.FontUtils;

import com.gzsr.AchievementManager;
import com.gzsr.AssetManager;
import com.gzsr.ConfigManager;
import com.gzsr.Controls;
import com.gzsr.Globals;
import com.gzsr.MusicPlayer;
import com.gzsr.controllers.AchievementController;
import com.gzsr.gfx.Animation;
import com.gzsr.gfx.Flashlight;
import com.gzsr.states.settings.AudioSettingsState;
import com.gzsr.states.settings.ControlSettingsState;
import com.gzsr.states.settings.DisplaySettingsState;
import com.gzsr.states.settings.GameSettingsState;
import com.gzsr.states.settings.GammaSettingsState;

public class LoadingState extends BasicGameState {
	public static final int ID = 7;

	private AssetManager assets;

	private float percentLoaded;

	@Override
	public void init(GameContainer gc, StateBasedGame game) throws SlickException {
		assets = AssetManager.getManager();

		percentLoaded = 0.0f;
	}

	@Override
	public void enter(GameContainer gc, StateBasedGame game) throws SlickException {
		if(!AssetManager.loadingComplete()) {
			loadFonts();
			loadImages();
			loadAnimations(); // has to be loaded after images
			loadSounds();

			ConfigManager.getInstance().init();
			if(ConfigManager.getInstance().getAttributes().getMap().containsKey("fullscreen")) Globals.app.setFullscreen(ConfigManager.getInstance().getAttributes().getBoolean("fullscreen"));
			if(ConfigManager.getInstance().getAttributes().getMap().containsKey("firstTimeGamma")) Globals.firstTimeGamma = ConfigManager.getInstance().getAttributes().getBoolean("firstTimeGamma");
			if(ConfigManager.getInstance().getAttributes().getMap().containsKey("shadowLevel")) Flashlight.setShadowOpacity(ConfigManager.getInstance().getAttributes().getFloat("shadowLevel"));

			AchievementController.getInstance().init();
			AchievementManager.init();
			Controls.getInstance().loadControls();
			AssetManager.finishLoad();
		}
	}

	@Override
	public void leave(GameContainer gc, StateBasedGame game) throws SlickException {
		MusicPlayer.getInstance().reset();
		MusicPlayer.getInstance().nextSong();
	}

	@Override
	public void update(GameContainer gc, StateBasedGame game, int delta) throws SlickException {
		percentLoaded = (float)AssetManager.assetsLoaded() / (float)AssetManager.assetsToLoad();
		if(AssetManager.assetsLoaded() == AssetManager.assetsToLoad()) {
			game.addState(new MenuState());

			game.addState(new AchievementMenuState());
			game.addState(new SettingsState());
			game.addState(new GameSettingsState());
			game.addState(new AudioSettingsState());
			game.addState(new DisplaySettingsState());
			game.addState(new GammaSettingsState());
			game.addState(new ControlSettingsState());

			game.addState(new GameState());
			game.addState(new ShopState());
			game.addState(new TalentsState());
			game.addState(new CraftingState());
			game.addState(new GameOverState());
			game.addState(new CreditsState());
			game.addState(new BlankState());

			game.init(gc);

			if(Globals.firstTimeGamma) game.enterState(GammaSettingsState.ID);
			else game.enterState(MenuState.ID); // we're done loading
		}
	}

	@Override
	public void render(GameContainer gc, StateBasedGame game, Graphics g) throws SlickException {
		g.resetTransform();
		g.clear();

		float lw = 400.0f;
		float lh = 50.0f;
		float lx = (Globals.WIDTH / 2) - (lw / 2);
		float ly = (Globals.HEIGHT / 2) - (lh / 2);
		float loadWidth = lw * percentLoaded;

		g.setColor(new Color(0x808080));
		g.fillRect(lx, ly, lw, lh);
		g.setColor(new Color(0x9B2111));
		g.fillRect(lx, ly, loadWidth, lh);
		g.setColor(Color.white);
		g.drawRect(lx, ly, lw, lh);

		g.setColor(Color.white);
		UnicodeFont uni = assets.getFont("PressStart2P-Regular_large");
		if(uni != null) {
			g.setFont(uni);
			FontUtils.drawCenter(uni, "Loading...", ((Globals.WIDTH / 2) - 200), (int)(ly - uni.getLineHeight() - 10), (int)lw, g.getColor());
		}
	}

	private void loadFonts() throws SlickException {
		String [] assetList = new String [] {
			"fonts/PressStart2P-Regular.ttf",
		};

		for(String asset : assetList) {
			String key = asset.substring((asset.indexOf('/') + 1),
										  asset.lastIndexOf('.'));
			assets.addFont((key + "_xs"), asset, 8, false, false);
			assets.addFont((key + "_small"), asset, 10, false, false);
			assets.addFont(key, asset, 16, false, false);
			assets.addFont((key + "_large"), asset, 32, false, false);
		}
	}

	private void loadImages() throws SlickException {
		String [] assetList = new String [] {
			// Primary Images
			"images/GZS_Background6.png",
			"images/GZS_Background02.png",
			"images/GZS_DeathScreen.png",
			"images/GZS_Joe-Portrait.png",
			"images/GZS_Player.png",
			"images/GZS_Crosshair2.png",
			// HUD Images
			"images/GZS_HUD_01.png",
			"images/GZS_Heart.png",
			"images/GZS_HUD_Armor.png",
			"images/GZS_HUD_Weapon.png",
			"images/GZS_HUD_Weapon_Ammo.png",
			"images/GZS_Life_Gem.png",
			"images/GZS_Experience_Bar.png",
			// Heart Rate Sprite Sheets
			"images/GZS_Heart_ASY.png",
			"images/GZS_Heart_SSR.png",
			"images/GZS_Heart_FSR.png",
			"images/GZS_Heart_STA.png",
			"images/GZS_Heart_SVT.png",
			// Item Images
			"images/GZS_Health.png",
			"images/GZS_Ammo.png",
			// Resources
			"images/GZS_Resource_Metal.png",
			"images/GZS_Resource_Cloth.png",
			"images/GZS_Resource_Glass.png",
			"images/GZS_Resource_Wood.png",
			"images/GZS_Resource_Electronics.png",
			"images/GZS_Resource_Power.png",
			// Status Images
			"images/GZS_Invulnerability.png",
			"images/GZS_Armor.png",
			"images/GZS_NightVision.png",
			"images/GZS_UnlimitedAmmo.png",
			"images/GZS_SpeedUp.png",
			"images/GZS_SlowDown.png",
			"images/GZS_Life.png",
			"images/GZS_ExtraLife.png",
			"images/GZS_ExpMultiplier.png",
			"images/GZS_CriticalChance.png",
			"images/GZS_PoisonIcon.png",
			"images/GZS_Paralysis.png",
			"images/GZS_Damage.png",
			"images/GZS_AcidEffect.png",
			"images/GZS_BurningEffect.png",
			// Enemy Images
			"images/GZS_Zumby3.png",
			"images/GZS_Rotdog3.png",
			"images/GZS_Upchuck3.png",
			"images/GZS_Gasbag2.png",
			"images/GZS_BigMama2.png",
			"images/GZS_TinyZumby.png",
			"images/GZS_Starfright.png",
			"images/GZS_ElSalvo.png",
			"images/GZS_Prowler.png",
			"images/GZS_Glorp.png",
			// Boss Images
			"images/GZS_Aberration2.png",
			"images/GZS_Aberration_Tentacle.png",
			"images/GZS_Zombat.png",
			"images/GZS_Stitches.png",
			"images/GZS_Hook.png",
			// Projectile Images
			"images/GZS_Arrow.png",
			"images/GZS_Arrow2.png",
			"images/GZS_FireParticle4.png",
			"images/GZS_FireAnimation1.png",
			"images/GZS_AcidParticle2.png",
			"images/GZS_HandEggParticle.png",
			"images/GZS_Claymore.png",
			"images/GZS_LaserTerminal.png",
			"images/GZS_TurretPieces.png",
			// Weapon Images
			"images/GZS_MuzzleFlash.png",
			"images/GZS_NailGun.png",
			"images/GZS_Nail.png",
			"images/GZS_Taser.png",
			"images/GZS_Taser_Dart.png",
			"images/GZS_Beretta.png",
			"images/GZS_SmithAndWesson.png",
			"images/GZS_Mp5.png",
			"images/GZS_RTPS.png",
			"images/GZS_Boomstick.png",
			"images/GZS_Bow.png",
			"images/GZS_Composite_Bow.png",
			"images/GZS_Crossbow.png",
			"images/GZS_Crossbowgun.png",
			"images/GZS_Remington.png",
			"images/GZS_AWP.png",
			"images/GZS_Flammenwerfer.png",
			"images/GZS_HandEgg.png",
			"images/GZS_ClaymoreWeapon.png",
			"images/GZS_Stinger.png",
			"images/GZS_Stinger_Missile.png",
			"images/GZS_LaserWire.png",
			"images/GZS_Turret.png",
			"images/GZS_BigRedButton.png",
			"images/GZS_Molotov.png",
			"images/GZS_Molotov_Icon.png",
			"images/GZS_Pipe_Bomb.png",
			"images/GZS_Pipe_Bomb_Icon.png",
			"images/GZS_Flak_Cannon.png",
			"images/GZS_Electric_Net_Cannon.png",
			// Melee Weapons
			"images/GZS_Baseball_Bat.png",
			"images/GZS_Baseball_Bat_Icon.png",
			"images/GZS_Spiked_Bat.png",
			"images/GZS_Spiked_Bat_Icon.png",
			"images/GZS_Machete.png",
			"images/GZS_Machete_Icon.png",
			"images/GZS_Bastard_Sword.png",
			"images/GZS_Bastard_Sword_Icon.png",
			"images/GZS_Lollipop.png",
			"images/GZS_Lollipop_Icon.png",
			// Effect Images
			"images/GZS_LightAlphaMap3.png",
			"images/GZS_Flashlight.png",
			"images/GZS_Explosion2.png",
			"images/GZS_PoisonExplosion.png",
			"images/GZS_BloodExplosion.png",
			// Munitions Talent Icons
			"images/GZS_Talent_Scout.png",
			"images/GZS_Talent_Inventor.png",
			"images/GZS_Talent_QuickFingers.png",
			"images/GZS_Talent_Soldier.png",
			"images/GZS_Talent_GunGuru.png",
			"images/GZS_Talent_Demolitions.png",
			"images/GZS_Talent_RapidFire.png",
			"images/GZS_Talent_Scavenger.png",
			"images/GZS_Talent_Commando.png",
			"images/GZS_Talent_Modder.png",
			"images/GZS_Talent_Engineer.png",
			"images/GZS_Talent_Despot.png",
			"images/GZS_Talent_Haste.png",
			// Fortification Talent Icons
			"images/GZS_Talent_Hearty.png",
			"images/GZS_Talent_MarathonMan.png",
			"images/GZS_Talent_Targeting.png",
			"images/GZS_Talent_Vigor.png",
			"images/GZS_Talent_Invigorated.png",
			"images/GZS_Talent_Unbreakable.png",
			"images/GZS_Talent_Manufacturing.png",
			"images/GZS_Talent_Firepower.png",
			"images/GZS_Talent_Relentless.png",
			"images/GZS_Talent_Undying.png",
			"images/GZS_Talent_LastStand.png",
			"images/GZS_Talent_Durability.png",
			// Tactics Talent Icons
			"images/GZS_Talent_Brutality.png",
			"images/GZS_Talent_Mercantile.png",
			"images/GZS_Talent_Savage.png",
			"images/GZS_Talent_Windfall.png",
			"images/GZS_Talent_Nimble.png",
			"images/GZS_Talent_Stockpile.png",
			"images/GZS_Talent_Ferocity.png",
			"images/GZS_Talent_Headshot.png",
			"images/GZS_Talent_Sustainability.png",
			"images/GZS_Talent_Assassin.png",
			"images/GZS_Talent_Stasis.png",
			// Misc Images
			"images/GZS_Cash.png",
			"images/GZS_Crafting_Icon.png",
			"images/GZS_Talent_Locked.png",
			"images/GZS_BuyButton2.png",
			"images/GZS_SellButton2.png",
			"images/GZS_AmmoButton.png",
			"images/GZS_Checkmark.png"
		};

		for(String asset : assetList) {
			String key = asset.substring((asset.indexOf('/') + 1),
										  asset.lastIndexOf('.'));
			assets.addImage(key, asset);
		}
	}

	private void loadAnimations() throws SlickException {
		{ // Heart Rate Animations
			Animation asystole = new Animation("GZS_Heart_ASY", 181, 22, 28, 86L, 1_000L, 1_000L);
			assets.addAnimation("GZS_Heart_ASY", asystole);

			Animation slowSinus = new Animation("GZS_Heart_SSR", 181, 22, 22, 109L, 1_000L, 1_000L);
			assets.addAnimation("GZS_Heart_SSR", slowSinus);

			Animation fastSinus = new Animation("GZS_Heart_FSR", 181, 22, 32, 75L, 1_000L, 1_000L);
			assets.addAnimation("GZS_Heart_FSR", fastSinus);

			Animation sinusTachycardia = new Animation("GZS_Heart_STA", 181, 22, 25, 96L, 1_000L, 1_000L);
			assets.addAnimation("GZS_Heart_STA", sinusTachycardia);

			Animation supraVentricularTachycardia = new Animation("GZS_Heart_SVT", 181, 22, 36, 67L, 1_000L, 1_000L);
			assets.addAnimation("GZS_Heart_SVT", supraVentricularTachycardia);
		} // End Heart Rate Animations

		Animation mf = new Animation("GZS_MuzzleFlash", 4, 8, 4, 25L, 100L, 100L);
		assets.addAnimation("GZS_MuzzleFlash", mf);

		Animation fire = new Animation("GZS_FireAnimation1", 16, 16, 8, 150L, 1200L, 1200L);
		assets.addAnimation("GZS_FireAnimation1", fire);

		Animation missile = new Animation("GZS_Stinger_Missile", 16, 64, 5, 50L, 250L, 250L);
		assets.addAnimation("GZS_Stinger_Missile", missile);

		Animation exp = new Animation("GZS_Explosion2", 128, 128, 8, 125L, 1000L, 1000L);
		assets.addAnimation("GZS_Explosion", exp);

		Animation pc = new Animation("GZS_PoisonExplosion", 128, 128, 8, 125L, 1000L, 1000L);
		assets.addAnimation("GZS_PoisonExplosion", pc);

		Animation be = new Animation("GZS_BloodExplosion", 128, 128, 8, 125L, 1000L, 1000L);
		assets.addAnimation("GZS_BloodExplosion", be);
	}

	private void loadSounds() throws SlickException {
		String [] assetList = new String [] {
			"sounds/grunt1.wav",
			"sounds/grunt2.wav",
			"sounds/grunt3.wav",
			"sounds/grunt4.wav",
			"sounds/shoot4.wav",
			"sounds/shoot3.wav",
			"sounds/bow_fire.wav",
			"sounds/nailgun.wav",
			"sounds/beretta_shot_01.wav",
			"sounds/revolver_shot_01.wav",
			"sounds/m4a1_shot_01.wav",
			"sounds/mossberg_shot_01.wav",
			"sounds/sniper_shot.wav",
			"sounds/grenade_launcher.wav",
			"sounds/throw2.wav",
			"sounds/landmine_armed.wav",
			"sounds/shotgun1.wav",
			"sounds/flamethrower2.wav",
			"sounds/flamethrower3.wav",
			"sounds/missile.wav",
			"sounds/out-of-ammo_click.wav",
			"sounds/explosion2.wav",
			"sounds/poison_cloud.wav",
			"sounds/zombie_moan_06.wav",
			"sounds/zombie_moan_09.wav",
			"sounds/buy_ammo2.wav",
			"sounds/level-up.wav",
			"sounds/powerup2.wav",
			"sounds/point_buy.wav",
			"sounds/party_horn.wav",
			"sounds/ears_ringing.wav",
			"sounds/heartbeat.wav"
		};

		for(String asset : assetList) {
			String key = asset.substring((asset.indexOf('/') + 1),
										  asset.lastIndexOf('.'));
			assets.addSound(key, asset);
		}
	}

	@Override
	public int getID() {
		return LoadingState.ID;
	}
}
