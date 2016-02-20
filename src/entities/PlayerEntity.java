package entities;

import game.Entity;
import graphics.SpriteAnimation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import org.jbox2d.dynamics.Fixture;

import physics.ICollidable;
import utils.Vector2;
import controls.ControllerEventListener;

public class PlayerEntity extends Entity<SpriteAnimation> {
	public enum SensorType {
		FOOT,
		INVALID
	}
	
	private static final int MAX_JUMP_LENGTH = 7;
	private int jumpLength = 0;
	private float moveSpeed = 10.0f;
	private float jumpStrength = 36.0f;
	private boolean jumpStarted = false;
	private boolean canJump = false;
	private Vector2 movementVector = new Vector2();
	private HashMap<SensorType, Fixture> sensors = new HashMap<SensorType, Fixture>();
	
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
	
	public void addSensor(Fixture f, SensorType type){
		if (f.isSensor()){
			sensors.put(type, f);
		}
		else {
			System.out.println("Fixture is not a sensor!");
		}
	}
	
	@Override
	public void collisionEnd(Fixture me, ICollidable other) {
		if (me == sensors.get(SensorType.FOOT)){
			canJump = false;
		}
	}
	
	@Override
	public void collisionStart(Fixture me, ICollidable other) {
		if (me == sensors.get(SensorType.FOOT)){
			canJump = true;
			jumpStarted = false;
			jumpLength = 0;
		}
	}
	
	public void moveLeft(){
		movementVector.x = -moveSpeed;
	}
	
	public void moveRight(){
		movementVector.x = moveSpeed;
	}
	
	public void moveUp(){
		if (canJump || (jumpStarted && jumpLength < MAX_JUMP_LENGTH)){
			movementVector.y = jumpStrength;
			jumpStarted = true;
		}
	}

	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		
		if (jumpStarted){
			jumpLength++;
		}
		
		if (movementVector.x != 0.0f){
			//applyForce(horizontalMovement);
			getBody().getBody().m_linearVelocity.x = movementVector.x;
			movementVector.x = 0.0f;
		}
		
		if (movementVector.y != 0.0f){
			//applyImpulse(verticalMovement);
			getBody().getBody().m_linearVelocity.y = movementVector.y;
			movementVector.y = 0.0f;
		}
	}
}
