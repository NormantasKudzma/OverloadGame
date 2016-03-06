package entities;

import org.jbox2d.dynamics.BodyType;

import engine.BaseGame;
import engine.Entity;


public class WallEntity extends Entity{
	public WallEntity(BaseGame game){
		super(game);
	}
	
	@Override
	public void initEntity() {
		super.initEntity();
		body.getBody().setType(BodyType.STATIC);
	}
}
