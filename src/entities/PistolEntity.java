package entities;

import utils.OverloadRandom;
import utils.Vector2;
import engine.BaseGame;

public class PistolEntity extends WeaponEntity{
	protected float maxAngle = 3.0f;
	
	public PistolEntity(BaseGame game) {
		super(game);
		numBullets = 8;
		shootCooldown = 1.0f;
	}
	
	@Override
	public void initBullet() {
		super.initBullet();
		bullet.setMovementSpeed(bullet.getSpeed() * 0.75f);
	}
	
	@Override
	public void shoot(Vector2 spawnPos, Vector2 weaponDir) {
		float angle = 0.0f;		
		angle = OverloadRandom.nextRandom((int)(2.0f * maxAngle)) - maxAngle;	
		BulletEntity e = spawnBullet(spawnPos, weaponDir.copy().rotate(angle));		
		e.setRotation(-angle);
		e.getPhysicsBody().getBody().setGravityScale(0.05f);
	}
}
