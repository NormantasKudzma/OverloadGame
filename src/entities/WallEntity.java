package entities;

import org.jbox2d.dynamics.BodyType;

import physics.PhysicsBody;

import engine.BaseGame;
import engine.Entity;


public class WallEntity extends Entity{
	public WallEntity(BaseGame game){
		super(game);
	}
	
	@Override
	public void initEntity(PhysicsBody.EBodyType type) {
		super.initEntity(type);
		if (type == PhysicsBody.EBodyType.INTERACTIVE)
		{
			body.getBody().setType(BodyType.STATIC);
		}
	}
}
