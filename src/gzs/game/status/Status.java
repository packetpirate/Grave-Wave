package gzs.game.status;

import gzs.game.utils.FileUtilities;
import javafx.scene.image.Image;

public enum Status {
	// TODO: Add images for acid and burning status effects.
	ACID(FileUtilities.LoadImage("")),
	BURNING(FileUtilities.LoadImage("")),
	EXP_MULTIPLIER(FileUtilities.LoadImage("GZS_ExpMultiplier.png")),
	INVULNERABLE(FileUtilities.LoadImage("GZS_Invulnerability.png")),
	NIGHT_VISION(FileUtilities.LoadImage("GZS_NightVision.png")),
	SPEED_UP(FileUtilities.LoadImage("GZS_SpeedUp.png")),
	UNLIMITED_AMMO(FileUtilities.LoadImage("GZS_UnlimitedAmmo.png"));
	
	private Image icon;
	public Image getIcon() { return icon; }
	
	Status(Image icon_) {
		this.icon = icon_;
	}
}
