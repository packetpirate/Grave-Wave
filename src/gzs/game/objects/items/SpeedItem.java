package gzs.game.objects.items;

import gzs.entities.Player;
import gzs.game.misc.Pair;
import gzs.game.status.SpeedEffect;
import gzs.game.status.Status;
import gzs.game.utils.FileUtilities;
import gzs.game.utils.SoundManager;
import javafx.scene.image.Image;
import javafx.scene.media.Media;

public class SpeedItem extends Item {
	private static final Image IMAGE = FileUtilities.LoadImage("GZS_SpeedUp.png");
	private static final Media SOUND = SoundManager.LoadSound("powerup2.wav");
	private static final long DURATION = 10_000L;
	private static final long EFFECT_DURATION = 10_000L;
	
	public SpeedItem(Pair<Double> pos, long cTime) {
		super(pos, cTime);
		icon = SpeedItem.IMAGE;
		duration = SpeedItem.DURATION;
	}

	@Override
	public void apply(Player player, long cTime) {
		SpeedEffect effect = new SpeedEffect(Status.SPEED_UP, SpeedItem.EFFECT_DURATION, cTime);
		player.addStatus(effect, cTime);
		player.setDoubleAttribute("spdMult", SpeedEffect.EFFECT);
		duration = 0L;
		SoundManager.PlaySound(SpeedItem.SOUND);
	}
}
