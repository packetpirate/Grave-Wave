package com.grave.entities.components;

import org.newdawn.slick.Color;

import com.grave.AssetManager;
import com.grave.entities.Player;
import com.grave.gfx.Animation;
import com.grave.talents.Talents;

public class HeartMonitor {
	public enum State {
		ASYSTOLE("GZS_Heart_ASY", 0, 0, 0, 0.0, -1L, Color.white),
		SLOW_SINUS("GZS_Heart_SSR", 60, 79, 10, 0.0, 1_000L, Color.white),
		FAST_SINUS("GZS_Heart_FSR", 80, 99, 10, 20.0, 750L, new Color(0xFFFF66)),
		SINUS_TACHYCARDIA("GZS_Heart_STA", 100, 149, 8, 40.0, 500L, new Color(0xFF9933)),
		SUPRA_VENTRICULAR_TACHYCARDIA("GZS_Heart_SVT", 150, 220, 5, 60.0, 250L, new Color(0x993333));

		private int begin, end;
		public int getBegin() { return begin; }
		public int getEnd() { return end; }

		private int decay;
		public int getDecay() { return decay; }

		private long soundDelay;
		public long getDelay() { return soundDelay; }

		private Color color;
		public Color getColor() { return color; }

		private double penalty;
		public double getPenalty() {
			double p = penalty;
			if(Talents.Fortification.INVIGORATED.active()) p /= 2;
			return p;
		}

		private Animation animation;
		public Animation getAnimation() { return animation; }

		State(String animation_, int begin_, int end_, int decay_, double penalty_, long delay_, Color color_) {
			this.animation = AssetManager.getManager().getAnimation(animation_);

			this.begin = begin_;
			this.end = end_;

			this.decay = decay_;
			this.soundDelay = delay_;
			this.penalty = penalty_;

			this.color = color_;
		}

		public static State getState(int bpm) {
			for(State state : values()) {
				if((bpm >= state.getBegin()) && (bpm <= state.getEnd())) {
					return state;
				}
			}

			return ASYSTOLE;
		}

		public static void reset(long cTime) {
			for(State state : values()) {
				state.getAnimation().restart(cTime);
			}
		}
	}

	private int bpm;
	public int getBPM() { return bpm; }
	public void setBPM(int val) {
		bpm = val;
		evaluateBPM();
	}

	public void addBPM(int val) {
		bpm += val;

		int start = State.SLOW_SINUS.getBegin();
		if(bpm < start) bpm = start;

		int end = State.SUPRA_VENTRICULAR_TACHYCARDIA.getEnd();
		if(bpm > end) bpm = end;

		evaluateBPM();
	}

	private void evaluateBPM() {
		Player player = Player.getPlayer();
		State newState = State.getState(bpm);

		double maxHealth = player.getAttributes().getDouble("maxHealth");
		double currentHealth = player.getAttributes().getDouble("health");
		double adjHealth = (maxHealth - newState.getPenalty());

		player.getAttributes().set("penalizedMaxHealth", adjHealth);
		if(currentHealth > adjHealth) {
			double overflow = (player.getAttributes().getDouble("penalizedOverflow") + (currentHealth - adjHealth));
			player.getAttributes().set("health", adjHealth);
			player.getAttributes().set("penalizedOverflow", overflow);
		}
	}

	private State currentState;

	private int decay;
	private long lastDecay;

	private long lastBeat;

	public State getState() { return State.getState(bpm); }

	public HeartMonitor() {
		reset();
	}

	public void update(long cTime) {
		State state = State.getState(bpm);
		decay = state.getDecay();
		if(Talents.Fortification.MARATHON_MAN.active()) decay += Talents.Fortification.MARATHON_MAN.ranks();

		int low = State.SLOW_SINUS.getBegin();
		long elapsed = (cTime - lastDecay);
		if((elapsed >= 1_000L) && (bpm > low)) {
			bpm -= decay;
			if(bpm < low) bpm = low;
			lastDecay = cTime;
		}

		if(!state.equals(currentState)) {
			currentState = state;
			if(state.equals(State.SLOW_SINUS)) {
				Player player = Player.getPlayer();
				double maxHealth = player.getAttributes().getDouble("maxHealth");
				player.getAttributes().set("penalizedMaxHealth", maxHealth);
				player.addHealth(player.getAttributes().getDouble("penalizedOverflow"));
				player.getAttributes().set("penalizedOverflow", 0.0);
			}

			state.getAnimation().restart(cTime);
		}

		long sinceLastBeat = (cTime - lastBeat);
		if(sinceLastBeat >= state.getDelay()) {
			AssetManager.getManager().getSound("heartbeat").play();
			lastBeat = cTime;
		}
	}

	public void reset() {
		reset(0L);
	}

	public void reset(long cTime) {
		this.bpm = State.SLOW_SINUS.getBegin();
		this.currentState = State.SLOW_SINUS;

		this.decay = State.SLOW_SINUS.getDecay();
		this.lastDecay = cTime;

		this.lastBeat = cTime;

		State.reset(cTime);
	}
}
