package gzs.entities;

import java.util.HashMap;
import java.util.Map;

import gzs.game.info.Globals;
import gzs.game.misc.Pair;
import gzs.game.utils.FileUtilities;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Player implements Entity {
	private Pair<Double> position;
	
	private Map<String, Integer> iAttributes;
	public int getIntAttribute(String key) { return iAttributes.get(key); }
	public void setIntAttribute(String key, int val) { iAttributes.put(key, val); }
	private Map<String, Double> dAttributes;
	public double getDoubleAttribute(String key) { return dAttributes.get(key); }
	public void setDoubleAttribute(String key, double val) { dAttributes.put(key, val); }
	
	private Image img;
	
	public Player() {
		position = new Pair<Double>((Globals.WIDTH / 2), (Globals.HEIGHT / 2));
		
		iAttributes = new HashMap<String, Integer>();
		dAttributes = new HashMap<String, Double>();
		resetAttributes();
		
		try {
			img = FileUtilities.LoadImage("GZS_Player.png");
		} catch(Exception e) {
			img = null;
			e.printStackTrace();
		}
	}
	
	@Override
	public void update(long cTime) {
		
	}

	@Override
	public void render(GraphicsContext gc, long cTime) {
		if(img != null) {
			gc.drawImage(img, (position.x - (img.getWidth() / 2)), 
							  (position.y - (img.getHeight() / 2)));
		} else {
			gc.setStroke(Color.BLACK);
			gc.setFill(Color.RED);
			gc.fillOval((position.x - 20), (position.y - 20), 40, 40);
		}
	}
	
	private void resetAttributes() {
		dAttributes.clear();
		
		dAttributes.put("health", 100.0);
		dAttributes.put("maxHealth", 100.0);
		iAttributes.put("lives", 3);
		iAttributes.put("money", 0);
		
		iAttributes.put("experience", 0);
		iAttributes.put("level", 1);
		iAttributes.put("skillPoints", 0);
		
		dAttributes.put("speed", 5.0);
		
		// Multipliers
		dAttributes.put("expMult", 1.0);
		dAttributes.put("spdMult", 1.0);
		dAttributes.put("damMult", 1.0);
		
		// Game Parameters
		dAttributes.put("theta", 0.0);
	}
}
