package entities.bullets;

import engine.BaseGame;
import engine.Entity;
import graphics.Sprite;

public class AmmoEntity extends Entity<Sprite>{
	public AmmoEntity(BaseGame game){
		super(game);
		setLifetime(1.0f);
	}
}
