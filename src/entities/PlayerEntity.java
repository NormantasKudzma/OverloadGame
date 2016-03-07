package entities;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import org.jbox2d.dynamics.Fixture;

import physics.ICollidable;
import utils.Vector2;
import controls.ControllerEventListener;
import engine.BaseGame;
import engine.Entity;
import game.OverloadMain;
import graphics.SpriteAnimation;

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
	private boolean tryShoot = false;
	private Vector2 movementVector = new Vector2();
	private Vector2 scale = null;
	private WeaponEntity currentWeapon = null;
	private HashMap<SensorType, Fixture> sensors = new HashMap<SensorType, Fixture>();
	
	public PlayerEntity(BaseGame game) {
		super(game);
	}
	
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
	public void collisionEnd(Fixture myFixture, Fixture otherFixture, ICollidable otherCollidable) {
		if (myFixture == sensors.get(SensorType.FOOT)){
			canJump = false;
		}
		
		if (myFixture == sensors.get(SensorType.LEFT)){
			leftSensorTouching = false;
		}
		
		if (myFixture == sensors.get(SensorType.RIGHT)){
			rightSensorTouching = false;
		}
	}
	
	@Override
	public void collisionStart(Fixture myFixture, Fixture otherFixture, ICollidable otherCollidable) {
		if (otherCollidable instanceof WeaponEntity){
			WeaponEntity weapon = (WeaponEntity)otherCollidable;
			if (currentWeapon != null){
				if (currentWeapon.equals(weapon)){
					return;
				}
				currentWeapon.detachFromPlayer();
			}
			currentWeapon = weapon;
			currentWeapon.attachToPlayer(this);
			return;
		}
		
		if (myFixture == sensors.get(SensorType.FOOT)){
			canJump = true;
			jumpStarted = false;
			jumpLength = 0;
			sprite.setState(AnimationState.RUN.index());
		}
		
		if (myFixture == sensors.get(SensorType.LEFT)){
			leftSensorTouching = true;
		}
		
		if (myFixture == sensors.get(SensorType.RIGHT)){
			rightSensorTouching = true;
		}
	}
	
	@Override
	public void initEntity() {
		super.initEntity();
		body.getBody().setSleepingAllowed(false);
	}
	
	public final void moveLeft(){
		if (leftSensorTouching){
			return;
		}
		
		if (movementVector.x > 0.0f){
			movementVector.x = 0.0f;
		}

		if (movementDirection != -1.0f){
			setScale(-scale.x, scale.y);
			movementDirection = -1.0f;
			
			if (currentWeapon != null){
				currentWeapon.flip(movementDirection);
			}
		}
	}
	
	public final void moveRight(){
		if (rightSensorTouching){
			return;
		}
		
		if (movementVector.x < 0.0f){
			movementVector.x = 0.0f;
		}
		
		if (movementDirection != 1.0f){
			setScale(scale.x, scale.y);
			movementDirection = 1.0f;
			
			if (currentWeapon != null){
				currentWeapon.flip(movementDirection);
			}
		}
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
	
	public final void shoot(){
		tryShoot = true;
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
		
		if (tryShoot){
			tryShoot = false;
			if (currentWeapon != null){
				currentWeapon.tryShoot();
			}
		}
	}
}
