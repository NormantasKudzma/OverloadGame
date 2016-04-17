package entities;

import org.jbox2d.dynamics.Fixture;

import physics.ICollidable;
import physics.PhysicsBody.EBodyType;
import physics.PhysicsBody.EMaskType;
import utils.Vector2;
import engine.BaseGame;

public class MapBoundsEntity extends WallEntity{

	public MapBoundsEntity(BaseGame game) {
		super(game);
	}
	
	@Override
	public void initEntity(EBodyType type) {
		super.initEntity(type);
		
		setPosition(Vector2.one);
		
		body.attachBoxCollider(new Vector2(0.05f, 3.0f), new Vector2(-1.5f, 0.5f), 0.0f, true);
		body.attachBoxCollider(new Vector2(0.05f, 3.0f), new Vector2(1.5f, 0.5f), 0.0f, true);
		body.attachBoxCollider(new Vector2(3.0f, 0.05f), new Vector2(0.0f, -1.5f), 0.0f, true);
	}
	
	@Override
	public void collisionStart(Fixture myFixture, Fixture otherFixture, ICollidable otherCollidable) {
		if (otherFixture.m_userData instanceof PlayerEntity){
			((PlayerEntity)otherFixture.m_userData).setDead(true);
		}
	}
	
	@Override
	public void render(Vector2 position, Vector2 scale, float rotation) {
		//
	}
}
