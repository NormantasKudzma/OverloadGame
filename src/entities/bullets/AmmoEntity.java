package entities.bullets;

import com.ovl.engine.BaseGame;
import com.ovl.engine.GameObject;

public class AmmoEntity extends GameObject {
	public AmmoEntity(BaseGame game){
		super(game);
		setLifetime(1.0f);
	}
}
