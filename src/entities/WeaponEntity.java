package entities;

import org.jbox2d.dynamics.Fixture;

import game.Entity;
import graphics.Sprite2D;
import utils.Vector2;

public abstract class WeaponEntity extends Entity<Sprite2D>{
	protected int numBullets = 6;
	protected float shootCooldown = 1.0f;
	protected Vector2 positionOffset = new Vector2();
	protected Vector2 muzzleOffset = new Vector2();
	
	protected AmmoEntity ammo = null;
	protected BulletEntity bullet = null;
	protected PlayerEntity player = null;
	
	public WeaponEntity(){
		
	}
	
	public void addChild(Entity e){
		if (e instanceof AmmoEntity){
			addAmmoEntity((AmmoEntity)e);
		}
		else if (e instanceof BulletEntity){
			addBulletEntity((BulletEntity)e);
		}
	}
	
	public void addAmmoEntity(AmmoEntity e){
		ammo = e;
	}
	
	public void addBulletEntity(BulletEntity e){
		bullet = e;
	}
	
	public void attachToPlayer(PlayerEntity e){
		player = e;
		if (player != null){
			body.destroyFixtures();
			body.getBody().setGravityScale(0.0f);
			flip(e.getScale().x);
		}
	}
	
	@Override
	public WeaponEntity clone() {
		Entity<Sprite2D> e = super.clone();
		WeaponEntity we = (WeaponEntity)e;
		we.numBullets = numBullets;
		we.shootCooldown = shootCooldown;
		we.positionOffset = positionOffset;
		we.muzzleOffset = muzzleOffset;
		we.ammo = ammo;
		we.bullet = bullet;
		we.body.getBody().setActive(true);
		return we;
	}
	
	public void detachFromPlayer(){
		// stub

		body.getBody().setGravityScale(1.0f);
		setLifetime(2.0f);
	}
	
	public void flip(float direction){
		if (direction > 0.0f){
			setPosition(player.getPosition().copy().add(positionOffset));
			setScale(getScale().setX(Math.abs(getScale().x)));
		}
		else
		{
			setPosition(player.getPosition().copy().sub(positionOffset));
			setScale(getScale().setX(-Math.abs(getScale().x)));
		}
	}
	
	@Override
	public void initEntity() {
		super.initEntity();
		body.getBody().setGravityScale(0.0f);
	}
	
	public void setMuzzleOffset(Vector2 offset){
		muzzleOffset = offset;
	}
	
	public void setPositionOffset(Vector2 offset){
		positionOffset = offset;
	}
	
	public abstract void shoot();
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		
		if (player != null){
			if (player.getScale().x > 0){
				setPosition(player.getPosition().copy().add(positionOffset));
			}
			else {
				setPosition(player.getPosition().copy().sub(positionOffset));
			}
		}
	}
}
