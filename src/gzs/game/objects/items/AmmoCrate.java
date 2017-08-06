package gzs.game.objects.items;

import java.util.List;

import gzs.entities.Player;
import gzs.game.info.Globals;
import gzs.game.misc.Pair;
import gzs.game.objects.weapons.Weapon;
import gzs.game.utils.FileUtilities;
import gzs.game.utils.SoundManager;
import javafx.scene.image.Image;
import javafx.scene.media.Media;

public class AmmoCrate extends Item {
	private static final Image IMAGE = FileUtilities.LoadImage("GZS_Ammo.png");
	private static final Media SOUND = SoundManager.LoadSound("powerup2.wav");
	private static final long DURATION = 10000L;
	
	public AmmoCrate(Pair<Double> pos, long cTime) {
		super(pos, cTime);
		icon = AmmoCrate.IMAGE;
		duration = AmmoCrate.DURATION;
	}

	@Override
	public void apply(Player player, long cTime) {
		List<Weapon> active = player.getActiveWeapons();
		int weapon = Globals.rand.nextInt(active.size());
		Weapon w = active.get(weapon);
		w.addInventoryAmmo(w.getClipSize());
		duration = 0L;
		SoundManager.PlaySound(AmmoCrate.SOUND);
	}
}
