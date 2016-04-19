package entities.bullets;

import engine.BaseGame;
import engine.Entity;
import graphics.Sprite2D;

public class AmmoEntity extends Entity<Sprite2D>{
	public AmmoEntity(BaseGame game){
		super(game);
		setLifetime(1.0f);
	}
}
