package entities.weapons;

import managers.EntityManager;

import org.jbox2d.dynamics.Body;

import utils.Vector2;
import engine.BaseGame;
import entities.bullets.AmmoEntity;
import entities.bullets.BulletEntity;

public class GrenadeEntity extends WeaponEntity{

	public GrenadeEntity(BaseGame game) {
		super(game);
		numBullets = 2;
		shootCooldown = 1.5f;
	}

	@Override
	public void detachFromPlayer() {
		setVisible(false);
		super.detachFromPlayer();
	}

	@Override
	protected void onShotReady() {
		super.onShotReady();
		setVisible(true);
	}
	
	@Override
	public void shoot(Vector2 spawnPos, Vector2 weaponDir) {
		setVisible(false);
		
		Vector2 throwDirection = weaponDir.copy().rotate(weaponDir.x * 62.0f).mul(2.1f);
		
		BulletEntity e = spawnBullet(spawnPos, throwDirection);
		Body body = e.getPhysicsBody().getBody();
		body.m_linearVelocity.setZero();
		body.m_fixtureList.setFriction(0.01f);
		body.m_fixtureList.setRestitution(0.5f);
		body.m_fixtureList.setSensor(false);
		body.setAngularDamping(0.0f);
		body.setGravityScale(1.0f);
		e.setCollisionFlags(EntityManager.ALL_COLLISIONS, EntityManager.ALL_COLLISIONS);
		e.applyImpulse(throwDirection);
		e.setLifetime(2.5f);
		e.setParent(this);
		
		AmmoEntity ammo = spawnAmmo(spawnPos);
		ammo.applyImpulse(new Vector2(0.0f, 1.0f));
		ammo.setLifetime(0.5f);
	}
}
