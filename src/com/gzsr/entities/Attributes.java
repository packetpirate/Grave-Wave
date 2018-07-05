package com.gzsr.entities;

import java.util.HashMap;
import java.util.Map;

public class Attributes {
	private Map<String, Object> attributes;
	public void reset() { attributes.clear(); }
	
	public Attributes() {
		this.attributes = new HashMap<String, Object>();
	}
	
	public int getInt(String key_) {
		Object obj = attributes.get(key_);
		if(obj != null) return ((Integer)obj).intValue();
		else return 0;
	}
	
	public float getFloat(String key_) {
		Object obj = attributes.get(key_);
		if(obj != null) return ((Float)obj).floatValue();
		else return 0;
	}
	
	public double getDouble(String key_) {
		Object obj = attributes.get(key_);
		if(obj != null) return ((Double)obj).doubleValue();
		else return 0;
	}
	
	public long getLong(String key_) {
		Object obj = attributes.get(key_);
		if(obj != null) return ((Long)obj).longValue();
		else return 0;
	}
	
	public void set(String key_, int val_) { attributes.put(key_, val_); }
	public void set(String key_, float val_) { attributes.put(key_, val_); }
	public void set(String key_, double val_) { attributes.put(key_, val_); }
	public void set(String key_, long val_) { attributes.put(key_, val_); }
	
	public void addTo(String key_, int val_) {
		Object obj = attributes.get(key_);
		if(obj != null) attributes.put(key_, (((Integer)obj).intValue() + val_));
	}
	
	public void addTo(String key_, float val_) {
		Object obj = attributes.get(key_);
		if(obj != null) attributes.put(key_, (((Float)obj).floatValue() + val_));
	}
	
	public void addTo(String key_, double val_) {
		Object obj = attributes.get(key_);
		if(obj != null) attributes.put(key_, (((Double)obj).doubleValue() + val_));
	}
	
	public void addTo(String key_, long val_) {
		Object obj = attributes.get(key_);
		if(obj != null) attributes.put(key_, (((Long)obj).longValue() + val_));
	}
}
