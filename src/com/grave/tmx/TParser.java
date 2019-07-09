package com.grave.tmx;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class TParser {
	public static TMap load(String filename) {
		try {
			File file = new File(filename);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(file);

			Element root = doc.getDocumentElement();

			root.normalize();

			int width = Integer.parseInt(root.getAttribute("width"));
			int height = Integer.parseInt(root.getAttribute("height"));
			int tileWidth = Integer.parseInt(root.getAttribute("tilewidth"));
			int tileHeight = Integer.parseInt(root.getAttribute("tileheight"));

			TMap map = new TMap(tileWidth, tileHeight, width, height);

			NodeList layers = root.getElementsByTagName("layer");
			for(int i = 0; i < layers.getLength(); i++) {
				Node layer = layers.item(i);
				if(layer.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) layer;
					String name = element.getAttribute("name");
					NodeList dataList = element.getElementsByTagName("data");
					for(int j = 0; j < dataList.getLength(); j++) {
						Node data = dataList.item(j);
						if(data.getNodeType() == Node.ELEMENT_NODE) {
							Element e_data = (Element) data;
							String d = e_data.getTextContent().replace("\n", "");
							String [] gids = d.split(",");

							TLayer tlay = createLayer(name, gids, width, height, tileWidth, tileHeight);
							map.addLayer(tlay);
						}
					}
				}
			}

			return map;
		} catch(Exception e) {
			System.err.println("ERROR while parsing TMX file!");
			e.printStackTrace();
			System.exit(1);
		}

		return null;
	}

	private static TLayer createLayer(String layerName, String [] gids, int mw, int mh, int tw, int th) {
		TLayer layer = new TLayer(layerName, mw, mh);

		for(int i = 0; i < gids.length; i++) {
			long gid = Long.parseLong(gids[i]);
			int x = (i % mw);
			int y = (i / mw);

			TTile tile = new TTile(gid, x, y);
			boolean walkable = walkabilityByTID(tile.getTID());
			if(!walkable) tile.setWalkable(walkable);

			layer.setTile(x, y, tile);
		}

		return layer;
	}

	private static boolean walkabilityByTID(int tid) {
		switch(tid) {
			case 33:
			case 34:
			case 35:
			case 36:
			case 37:
			case 38:
			case 39:
			case 41:
			case 42:
			case 43:
			case 44:
			case 49:
			case 50:
			case 51:
			case 52:
			case 57:
			case 58:
			case 59:
			case 60:
			case 61:
				return false;
			default:
				return true;
		}
	}
}