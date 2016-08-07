package entities.weapons;

import com.ovl.engine.BaseGame;
import com.ovl.utils.OverloadRandom;
import com.ovl.utils.Vector2;

import entities.bullets.BulletEntity;

public class ShotgunEntity extends WeaponEntity{
	protected int shellsPerShot = 4;
	protected float maxAngle = 30.0f;
	protected float maxSpeedDeviation = 15.0f;
	
	public ShotgunEntity(BaseGame game) {
		super(game);
		numBullets = 3;
	}
	
	@Override
	public void shoot(Vector2 spawnPos, Vector2 weaponDir) {
		float angle = 0.0f;
		float speed = 0.0f;
		Vector2 direction = null;
		for (int i = 0; i < shellsPerShot; ++i){
			angle = OverloadRandom.nextRandom((int)(2.0f * maxAngle)) - maxAngle + 10.0f;
			direction = weaponDir.copy().rotate(angle);
			
			BulletEntity e = spawnBullet(spawnPos, direction);
			e.setRotation(-angle);
			e.getPhysicsBody().getBody().setGravityScale(1.15f);
			
			speed = (OverloadRandom.nextRandom((int)(2.0f * maxSpeedDeviation)) - maxSpeedDeviation) + 1.0f;
			speed = e.getSpeed() * (0.85f + 0.01f * speed);
			e.setMovementSpeed(speed);
			e.setLifetime(e.getLifetime() * 0.4f);
		}
		
		//game.getSoundManager().play(ESound.SHOTGUN_SHOOT, false);
	}
}
