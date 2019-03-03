package com.gzsr;

import org.newdawn.slick.Input;

import com.gzsr.misc.MouseInfo;

public class Controls {
	private static Controls instance = null;

	private enum KeyState {
		NONE, PRESSED, RELEASED;
	}

	public enum Layout {
		MOVE_UP(Input.KEY_W, "Move Up", "W"),
		MOVE_DOWN(Input.KEY_S, "Move Down", "S"),
		MOVE_LEFT(Input.KEY_A, "Move Left", "A"),
		MOVE_RIGHT(Input.KEY_D, "Move Right", "D"),
		RELOAD(Input.KEY_R, "Reload", "R"),
		PREV_WEAPON(Input.KEY_Q ,"Prev Weapon","Q"),
		NEXT_WEAPON(Input.KEY_E, "Next Weapon", "E"),
		FLASHLIGHT(Input.KEY_F, "Flashlight", "F"),
		NEXT_WAVE(Input.KEY_N, "Next Wave", "N"),
		TALENTS_SCREEN(Input.KEY_T, "Train", "T"),
		SHOP_SCREEN(Input.KEY_B, "Shop", "B"),
		CRAFT_SCREEN(Input.KEY_C, "Craft", "C"),
		PAUSE_GAME(Input.KEY_P, "Pause", "P"),
		OPEN_CONSOLE(Input.KEY_GRAVE, "Console", "~");

		private int key;
		public int getKey() { return key; }
		public void setKey(int key_) {
			this.key = key_;

			if(key_ != -1) {
				for(Layout key : values()) {
					if((key != this) && (key.getKey() == key_)) {
						// Unset the other mapping using this input.
						key.setKey(-1);
						key.setDisplay("None");
					}
				}
			}
		}

		private String name;
		public String getName() { return name; }

		private String display;
		public String getDisplay() { return display; }
		public void setDisplay(String display_) { this.display = display_; }

		private KeyState state;
		public KeyState getState() { return state; }
		public void setState(KeyState state_) { this.state = state_; }

		Layout(int key_, String name_, String display_) {
			this.key = key_;
			this.name = name_;
			this.display = display_;
			this.state = KeyState.NONE;
		}

		public static Layout identify(int key) {
			for(int i = 0; i < values().length; i++) {
				Layout control = values()[i];
				if(control.getKey() == key) return control;
			}

			return null;
		}

		public static void clearReleased() {
			for(int i = 0; i < values().length; i++) {
				Layout key = values()[i];
				if(key.getState().equals(KeyState.RELEASED)) key.setState(KeyState.NONE);
			}
		}

		public static void reset() {
			for(int i = 0; i < values().length; i++) {
				Layout key = values()[i];
				key.setState(KeyState.NONE);
			}
		}

		public static String findDisplay(int key, char c) {
			return Input.getKeyName(key);
		}
	}

	private MouseInfo mouse;
	public MouseInfo getMouse() { return mouse; }

	public Controls() {
		this.mouse = new MouseInfo();
	}

	public static Controls getInstance() {
		if(instance == null) instance = new Controls();
		return instance;
	}

	public void press(int key) {
		Layout control = Layout.identify(key);
		if(control != null) control.setState(KeyState.PRESSED);
	}

	public void press(Layout key) {
		key.setState(KeyState.PRESSED);
	}

	public boolean isPressed(Layout key) {
		return key.getState().equals(KeyState.PRESSED);
	}

	public void release(int key) {
		Layout control = Layout.identify(key);
		if(control != null) control.setState(KeyState.RELEASED);
	}

	public void release(Layout key) {
		key.setState(KeyState.RELEASED);
	}

	public boolean isReleased(Layout key) {
		return key.getState().equals(KeyState.RELEASED);
	}

	public void loadControls() {
		ConfigManager config = ConfigManager.getInstance();
		Layout [] controls = Layout.values();
		for(Layout control : controls) {
			String property = String.format("ctrl%s", control.getName().replaceAll(" ", ""));
			if(config.getAttributes().getMap().containsKey(property)) {
				int val = config.getAttributes().getInt(property);
				control.setKey(val);
				control.setDisplay(Input.getKeyName(val));
			}
		}
	}

	public void resetAll() {
		Layout.reset();
		mouse.setLeftDown(false);
		mouse.setRightDown(false);
	}
}
