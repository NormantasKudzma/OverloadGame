package entities;

import utils.Vector2;
import engine.BaseGame;
import engine.Entity;
import graphics.Sprite2D;

public abstract class WeaponEntity extends Entity<Sprite2D>{
	protected int numBullets = 6;
	protected float shootCooldown = 2.0f;
	protected float shootTimer = 0.0f;
	protected boolean deleteFixtures = false;
	protected Vector2 positionOffset = new Vector2();
	protected Vector2 muzzleOffset = new Vector2();
	
	protected AmmoEntity ammo = null;
	protected BulletEntity bullet = null;
	protected PlayerEntity player = null;
	
	public WeaponEntity(BaseGame game){
		super(game);
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
			deleteFixtures = true;
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
	
	public void tryShoot(){
		if (shootTimer <= 0.0f && numBullets > 0){
			--numBullets;
			shootTimer = shootCooldown;
			shoot();
			if (numBullets < 0){
				detachFromPlayer();
			}
		}
	}
	
	public abstract void shoot();
	
	protected BulletEntity spawnBullet(Vector2 pos, Vector2 dir){
		if (bullet != null){
			return spawnBullet(pos, dir, bullet.getSpeed());
		}
		return null;
	}
	
	protected BulletEntity spawnBullet(Vector2 pos, Vector2 dir, float speed){
		if (bullet != null){
			BulletEntity e = (BulletEntity)bullet.clone();
			e.setPosition(pos);
			e.setDirection(dir);
			e.setMovementSpeed(speed);
			game.addEntity(e);
			return e;
		}
		return null;
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		
		if (shootTimer > 0.0f){
			shootTimer -= deltaTime;
		}
		
		if (player != null){
			if (player.getScale().x > 0){
				setPosition(player.getPosition().copy().add(positionOffset));
			}
			else {
				setPosition(player.getPosition().copy().sub(positionOffset));
			}
		}
		
		if (deleteFixtures){
			deleteFixtures = false;
			body.destroyFixtures();
		}
	}
}
