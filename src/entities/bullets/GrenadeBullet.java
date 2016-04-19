package entities.bullets;

import org.jbox2d.dynamics.Fixture;

import physics.ICollidable;
import physics.PhysicsBody.EBodyType;
import engine.BaseGame;
import entities.PlayerEntity;

public class GrenadeBullet extends BulletEntity{

	public GrenadeBullet(BaseGame game) {
		super(game);
	}

	@Override
	public void collisionStart(Fixture myFixture, Fixture otherFixture, ICollidable otherCollidable) {
		if (otherCollidable instanceof PlayerEntity){
			markForDestruction();
		}
	}
	
	@Override
	public void markForDestruction() {
		super.markForDestruction();
		// spawn explosion
	}
}