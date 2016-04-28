package entities.weapons;

import managers.PlayerManager;

import org.jbox2d.dynamics.Fixture;

import physics.Collidable;
import physics.PhysicsBody.EMaskType;
import utils.Vector2;
import engine.BaseGame;
import entities.PlayerEntity;
import game.ESound;

public class ScimitarEntity extends MeleeWeapon {
	private Vector2 impulseStrength = new Vector2(90.0f, 45.0f);
	private Vector2 currentImpulse;
	private boolean attackStarted = false;
	
	public ScimitarEntity(BaseGame game) {
		super(game);
		shootCooldown = 0.8f;
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
		onDestroyFixtures();
		super.detachFromPlayer();
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
		body.setCollisionFlags(PlayerManager.WEAPON_COLLIDER & ~player.getCategory(), EMaskType.SET);
		attackStarted = true;
		
		game.getSoundManager().play(ESound.SWORD_SLASH, false);
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		
		if (shootTimer > 0.45f && player != null){
			player.setHorizontalVelocity(currentImpulse.x);
			player.setVerticalVelocity(currentImpulse.y);
			currentImpulse.mul(0.85f);
		}
		else {
			if (attackStarted){
				attackStarted = false;
				onDestroyFixtures();
			}
		}
	}
}
