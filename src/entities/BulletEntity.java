package entities;

import game.Entity;
import graphics.Sprite2D;

public class BulletEntity extends Entity<Sprite2D>{
	public BulletEntity(){
		
	}
	
	@Override
	public void initEntity() {
		super.initEntity();
		body.getBody().setBullet(true);
		body.getBody().setGravityScale(0.0f);
	}
}
