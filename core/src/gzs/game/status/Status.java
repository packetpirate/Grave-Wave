package gzs.game.status;

import com.badlogic.gdx.graphics.Texture;

public enum Status {
	// TODO: Add images for acid and burning status effects.
	ACID(new Texture("")),
	BURNING(new Texture("")),
	EXP_MULTIPLIER(new Texture("GZS_ExpMultiplier.png")),
	INVULNERABLE(new Texture("GZS_Invulnerability.png")),
	NIGHT_VISION(new Texture("GZS_NightVision.png")),
	SPEED_UP(new Texture("GZS_SpeedUp.png")),
	UNLIMITED_AMMO(new Texture("GZS_UnlimitedAmmo.png"));
	
	private Texture icon;
	public Texture getIcon() { return icon; }
	
	Status(Texture icon_) {
		this.icon = icon_;
	}
}