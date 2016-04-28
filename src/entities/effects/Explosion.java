package entities.effects;

import engine.BaseGame;
import game.ESound;
import graphics.SpriteAnimation;

public class Explosion extends EffectEntity{
	public Explosion(BaseGame game) {
		super(game);
	}
	
	@Override
	public void onEffectEnd() {
		setVisible(false);
	}

	@Override
	public void setSprite(SpriteAnimation spr) {
		spr.setFrameDelay(0.015f);
		super.setSprite(spr);
	}
	
	@Override
	public void start() {
		super.start();
		setVisible(true);
		setLifetime(duration);

		game.getSoundManager().play(ESound.EXPLODE, false);
	}
}
