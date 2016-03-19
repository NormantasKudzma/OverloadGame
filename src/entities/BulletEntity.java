package entities;

import org.jbox2d.dynamics.Filter;
import org.jbox2d.dynamics.Fixture;

import physics.ICollidable;
import physics.PhysicsBody;
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
		if (otherFixture.isSensor()){
			return;
		}
		markForDestruction();
		
		if (otherCollidable instanceof Entity){
			/*if (otherCollidable instanceof PlayerEntity){
				PlayerEntity player = (PlayerEntity)otherCollidable;
				player.markForDestruction();
			}*/
			
			Filter f1 = myFixture.getFilterData();
			Filter f2 = otherFixture.getFilterData();
			System.out.printf("My category[%s], mask[%s], other category[%s], mask[%s]\n", 
												Integer.toBinaryString(f1.categoryBits), 
												Integer.toBinaryString(f1.maskBits), 
												Integer.toBinaryString(f2.categoryBits), 
												Integer.toBinaryString(f2.maskBits));
		}
	}
	
	@Override
	public void initEntity(PhysicsBody.EBodyType type) {
		super.initEntity(type);
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
		parent.initBullet();
	}
}
