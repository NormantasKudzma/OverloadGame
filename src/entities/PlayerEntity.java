package entities;

import game.Entity;
import graphics.SpriteAnimation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import utils.Vector2;
import controls.ControllerEventListener;

public class PlayerEntity extends Entity<SpriteAnimation> {
	private float moveSpeed = 0.05f;
	private Vector2 moveDirection = new Vector2();
	
	public final ControllerEventListener getEventListenerForMethod(final String methodName) {
		try {
			return new ControllerEventListener(){
				final PlayerEntity object = PlayerEntity.this;
				final Method method = object.getClass().getMethod(methodName);
				
				@Override
				public void handleEvent(long eventArg, Vector2 pos, int... params) {
					try {
						method.invoke(object);
					}
					catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			};
		}
		catch (NoSuchMethodException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void moveLeft(){
		moveDirection.x = -moveSpeed;
	}
	
	public void moveRight(){
		moveDirection.x = moveSpeed;
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		if (!moveDirection.equals(Vector2.zero)){
			applyImpulse(moveDirection);
			moveDirection.reset();
		}
	}
}
