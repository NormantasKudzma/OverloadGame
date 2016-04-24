package entities;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import org.jbox2d.dynamics.Fixture;

import physics.Collidable;
import physics.PhysicsBody;
import ui.Overlay;
import utils.Vector2;
import controls.AbstractController;
import controls.ControllerEventListener;
import engine.BaseGame;
import engine.GameObject;
import entities.weapons.WeaponEntity;
import game.OverloadGame;
import game.OverloadMain;
import graphics.SpriteAnimation;

public class PlayerEntity extends GameObject<SpriteAnimation> {
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
	private int index;
	private int jumpLength = 0;
	private int categoryMask = 0;
	private float acceleration = 1.0f;
	private float movementDirection = 0.0f;
	private float jumpStrength = 36.0f;
	private boolean canJump = false;
	private boolean canMove = true;
	private boolean isDead = false;
	private boolean deadFlagChanged = true;
	private boolean jumpStarted = false;
	private boolean leftSensorTouching = false;
	private boolean rightSensorTouching = false;
	private boolean tryShoot = false;
	private Vector2 movementVector = new Vector2();
	private Vector2 scale = null;					// Separate scale, so sprite can be easily flipped
	private WeaponEntity currentWeapon = null;
	private HashMap<SensorType, Fixture> sensors = new HashMap<SensorType, Fixture>();
	private AbstractController controller = null;
	private ArrayList<ControllerEventListener> controls = new ArrayList<ControllerEventListener>();
	
	private OverloadGame overloadGame;
	private Overlay overlay;
	
	public PlayerEntity(BaseGame game) {
		super(game);
		overloadGame = (OverloadGame)game;
		overlay = overloadGame.getOverlay();
	}
	
	public final ControllerEventListener getEventListenerForMethod(AbstractController controller, final String methodName) {
		try {
			if (this.controller == null){
				this.controller = controller;
			}
			
			ControllerEventListener listener = new ControllerEventListener(){
				final PlayerEntity object = PlayerEntity.this;
				final Method method = object.getClass().getMethod(methodName);
				
				@Override
				public void handleEvent(long eventArg, Vector2 pos, int... params) {
					try {
						method.invoke(object);
					}
					catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						
					}
				}
			};
			
			controls.add(listener);
			return listener;
		}
		catch (NoSuchMethodException e) {
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
	
	public void canMove(boolean canMove){
		this.canMove = canMove;
	}
	
	@Override
	public void collisionEnd(Fixture myFixture, Fixture otherFixture, Collidable otherCollidable) {
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
	public void collisionStart(Fixture myFixture, Fixture otherFixture, Collidable otherCollidable) {
		if (!otherFixture.isSensor()){
			if (myFixture == sensors.get(SensorType.FOOT)){
				canJump = true;
				jumpStarted = false;
				jumpLength = 0;
				sprite.setState(AnimationState.RUN.index());
			}
			else if (myFixture == sensors.get(SensorType.LEFT)){
				leftSensorTouching = true;
			}
			else if (myFixture == sensors.get(SensorType.RIGHT)){
				rightSensorTouching = true;
			}
		}
		
		if (otherCollidable instanceof WeaponEntity){
			WeaponEntity weapon = (WeaponEntity)otherCollidable;
			if (currentWeapon != null && currentWeapon.equals(weapon)){
				return;
			}
			
			if (weapon.attachToPlayer(this)){
				if (currentWeapon != null){
					currentWeapon.detachFromPlayer();
				}
				currentWeapon = weapon;
				overlay.updateNumBullets(index, currentWeapon.getNumBullets());
			}
		}
	}
	
	@Override
	public void destroy() {
		super.destroy();
		
		if (controller != null){
			for (ControllerEventListener listener : controls){
				controller.removeKeybind(listener);
			}
			controls.clear();
			controls = null;
			controller = null;
		}
		
		if (currentWeapon != null){
			currentWeapon.detachFromPlayer();
			currentWeapon = null;
		}
	}
	
	public int getCategory(){
		return categoryMask;
	}
	
	public WeaponEntity getWeapon(){
		return currentWeapon;
	}
	
	@Override
	public void initEntity(PhysicsBody.EBodyType type) {
		super.initEntity(type);
		body.getBody().setSleepingAllowed(false);
		body.getBody().setBullet(true);
	}
	
	public boolean isDead(){
		return isDead;
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

	public void onDestroy(){
		super.onDestroy();
		if (currentWeapon != null){
			currentWeapon.detachFromPlayer();
			currentWeapon = null;
		}
	}
	
	public void reset(){
		if (currentWeapon != null){
			currentWeapon.setVisible(false);
			currentWeapon.detachFromPlayer();
			currentWeapon = null;
		}
		
		setHorizontalVelocity(0.0f);
		setVerticalVelocity(0.0f);
		moveRight();
	}
	
	public void setCategory(int category){
		categoryMask = category;
	}
	
	public void setDead(boolean isDead){
		this.isDead = isDead;
		deadFlagChanged = true;
	}
	
	public void setIndex(int i){
		index = i;
	}
	
	@Override
	public void setScale(Vector2 scale) {
		super.setScale(scale);
		this.scale = scale.copy();
		if (scale.x < 0){
			scale.x = -scale.x;
		}
	}
	
	public final void shoot(){
		tryShoot = true;
	}
	
	@Override
	public void update(float deltaTime) {
		if (deadFlagChanged){
			deadFlagChanged = false;
			
			setVisible(!isDead);
			body.getBody().setActive(!isDead);
			
			if (currentWeapon != null){
				currentWeapon.detachFromPlayer();
				currentWeapon = null;
			}
			
			if (isDead){
				overloadGame.getPlayerManager().playerDeath(this);
			}
		}

		if (isDead || overlay.isUIBlurred()){
			return;
		}
		
		super.update(deltaTime);
		
		if (canMove){
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
		
		if (tryShoot){
			tryShoot = false;
			if (currentWeapon != null){
				int numBullets = currentWeapon.tryShoot();
				overlay.updateNumBullets(index, numBullets);
			}
		}
	}
	
	public void weaponDetached(){
		currentWeapon = null;
		overlay.updateNumBullets(index, 0);
	}
}
