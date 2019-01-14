package com.gzsr.entities.components;

import com.gzsr.AssetManager;
import com.gzsr.gfx.Animation;

public class HeartMonitor {
	public enum State {
		ASYSTOLE("GZS_Heart_ASY", 0, 0, 0),
		SLOW_SINUS("GZS_Heart_SSR", 60, 79, 10),
		FAST_SINUS("GZS_Heart_FSR", 80, 99, 10),
		SINUS_TACHYCARDIA("GZS_Heart_STA", 100, 149, 8),
		SUPRA_VENTRICULAR_TACHYCARDIA("GZS_Heart_SVT", 150, 220, 5);

		private int begin, end;
		public int getBegin() { return begin; }
		public int getEnd() { return end; }

		private int decay;
		public int getDecay() { return decay; }

		private Animation animation;
		public Animation getAnimation() { return animation; }

		State(String animation_, int begin_, int end_, int decay_) {
			this.animation = AssetManager.getManager().getAnimation(animation_);

			this.begin = begin_;
			this.end = end_;

			this.decay = decay_;
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
	public void setBPM(int val) { bpm = val; }
	public void addBPM(int val) {
		bpm += val;
		int end = State.SUPRA_VENTRICULAR_TACHYCARDIA.getEnd();
		if(bpm > end) bpm = end;
	}

	private State currentState;

	private int decay;
	private long lastDecay;

	public State getState() { return State.getState(bpm); }

	public HeartMonitor() {
		reset();
	}

	public void update(long cTime) {
		State state = State.getState(bpm);
		decay = state.getDecay();

		int low = State.SLOW_SINUS.getBegin();
		long elapsed = (cTime - lastDecay);
		if((elapsed >= 1_000L) && (bpm > low)) {
			bpm -= decay;
			if(bpm < low) bpm = low;
			lastDecay = cTime;
		}

		if(!state.equals(currentState)) {
			currentState = state;
			state.getAnimation().restart(cTime);
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

		State.reset(cTime);
	}
}
