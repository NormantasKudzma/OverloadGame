package entities;

import game.Entity;
import game.OverloadMain;
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
		LEFT,
		RIGHT,
		INVALID
	}
	
	public enum AnimationState {
		RUN(0),
		JUMP(1);
		
		private final int index;
		
		private AnimationState(int i){
			index = i;
		}
		
		public final int index(){
			return index;
		}
	}
	
	private static final int MAX_JUMP_LENGTH = 7;
	private static final float MAX_MOVE_SPEED = 14.0f;
	private int jumpLength = 0;
	private float acceleration = 1.0f;
	private float movementDirection = 0.0f;
	private float jumpStrength = 36.0f;
	private boolean canJump = false;
	private boolean jumpStarted = false;
	private boolean leftSensorTouching = false;
	private boolean rightSensorTouching = false;
	private Vector2 movementVector = new Vector2();
	private Vector2 scale = null;
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
						if (OverloadMain.IS_DEBUG_BUILD){
							e.printStackTrace();
						}
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
		
		if (me == sensors.get(SensorType.LEFT)){
			leftSensorTouching = false;
		}
		
		if (me == sensors.get(SensorType.RIGHT)){
			rightSensorTouching = false;
		}
	}
	
	@Override
	public void collisionStart(Fixture me, ICollidable other) {
		if (me == sensors.get(SensorType.FOOT)){
			canJump = true;
			jumpStarted = false;
			jumpLength = 0;
			sprite.setState(AnimationState.RUN.index());
		}
		
		if (me == sensors.get(SensorType.LEFT)){
			leftSensorTouching = true;
		}
		
		if (me == sensors.get(SensorType.RIGHT)){
			rightSensorTouching = true;
		}
	}
	
	public final void moveLeft(){
		if (leftSensorTouching){
			return;
		}
		
		if (movementVector.x > 0.0f){
			movementVector.x = 0.0f;
		}

		super.setScale(-scale.x, scale.y);
		movementDirection = -1.0f;
	}
	
	public final void moveRight(){
		if (rightSensorTouching){
			return;
		}
		
		if (movementVector.x < 0.0f){
			movementVector.x = 0.0f;
		}

		super.setScale(scale.x, scale.y);
		movementDirection = 1.0f;
	}
	
	public final void moveUp(){
		if (canJump || (jumpStarted && jumpLength < MAX_JUMP_LENGTH)){
			movementVector.y = jumpStrength;
			jumpStarted = true;
			sprite.setState(AnimationState.JUMP.index());
		}
	}

	@Override
	public void setScale(Vector2 scale) {
		super.setScale(scale);
		this.scale = scale.copy();
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		
		if (jumpStarted){
			jumpLength++;
		}
		
		if (movementDirection != 0.0f){
			if (Math.abs(movementVector.x) > MAX_MOVE_SPEED){
				movementVector.x = MAX_MOVE_SPEED * movementDirection;
			}
			else {
				movementVector.x += acceleration * movementDirection;
			}
			setHorizontalVelocity(movementVector.x);
			movementDirection = 0.0f;
			sprite.setPaused(false);
		}
		else {
			sprite.setPaused(true);
			if (Math.abs(movementVector.x) > 0.0f){
				setHorizontalVelocity(movementVector.x * 0.5f);
				movementVector.x = 0.0f;
			}
		}
		
		if (movementVector.y != 0.0f){
			setVerticalVelocity(movementVector.y);
			movementVector.y = 0.0f;
		}
	}
}
