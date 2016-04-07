package managers;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import physics.PhysicsBody;
import utils.ConfigManager;
import utils.Vector2;
import engine.BaseGame;
import engine.Entity;
import engine.OverloadEngine;
import entities.PlayerEntity;
import entities.WallEntity;
import game.OverloadGame;
import game.Paths;
import graphics.Layer;
import graphics.Sprite2D;

public class MapManager extends EntityManager{
	private String playerLayer = null;
	
	public MapManager(BaseGame game) {
		super(game);
	}

	public String getPlayersLayer(){
		return playerLayer;
	}
	
	public void loadMap(BaseGame game, String path){
		JSONObject json = ConfigManager.loadConfigAsJson(path);
		
		HashMap<String, Sprite2D> spriteSheets = new HashMap<String, Sprite2D>();
		HashMap<String, Entity> entities = new HashMap<String, Entity>();		

		Vector2 mapSize = Vector2.fromJsonArray(json.getJSONArray("mapsize")).div(2.0f);
		
		loadSpriteSheets(json, spriteSheets);
		loadEntities(json, spriteSheets, entities, mapSize);
		loadColliders(json, game);
		
		JSONArray layersJsonArray = json.getJSONArray("layers");
		for (int i = 0; i < layersJsonArray.length(); ++i){
			JSONObject layerJson = layersJsonArray.getJSONObject(i);
			loadLayer(layerJson, game, entities, mapSize);
		}
		
		loadPlayerPositions(json, mapSize);
	}

	private void loadColliders(JSONObject json, BaseGame game) {
		JSONArray colliderArrayJson = json.getJSONArray("colliders");
		JSONArray mapSizeJson = json.getJSONArray("mapsize");
		Vector2 mapSize = Vector2.fromJsonArray(mapSizeJson).div(2.0f);
		WallEntity colliderEntity = new WallEntity(game);
		colliderEntity.initEntity(PhysicsBody.EBodyType.INTERACTIVE);
		colliderEntity.setVisible(false);
		
		for (int i = 0; i < colliderArrayJson.length(); ++i){
			JSONArray collJson = colliderArrayJson.getJSONArray(i);
			Vector2[] collVerts = new Vector2[collJson.length()];
			for (int j = 0; j < collJson.length(); ++j){
				JSONObject coll = collJson.getJSONObject(j);
				collVerts[j] = new Vector2((float)coll.getDouble("x"), (float)coll.getDouble("y"));
				collVerts[j].div(mapSize);
			}
			colliderEntity.getPhysicsBody().attachPolygonCollider(collVerts);
		}
		colliderEntity.setCollisionFlags(WALL_CATEGORY, WALL_COLLIDER);
		
		game.addEntity(colliderEntity);
	}

	private void loadEntities(JSONObject json, HashMap<String, Sprite2D> spriteSheets, HashMap<String, Entity> entities, Vector2 mapSize) {
		JSONArray entityArrayJson = json.getJSONArray("entities");
		float gridSize = OverloadEngine.frameWidth / mapSize.x;
		
		for (int i = 0; i < entityArrayJson.length(); ++i){
			try {
				JSONObject entityJson = entityArrayJson.getJSONObject(i);
				Object obj = Class.forName(entityJson.getString("type")).getDeclaredConstructor(BaseGame.class).newInstance(game);
				if (obj instanceof Entity){
					Entity e = (Entity) obj;
					e.initEntity(PhysicsBody.EBodyType.NON_INTERACTIVE);
					Sprite2D sheet = spriteSheets.get(entityJson.get("sheet"));
					int x = entityJson.getInt("x");
					int y = entityJson.getInt("y");
					int w = entityJson.getInt("w");
					int h = entityJson.getInt("h");
					e.setSprite(Sprite2D.getSpriteFromSheet(x, y, w, h, sheet));
					e.setScale(e.getScale().mul((float)entityJson.getDouble("scale")).mul(gridSize / (float)w));
					entities.put(entityJson.getString("name"), e);
				}
			}
			catch (Exception e){
				e.printStackTrace();
			}
		}
	}
	
	private void loadLayer(JSONObject json, BaseGame game, HashMap<String, Entity> mapEntities, Vector2 mapSize) {
		String layerName = json.getString("name");
		int index = json.getInt("index");
		Layer layer = new Layer(layerName, index);
		
		if (json.has("isPlayersLayer")){
			playerLayer = layerName;
		}
		
		JSONArray objectsArrayJson = json.getJSONArray("objects");
		for (int i = 0; i < objectsArrayJson.length(); ++i){
			try {
				JSONObject entityJson = objectsArrayJson.getJSONObject(i);
				JSONArray scaleJson = entityJson.getJSONArray("scale");
				Vector2 tileScale = Vector2.fromJsonArray(scaleJson);
				JSONArray positionJson = entityJson.getJSONArray("position");
				Entity e = mapEntities.get(entityJson.getString("entity"));
				Entity clone = (Entity)e.getClass().getDeclaredConstructor(BaseGame.class).newInstance(game);
				clone.initEntity(PhysicsBody.EBodyType.NON_INTERACTIVE);
				clone.setSprite(e.getSprite());
				clone.setScale(e.getScale().copy().mul(tileScale));
				clone.setPosition(Vector2.fromJsonArray(positionJson).div(mapSize));
				layer.addEntity(clone);
			}
			catch (Exception e){
				e.printStackTrace();
			}
		}
		game.addLayer(layer);
	}

	private void loadPlayerPositions(JSONObject json, Vector2 mapSize) {
		JSONArray playersArrayJson = json.getJSONArray("players");
		for (int i = 0; i < playersArrayJson.length(); ++i){
			JSONObject playerJson = playersArrayJson.getJSONObject(i);
			Vector2 position = Vector2.fromJsonArray(playerJson.getJSONArray("position")).div(mapSize);
			PlayerEntity player = ((OverloadGame)game).getPlayerManager().getPlayer(i);
			if (player != null){
				player.setPosition(position);
			}
		}
	}
	
	private void loadSpriteSheets(JSONObject json, HashMap<String, Sprite2D> spriteSheets) {
		JSONArray spriteSheetJson = json.getJSONArray("spritesheets");
		
		for (int i = 0; i < spriteSheetJson.length(); ++i){
			JSONObject sheet = spriteSheetJson.getJSONObject(i);
			spriteSheets.put(sheet.getString("name"), new Sprite2D(Paths.SPRITESHEETS + sheet.getString("path")));
		}
	}
}
