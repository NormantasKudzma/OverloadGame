package entities;

import game.Entity;
import graphics.Sprite2D;

public class WeaponEntity extends Entity<Sprite2D>{
	protected int numBullets = 6;
	protected float shootCooldown = 1.0f;
	
	protected AmmoEntity ammo = null;
	protected BulletEntity bullet = null;
	
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
		
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
	}
}
