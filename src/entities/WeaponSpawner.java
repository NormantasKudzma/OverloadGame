package entities;

import java.util.ArrayList;

import managers.WeaponManager;

import org.jbox2d.callbacks.QueryCallback;
import org.jbox2d.collision.AABB;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;

import physics.PhysicsBody.EBodyType;
import physics.PhysicsWorld;
import utils.OverloadRandom;
import utils.Vector2;
import engine.BaseGame;
import engine.Entity;
import game.OverloadGame;
import graphics.Sprite2D;

public class WeaponSpawner extends Entity<Sprite2D> implements QueryCallback{	
	protected boolean isSpawnFull = false;
	protected float cooldown = 10.0f;
	protected float cooldownTimer = 5.1f;
	protected String spawnPool[];
	protected Vector2 spawnOffset = Vector2.zero;
	protected AABB spawnAABB;
	
	public WeaponSpawner(BaseGame game){
		super(game);
	}
	
	@Override
	public void initEntity(EBodyType type) {
		super.initEntity(type);
	}

	@Override
	public boolean reportFixture(Fixture f) {
		if (f.getUserData() != null)
		{
			if (f.getUserData() instanceof WeaponEntity){
				WeaponEntity weapon = (WeaponEntity)f.getUserData();
				if (weapon.getPlayer() == null){
					isSpawnFull = true;
					return false;
				}
			}
			else if (f.getUserData() instanceof PlayerEntity){
				isSpawnFull = true;
				return false;
			}
		}
		return true;
	}
	
	public void setSpawnPool(String weapons[]){
		spawnPool = weapons;
	}
	
	public void setSpawnOffset(Vector2 pos){
		spawnOffset = pos;
	}
	
	protected void trySpawn(){
		isSpawnFull = false;
		if (spawnAABB == null){
			Vector2 spawnPos = getPosition().copy().add(spawnOffset);
			Vec2 upperBound = spawnPos.add(getSprite().getHalfSize()).toVec2();
			Vec2 lowerBound = spawnPos.copy().sub(getSprite().getHalfSize()).toVec2();
			spawnAABB = new AABB(lowerBound, upperBound);
		}
		PhysicsWorld.getInstance().getWorld().queryAABB(this, spawnAABB);
		
		if (!isSpawnFull){
			WeaponManager manager = ((OverloadGame)game).getWeaponManager();
			WeaponEntity weapon = null;
			if (spawnPool == null){	
				int index = OverloadRandom.nextRandom(manager.getWeaponMap().size());
				ArrayList<String> keys = new ArrayList<String>(manager.getWeaponMap().keySet());
				weapon = manager.getWeapon(keys.get(index));
			}
			else {
				int index = OverloadRandom.nextRandom(spawnPool.length);
				weapon = manager.getWeapon(spawnPool[index]);
			}
			
			if (weapon != null){
				weapon = weapon.clone();
				weapon.getPhysicsBody().getBody().setActive(true);
				weapon.setPosition(getPosition().copy().add(spawnOffset));
				game.addEntity(weapon, ((OverloadGame)game).getMapManager().getPlayersLayer());
			}
		}
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		
		cooldownTimer -= deltaTime;
		if (cooldownTimer <= 0.0f){
			cooldownTimer = cooldown;
			trySpawn();
		}
	}
}
