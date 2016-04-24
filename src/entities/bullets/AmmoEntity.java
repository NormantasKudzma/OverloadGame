package entities.bullets;

import engine.BaseGame;
import engine.GameObject;
import graphics.Sprite;

public class AmmoEntity extends GameObject<Sprite>{
	public AmmoEntity(BaseGame game){
		super(game);
		setLifetime(1.0f);
	}
}
