package com.gzsr.tmx;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;

import com.gzsr.AssetManager;
import com.gzsr.entities.Entity;

public class TMap implements Entity {
	private List<TLayer> layers;
	public List<TLayer> getLayers() { return layers; }
	public TLayer getLayer(int i) {
		try {
			TLayer layer = layers.get(i);
			return layer;
		} catch(IndexOutOfBoundsException ioo) {
			return null;
		}
	}
	public void addLayer(TLayer layer) { layers.add(layer); }

	// The width and height, in pixels, of each tile on the map.
	private int tileWidth, tileHeight;
	public int getTileWidth() { return tileWidth; }
	public int getTileHeight() { return tileHeight; }

	// The width and height of the map in terms of number of tiles.
	private int mapWidth, mapHeight;
	public int getMapWidth() { return mapWidth; }
	public int getMapHeight() { return mapHeight; }

	public int getMapWidthTotal() { return (tileWidth * mapWidth); }
	public int getMapHeightTotal() { return (tileHeight * mapHeight); }

	private Image map;
	private Graphics context;

	public TMap(int tw, int th, int mw, int mh) {
		layers = new ArrayList<TLayer>();

		tileWidth = tw;
		tileHeight = th;
		mapWidth = mw;
		mapHeight = mh;

		try {
			map = new Image((tw * mw), (th * mh));
		} catch(SlickException se) {
			System.err.println("ERROR: Could not create new image for TMap.");
		}
	}

	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
		// Update any tiles that might need updating? Might not even be needed...
	}
	@Override
	public void render(Graphics g, long cTime) {
		if(map != null) g.drawImage(map, 0.0f, 0.0f);
	}

	/**
	 * Constructs the map image from available map data loaded from the TMX file.
	 */
	public void constructMap() {
		try {
			Image tileset = AssetManager.getManager().getImage("grave_wave_tiles");
			context = map.getGraphics();
			context.setBackground(Color.black);
			context.clear();

			for(int i = 0; i < layers.size(); i++) {
				TLayer layer = layers.get(i);
				for(int y = 0; y < mapHeight; y++) {
					for(int x = 0; x < mapWidth; x++) {
						TTile tile = layer.getTile(x, y);
						Image img = tile.getImage(tileset, tileWidth, tileHeight);
						if(img != null) {
							boolean fh = tile.isFlipped(TTile.FLIP_HORIZONTAL);
							boolean fv = tile.isFlipped(TTile.FLIP_VERTICAL);
							boolean fd = tile.isFlipped(TTile.FLIP_DIAGONAL);

							int angle = 0;
							if(fh && fd) angle = 90;
							else if(fh && fv) angle = 180;
							else if(fv && fd) angle = 270;

							if(angle != 0) context.rotate(((x * tileWidth) + (tileWidth / 2)), ((y * tileHeight) + (tileHeight / 2)), angle);
							context.drawImage(img, (x * tileWidth), (y * tileHeight));
							if(angle != 0) context.resetTransform();
						}
					}
				}
			}

			context.flush();
		} catch(SlickException se) {
			System.err.println("ERROR: Could not construct map!");
			se.printStackTrace();
		}
	}

	/**
	 * Determines if an (x, y) position is walkable on all layers.
	 * @param x The x-coordinate of the tile to check.
	 * @param y The y-coordinate of the tile to check.
	 * @return True if this position is walkable on all layers. False if this position isn't walkable on any one of the map's layers.
	 */
	public boolean isWalkable(int x, int y) {
		boolean walkable = false;
		for(int i = 0; i < layers.size(); i++) {
			TLayer layer = layers.get(i);
			walkable = layer.getTile(x, y).isWalkable();
		}

		return walkable;
	}

	@Override
	public String getName() { return "Tiled Map"; }

	@Override
	public String getTag() { return "tmap"; }

	@Override
	public String getDescription() { return "Tiled Map"; }

	@Override
	public int getLayer() { return 0; }
}
