package managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ovl.engine.BaseGame;
import com.ovl.engine.GameObject;
import com.ovl.engine.OverloadEngine;
import com.ovl.graphics.Layer;
import com.ovl.graphics.Sprite;
import com.ovl.physics.PhysicsBody;
import com.ovl.physics.PhysicsBody.EBodyType;
import com.ovl.utils.ConfigManager;
import com.ovl.utils.Vector2;

import entities.MapBoundsEntity;
import entities.PlayerEntity;
import entities.WallEntity;
import game.OverloadGame;
import game.Paths;

public class MapManager extends EntityManager{
	private String backgroundLayer = null;
	private String playersLayer = null;
	private String weaponsLayer = null;
	private Vector2 playerScale = null;
	private ArrayList<String> lastMapLayers = new ArrayList<String>();
	private ArrayList<String> mapList = new ArrayList<String>();
	private ArrayList<String> mapPool = new ArrayList<String>();
	
	public MapManager(BaseGame game) {
		super(game);
		loadMapPool();
	}

	public void cleanUpLayers(){
		if (playerScale != null){
			Vector2 invScale = Vector2.invert(playerScale);
			PlayerManager playerManager = ((OverloadGame)game).getPlayerManager();
			playerManager.scalePlayers(invScale);
			((OverloadGame)game).getWeaponManager().scaleWeapons(invScale);
			playerScale = null;
		}
		
		Layer l = null;
		for (String i : lastMapLayers){
			if ((l = game.getLayer(i)) != null){
				if (playersLayer == null || !l.getName().equals(playersLayer)){
					l.destroy();
				}
				l.clear();
				game.removeLayer(i);
			}
		}
		lastMapLayers.clear();
		
		if (playersLayer != null){
			game.getLayer(playersLayer).clear();			
			playersLayer = null;
		}
		backgroundLayer = null;
		weaponsLayer = null;
	}
	
	public void destroy(){
		cleanUpLayers();
	}
	
	public String getPlayersLayer(){
		return playersLayer;
	}
	
	public String getWeaponsLayer(){
		return weaponsLayer;
	}
	
	private void loadColliders(JSONObject json, BaseGame game) {
		JSONArray colliderArrayJson = json.getJSONArray("colliders");
		JSONArray mapSizeJson = json.getJSONArray("mapsize");
		Vector2 mapSize = Vector2.fromJsonArray(mapSizeJson).div(2.0f);
		WallEntity colliderEntity = new WallEntity();
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
		
		game.addObject(colliderEntity, backgroundLayer);
	}

	private void loadEntities(JSONObject json, HashMap<String, Sprite> spriteSheets, HashMap<String, GameObject> entities, Vector2 mapSize) {
		JSONArray entityArrayJson = json.getJSONArray("entities");
		float gridSize = OverloadEngine.getInstance().frameWidth / mapSize.x;
		
		for (int i = 0; i < entityArrayJson.length(); ++i){
			try {
				JSONObject entityJson = entityArrayJson.getJSONObject(i);
				Object obj = Class.forName(entityJson.getString("type")).getDeclaredConstructor(BaseGame.class).newInstance(game);
				if (obj instanceof GameObject){
					GameObject e = (GameObject) obj;
					e.initEntity(PhysicsBody.EBodyType.NON_INTERACTIVE);
					Sprite sheet = spriteSheets.get(entityJson.get("sheet"));
					int x = entityJson.getInt("x");
					int y = entityJson.getInt("y");
					int w = entityJson.getInt("w");
					int h = entityJson.getInt("h");
					e.setSprite(Sprite.getSpriteFromSheet(x, y, w, h, sheet));
					e.setScale(e.getScale().mul((float)entityJson.getDouble("scale")).mul(gridSize / (float)w));
					entities.put(entityJson.getString("name"), e);
				}
			}
			catch (Exception e){
				e.printStackTrace();
			}
		}
	}
	
	private void loadLayer(JSONObject json, BaseGame game, HashMap<String, GameObject> mapEntities, Vector2 mapSize) {
		String layerName = json.getString("name");
		int index = json.getInt("index");
		Layer layer = new Layer(layerName, index);
		
		JSONArray objectsArrayJson = json.getJSONArray("objects");
		for (int i = 0; i < objectsArrayJson.length(); ++i){
			try {
				JSONObject entityJson = objectsArrayJson.getJSONObject(i);
				JSONArray scaleJson = entityJson.getJSONArray("scale");
				Vector2 tileScale = Vector2.fromJsonArray(scaleJson);
				JSONArray positionJson = entityJson.getJSONArray("position");
				GameObject e = mapEntities.get(entityJson.getString("entity"));
				GameObject clone = (GameObject)e.clone();
				clone.setScale(e.getScale().copy().mul(tileScale));
				clone.setPosition(Vector2.fromJsonArray(positionJson).div(mapSize));
				layer.addObject(clone);
			}
			catch (Exception e){
				e.printStackTrace();
			}
		}
		game.addLayer(layer);
		
		if (json.has("isBackgroundLayer")){
			backgroundLayer = layerName;
		}
		
		if (json.has("isWeaponsLayer")){
			weaponsLayer = layerName;
		}
		
		if (json.has("isPlayersLayer")){
			playersLayer = layerName;
		}
		else {
			lastMapLayers.add(layerName);
		}
	}
	
	public void loadMap(String path){
		cleanUpLayers();
		
		JSONObject json = ConfigManager.loadConfigAsJson(path);
		
		HashMap<String, Sprite> spriteSheets = new HashMap<String, Sprite>();
		HashMap<String, GameObject> entities = new HashMap<String, GameObject>();		

		Vector2 mapSize = Vector2.fromJsonArray(json.getJSONArray("mapsize")).div(2.0f);
		
		loadSpriteSheets(json, spriteSheets);
		loadEntities(json, spriteSheets, entities, mapSize);
		
		JSONArray layersJsonArray = json.getJSONArray("layers");
		for (int i = 0; i < layersJsonArray.length(); ++i){
			JSONObject layerJson = layersJsonArray.getJSONObject(i);
			loadLayer(layerJson, game, entities, mapSize);
		}
		
		loadPlayerPositions(json, mapSize);
		loadColliders(json, game);
		
		MapBoundsEntity mapBoundsEntity = new MapBoundsEntity();
		mapBoundsEntity.initEntity(EBodyType.INTERACTIVE);
		mapBoundsEntity.setCollisionFlags(WALL_CATEGORY, WALL_COLLIDER);
		game.addObject(mapBoundsEntity, backgroundLayer);
	}

	private void loadMapPool(){
		JSONObject json = ConfigManager.loadConfigAsJson(Paths.MAPS + "MapPool.json");
		JSONArray mapsJsonArray = json.getJSONArray("maps");
		
		for (int i = 0; i < mapsJsonArray.length(); ++i){
			mapPool.add(mapsJsonArray.getString(i));
		}
	}
	
	public void loadNextMap(){
		if (mapList.isEmpty()){
			mapList.addAll(mapPool);
			Collections.shuffle(mapList);
		}
		
		String mapName = mapList.get(0);
		mapList.remove(0);
		
		loadMap(Paths.MAPS + mapName);
	}

	private void loadPlayerPositions(JSONObject json, Vector2 mapSize) {
		PlayerManager playerManager = ((OverloadGame)game).getPlayerManager();
		
		if (json.has("playerScale")){
			playerScale = Vector2.fromJsonArray(json.getJSONArray("playerScale"));
			playerManager.scalePlayers(playerScale);
			((OverloadGame)game).getWeaponManager().scaleWeapons(playerScale);
		}
		
		JSONArray playersArrayJson = json.getJSONArray("players");
		for (int i = 0; i < playersArrayJson.length(); ++i){
			JSONObject playerJson = playersArrayJson.getJSONObject(i);
			Vector2 position = Vector2.fromJsonArray(playerJson.getJSONArray("position")).div(mapSize);
			PlayerEntity player = ((OverloadGame)game).getPlayerManager().getPlayer(i);
			if (player != null){
				player.setPosition(position);
				
				if (playerManager.isPlayerEnabled(i)){
					game.addObject(player, playersLayer);
				}
			}
		}
	}
	
	private void loadSpriteSheets(JSONObject json, HashMap<String, Sprite> spriteSheets) {
		JSONArray spriteSheetJson = json.getJSONArray("spritesheets");
		
		for (int i = 0; i < spriteSheetJson.length(); ++i){
			JSONObject sheet = spriteSheetJson.getJSONObject(i);
			spriteSheets.put(sheet.getString("name"), new Sprite(Paths.SPRITESHEETS + sheet.getString("path")));
		}
	}
}
