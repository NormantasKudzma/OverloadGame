package entities.effects;

import com.ovl.engine.BaseGame;
import com.ovl.graphics.Renderable;
import com.ovl.graphics.SpriteAnimation;

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

		//game.getSoundManager().play(ESound.EXPLODE, false);
	}
}
