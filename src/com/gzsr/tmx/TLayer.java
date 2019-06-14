package com.gzsr.tmx;

public class TLayer {
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

	public TLayer(int width, int height) {
		tiles = new TTile[height][width];
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				// Set a default tile in case something goes wrong during the load process.
				tiles[y][x] = new TTile(0, x, y);
			}
		}
	}
}
