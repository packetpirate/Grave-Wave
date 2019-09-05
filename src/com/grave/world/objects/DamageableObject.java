package com.grave.world.objects;

import java.util.Iterator;
import java.util.List;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.state.BasicGameState;

import com.grave.AssetManager;
import com.grave.entities.Entity;
import com.grave.entities.Player;
import com.grave.gfx.Layers;
import com.grave.misc.Pair;
import com.grave.objects.weapons.Explosion;
import com.grave.states.GameState;
import com.grave.tmx.TTile;
import com.grave.world.GameObject;
import com.grave.world.Interactions;
import com.grave.world.Level;

public class DamageableObject extends GameObject {
	private String tileset;
	private int tid, x, y, tw, th;

	private Shape collider;
	public Shape getCollider() { return collider; }

	private Interactions damagedInteraction;

	private boolean broken;
	public boolean isBroken() { return broken; }

	public DamageableObject(Type type_, Interactions damagedInteraction_, Pair<Float> position_, float radius_,
							String tileset_, int tid_, Pair<Integer> tileCoords_, Pair<Integer> tileSize_) {
		super(type_, position_);

		tileset = tileset_;
		tid = tid_;
		x = tileCoords_.x;
		y = tileCoords_.y;
		tw = tileSize_.x;
		th = tileSize_.y;

		collider = new Circle(position_.x, position_.y, radius_);
		damagedInteraction = damagedInteraction_;
	}

	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
		// Check for collisions with projectiles and explosions.
		if(!broken) {
			Level level = ((GameState)gs).getLevel();
			boolean hitByPlayer = false;
			boolean hitByOther = false;

			// Check Player projectiles first.
			{
				Player player = Player.getPlayer();
				hitByPlayer = player.checkWeapons((GameState)gs, this, cTime);
			}

			// Check for collision with explosions.
			{
				List<Entity> explosions = level.getEntitiesByTag("explosion");
				if((explosions != null) && !explosions.isEmpty()) {
					Iterator<Entity> it = explosions.iterator();
					while(it.hasNext()) {
						Explosion exp = (Explosion) it.next();
						if(exp.getCollider().intersects(collider) || exp.getCollider().contains(collider)) {
							hitByOther = true;
						}
					}
				}
			}

			// If the object has been damaged by a projectile or explosion, execute its interaction.
			if(hitByPlayer || hitByOther) {
				// For simplicity, only one hit is required to "damage" a damageable object.
				broken = true;
				damagedInteraction.execute((GameState)gs, position, cTime);

				// Make the origin tile walkable again.
				TTile tile = level.getMap().getLayerByName("Objects").getTile(x, y);
				tile.setWalkable(true);
			}

			// TODO: Modify if damageable objects should be affected by enemy projectiles.
		}
	}

	@Override
	public void render(GameState gs, Graphics g, long cTime) {
		Image tilesetImg = AssetManager.getManager().getImage(tileset);
		Image img = TTile.getImage(tilesetImg, tid, tw, th);
		if(img != null) img.draw((position.x - (tw / 2)), (position.y - (th / 2)));
	}

	@Override
	public int getLayer() {
		return Layers.ITEMS.val();
	}
}
