package entities.bullets;

import org.jbox2d.dynamics.Fixture;

import physics.Collidable;
import physics.PhysicsBody;
import utils.Vector2;
import engine.BaseGame;
import engine.Entity;
import entities.PlayerEntity;
import entities.weapons.WeaponEntity;
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
	public void collisionStart(Fixture myFixture, Fixture otherFixture, Collidable otherCollidable) {		
		if (otherFixture.isSensor()){
			return;
		}
		markForDestruction();
		
		if (otherCollidable instanceof PlayerEntity){
			PlayerEntity player = (PlayerEntity)otherCollidable;
			player.setDead(true);
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
		direction = dir.copy().normalized();
		setScale(getScale().mul(Math.signum(dir.x), 1.0f));
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
