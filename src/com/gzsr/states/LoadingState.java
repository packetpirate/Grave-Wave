package com.gzsr.states;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.FontUtils;

import com.gzsr.AssetManager;
import com.gzsr.Globals;
import com.gzsr.gfx.Animation;
import com.gzsr.states.settings.AudioSettingsState;
import com.gzsr.states.settings.ControlSettingsState;
import com.gzsr.states.settings.DisplaySettingsState;
import com.gzsr.states.settings.GameSettingsState;
import com.gzsr.states.settings.ShadowSettingsState;

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
			
			AssetManager.finishLoad();
		}
	}
	
	@Override
	public void update(GameContainer gc, StateBasedGame game, int delta) throws SlickException {
		percentLoaded = (float)AssetManager.assetsLoaded() / (float)AssetManager.assetsToLoad();
		if(AssetManager.assetsLoaded() == AssetManager.assetsToLoad()) {
			game.addState(new MenuState());
			
			game.addState(new SettingsState());
			game.addState(new GameSettingsState());
			game.addState(new AudioSettingsState());
			game.addState(new DisplaySettingsState());
			game.addState(new ShadowSettingsState());
			game.addState(new ControlSettingsState());
			
			game.addState(new GameState());
			game.addState(new ShopState());
			game.addState(new TrainState());
			game.addState(new GameOverState());
			game.addState(new CreditsState());
			game.addState(new BlankState());
			
			game.init(gc);
			game.enterState(MenuState.ID); // we're done loading
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
			"images/GZS_Crosshair.png",
			// Item Images
			"images/GZS_Health.png",
			"images/GZS_Ammo.png",
			// Status Images
			"images/GZS_Invulnerability.png",
			"images/GZS_NightVision.png",
			"images/GZS_UnlimitedAmmo.png",
			"images/GZS_SpeedUp.png",
			"images/GZS_Life.png",
			"images/GZS_ExtraLife.png",
			"images/GZS_ExpMultiplier.png",
			"images/GZS_CriticalChance.png",
			"images/GZS_PoisonIcon.png",
			// Enemy Images
			"images/GZS_Zumby2.png",
			"images/GZS_Rotdog2.png",
			"images/GZS_Upchuck2.png",
			"images/GZS_Gasbag2.png",
			"images/GZS_BigMama2.png",
			"images/GZS_TinyZumby.png",
			// Boss Images
			"images/GZS_Aberration2.png",
			"images/GZS_Zombat.png",
			"images/GZS_Stitches.png",
			"images/GZS_Hook.png",
			// Projectile Images
			"images/GZS_Arrow.png",
			"images/GZS_FireParticle.png",
			"images/GZS_AcidParticle2.png",
			"images/GZS_HandEggParticle.png",
			"images/GZS_Claymore.png",
			"images/GZS_LaserTerminal.png",
			"images/GZS_TurretPieces.png",
			// Weapon Images
			"images/GZS_MuzzleFlash.png",
			"images/GZS_Popgun.png",
			"images/GZS_RTPS.png",
			"images/GZS_Boomstick.png",
			"images/GZS_Bow.png",
			"images/GZS_Flammenwerfer.png",
			"images/GZS_HandEgg.png",
			"images/GZS_ClaymoreWeapon.png",
			"images/GZS_LaserWire.png",
			"images/GZS_Turret.png",
			"images/GZS_BigRedButton.png",
			// Effect Images
			"images/GZS_LightAlphaMap3.png",
			"images/GZS_Flashlight3.png",
			"images/GZS_Explosion.png",
			"images/GZS_PoisonExplosion.png",
			"images/GZS_BloodExplosion.png",
			// Misc Images
			"images/GZS_Backpack.png",
			"images/GZS_SkillUpButton.png",
			"images/GZS_SkillDownButton.png",
			"images/GZS_BuyButton2.png",
			"images/GZS_SellButton2.png",
			"images/GZS_AmmoButton.png"
		};
		
		for(String asset : assetList) {
			String key = asset.substring((asset.indexOf('/') + 1), 
										  asset.lastIndexOf('.'));
			assets.addImage(key, asset);
		}
	}
	
	private void loadAnimations() throws SlickException {
		Animation mf = new Animation("GZS_MuzzleFlash", 4, 8, 4, 25L, 100L, 100L);
		assets.addAnimation("GZS_MuzzleFlash", mf);
		
		Animation exp = new Animation("GZS_Explosion", 128, 128, 8, 125L, 1000L, 1000L);
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
			"sounds/throw2.wav",
			"sounds/landmine_armed.wav",
			"sounds/shotgun1.wav",
			"sounds/flamethrower2.wav",
			"sounds/explosion2.wav",
			"sounds/poison_cloud.wav",
			"sounds/buy_ammo2.wav",
			"sounds/powerup2.wav",
			"sounds/point_buy.wav"
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
