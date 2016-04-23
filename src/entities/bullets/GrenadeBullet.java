package entities.bullets;

import managers.PlayerManager;

import org.jbox2d.callbacks.RayCastCallback;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;

import physics.Collidable;
import physics.PhysicsWorld;
import utils.Vector2;
import engine.BaseGame;
import engine.Entity;
import entities.PlayerEntity;
import entities.effects.EffectEntity;
import entities.weapons.WeaponEntity;
import game.OverloadGame;

public class GrenadeBullet extends BulletEntity {
	class ClosestRaycast implements RayCastCallback{
		private Fixture closest = null;
		
		public Fixture getClosest(Vector2 from, Vector2 to){
			closest = null;
			PhysicsWorld.getInstance().getWorld().raycast(this, from.toVec2(), to.toVec2());
			return closest;
		}
		
		public float reportFixture(Fixture f, Vec2 p1, Vec2 p2, float fraction) {
			if (f.m_userData instanceof Entity){
				boolean isAlive = true;
				if (f.m_userData instanceof PlayerEntity){
					isAlive = !((PlayerEntity)f.m_userData).isDead();
				}
				
				if (((Entity)f.m_userData).isVisible() && isAlive){
					closest = f;
				}
			}
			return fraction;
		}
	}
	
	private ClosestRaycast raycast = new ClosestRaycast();
	private EffectEntity effect = null;
	private float blastRadius = 0.18f;
	
	public GrenadeBullet(BaseGame game) {
		super(game);
	}

	@Override
	public void collisionStart(Fixture myFixture, Fixture otherFixture, Collidable otherCollidable) {
		if (otherCollidable instanceof PlayerEntity){
			PlayerEntity player = (PlayerEntity)otherCollidable;
			if (!player.isDead() && player.isVisible()){
				markForDestruction();
				player.setDead(true);
			}
		}
	}
	
	private void explode(){
		PlayerManager playerManager = ((OverloadGame)game).getPlayerManager();
		for (int i = 0; i < PlayerManager.NUM_PLAYERS; ++i){
			if (!playerManager.isPlayerEnabled(i)){
				continue;
			}
			
			PlayerEntity player = playerManager.getPlayer(i);
			Fixture closest = raycast.getClosest(getPosition(), player.getPosition());
			if (closest != null && closest.m_userData instanceof PlayerEntity){
				PlayerEntity target = (PlayerEntity)closest.m_userData;
				float dist = Vector2.distance(getPosition(), target.getPosition());
				System.out.println("Dist to " + i + " : " + dist);
				if (dist < blastRadius){
					target.setDead(true);
				}
			}
		}
		
		effect.setPosition(getPosition());
		effect.start();
	}
	
	@Override
	public void markForDestruction() {
		super.markForDestruction();
		explode();
	}
	
	@Override
	public void setParent(WeaponEntity parent) {
		effect = parent.spawnEffect(getPosition());
		super.setParent(parent);
	}
}
