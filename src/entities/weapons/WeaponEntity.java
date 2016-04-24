package entities.weapons;

import managers.EntityManager;
import physics.PhysicsBody;
import physics.PhysicsBody.EBodyType;
import utils.FastMath;
import utils.Vector2;
import engine.BaseGame;
import engine.GameObject;
import entities.PlayerEntity;
import entities.bullets.AmmoEntity;
import entities.bullets.BulletEntity;
import entities.effects.EffectEntity;
import game.OverloadGame;
import graphics.SpriteAnimation;

public abstract class WeaponEntity extends GameObject<SpriteAnimation>{
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
	
	protected int numBullets = 6;						// Clip size
	protected float oldDirection = 0.0f;				
	protected float shootCooldown = 2.0f;				// Interval between shots
	protected float shootTimer = 0.0f;					// Cooldown until next shot
	protected float hoverProgress = 0.0f;
	protected boolean destroyWeaponFixtures = false;	// Should weapon's fixtures be destroyed
	protected boolean detachFromPlayer = false;			// Should weapon detach on next frame
	protected boolean isAttached = false;				// Has weapon been attached to a player
	protected Vector2 positionOffset = new Vector2();	// Offset from player's position, when attached
	protected Vector2 muzzleOffset = new Vector2();		// Bullet offset from weapon's position when shooting
	
	protected AmmoEntity ammo = null;
	protected BulletEntity bullet = null;
	protected EffectEntity effect = null;
	protected PlayerEntity player = null;
	
	public WeaponEntity(BaseGame game){
		super(game);
	}
	
	public void addChild(GameObject e){
		if (e instanceof AmmoEntity){
			ammo = (AmmoEntity)e;
		}
		else if (e instanceof BulletEntity){
			bullet = (BulletEntity)e;
		}
		else if (e instanceof EffectEntity){
			effect = (EffectEntity)e;
		}
	}
	
	public boolean attachToPlayer(PlayerEntity e){
		if (detachFromPlayer || isLifetimeFinite || isAttached){
			return false;
		}
		
		player = e;
		if (player != null){
			isAttached = true;
			destroyWeaponFixtures = true;
			flip(e.getScale().x);
		}
		return true;
	}
	
	@Override
	public WeaponEntity clone() {
		GameObject<SpriteAnimation> e = super.clone();
		WeaponEntity we = (WeaponEntity)e;
		we.numBullets = numBullets;
		we.shootCooldown = shootCooldown;
		we.positionOffset = positionOffset;
		we.muzzleOffset = muzzleOffset;
		
		if (ammo != null){
			we.ammo = (AmmoEntity)ammo.clone();
		}
		
		if (bullet != null){
			we.bullet = (BulletEntity)bullet.clone();
		}
		
		if (effect != null){
			we.effect = (EffectEntity)effect.clone();
		}
		
		if (we.body.getType() == EBodyType.INTERACTIVE){
			we.body.getBody().setActive(true);
		}
		return we;
	}
	
	@Override
	public void destroy() {
		super.destroy();
		
		if (ammo != null){
			ammo.destroy();
			ammo = null;
		}
		
		if (bullet != null){
			bullet.destroy();
			bullet = null;
		}
		
		if (effect != null){
			effect.destroy();
			effect = null;
		}
		
		if (player != null){
			player.weaponDetached();
			player = null;
		}

		positionOffset = null;
		muzzleOffset = null;
	}
	
	public void detachFromPlayer(){		
		detachFromPlayer = true;
		player = null;
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
	
	public AmmoEntity getAmmo(){
		return ammo;
	}
	
	public EffectEntity getEffect(){
		return effect;
	}
	
	public BulletEntity getBullet(){
		return bullet;
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
	
	protected boolean isSpawnFree(Vector2 position){
		return true;
	}
	
	protected void onDestroyFixtures(){
		body.setCollisionFlags(EntityManager.NO_COLLISIONS, PhysicsBody.EMaskType.SET);
	}
	
	protected void onShotReady(){
		sprite.setState(WeaponAnimation.IDLE.getIndex());
	}
	
	public void setMuzzleOffset(Vector2 offset){
		muzzleOffset = offset;
	}
	
	public void setPositionOffset(Vector2 offset){
		positionOffset = offset;
	}
	
	public int tryShoot(){
		if (shootTimer <= 0.0f && numBullets > 0){
			Vector2 weaponDirection = getScale().x > 0 ? Vector2.right : Vector2.left;
			Vector2 spawnPos = getPosition().copy().add(muzzleOffset.x * weaponDirection.x, muzzleOffset.y);
			if (isSpawnFree(spawnPos)){			
				--numBullets;
				shootTimer = shootCooldown;
				
				shoot(spawnPos, weaponDirection);
				sprite.setState(WeaponAnimation.ON_COOLDOWN.getIndex());
				if (numBullets <= 0){
					if (player != null){
						player.weaponDetached();
						player = null;
					}
					detachFromPlayer();
				}
			}
		}
		return getNumBullets();
	}
	
	// Perform a shot.
	// spawnPos - spawn position for the bullet
	// weaponDir - normalized weapon direction (left/right)
	public abstract void shoot(Vector2 spawnPos, Vector2 weaponDir);
	
	public AmmoEntity spawnAmmo(Vector2 pos){
		if (ammo != null){
			AmmoEntity e = (AmmoEntity)ammo.clone();
			e.setPosition(pos);
			game.addEntity(e, ((OverloadGame)game).getMapManager().getWeaponsLayer());
			return e;
		}
		return null;
	}
	
	public BulletEntity spawnBullet(Vector2 pos, Vector2 dir){
		if (bullet != null){
			return spawnBullet(pos, dir, bullet.getSpeed());
		}
		return null;
	}
	
	public BulletEntity spawnBullet(Vector2 pos, Vector2 dir, float speed){
		if (bullet != null){
			BulletEntity e = (BulletEntity)bullet.clone();
			e.setDirection(dir);
			e.setMovementSpeed(speed);
			if (player != null){
				e.getPhysicsBody().setCollisionFlags(player.getCategory(), PhysicsBody.EMaskType.EXCLUDE);
			}
			e.setPosition(pos);
			game.addEntity(e, ((OverloadGame)game).getMapManager().getWeaponsLayer());
			return e;
		}
		return null;
	}
	
	public EffectEntity spawnEffect(Vector2 pos){
		if (effect != null){
			EffectEntity e = (EffectEntity)effect.clone();
			e.setPosition(pos);
			e.setVisible(false);
			game.addEntity(e, ((OverloadGame)game).getMapManager().getWeaponsLayer());
			return e;
		}
		return null;
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		
		if (destroyWeaponFixtures){
			destroyWeaponFixtures = false;
			onDestroyFixtures();
		}
		
		if (detachFromPlayer){
			detachFromPlayer = false;
			body.getBody().setGravityScale(0.4f);
			body.getBody().setAwake(true);
			setLifetime(1.0f);
			return;
		}
		
		if (shootTimer > 0.0f){
			shootTimer -= deltaTime;
			if (shootTimer <= 0.0f && numBullets > 0){
				onShotReady();
			}
		}
		
		if (!isAttached){
			hoverProgress += deltaTime;
			if (hoverProgress >= 6.28f){
				hoverProgress = 0.0f;
			}
			setPosition(getPosition().add(0.0f, FastMath.sin(hoverProgress) * 0.00035f));			
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
	}
}
