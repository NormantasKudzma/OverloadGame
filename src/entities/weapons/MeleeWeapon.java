package entities.weapons;

import engine.BaseGame;

public abstract class MeleeWeapon extends WeaponEntity {

	public MeleeWeapon(BaseGame game) {
		super(game);
		numBullets = Integer.MAX_VALUE;
	}
	
	@Override
	public int getNumBullets() {
		return 0;
	}
}
