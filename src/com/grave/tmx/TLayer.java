package com.grave.tmx;

public class TLayer {
	private String name;
	public String getLayerName() { return name; }

	private TTile [][] tiles;
	public TTile [][] getTiles() { return tiles; }
	public TTile getTile(int x, int y) {
		try {
			TTile tile = tiles[y][x];
			return tile;
		} catch (ArrayIndexOutOfBoundsException aio) {
			return null;
		}
	}
	public void setTile(int x, int y, TTile tile) {
		try {
			tiles[y][x] = tile;
		} catch(ArrayIndexOutOfBoundsException aio) {
		}
	}

	public TLayer(String name_, int mw, int mh) {
		this.name = name_;

		tiles = new TTile[mh][mw];
		for(int y = 0; y < mh; y++) {
			for(int x = 0; x < mw; x++) {
				// Set a default tile in case something goes wrong during the load process.
				tiles[y][x] = new TTile(0, x, y);
			}
		}
	}
}
