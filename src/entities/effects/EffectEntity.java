package entities.effects;

import engine.BaseGame;
import engine.Entity;
import graphics.SpriteAnimation;

public class EffectEntity extends Entity<SpriteAnimation>{
	public float duration = 0.0f;
	
	public EffectEntity(BaseGame game) {
		super(game);
	}
	
	public void onEffectEnd(){
		
	}
	
	public void start(){
		sprite.setState(0);
		duration = sprite.getDuration();
		sprite.setPaused(false);
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
