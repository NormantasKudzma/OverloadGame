package entities;

import utils.OverloadRandom;
import utils.Vector2;
import engine.BaseGame;

public class ShotgunEntity extends WeaponEntity{
	protected int shellsPerShot = 4;
	protected float maxAngle = 30.0f;
	
	public ShotgunEntity(BaseGame game) {
		super(game);
	}
	
	@Override
	public void shoot() {
		Vector2 side = getScale().x > 0 ? Vector2.right : Vector2.left;
		Vector2 spawnPos = getPosition().copy().add(muzzleOffset);
		
		float angle = 0.0f;
		Vector2 direction = null;
		for (int i = 0; i < shellsPerShot; ++i){
			angle = OverloadRandom.nextRandom((int)(2.0f * maxAngle)) - maxAngle;
			direction = side.copy().rotate(angle);
			
			BulletEntity e = spawnBullet(spawnPos, direction);
			e.setRotation(-angle);
		}
	}
}
