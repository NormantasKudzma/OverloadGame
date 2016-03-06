package entities;

import org.jbox2d.dynamics.Fixture;

import physics.ICollidable;
import utils.Vector2;
import engine.BaseGame;
import engine.Entity;
import graphics.Sprite2D;

public class BulletEntity extends Entity<Sprite2D>{
	protected float movementSpeed = 1.25f;
	protected float totalLifetime = 1.0f;
	protected Vector2 direction = new Vector2();
	protected WeaponEntity parent = null;
	
	public BulletEntity(BaseGame game){
		super(game);
	}
	
	@Override
	public void collisionStart(Fixture myFixture, Fixture otherFixture, ICollidable otherCollidable) {	
		if (otherCollidable instanceof Entity){
			if (otherCollidable instanceof PlayerEntity){
				PlayerEntity player = (PlayerEntity)otherCollidable;
				player.markForDestruction();
			}
			
			// If bullet collides with a normal fixture
			if (!otherFixture.isSensor()){
				this.markForDestruction();
			}
		}
	}
	
	@Override
	public void initEntity() {
		super.initEntity();
		body.getBody().setBullet(true);
		body.getBody().setGravityScale(0.0f);
		body.getBody().setSleepingAllowed(false);
		body.getBody().setLinearDamping(0.0f);
	}
	
	public float getSpeed(){
		return movementSpeed;
	}
	
	public void setDirection(Vector2 dir){
		direction = dir.normalized();
		setMovementSpeed(movementSpeed);
	}
	
	public void setMovementSpeed(float movementSpeed){
		this.movementSpeed = movementSpeed;
		direction.mul(movementSpeed);
		setLinearVelocity(direction);
		setLifetime(totalLifetime);
	}
	
	public void setParent(WeaponEntity parent){
		this.parent = parent;
	}
}
