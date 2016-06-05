package entities.effects;

import engine.BaseGame;
import game.ESound;
import graphics.Renderable;
import graphics.SpriteAnimation;

public class Explosion extends EffectEntity {
	public Explosion(BaseGame game) {
		super(game);
	}
	
	@Override
	public void onEffectEnd() {
		setVisible(false);
	}

	@Override
	public void setSprite(Renderable spr) {
		if (spr instanceof SpriteAnimation){
			SpriteAnimation anim = (SpriteAnimation)spr;
			anim.setFrameDelay(0.015f);
		}
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
