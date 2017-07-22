package gzs.entities;

import java.util.HashMap;
import java.util.Map;

import gzs.game.info.Globals;
import gzs.game.misc.Pair;
import gzs.game.objects.weapons.AssaultRifle;
import gzs.game.objects.weapons.Weapon;
import gzs.game.utils.FileUtilities;
import gzs.math.Calculate;
import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;

public class Player implements Entity {
	private Pair<Double> position;
	public Pair<Double> getPosition() { return position; }
	public void move(double xOff, double yOff) {
		double tx = position.x + xOff;
		double ty = position.y + yOff;
		if((tx >= 0) && (tx < Globals.WIDTH) && 
		   (ty >= 0) && (ty < Globals.HEIGHT)) {
			position.x += xOff;
			position.y += yOff;
		}
	}
	
	private Map<String, Integer> iAttributes;
	public int getIntAttribute(String key) { return iAttributes.get(key); }
	public void setIntAttribute(String key, int val) { iAttributes.put(key, val); }
	private Map<String, Double> dAttributes;
	public double getDoubleAttribute(String key) { return dAttributes.get(key); }
	public void setDoubleAttribute(String key, double val) { dAttributes.put(key, val); }
	
	private Weapon currentWeapon;
	public Weapon getCurrentWeapon() { return currentWeapon; }
	
	private Image img;
	
	public Player() {
		position = new Pair<Double>((Globals.WIDTH / 2), (Globals.HEIGHT / 2));
		
		iAttributes = new HashMap<String, Integer>();
		dAttributes = new HashMap<String, Double>();
		resetAttributes();
		
		currentWeapon = new AssaultRifle();
		
		img = FileUtilities.LoadImage("GZS_Player.png");
	}
	
	@Override
	public void update(long cTime) {
		double speed = getDoubleAttribute("speed") * getDoubleAttribute("spdMult");
		if(Globals.inputs.contains("W")) move(0, -speed);
		if(Globals.inputs.contains("A")) move(-speed, 0);
		if(Globals.inputs.contains("S")) move(0, speed);
		if(Globals.inputs.contains("D")) move(speed, 0);
		if(Globals.inputs.contains("R") && 
		   !currentWeapon.isReloading(cTime) &&
		   (currentWeapon.getClipAmmo() != currentWeapon.getClipSize())) {
			currentWeapon.reload(cTime);
		}
		
		if(Globals.mouse.isMouseDown() && currentWeapon.canFire(cTime)) {
			currentWeapon.fire(new Pair<Double>(position.x, position.y), 
							   getDoubleAttribute("theta"), cTime);
		}
		
		currentWeapon.update(cTime);
		
		// Calculate the player's rotation based on mouse position.
		setDoubleAttribute("theta", Calculate.Hypotenuse(position, Globals.mouse.getPosition()));
	}

	@Override
	public void render(GraphicsContext gc, long cTime) {
		currentWeapon.render(gc, cTime);
		if(img != null) {
			ImageView iv = new ImageView(img);
			SnapshotParameters params = new SnapshotParameters();
			params.setFill(Color.TRANSPARENT);
		    params.setTransform(new Rotate(Math.toDegrees(getDoubleAttribute("theta") + (Math.PI / 2)), 
		    							  (img.getWidth() / 2), img.getHeight() / 2));
		    params.setViewport(new Rectangle2D(0, 0, img.getWidth(), img.getHeight()));
			gc.drawImage(iv.snapshot(params, null), (position.x - (img.getWidth() / 2)), 
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
		
		dAttributes.put("speed", 3.0);
		
		// Multipliers
		dAttributes.put("expMult", 1.0);
		dAttributes.put("spdMult", 1.0);
		dAttributes.put("damMult", 1.0);
		
		// Game Parameters
		dAttributes.put("theta", 0.0);
	}
}
