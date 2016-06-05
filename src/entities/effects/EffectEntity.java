package entities.effects;

import engine.BaseGame;
import engine.GameObject;
import graphics.SpriteAnimation;

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
