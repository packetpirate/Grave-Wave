package com.gzsr.entities.enemies.bosses;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.state.BasicGameState;

import com.gzsr.AssetManager;
import com.gzsr.Globals;
import com.gzsr.entities.Player;
import com.gzsr.entities.enemies.EnemyType;
import com.gzsr.entities.enemies.LootTable;
import com.gzsr.gfx.particles.Particle;
import com.gzsr.gfx.particles.ProjectileType;
import com.gzsr.gfx.particles.StatusProjectile;
import com.gzsr.math.Calculate;
import com.gzsr.math.Dice;
import com.gzsr.misc.Pair;
import com.gzsr.objects.items.Powerups;
import com.gzsr.objects.weapons.DamageType;
import com.gzsr.states.GameState;
import com.gzsr.status.AcidEffect;
import com.gzsr.status.DamageEffect;
import com.gzsr.status.ParalysisEffect;
import com.gzsr.status.Status;

public class Aberration extends Boss {
	private static final int FIRST_WAVE = 15;
	private static final int SPAWN_COST = 25;
	private static final float SPEED = 0.12f;
	private static final float BILE_DEVIATION = (float)(Math.PI / 6);
	private static final long BILE_DELAY = 25L;
	private static final int BILE_PER_TICK = 5;
	private static final float ATTACK_DIST = 200.0f;
	private static final long ATTACK_DELAY = 1_500L;
	
	private static final String TENTACLE_IMAGE = "GZS_Aberration_Tentacle";
	private static final float TENTACLE_ATTACK_DIST = 256.0f;
	private static final float TENTACLE_DEVIATION = (float)(Math.PI / 6);
	private static final long TENTACLE_COOLDOWN = 10_000L;
	private static final long TENTACLE_EFFECT_DURATION = 3_000L;
	private static final long TENTACLE_DAMAGE_INTERVAL = 1_000L;
	private static final float TENTACLE_GROWTH_RATE = 0.025f;
	
	private static final Dice HEALTH = new Dice(100, 10);
	private static final int HEALTH_MOD = 2_500;
	
	private static final Dice TENTACLE_DAMAGE = new Dice(5, 4);
	private static final int TENTACLE_DAMAGE_MOD = 5;
	
	public static final LootTable LOOT = new LootTable()
			.addItem(Powerups.Type.HEALTH, 1.0f)
			.addItem(Powerups.Type.AMMO, 1.0f)
			.addItem(Powerups.Type.EXTRA_LIFE, 0.40f)
			.addItem(Powerups.Type.CRIT_CHANCE, 0.10f)
			.addItem(Powerups.Type.NIGHT_VISION, 0.20f);
	
	private Shape [] tentacles;
	private boolean tentacleAttack;
	private boolean playerGrabbed;
	private long timePlayerGrabbed;
	private long lastTentacleAttack;
	private float tentacleLength;
	
	private List<StatusProjectile> bile;
	private long lastBile;
	
	public Aberration(Pair<Float> position_) {
		super(EnemyType.ABERRATION, position_);
		
		this.health = Aberration.HEALTH.roll(Aberration.HEALTH_MOD);
		
		this.damageImmunities.add(DamageType.CORROSIVE);
		this.statusHandler.addImmunity(Status.PARALYSIS);
		this.statusHandler.addImmunity(Status.POISON);
		
		this.tentacles = new Shape[3];
		this.tentacleAttack = false;
		this.playerGrabbed = false;
		this.timePlayerGrabbed = 0L;
		this.lastTentacleAttack = -TENTACLE_COOLDOWN;
		this.tentacleLength = 0.0f;
		
		this.bile = new ArrayList<StatusProjectile>();
		this.lastBile = 0L;
	}
	
	@Override
	public void update(BasicGameState gs, long cTime, int delta) {
		Player player = Player.getPlayer();
		if(!dead()) {
			// Need to make sure to update the status effects first.
			statusHandler.update((GameState)gs, cTime, delta);
			
			updateFlash(cTime);
			if(!tentacleAttack && !playerGrabbed) theta = Calculate.Hypotenuse(position, player.getPosition());
			
			handleTentacleAttacks(cTime);
			
			if(!nearPlayer(Aberration.ATTACK_DIST)) {
				animation.getCurrentAnimation().update(cTime);
				if(player.isAlive() && !tentacleAttack && !touchingPlayer()) move((GameState)gs, delta);
			} else vomit(cTime);
		}
		
		postDamageTexts();
		
		// Update bile projectiles.
		Iterator<StatusProjectile> it = bile.iterator();
		while(it.hasNext()) {
			StatusProjectile p = it.next();
			if(p.isAlive(cTime)) {
				p.update(gs, cTime, delta);
				if(player.checkCollision(p)) {
					p.applyEffect(player, cTime);
					it.remove();
				}
			} else it.remove(); // need iterator instead of stream so we can remove if they're dead :/
		}
	}
	
	private void handleTentacleAttacks(long cTime) {
		// Handle tentacle attacks.
		Player player = Player.getPlayer();
		long elapsed = (cTime - lastTentacleAttack);
		if(playerGrabbed) {
			long timeGrabbed = (cTime - timePlayerGrabbed);
			if(timeGrabbed > 3_000L) {
				playerGrabbed = false;
				tentacleLength = 0.0f;
				lastTentacleAttack = cTime;
				for(int i = 0; i < 3; i++) {
					tentacles[i] = null;
				}
			}
		} else if(!tentacleAttack && (elapsed >= TENTACLE_COOLDOWN) && nearPlayer(TENTACLE_ATTACK_DIST * 2)) {
			tentacleAttack = true;
			lastTentacleAttack = cTime;
			tentacleLength = 0.0f;
		} else if(tentacleAttack) {
			if(elapsed >= 2_500L) {
				tentacleAttack = false;
				tentacleLength = 0.0f;
			} else {
				if(player.isAlive()) {
					tentacleLength += TENTACLE_GROWTH_RATE;
					if(tentacleLength > 1.0f) tentacleLength = 1.0f;
					for(int i = 0; i < 3; i++) {
						float ang = ((theta - TENTACLE_DEVIATION) + (i * TENTACLE_DEVIATION));
						float oX = ((position.x - 128.0f) + (TENTACLE_ATTACK_DIST / 2));
						float oY = position.y;
						
						Rectangle rect = new Rectangle(oX, (oY - 16.0f), (tentacleLength * TENTACLE_ATTACK_DIST), 32.0f);
						tentacles[i] = rect.transform(Transform.createRotateTransform(ang, position.x, position.y));
						
						// Check for collision with player.
						if(player.getCollider().intersects(tentacles[i])) {
							player.getStatusHandler().addStatus(new ParalysisEffect(TENTACLE_EFFECT_DURATION, cTime), cTime);
							player.getStatusHandler().addStatus(new DamageEffect(DamageType.CORROSIVE, Aberration.TENTACLE_DAMAGE, TENTACLE_DAMAGE_MOD, TENTACLE_DAMAGE_INTERVAL, TENTACLE_EFFECT_DURATION, cTime), cTime);
							playerGrabbed = true;
							tentacleAttack = false;
							timePlayerGrabbed = cTime;
						}
					}
				}
			}
		}
	}
	
	private void vomit(long cTime) {
		long sincePlayerGrabbed = (cTime - timePlayerGrabbed);
		boolean canVomit = (sincePlayerGrabbed >= 5_000L);
		if(Player.getPlayer().isAlive() && !tentacleAttack && !playerGrabbed && canVomit && (cTime >= (lastBile + Aberration.BILE_DELAY))) {
			for(int i = 0; i < Aberration.BILE_PER_TICK; i++) {
				Color color = ProjectileType.BILE.getColor();
				float velocity = ProjectileType.BILE.getVelocity();
				float width = ProjectileType.BILE.getWidth();
				float height = ProjectileType.BILE.getHeight();
				long lifespan = ProjectileType.BILE.getLifespan() * 2;
				float angle = (theta + (float)(Math.PI / 2)) + getBileDeviation();
				float angularVel = ((Globals.rand.nextInt(3) - 1) * 0.001f) * Globals.rand.nextFloat();
				
				Particle particle = new Particle("GZS_AcidParticle2", color, position, velocity, angle,
												 angularVel, new Pair<Float>(width, height), 
												 lifespan, cTime);
				
				AcidEffect acid = new AcidEffect(cTime);
				StatusProjectile projectile = new StatusProjectile(particle, 0.0, false, acid);
				bile.add(projectile);
			}
			lastBile = cTime;
		}
	}
	
	private float getBileDeviation() {
		int rl = Globals.rand.nextInt(3) - 1;
		return ((Globals.rand.nextFloat() * Aberration.BILE_DEVIATION) * rl);
	}
	
	@Override
	public void render(Graphics g, long cTime) {
		// Render the Aberration's tentacles.
		if(!dead()) {
			for(int i = 0; i < 3; i++) {
				Image tentacle = AssetManager.getManager().getImage(TENTACLE_IMAGE);
				if((tentacleAttack || playerGrabbed) && (tentacles[i] != null) && (tentacle != null)) {
					float adjTheta = (theta - TENTACLE_DEVIATION) + (i * TENTACLE_DEVIATION);
					float deg = (float)Math.toDegrees(adjTheta);
					float x = (position.x + (float)(Math.cos(adjTheta) * (TENTACLE_ATTACK_DIST / 2)));
					float y = (position.y + (float)(Math.sin(adjTheta) * (TENTACLE_ATTACK_DIST / 2)));
					
					g.rotate(x, y, deg);
					tentacle.draw((x - 128.0f), (y - 16.0f), (tentacleLength * (tentacle.getWidth() * 2.0f)), tentacle.getHeight());
					g.rotate(x, y, -deg);
					
					if(Globals.SHOW_COLLIDERS) {
						g.setColor(Color.red);
						g.draw(tentacles[i]);
					}
				}
			}
		}
		
		// Even if Aberration is dead, render its particles until they all die.
		if(!bile.isEmpty()) bile.stream().filter(p -> p.isAlive(cTime)).forEach(p -> p.render(g, cTime));
		// Only render the Aberration until it dies.
		if(!dead()) animation.getCurrentAnimation().render(g, position, theta, shouldDrawFlash(cTime));
		statusHandler.render(g, cTime);
		
		if(Globals.SHOW_COLLIDERS) {
			g.setColor(Color.red);
			g.draw(bounds);
		}
	}

	@Override
	public boolean isAlive(long cTime) {
		return (!dead() || !bile.isEmpty());
	}

	@Override
	public void move(GameState gs, int delta) {
		if(!tentacleAttack && !playerGrabbed) {
			velocity.x = (float)Math.cos(theta) * Aberration.SPEED * delta;
			velocity.y = (float)Math.sin(theta) * Aberration.SPEED * delta;
	
			avoidObstacles(gs, delta);
			
			if(!moveBlocked) {
				position.x += velocity.x;
				position.y += velocity.y;
			}
			
			moveBlocked = false;
			
			bounds.setCenterX(position.x);
			bounds.setCenterY(position.y);
		}
	}
	
	@Override
	public float getCohesionDistance() {
		return (Math.min(type.getFrameWidth(), type.getFrameHeight()) * 2);
	}
	
	@Override
	public float getSeparationDistance() {
		return Math.min(type.getFrameWidth(), type.getFrameHeight());
	}
	
	@Override
	public void takeDamage(DamageType type, double amnt, float knockback, long cTime, int delta) {
		takeDamage(type, amnt, knockback, (float)(theta + Math.PI), cTime, delta, true);
	}
	
	@Override
	public void takeDamage(DamageType type, double amnt, float knockback, float knockbackTheta, long cTime, int delta, boolean flash) {
		takeDamage(type, amnt, knockback, knockbackTheta, cTime, delta, flash, false);
	}
	
	@Override
	public void takeDamage(DamageType type, double amnt, float knockback, float knockbackTheta, long cTime, int delta, boolean flash, boolean isCritical) {
		if(!dead() && !damageImmunities.contains(type)) {
			health -= amnt;
			
			createDamageText(amnt, 64.0f, knockbackTheta, cTime, isCritical);
			
			if(flash) {
				hit = true;
				hitTime = cTime;
			}
		}
	}
	
	@Override
	public long getAttackDelay() { return Aberration.ATTACK_DELAY; }
	
	@Override
	public float getSpeed() { return Aberration.SPEED; }

	public static int appearsOnWave() { return FIRST_WAVE; }
	
	public static int getSpawnCost() { return Aberration.SPAWN_COST; }
	
	@Override
	public String getName() {
		return "Aberration";
	}
	
	@Override
	public String getDescription() {
		return "Aberration";
	}
	
	@Override
	public String print() {
		return String.format("%s at (%.2f, %.2f) - %.2f health - Bile Left: %d",
							 getName(), position.x, position.y, health, bile.size());
	}
	
	@Override
	public LootTable getLootTable() { return Aberration.LOOT; }
}
