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
		FLASHLIGHT(Input.KEY_F, "Flashlight", "F"),
		NEXT_WAVE(Input.KEY_N, "Next Wave", "N"),
		TALENTS_SCREEN(Input.KEY_T, "Train", "T"),
		SHOP_SCREEN(Input.KEY_B, "Shop", "B"),
		PAUSE_GAME(Input.KEY_P, "Pause", "P"),
		OPEN_CONSOLE(Input.KEY_GRAVE, "Console", "~"),
		WEAPON_1(Input.KEY_1, "Weapon 1", "1"),
		WEAPON_2(Input.KEY_2, "Weapon 2", "2"),
		WEAPON_3(Input.KEY_3, "Weapon 3", "3"),
		WEAPON_4(Input.KEY_4, "Weapon 4", "4"),
		WEAPON_5(Input.KEY_5, "Weapon 5", "5"),
		WEAPON_6(Input.KEY_6, "Weapon 6", "6"),
		WEAPON_7(Input.KEY_7, "Weapon 7", "7"),
		WEAPON_8(Input.KEY_8, "Weapon 8", "8"),
		WEAPON_9(Input.KEY_9, "Weapon 9", "9"),
		WEAPON_10(Input.KEY_0, "Weapon 10", "0");
		
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
		
		// To be used if game migrates to polled input.
		/*
		public void handle(boolean down) {
			if(state == KeyState.PRESSED) {
				if(!down) state = KeyState.RELEASED;
			} else if(state == KeyState.RELEASED) {
				if(down) state = KeyState.PRESSED;
				else state = KeyState.NONE;
			} else {
				if(down) state = KeyState.PRESSED;
			}
		}*/
		
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
			/*
			switch(key) {
				case Input.KEY_SPACE: return "Space";
				case Input.KEY_LCONTROL: return "Left Control";
				case Input.KEY_RCONTROL: return "Right Control";
				case Input.KEY_LSHIFT: return "Left Shift";
				case Input.KEY_RSHIFT: return "Right Shift";
				case Input.KEY_LALT: return "Left Alt";
				case Input.KEY_RALT: return "Right Alt";
				case Input.KEY_TAB: return "Tab";
				case Input.KEY_CAPITAL: return "Caps Lock";
				case Input.KEY_BACK: return "Backspace";
				case Input.KEY_ENTER: return "Enter";
				case Input.KEY_GRAVE: return "~";
				default: return Character.toString(c);
			}*/
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
	
	// To be used if the game migrates to polled input.
	/*
	public void poll(GameContainer gc) {
		Input in = gc.getInput();
		Layout [] keys = Layout.values();
		for(Layout key : keys) {
			key.handle(in.isKeyDown(key.getKey()));
		}
	}*/
	
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
