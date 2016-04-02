package entities;

import managers.EntityManager;
import physics.PhysicsBody;
import utils.Vector2;
import engine.BaseGame;
import engine.Entity;
import graphics.Sprite2D;
import graphics.SpriteAnimation;

public abstract class WeaponEntity extends Entity<SpriteAnimation>{
	enum WeaponAnimation {
		IDLE(0),
		ON_COOLDOWN(1);
		
		private int index;
		
		private WeaponAnimation(int i){
			index = i;
		}
		
		public int getIndex(){
			return index;
		}
	}
	
	protected int numBullets = 6;
	protected float oldDirection = 0.0f;
	protected float shootCooldown = 2.0f;
	protected float shootTimer = 0.0f;
	protected boolean destroyWeaponFixtures = false;
	protected boolean detachFromPlayer = false;
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
			destroyWeaponFixtures = true;
			flip(e.getScale().x);
		}
	}
	
	@Override
	public WeaponEntity clone() {
		Entity<SpriteAnimation> e = super.clone();
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
		detachFromPlayer = true;
	}
	
	public void flip(float direction){
		if (player == null || player.isDestroyed()){
			detachFromPlayer();
			return;
		}
		
		if (direction == oldDirection){
			return;
		}
		
		Vector2 playerPos = player.getPosition();
		if (direction > 0.0f){
			setPosition(playerPos.x + positionOffset.x, playerPos.y + positionOffset.y);
			setScale(getScale().setX(Math.abs(getScale().x)));
		}
		else
		{
			setPosition(playerPos.x - positionOffset.x, playerPos.y + positionOffset.y);
			setScale(getScale().setX(-Math.abs(getScale().x)));
		}
		oldDirection = direction;
	}
	
	public int getNumBullets(){
		return numBullets;
	}
	
	public PlayerEntity getPlayer(){
		return player;
	}
	
	public void initBullet(){
		//stub
	}
	
	@Override
	public void initEntity(PhysicsBody.EBodyType type) {
		super.initEntity(type);
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
			
			Vector2 weaponDirection = getScale().x > 0 ? Vector2.right : Vector2.left;
			Vector2 spawnPos = getPosition().copy().add(muzzleOffset.x * weaponDirection.x, muzzleOffset.y);
			shoot(spawnPos, weaponDirection);
			sprite.setState(WeaponAnimation.ON_COOLDOWN.getIndex());
			if (numBullets <= 0){
				detachFromPlayer();
			}
		}
	}
	
	// Perform a shot.
	// spawnPos - spawn position for the bullet
	// weaponDir - normalized weapon direction (left/right)
	public abstract void shoot(Vector2 spawnPos, Vector2 weaponDir);
	
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
			if (player != null){
				e.getPhysicsBody().setCollisionFlags(player.getCategory(), PhysicsBody.EMaskType.EXCLUDE);
				//e.getPhysicsBody().setCollisionCategory(player.getCategory(), PhysicsBody.EMaskType.SET);
			}
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
			if (shootTimer <= 0.0f && numBullets > 0){
				sprite.setState(WeaponAnimation.IDLE.getIndex());
			}
		}
		
		if (player != null && !player.isDestroyed()){
			Vector2 playerPos = player.getPosition();
			if (player.getScale().x > 0){
				setPosition(playerPos.x + positionOffset.x, playerPos.y + positionOffset.y);
			}
			else {
				setPosition(playerPos.x - positionOffset.x, playerPos.y + positionOffset.y);
			}
		}
		
		if (destroyWeaponFixtures){
			destroyWeaponFixtures = false;
			body.setCollisionFlags(EntityManager.NO_COLLISIONS, PhysicsBody.EMaskType.SET);
		}
		
		if (detachFromPlayer){
			detachFromPlayer = false;
			if (player != null){
				player.weaponDetached();
				player = null;
			}
			body.getBody().setGravityScale(0.4f);
			body.getBody().setAwake(true);
			setLifetime(1.0f);
		}
	}
}
