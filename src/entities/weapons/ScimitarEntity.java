package entities.weapons;

import org.jbox2d.dynamics.Fixture;

import physics.Collidable;
import physics.PhysicsBody.EMaskType;
import utils.Vector2;
import engine.BaseGame;
import entities.PlayerEntity;

public class ScimitarEntity extends MeleeWeapon {
	private Vector2 impulseStrength = new Vector2(90.0f, 45.0f);
	private Vector2 currentImpulse;
	
	public ScimitarEntity(BaseGame game) {
		super(game);
		shootCooldown = 0.7f;
	}
	
	@Override
	public void collisionStart(Fixture myFixture, Fixture otherFixture, Collidable otherCollidable) {
		if (otherCollidable instanceof PlayerEntity && isAttached){
			PlayerEntity target = (PlayerEntity)otherCollidable;
			// Check reference equality!
			if (target != player){
				target.setDead(true);
			}
		}
	}
	
	@Override
	public void detachFromPlayer() {
		if (player != null){
			player.canMove(true);
		}
		super.onDestroyFixtures();
		super.detachFromPlayer();
	}
	
	@Override
	protected void onDestroyFixtures() {
		body.setCollisionFlags(player.getCategory(), EMaskType.EXCLUDE);
	}
	
	@Override
	protected void onShotReady() {
		if (player != null){
			player.canMove(true);
		}
	}
	
	@Override
	public void shoot(Vector2 spawnPos, Vector2 weaponDir) {
		player.canMove(false);
		currentImpulse = impulseStrength.copy().mul(weaponDir.x, 1.0f);
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		
		if (shootTimer > 0.35f && player != null){
			player.setHorizontalVelocity(currentImpulse.x);
			player.setVerticalVelocity(currentImpulse.y);
			currentImpulse.mul(0.85f);
		}
	}
}
