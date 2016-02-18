package entities;

import org.jbox2d.dynamics.BodyType;

import game.Entity;

public class WallEntity extends Entity{
	public WallEntity(){
		
	}
	
	@Override
	public void initEntity() {
		super.initEntity();
		body.getBody().setType(BodyType.STATIC);
	}
}
