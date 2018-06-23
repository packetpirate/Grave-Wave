package com.gzsr;

import org.newdawn.slick.Input;

import com.gzsr.misc.MouseInfo;

public class Controls {
	private static Controls instance = null;
	
	private enum KeyState {
		NONE, PRESSED, RELEASED;
	}
	
	public enum Layout {
		MOVE_UP(Input.KEY_W),
		MOVE_DOWN(Input.KEY_S),
		MOVE_LEFT(Input.KEY_A),
		MOVE_RIGHT(Input.KEY_D),
		RELOAD(Input.KEY_R),
		WEAPON_1(Input.KEY_1),
		WEAPON_2(Input.KEY_2),
		WEAPON_3(Input.KEY_3),
		WEAPON_4(Input.KEY_4),
		WEAPON_5(Input.KEY_5),
		WEAPON_6(Input.KEY_6),
		WEAPON_7(Input.KEY_7),
		WEAPON_8(Input.KEY_8),
		WEAPON_9(Input.KEY_9),
		WEAPON_10(Input.KEY_0),
		NEXT_WAVE(Input.KEY_N),
		TRAIN_SCREEN(Input.KEY_T),
		SHOP_SCREEN(Input.KEY_B),
		OPEN_CONSOLE(Input.KEY_GRAVE),
		PAUSE_GAME(Input.KEY_P);
		
		private int key;
		public int getKey() { return key; }
		public void setKey(int key_) { this.key = key_; }
		
		private KeyState state;
		public KeyState getState() { return state; }
		public void setState(KeyState state_) { this.state = state_; }
		
		Layout(int key_) {
			this.key = key_;
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
	
	public void resetAll() {
		Layout.reset();
		mouse.setMouseDown(false);
	}
}
