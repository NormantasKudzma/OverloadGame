package entities.weapons;

import utils.Vector2;
import engine.BaseGame;

public class ScimitarEntity extends MeleeWeapon {
	public ScimitarEntity(BaseGame game) {
		super(game);
		shootCooldown = 0.5f;
	}

	@Override
	public void shoot(Vector2 spawnPos, Vector2 weaponDir) {
		// TODO Auto-generated method stub
		
	}
}
