package com.grave.tmx;

import org.newdawn.slick.Image;

public class TTile {
	public static final long FLIP_HORIZONTAL = 0x80000000L;
	public static final long FLIP_VERTICAL   = 0x40000000L;
	public static final long FLIP_DIAGONAL   = 0x20000000L;

	// The index of the tile in the tileset corresponding to this tile.
	private int tid;
	public int getTID() { return tid; }

	// These are coordinates for the tile's position on the map, not on the tileset.
	private int x, y;
	public int getX() { return x; }
	public int getY() { return y; }

	// Flip flags. Tells us if this tile should be flipped horizontally, vertically, diagonally, or some combination of the three.
	private boolean fh, fv, fd;
	public boolean isFlipped(long flag) {
		if(flag == FLIP_HORIZONTAL) return fh;
		else if(flag == FLIP_VERTICAL) return fv;
		else if(flag == FLIP_DIAGONAL) return fd;
		else return false;
	}

	private boolean walkable;
	public boolean isWalkable() { return walkable; }
	public void setWalkable(boolean walkable_) { walkable = walkable_; }

	public TTile(long tid_, int x_, int y_) {
		this(tid_, x_, y_, true);
	}

	public TTile(long tid_, int x_, int y_, boolean walkable_) {
		this.x = x_;
		this.y = y_;

		this.fh = false;
		this.fv = false;
		this.fd = false;

		this.walkable = walkable_;

		// If any of the flip bits are set, toggle the relevant flag.
		if((tid_ & FLIP_HORIZONTAL) == FLIP_HORIZONTAL) fh = true;
		if((tid_ & FLIP_VERTICAL) == FLIP_VERTICAL) fv = true;
		if((tid_ & FLIP_DIAGONAL) == FLIP_DIAGONAL) fd = true;

		// Clear the flip flags from the TID.
		tid_ = (tid_ & ~(FLIP_HORIZONTAL | FLIP_VERTICAL | FLIP_DIAGONAL));

		this.tid = (int) tid_;
	}

	/**
	 * Gets a tile from the tileset matching this tile's TID.
	 * @param tileset A tileset image containing all tiles.
	 * @param w The width of the tile.
	 * @param h The height of the tile.
	 * @return A sub-image containing the tile matching the TID.
	 */
	public static Image getImage(Image tileset, int tid, int w, int h) {
		if(tid == 0) return null;

		int nh = tileset.getWidth() / w; // Number of tiles in this tileset horizontally.

		// Offsets to be multiplied by tile width and height to get origin point of tile.
		int ox = ((tid - 1) % nh); // Subtract 1 because the TID numbers are 1-indexed (why use 0 to indicate nothing? why not -1? stupid Tiled...)
		int oy = ((tid - 1) / nh);

		Image sub = tileset.getSubImage((ox * w), (oy * h), w, h);
		return sub;
	}

	public static boolean isStaticTile(int tid) {
		// TODO: Update with more dynamic game object TIDs.
		switch(tid) {
			case 39: return false;
			default: return true;
		}
	}
}
