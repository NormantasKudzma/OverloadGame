package entities;

import game.OverloadEngine;
import game.Paths;
import graphics.Sprite2D;
import graphics.SpriteAnimation;

import org.jbox2d.dynamics.Fixture;
import org.json.JSONArray;
import org.json.JSONObject;

import utils.ConfigManager;
import utils.Vector2;
import controls.AbstractController;
import controls.ControllerEventListener;
import controls.ControllerKeybind;
import controls.ControllerManager;
import controls.EController;

public class PlayerManager {
	private PlayerEntity[] playerEntities = new PlayerEntity[4];
	
	public PlayerEntity getPlayer(int index){
		if (index < 0 || index >= playerEntities.length){
			return null;
		}
		return playerEntities[index];
	}
	
	public void loadPlayers(){
		JSONObject playerFileJson = ConfigManager.loadConfigAsJson(Paths.PLAYERS + "Players.json");
		Sprite2D spritesheet = new Sprite2D(Paths.SPRITESHEETS + playerFileJson.getString("spritesheet"));
		Vector2 sheetSizeCoef = new Vector2(spritesheet.getTexture().getWidth(), spritesheet.getTexture().getHeight());
		sheetSizeCoef.div(spritesheet.getTexture().getImageWidth(), spritesheet.getTexture().getImageHeight());
		JSONArray playerArrayJson = playerFileJson.getJSONArray("players");
		
		Vector2 aspectRatioScale = new Vector2(OverloadEngine.aspectRatio, 1.0f / OverloadEngine.aspectRatio);
		Vector2[] colliderVerts = loadCollider(playerFileJson);
		Vector2[][] sensorVerts = loadSensors(playerFileJson);
		
		for (int i = 0; i < playerArrayJson.length(); ++i){
			JSONObject playerJson = playerArrayJson.getJSONObject(i);
			PlayerEntity player = new PlayerEntity();
			player.initEntity();
			
			loadAnimations(playerJson, player, spritesheet, sheetSizeCoef);
			loadControls(playerJson, player);
			
			player.setPosition(1.0f, 1.0f);
			player.setScale(player.getScale().div(aspectRatioScale));
			player.getBody().attachPolygonCollider(colliderVerts);
			
			for (int j = 0; j < sensorVerts.length; ++j){
				Fixture f = player.getBody().attachPolygonCollider(sensorVerts[j], true);
				player.addSensor(f, PlayerEntity.SensorType.FOOT); // FIXME string -> type
			}

			player.getBody().getBody().getFixtureList().setRestitution(0.0f);
			player.getBody().getBody().getFixtureList().setFriction(1.7f);
			player.getBody().getBody().getFixtureList().setDensity(2.0f);
			playerEntities[i] = player;
		}
	}

	private Vector2[][] loadSensors(JSONObject json) {
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
				sensorVerts[i][j].div(OverloadEngine.frameWidth, OverloadEngine.frameHeight);
				sensorVerts[i][j].mul(1.25f);
			}
		}
		return sensorVerts;
	}

	private void loadControls(JSONObject playerJson, PlayerEntity player) {
		EController controlMethod = EController.getFromString(playerJson.getString("controlmethod"));
		int controllerIndex = playerJson.getInt("controlindex");
		AbstractController controller = ControllerManager.getInstance().getController(controlMethod, controllerIndex);
		
		JSONArray controlsArrayJson = playerJson.getJSONArray("controls");
		for (int i = 0; i < controlsArrayJson.length(); ++i){
			JSONObject control = controlsArrayJson.getJSONObject(i);
			long keyMask = control.getLong("keymask");
			String methodName = control.getString("method");
			final ControllerEventListener listener = player.getEventListenerForMethod(methodName);
			controller.addKeybind(new ControllerKeybind(keyMask, listener));
		}
	}

	private Vector2[] loadCollider(JSONObject json) {
		JSONArray colliderArrayJson = json.getJSONArray("collider");

		Vector2[] collVerts = new Vector2[colliderArrayJson.length()];
		for (int i = 0; i < colliderArrayJson.length(); ++i){
			JSONObject coll = colliderArrayJson.getJSONObject(i);
			collVerts[i] = new Vector2((float)coll.getDouble("x"), (float)coll.getDouble("y"));
			// Also, convert pixel coordinates to normalized coordinates
			collVerts[i].div(OverloadEngine.frameWidth, OverloadEngine.frameHeight);
			collVerts[i].mul(1.25f);
		}
		
		return collVerts;
	}

	private void loadAnimations(JSONObject playerJson, PlayerEntity player, Sprite2D spritesheet, Vector2 sheetSizeCoef) {
		JSONArray animationsJson = playerJson.getJSONArray("animations");
		Sprite2D[][] spriteAnimations = new Sprite2D[animationsJson.length()][];
		for (int j = 0; j < animationsJson.length(); ++j){
			JSONArray animation = animationsJson.getJSONArray(j);
			Sprite2D[] sprites = new Sprite2D[animation.length()];
			for (int k = 0; k < animation.length(); k++){
				JSONObject frameJson = animation.getJSONObject(k);
				Vector2 topLeft = new Vector2((float)frameJson.getDouble("x"), (float)frameJson.getDouble("y"));
				Vector2 botRight = new Vector2((float)frameJson.getDouble("w"), (float)frameJson.getDouble("h")).add(topLeft);
				topLeft.mul(sheetSizeCoef);
				botRight.mul(sheetSizeCoef);
				
				sprites[k] = new Sprite2D(spritesheet.getTexture(), topLeft, botRight);
			}
			spriteAnimations[j] = sprites;
		}
		
		SpriteAnimation spriteAnim = new SpriteAnimation();
		spriteAnim.setSpriteArray(spriteAnimations);
		player.setSprite(spriteAnim);
	}
}
