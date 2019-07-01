package com.grave.objects.crafting;

public class Resources {
	public static final int METAL = 0;
	public static final int CLOTH = 1;
	public static final int GLASS = 2;
	public static final int WOOD = 3;
	public static final int ELECTRONICS = 4;
	public static final int POWER = 5;

	private int [] resources;
	public int get(int resource) { return resources[resource]; }
	public int [] getAll() { return resources; }
	public void add(int resource, int amnt) {
		int adj = (resources[resource] + amnt);
		if(adj < 0) adj = 0; // Resource counts can't be negative.
		resources[resource] = adj;
	}

	public boolean hasEnough(int resource, int amnt) {
		return (resources[resource] >= amnt);
	}

	public Resources() {
		resources = new int[6];
		reset();
	}

	public void reset() {
		for(int i = 0; i < resources.length; i++) {
			resources[i] = 0;
		}
	}

	public static String getName(int resource) {
		switch(resource) {
			case METAL:       return "Metal";
			case CLOTH:       return "Cloth";
			case GLASS:       return "Glass";
			case WOOD:        return "Wood";
			case ELECTRONICS: return "Electronics";
			case POWER:       return "Power";
			default:          return "Resource";
		}
	}

	public static String getIconName(int resource) {
		switch(resource) {
			case METAL:       return "GZS_Resource_Metal";
			case CLOTH:       return "GZS_Resource_Cloth";
			case GLASS:       return "GZS_Resource_Glass";
			case WOOD:        return "GZS_Resource_Wood";
			case ELECTRONICS: return "GZS_Resource_Electronics";
			case POWER:       return "GZS_Resource_Power";
			default:
				              return "GZS_Resource_Metal";
		}
	}
}
