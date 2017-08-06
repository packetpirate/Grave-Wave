package gzs.game.objects.items;

import gzs.entities.Player;
import gzs.game.info.Globals;
import gzs.game.misc.Pair;
import gzs.game.utils.FileUtilities;
import gzs.game.utils.SoundManager;
import javafx.scene.image.Image;
import javafx.scene.media.Media;

public class HealthKit extends Item {
	private static final Image IMAGE = FileUtilities.LoadImage("GZS_Health.png");
	private static final Media SOUND = SoundManager.LoadSound("powerup2.wav");
	private static final long DURATION = 10000L;
	private static final double MAX_RESTORE = 75.0;
	
	public HealthKit(Pair<Double> pos, long cTime) {
		super(pos, cTime);
		icon = HealthKit.IMAGE;
		duration = HealthKit.DURATION;
	}

	@Override
	public void update(long cTime) {
		
	}
	
	@Override
	public void apply(Player player, long cTime) {
		double amnt = Globals.rand.nextDouble() * HealthKit.MAX_RESTORE;
		player.addHealth(amnt);
		duration = 0L;
		SoundManager.PlaySound(HealthKit.SOUND);
	}
}
