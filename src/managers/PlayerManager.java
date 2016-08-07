package managers;

import org.jbox2d.dynamics.Fixture;
import org.json.JSONArray;
import org.json.JSONObject;

import ui.Overlay;

import com.ovl.controls.Controller;
import com.ovl.controls.ControllerEventListener;
import com.ovl.controls.ControllerKeybind;
import com.ovl.controls.ControllerManager;
import com.ovl.engine.BaseGame;
import com.ovl.graphics.Sprite;
import com.ovl.graphics.SpriteAnimation;
import com.ovl.physics.PhysicsBody;
import com.ovl.utils.ConfigManager;
import com.ovl.utils.Vector2;

import entities.PlayerEntity;
import entities.PlayerEntity.SensorType;
import game.OverloadGame;
import game.Paths;

public class PlayerManager extends EntityManager{
	public static final int PLAYER_COLLIDERS[] = {EntityManager.PLAYER1_CATEGORY, 
												EntityManager.PLAYER2_CATEGORY, 
												EntityManager.PLAYER3_CATEGORY, 
												EntityManager.PLAYER4_CATEGORY};
	public static final int NUM_PLAYERS = 4;
	private boolean playerEnabled[] = new boolean[]{true, true, true, true};
	private PlayerEntity playerEntities[] = new PlayerEntity[NUM_PLAYERS];

	public PlayerManager(BaseGame game) {
		super(game);
	}
	
	public void destroy(){
		for (int i = 0; i < playerEntities.length; ++i){
			playerEntities[i].destroy();
		}
	}
	
	public PlayerEntity getPlayer(int index){
		if (index < 0 || index >= playerEntities.length){
			return null;
		}
		return playerEntities[index];
	}
	
	public boolean isPlayerEnabled(int index){
		return playerEnabled[index];
	}
	
	public void loadPlayers(){
		JSONObject playerFileJson = ConfigManager.loadConfigAsJson(Paths.PLAYERS + "Players.json");
		Sprite spriteSheet = new Sprite(Paths.SPRITESHEETS + playerFileJson.getString("spritesheet"));
		JSONArray playerArrayJson = playerFileJson.getJSONArray("players");
		Vector2 playerScale = new Vector2((float)playerFileJson.getDouble("scale"), (float)playerFileJson.getDouble("scale"));
		
		Vector2[] colliderVerts = loadCollider(playerFileJson, playerScale);
		Vector2[][] sensorVerts = loadSensors(playerFileJson, playerScale);
		SensorType[] sensorTypes = new SensorType[]{SensorType.FOOT, SensorType.LEFT, SensorType.RIGHT};
		
		for (int i = 0; i < playerArrayJson.length(); ++i){
			JSONObject playerJson = playerArrayJson.getJSONObject(i);
			PlayerEntity player = new PlayerEntity(game);
			player.initEntity(PhysicsBody.EBodyType.INTERACTIVE);
			player.setIndex(i);
			
			loadAnimations(playerJson, player, spriteSheet);
			loadControls(playerJson, player);
			
			player.setPosition(-1.0f, -1.0f);
			player.setScale(player.getScale().mul(playerScale));
			player.getPhysicsBody().attachPolygonCollider(colliderVerts);
			
			for (int j = 0; j < sensorVerts.length; ++j){
				Fixture f = player.getPhysicsBody().attachPolygonCollider(sensorVerts[j], true);
				player.addSensor(f, sensorTypes[j]);
			}
			
			// Only set collision category and flags after attaching all the fixtures
			player.setCategory(PLAYER_COLLIDERS[i]);
			player.setCollisionFlags(PLAYER_COLLIDERS[i], PLAYER_COLLIDER);

			player.getPhysicsBody().getBody().getFixtureList().setRestitution(0.0f);
			player.getPhysicsBody().getBody().getFixtureList().setFriction(100.0f);
			player.getPhysicsBody().getBody().getFixtureList().setDensity(1.25f);

			playerEntities[i] = player;
		}
	}

	private Vector2[][] loadSensors(JSONObject json, Vector2 globalScale) {
		Vector2[][] sensorVerts = null;
		JSONArray sensorArrayJson = json.getJSONArray("sensors");
		sensorVerts = new Vector2[sensorArrayJson.length()][];
		for (int i = 0; i < sensorArrayJson.length(); ++i){
			JSONArray sensorJson = sensorArrayJson.getJSONArray(i);
			sensorVerts[i] = new Vector2[sensorJson.length()];
			for (int j = 0; j < sensorJson.length(); ++j){
				JSONObject sensor = sensorJson.getJSONObject(j);
				sensorVerts[i][j] = new Vector2((float)sensor.getDouble("x"), (float)sensor.getDouble("y"));
				// Convert pixel coordinates to normalized coordinates
				//sensorVerts[i][j].div(OverloadEngine.frameWidth, OverloadEngine.frameHeight);
				//sensorVerts[i][j].mul(1.25f);
				Vector2.pixelCoordsToNormal(sensorVerts[i][j]);
				sensorVerts[i][j].mul(globalScale);
			}
		}
		return sensorVerts;
	}

	private void loadControls(JSONObject playerJson, PlayerEntity player) {
		Controller.Type controlMethod = Controller.Type.getFromString(playerJson.getString("controlmethod"));
		int controllerIndex = playerJson.getInt("controlindex");
		Controller controller = ControllerManager.getInstance().getController(controlMethod, controllerIndex);
		
		if (controller != null){
			JSONArray controlsArrayJson = playerJson.getJSONArray("controls");
			for (int i = 0; i < controlsArrayJson.length(); ++i){
				JSONObject control = controlsArrayJson.getJSONObject(i);
				long keyMask = control.getLong("keymask");
				String methodName = control.getString("method");
				final ControllerEventListener listener = player.getEventListenerForMethod(controller, methodName);
				controller.addKeybind(new ControllerKeybind(keyMask, listener));
			}
			controller.startController();
		}
	}

	private Vector2[] loadCollider(JSONObject json, Vector2 globalScale) {
		JSONArray colliderArrayJson = json.getJSONArray("collider");

		Vector2[] collVerts = new Vector2[colliderArrayJson.length()];
		for (int i = 0; i < colliderArrayJson.length(); ++i){
			JSONObject coll = colliderArrayJson.getJSONObject(i);
			collVerts[i] = new Vector2((float)coll.getDouble("x"), (float)coll.getDouble("y"));
			Vector2.pixelCoordsToNormal(collVerts[i]);
			collVerts[i].mul(globalScale);
		}
		
		return collVerts;
	}

	private void loadAnimations(JSONObject playerJson, PlayerEntity player, Sprite spritesheet) {
		JSONArray animationsJson = playerJson.getJSONArray("animations");
		Sprite[][] spriteAnimations = new Sprite[animationsJson.length()][];
		for (int j = 0; j < animationsJson.length(); ++j){
			JSONArray animation = animationsJson.getJSONArray(j);
			Sprite[] sprites = new Sprite[animation.length()];
			for (int k = 0; k < animation.length(); k++){
				JSONObject frameJson = animation.getJSONObject(k);
				
				int x = frameJson.getInt("x");
				int y = frameJson.getInt("y");
				int w = frameJson.getInt("w");
				int h = frameJson.getInt("h");				
				sprites[k] = Sprite.getSpriteFromSheet(x, y, w, h, spritesheet);
			}
			spriteAnimations[j] = sprites;
		}
		
		SpriteAnimation spriteAnim = new SpriteAnimation();
		spriteAnim.setSpriteArray(spriteAnimations);
		player.setSprite(spriteAnim);
	}

	public void playerDeath(PlayerEntity player){
		int numAlive = 0;
		int aliveIndex = -1;
		
		Overlay overlay = ((OverloadGame)game).getOverlay();
		
		for (int i = 0; i < NUM_PLAYERS; ++i){
			if (player.equals(playerEntities[i])){
				overlay.setPlayerDead(i, true);
			}
			
			if (!playerEntities[i].isDead() && playerEnabled[i]){
				++numAlive;
				aliveIndex = i;
			}
		}
		
		if (numAlive == 1 && aliveIndex != -1){
			overlay.gameEnding();
			overlay.addPoint(aliveIndex);
		}
		else if (numAlive <= 0 && !overlay.isUIBlurred()){
			overlay.gameEnding();
		}
	}

	public void reset(){
		for (int i = 0; i < playerEntities.length; ++i){
			playerEntities[i].setDead(false);
			playerEntities[i].reset();
		}
	}
	
	public void scalePlayers(Vector2 scale){
		for (int i = 0; i < NUM_PLAYERS; ++i){
			PlayerEntity p = playerEntities[i];
			p.setScale(p.getScale().mul(scale));
			p.getPhysicsBody().resizeColliders(scale);
		}
	}
	
	public void setPlayerEnabled(int index, boolean enabled){
		playerEnabled[index] = enabled;
		playerEntities[index].setVisible(enabled);
		playerEntities[index].getPhysicsBody().getBody().setActive(false);
	}
}
