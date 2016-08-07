package entities.effects;

import com.ovl.engine.BaseGame;
import com.ovl.engine.GameObject;
import com.ovl.graphics.SpriteAnimation;

public class EffectEntity extends GameObject {
	public float duration = 0.0f;
	
	public EffectEntity(BaseGame game) {
		super(game);
	}
	
	public void onEffectEnd(){
		
	}
	
	public void start(){
		duration = 0.01f;
		
		if (sprite instanceof SpriteAnimation){
			SpriteAnimation anim = (SpriteAnimation)sprite;
			anim.setState(0);
			duration = anim.getDuration();
			anim.setPaused(false);
			spriteUpdatable = anim;
		}
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		
		if (duration > 0.0f){
			duration -= deltaTime;
			if (duration <= 0.0f){
				onEffectEnd();
			}
		}
	}
}
